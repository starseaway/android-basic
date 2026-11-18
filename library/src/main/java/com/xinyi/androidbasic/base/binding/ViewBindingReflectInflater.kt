package com.xinyi.androidbasic.base.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException

/**
 * ViewBinding 反射加载器
 *
 * 调用 Binding 生成类上的 `inflate` 静态方法创建绑定对象。
 *
 * @author 杨耿雷
 * @date 2026/11/18 10:29
 */
object ViewBindingReflectInflater {

    /**
     * 通过反射加载 Binding
     *
     * 兼容三参数与两参数 `inflate` 签名（后者常见于 `<merge>` 根布局）。
     *
     * @param bindingClass Binding Class
     * @param inflater 布局加载器
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     * @return Binding 实例
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> inflate(
        bindingClass: Class<VB>,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): VB {
        try {
            return inflateInternal(bindingClass, inflater, parent, attachToParent) as VB
        } catch (throwable: Throwable) {
            throw IllegalStateException(
                "无法通过 ViewBinding 反射加载：${bindingClass.name}",
                unwrapReflectException(throwable),
            )
        }
    }

    /**
     * 实际反射调用 Binding 生成类的 `inflate`
     *
     * 优先三参数签名 `(LayoutInflater, ViewGroup, boolean)`；
     * 若不存在（常见于根节点为 `<merge>`），回退两参数签名，此时 [parent] 不可为空。
     *
     * @param bindingClass Binding Class
     * @param inflater 布局加载器
     * @param parent 父容器；两参数回退路径下不可为 null
     * @param attachToParent 是否附加到父布局（仅三参数路径生效）
     * @return Binding 实例
     */
    private fun inflateInternal(
        bindingClass: Class<*>,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): ViewBinding {
        try {
            val threeArgMethod = bindingClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                java.lang.Boolean.TYPE,
            )
            return threeArgMethod.invoke(null, inflater, parent, attachToParent) as ViewBinding
        } catch (_: NoSuchMethodException) {
            requireNotNull(parent) {
                "${bindingClass.name} 缺少三参数 inflate，且 parent 不能为空"
            }
            val twoArgMethod = bindingClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
            )
            return twoArgMethod.invoke(null, inflater, parent) as ViewBinding
        }
    }

    /**
     * 展开反射调用异常，返回真实 cause
     *
     * @param throwable 反射过程中捕获的异常
     * @return [InvocationTargetException] 时取其 targetException，否则原样返回
     */
    private fun unwrapReflectException(throwable: Throwable): Throwable {
        return if (throwable is InvocationTargetException) {
            throwable.targetException ?: throwable
        } else {
            throwable
        }
    }
}