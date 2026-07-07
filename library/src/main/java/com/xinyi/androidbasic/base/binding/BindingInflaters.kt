package com.xinyi.androidbasic.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding 统一加载工具
 *
 * 优先通过反射调用生成类的 `inflate(LayoutInflater, ViewGroup, Boolean)`，
 * 失败时回退到 [DataBindingUtil.inflate] 以兼容 `<layout>` 布局。
 *
 * @author 杨耿雷
 * @date 2026/7/7 13:52
 */
object BindingInflaters {

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
        inflateByReflection(context, layoutId, inflater, parent, attachToParent)?.let {
            return it as VB
        }
        return DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent) as VB
    }

    /**
     * 通过反射加载 ViewBinding
     *
     * @param context 用于解析包名与资源名
     * @param layoutId 布局资源 ID
     * @param inflater LayoutInflater
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     * @return 绑定的 ViewBinding 对象
     */
    @Suppress("UNCHECKED_CAST")
    private fun inflateByReflection(
        context: Context,
        layoutId: Int,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): ViewBinding? {
        return try {
            val layoutName = context.resources.getResourceEntryName(layoutId)
            val bindingClassName = layoutNameToBindingClassName(context.packageName, layoutName)
            val bindingClass = Class.forName(bindingClassName)
            val inflateMethod = bindingClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType,
            )
            inflateMethod.invoke(null, inflater, parent, attachToParent) as ViewBinding
        } catch (_: Exception) {
            null
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
}