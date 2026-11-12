package com.xinyi.androidbasic.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

/**
 * ViewBinding 统一加载工具
 *
 * 优先通过反射调用 ViewBinding 的 `inflate`：
 * 普通布局为 `inflate(LayoutInflater, ViewGroup, Boolean)`，
 * `<merge>` 布局只有 `inflate(LayoutInflater, ViewGroup)`，必须带 parent 且 attach。
 *
 * Binding Class 类路径通过组件的泛型继承关系自动获取，不会依赖 applicationId 或 namespace。
 *
 * 若当无法解析绑定类、且主模块启用了 DataBinding 时 会回退到 [DataBindingUtil.inflate] 以兼容 `<layout>` 根布局。
 *
 * @author 杨耿雷
 * @date 2026/7/7 13:52
 */
object BindingInflaters {

    /**
     * DataBinding 聚合 Mapper 的类名；仅在消费方开启 dataBinding 时由 AGP 生成。
     */
    private const val DATA_BINDER_MAPPER_CLASS = "androidx.databinding.DataBinderMapperImpl"

    /**
     * 根据布局 ID 自动加载 ViewBinding
     *
     * 通过 [context] 对应组件的泛型继承关系获取 Binding Class，
     * 不再根据 applicationId 拼接绑定类路径。
     *
     * @param context 用于解析组件泛型与检查 DataBinding 环境
     * @param layoutId 布局资源 ID
     * @param inflater LayoutInflater
     * @param parent 父容器，可为 null
     * @param attachToParent 是否 attach 到 parent
     */
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> inflate(
        context: Context,
        layoutId: Int,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): VB {
        val layoutName = context.resources.getResourceEntryName(layoutId)

        val bindingClass = try {
            resolveBindingClass(context.javaClass)
        } catch (throwable: Throwable) {
            val error = unwrapReflectException(throwable)

            return inflateByDataBinding(
                context = context,
                layoutId = layoutId,
                inflater = inflater,
                parent = parent,
                attachToParent = attachToParent,
                bindingClassName = "<泛型解析失败：${context.javaClass.name}>",
                layoutName = layoutName,
                missingClass = error,
            )
        }

        try {
            return inflateByReflection(
                bindingClass,
                inflater,
                parent,
                attachToParent,
            ) as VB
        } catch (throwable: Throwable) {
            // 方法已找到但 inflate 失败，或 merge 签名不匹配，都直接抛真实异常
            throw unwrapReflectException(throwable)
        }
    }

    /**
     * 从组件的泛型继承链中解析 ViewBinding Class
     *
     * 支持直接继承和多层泛型继承。
     *
     * 例如：
     * ```
     * abstract class CommonActivity<VB : ViewBinding> :BaseViewBindingActivity<VB>()
     *
     * class InitActivity : CommonActivity<ActivityTrainerInitBinding>()
     * ```
     *
     * @param componentClass 具体组件类型
     * @return 实际的 ViewBinding Class
     */
    private fun resolveBindingClass(componentClass: Class<*>): Class<out ViewBinding> {
        // 保存泛型变量与实际类型之间的映射关系
        val typeArguments = mutableMapOf<TypeVariable<*>, Type>()

        var currentType: Type = componentClass

        while (true) {
            val rawClass: Class<*>

            when (currentType) {
                is ParameterizedType -> {
                    rawClass = currentType.rawType as? Class<*>
                        ?: throw IllegalStateException("无法解析泛型原始类型：${currentType.rawType}")

                    val variables = rawClass.typeParameters
                    val arguments = currentType.actualTypeArguments

                    // 建立当前类的泛型参数映射
                    variables.forEachIndexed { index, variable ->
                        typeArguments[variable] = arguments[index]
                    }

                    // 检查当前类声明的泛型参数
                    arguments.forEach { argument ->
                        val resolvedType = resolveType(
                            argument,
                            typeArguments,
                        )

                        findBindingClass(resolvedType)?.let {
                            return it
                        }
                    }
                }
                is Class<*> -> {
                    rawClass = currentType
                }
                else -> {
                    throw IllegalStateException(
                        "无法解析组件继承类型：$currentType"
                    )
                }
            }
            // 沿继承链继续向上解析
            currentType = rawClass.genericSuperclass ?: break
        }
        throw IllegalStateException(
            "无法从 ${componentClass.name} 的泛型继承链中解析 ViewBinding 类型"
        )
    }

    /**
     * 展开泛型变量，获取实际类型
     *
     * 根据泛型变量与实际类型的映射关系，逐层替换 [TypeVariable]，
     * 直到获取最终类型或遇到无法继续解析的类型。
     *
     * 通过 [resolving] 记录当前解析路径中的泛型变量，避免循环引用导致无限递归。
     *
     * @param type 待解析的泛型类型
     * @param typeArguments 泛型变量与实际类型之间的映射关系
     * @param resolving 当前递归过程中已访问的泛型变量，用于检测循环引用
     * @return 展开后的实际类型；无法继续解析时返回当前类型
     */
    private fun resolveType(
        type: Type,
        typeArguments: Map<TypeVariable<*>, Type>,
        resolving: MutableSet<TypeVariable<*>> = mutableSetOf(),
    ): Type {
        return when (type) {
            is TypeVariable<*> -> {
                if (!resolving.add(type)) {
                    return type
                }

                val actualType = typeArguments[type] ?: return type
                if (actualType == type) {
                    return type
                }
                resolveType(actualType, typeArguments, resolving)
            }
            is WildcardType -> {
                type.upperBounds.firstOrNull()?.let {
                    resolveType(it, typeArguments, resolving)
                } ?: type
            }
            else -> type
        }
    }

    /**
     * 从实际类型中提取 ViewBinding Class
     *
     * 支持普通 Class、ParameterizedType 和 WildcardType。
     *
     * @param type 要解析的类型
     */
    private fun findBindingClass(type: Type): Class<out ViewBinding>? {
        val bindingClass = when (type) {
            is Class<*> -> type
            is ParameterizedType -> {
                type.rawType as? Class<*>
            }
            is WildcardType -> {
                type.upperBounds.firstOrNull()?.let {
                    findBindingClass(it)
                }
            }
            else -> null
        }

        if (bindingClass is Class<*> && ViewBinding::class.java.isAssignableFrom(bindingClass)) {
            @Suppress("UNCHECKED_CAST")
            return bindingClass as Class<out ViewBinding>
        }

        return null
    }

    /**
     * 当无法解析 ViewBinding 类时，尝试使用 DataBinding 加载布局。
     *
     * @param bindingClassName 预期的 ViewBinding 类名或解析失败信息
     * @param layoutName 布局资源名称
     * @param missingClass 原始 Binding 类型解析异常
     * @return 加载完成的 DataBinding 对象
     */
    @Suppress("UNCHECKED_CAST")
    private fun <VB : ViewBinding> inflateByDataBinding(
        context: Context,
        layoutId: Int,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
        bindingClassName: String,
        layoutName: String,
        missingClass: Throwable,
    ): VB {
        if (!isDataBindingAvailable(context)) {
            throw IllegalStateException(
                "无法获取 ViewBinding 类 $bindingClassName（layout=$layoutName）",
                missingClass,
            )
        }

        try {
            return DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent) as VB
        } catch (throwable: Throwable) {
            val dataBindingError = unwrapReflectException(throwable)
            throw dataBindingError.initCauseIfNeeded(missingClass)
        }
    }

    /**
     * 通过反射加载 ViewBinding
     *
     * @param bindingClass 绑定类
     * @param inflater LayoutInflater
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     */
    private fun inflateByReflection(
        bindingClass: Class<*>,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): ViewBinding {
        val threeArg = findMethodOrNull(
            bindingClass,
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            java.lang.Boolean.TYPE,
        )
        if (threeArg != null) {
            return threeArg.invoke(null, inflater, parent, attachToParent) as ViewBinding
        }

        // <merge> 布局只生成 inflate(LayoutInflater, ViewGroup)
        val twoArg = findMethodOrNull(
            bindingClass,
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
        ) ?: throw NoSuchMethodException(
            "${bindingClass.name}.inflate(LayoutInflater, ViewGroup) 或 inflate(LayoutInflater, ViewGroup, boolean)",
        )

        if (parent == null || !attachToParent) {
            throw IllegalArgumentException(
                "${bindingClass.name} 是 merge 布局的 ViewBinding，必须传入 parent 且 attachToParent=true",
            )
        }
        return twoArg.invoke(null, inflater, parent) as ViewBinding
    }

    /**
     * 查找 bindingClass 中指定的方法名
     *
     * @return 不存在时返回 null
     */
    private fun findMethodOrNull(
        bindingClass: Class<*>,
        name: String,
        vararg parameterTypes: Class<*>
    ): java.lang.reflect.Method? {
        return try {
            bindingClass.getMethod(name, *parameterTypes)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    /**
     * 消费方是否已生成 DataBinding Mapper（即开启了 dataBinding）
     */
    private fun isDataBindingAvailable(context: Context): Boolean {
        return try {
            val classLoader = context.applicationContext.classLoader ?: context.javaClass.classLoader
            Class.forName(DATA_BINDER_MAPPER_CLASS, false, classLoader)
            true
        } catch (_: ClassNotFoundException) {
            false
        } catch (_: NoClassDefFoundError) {
            false
        }
    }

    /**
     * 将布局名称转换为绑定类名称
     *
     * @param packageName 包名
     * @param layoutName 布局名称
     * @return 绑定类名称
     */
    private fun layoutNameToBindingClassName(packageName: String, layoutName: String): String {
        val className = layoutName.split('_')
            .joinToString("") { segment -> segment.replaceFirstChar { it.uppercaseChar() } }
        return "$packageName.databinding.${className}Binding"
    }

    /**
     * 展开反射调用异常，暴露真实 cause
     */
    private fun unwrapReflectException(throwable: Throwable): Throwable {
        return if (throwable is InvocationTargetException) {
            throwable.targetException ?: throwable
        } else {
            throwable
        }
    }

    /**
     * 若尚未设置 cause，则补上先前异常，便于排查
     */
    private fun Throwable.initCauseIfNeeded(cause: Throwable?): Throwable {
        if (cause != null && this.cause == null) {
            try {
                initCause(cause)
            } catch (_: IllegalStateException) {
                // 已有 cause 时忽略
            }
        }
        return this
    }
}