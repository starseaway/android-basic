package com.xinyi.androidbasic.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException

/**
 * ViewBinding 统一加载工具
 *
 * 优先通过反射调用 ViewBinding 的 `inflate`：
 * 普通布局为 `inflate(LayoutInflater, ViewGroup, Boolean)`，
 * `<merge>` 布局只有 `inflate(LayoutInflater, ViewGroup)`，必须带 parent 且 attach。
 *
 * 仅当绑定类不存在、且消费方启用了 DataBinding 时，才回退到
 * [DataBindingUtil.inflate] 以兼容 `<layout>` 根布局。
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
     * @param context 用于解析包名与资源名
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
        val bindingClassName = layoutNameToBindingClassName(context.packageName, layoutName)
        val classLoader = context.applicationContext.classLoader ?: context.javaClass.classLoader

        val bindingClass = try {
            Class.forName(bindingClassName, false, classLoader)
        } catch (throwable: Throwable) {
            val error = unwrapReflectException(throwable)
            if (error is ClassNotFoundException || error is NoClassDefFoundError) {
                return inflateByDataBindingOrThrow(
                    context = context,
                    layoutId = layoutId,
                    inflater = inflater,
                    parent = parent,
                    attachToParent = attachToParent,
                    bindingClassName = bindingClassName,
                    layoutName = layoutName,
                    missingClass = error,
                )
            }
            throw error
        }

        try {
            return inflateByReflection(bindingClass, inflater, parent, attachToParent) as VB
        } catch (throwable: Throwable) {
            // 方法已找到但 inflate 失败，或 merge 签名不匹配，都直接抛真实异常
            throw unwrapReflectException(throwable)
        }
    }

    /**
     * 绑定类不存在时，尝试 DataBinding 回退；未启用则说明 ViewBinding 类未生成
     */
    @Suppress("UNCHECKED_CAST")
    private fun <VB : ViewBinding> inflateByDataBindingOrThrow(
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
                "找不到 ViewBinding 类 $bindingClassName（layout=$layoutName）",
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
     * 查找方法；不存在时返回 null
     */
    private fun findMethodOrNull(
        bindingClass: Class<*>,
        name: String,
        vararg parameterTypes: Class<*>,
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
