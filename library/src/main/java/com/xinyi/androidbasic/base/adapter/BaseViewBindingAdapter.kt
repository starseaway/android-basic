package com.xinyi.androidbasic.base.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.adapter.with.ViewBindingViewHolder

/**
 * 单布局的 ViewBindingViewHolder 的适配器基类
 *
 * @author 新一
 * @date 2025/4/21 9:22
 */
abstract class BaseViewBindingAdapter<M, VB : ViewBinding> : BaseAdapter<M, ViewBindingViewHolder<VB>> {

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    constructor(context: Context?) : super(context)

    /**
     * 构造函数，带初始数据集合
     *
     * @param listBeans 初始数据列表
     * @param context 上下文对象
     */
    constructor(listBeans: MutableList<M>, context: Context?) : super(listBeans, context)

    /**
     * 初始化布局ID
     *
     * @return 返回ID
     */
    abstract fun initLayoutId(): Int

    /**
     * 创建默认的 ViewBindingViewHolder 对象
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 当前 item 类型
     */
    override fun onCreateView(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<VB> {
        // 创建ViewHolder
        return ViewBindingViewHolder(initLayoutId(), parent)
    }

    /**
     * 绑定数据到 ViewHolder 上，用于将指定位置的数据项与其视图进行绑定。
     */
    override fun onBindViewData(holder: ViewBindingViewHolder<VB>, item: M, position: Int) {
        onBindViewDataBinding(holder.binding, item, position)
    }

    /**
     * 局部刷新绑定到 ViewBinding
     *
     * 默认回退到完整绑定；子类可按 [payloads] 只更新必要控件
     */
    override fun onBindViewPayload(holder: ViewBindingViewHolder<VB>, item: M, position: Int, payloads: List<Any?>) {
        onBindViewPayloadBinding(holder.binding, item, position, payloads)
    }

    /**
     * 将指定位置的数据项绑定到 ViewHolder 上
     *
     * @param binding 当前条目的 ViewHolder
     * @param item 当前条目的数据
     * @param position 当前条目的初始 position
     */
    open fun onBindViewDataBinding(binding: VB, item: M, position: Int) {}

    /**
     * 局部刷新绑定
     *
     * 默认回退到 [onBindViewDataBinding]
     */
    open fun onBindViewPayloadBinding(binding: VB, item: M, position: Int, payloads: List<Any?>) {
        onBindViewDataBinding(binding, item, position)
    }
}