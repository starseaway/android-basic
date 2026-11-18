package com.xinyi.androidbasic.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

/**
 * DataBinding 布局加载器
 *
 * 基于 DataBinding 官方 Mapper，按布局资源 ID 创建绑定对象。
 *
 * @author 杨耿雷
 * @date 2026/11/18 10:10
 */
object DataBindingInflater {

    /**
     * DataBinding 聚合 Mapper 的类名；仅在消费方开启 dataBinding 时由 AGP 生成
     */
    private const val DATA_BINDER_MAPPER_CLASS = "androidx.databinding.DataBinderMapperImpl"

    /**
     * 判断当前 ClassLoader 下 DataBinding 是否可用
     *
     * 通过探测 [DATA_BINDER_MAPPER_CLASS] 是否存在来确认工程已启用 DataBinding。
     *
     * @param context 用于获取 ClassLoader 的上下文
     * @return 可走 DataBinding 加载路径时返回 true
     */
    @JvmStatic
    fun isAvailable(context: Context): Boolean {
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
     * 按布局资源 ID 通过 DataBinding 加载绑定对象
     *
     * 依赖官方 [DataBindingUtil.inflate]，要求工程已启用 DataBinding。
     *
     * @param context 上下文，用于检测 DataBinding 是否可用
     * @param layoutId 布局资源 ID
     * @param inflater 布局加载器
     * @param parent 父容器，可为 null
     * @param attachToParent 是否附加到父布局
     * @return Binding 实例
     * @throws IllegalStateException 未启用 DataBinding，或 Mapper 无法解析该布局时抛出
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> inflate(
        context: Context,
        layoutId: Int,
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean,
    ): VB {
        if (!isAvailable(context)) {
            throw IllegalStateException(
                "当前工程未启用 DataBinding，无法仅凭 layoutId 加载绑定类。" +
                        "请传入 Binding Class，或使用带 ViewBinding 泛型的宿主进行加载。")
        }

        try {
            return DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent) as VB
        } catch (throwable: Throwable) {
            throw IllegalStateException("无法通过 DataBindingUtil 加载布局（layoutId=$layoutId）", throwable)
        }
    }
}