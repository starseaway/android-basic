package com.xinyi.androidbasic.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding / DataBinding 统一加载入口
 *
 * 按入参提供三种加载方式：宿主泛型、Binding Class、layoutId。
 *
 * @author 杨耿雷
 * @date 2026/7/7 13:52
 */
object BindingInflaters {

    /**
     * 根据宿主类上的 ViewBinding 泛型加载绑定对象
     *
     * @param owner 声明了具体 Binding 泛型的宿主实例
     * @param inflater 布局加载器
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     */
    @JvmStatic
    fun <VB : ViewBinding> inflate(
        owner: Any,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): VB {
        return inflate(ViewBindingTypeResolver.resolve(owner), inflater, parent, attachToParent)
    }

    /**
     * 根据 Binding Class 加载绑定对象
     *
     * @param bindingClass 具体 Binding 类型
     * @param inflater 布局加载器
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     */
    @JvmStatic
    fun <VB : ViewBinding> inflate(
        bindingClass: Class<VB>,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): VB {
        return ViewBindingReflectInflater.inflate(bindingClass, inflater, parent, attachToParent)
    }

    /**
     * 根据布局资源 ID 加载绑定对象
     *
     * 可同时传入 [bindingClass]；未传入时走 DataBinding 加载路径。
     *
     * @param context 上下文
     * @param layoutId 布局资源 ID
     * @param inflater 布局加载器
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     * @param bindingClass 可选的 Binding 类型
     */
    @JvmStatic
    fun <VB : ViewBinding> inflate(
        context: Context,
        layoutId: Int,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
        bindingClass: Class<VB>? = null,
    ): VB {
        if (bindingClass != null) {
            return inflate(bindingClass, inflater, parent, attachToParent)
        }
        return DataBindingInflater.inflate(context, layoutId, inflater, parent, attachToParent)
    }

    /**
     * 解析宿主类上的 ViewBinding 泛型对应的 Class
     *
     * @param owner 声明了具体 Binding 泛型的宿主实例
     * @return Binding Class
     */
    @JvmStatic
    fun <VB : ViewBinding> resolveBindingClass(owner: Any): Class<VB> {
        return ViewBindingTypeResolver.resolve(owner)
    }
}