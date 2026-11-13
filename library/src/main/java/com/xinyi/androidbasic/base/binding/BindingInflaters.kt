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
 * 优先通过反射，调用类的 `inflate(LayoutInflater, ViewGroup, Boolean)`。
 *
 * 仅当消费方启用了 DataBinding（存在 `DataBinderMapperImpl`）时，才回退到
 * [DataBindingUtil.inflate] 以兼容 `<layout>` 布局；纯 ViewBinding 工程不再强依赖 DataBinding。
 *
 * @author 杨耿雷
 * @date 2026/7/7 13:52
 */
object BindingInflaters {

    /**
     * 宿主 App 的 namespace 缓存
     */
    @Volatile
    private lateinit var sAppNamespace: String

    /**
     * 宿主 App namespace 对应的 Meta-data 名称
     */
    private const val META_HOST_APP_NAMESPACE = "com.xinyi.androidbasic.HOST_APP_NAMESPACE"

    /**
     * DataBinding 聚合 Mapper 的类名；仅在消费方开启 dataBinding 时由 AGP 生成。
     */
    private const val DATA_BINDER_MAPPER_CLASS = "androidx.databinding.DataBinderMapperImpl"

    /**
     * 根据布局 ID 自动加载 ViewBinding
     *
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
        val bindingClassNames = layoutNameToBindingClassNames(context, layoutName)

        var reflectionError: Throwable? = null

        // 依次尝试候选 Binding 类路径，仅在类不存在时继续尝试。
        for (bindingClassName in bindingClassNames) {
            try {
                return inflateByReflection(
                    context = context,
                    bindingClassName = bindingClassName,
                    inflater = inflater,
                    parent = parent,
                    attachToParent = attachToParent,
                ) as VB
            } catch (throwable: Throwable) {
                val error = unwrapReflectException(throwable)
                reflectionError = error

                // 类不存在时继续尝试下一个候选路径，其他异常直接终止。
                if (error !is ClassNotFoundException) {
                    break
                }
            }
        }

        val error = requireNotNull(reflectionError) {
            "No ViewBinding class candidates found for layout: $layoutName"
        }

        if (isDataBindingAvailable(context)) {
            try {
                return DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent) as VB
            } catch (throwable: Throwable) {
                throw IllegalStateException(
                    "无法加载布局绑定类：$bindingClassNames（layout=$layoutName）",
                    throwable.initCauseIfNeeded(error),
                )
            }
        }

        throw IllegalStateException(
            "无法通过 ViewBinding 反射加载：$bindingClassNames（layout=$layoutName）。" +
                    "当前工程未启用 DataBinding，已跳过 DataBindingUtil 回退。",
            error,
        )
    }

    /**
     * 通过反射加载 ViewBinding
     *
     * @param context 用于获取 ClassLoader
     * @param bindingClassName 绑定类全名
     * @param inflater LayoutInflater
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     * @return 绑定的 ViewBinding 对象
     */
    private fun inflateByReflection(
        context: Context,
        bindingClassName: String,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): ViewBinding {
        val classLoader = context.applicationContext.classLoader ?: context.javaClass.classLoader
        val bindingClass = Class.forName(bindingClassName, false, classLoader)
        val inflateMethod = bindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            java.lang.Boolean.TYPE,
        )
        @Suppress("UNCHECKED_CAST")
        return inflateMethod.invoke(null, inflater, parent, attachToParent) as ViewBinding
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
     * @param context 宿主上下文
     * @param layoutName 布局名称
     * @return 绑定类名称
     */
    private fun layoutNameToBindingClassNames(context: Context, layoutName: String): List<String> {
        val className = layoutName.split('_')
            .joinToString("") { segment ->
                segment.replaceFirstChar { it.uppercaseChar() }
            }

        return buildList {
            // 优先使用 namespace
            runCatching {
                getHostAppNamespace(context)
            }.getOrNull()?.let {
                add("$it.databinding.${className}Binding")
            }

            // 兼容 applicationId 与 namespace 一致的项目及布局预览
            add("${context.packageName}.databinding.${className}Binding")
        }.distinct()
    }

    /**
     * 获取宿主 App 的 namespace
     *
     * @param context 宿主上下文
     * @return 宿主 App 的 namespace
     */
    private fun getHostAppNamespace(context: Context): String {
        if (!::sAppNamespace.isInitialized) {
            val applicationInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                android.content.pm.PackageManager.GET_META_DATA
            )

            val namespace = applicationInfo.metaData?.getString(META_HOST_APP_NAMESPACE)

            // 由于多变体可能使用不同的 applicationId，所以需要通过 namespace 定位 ViewBinding 生成类。
            sAppNamespace = requireNotNull(namespace) {
                "Missing host app namespace. Please configure " +
                        """manifestPlaceholders["appNamespace"] = namespace in app/build.gradle.kts."""
            }
        }
        return sAppNamespace
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
     * 若尚未设置 cause，则补上反射阶段异常，便于排查
     */
    private fun Throwable.initCauseIfNeeded(cause: Throwable?): Throwable {
        if (cause != null && this.cause == null) {
            initCause(cause)
        }
        return this
    }
}