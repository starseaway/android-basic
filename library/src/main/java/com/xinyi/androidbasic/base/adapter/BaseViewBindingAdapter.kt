package com.xinyi.androidbasic.base.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.xinyi.androidbasic.base.adapter.with.ViewBindingViewHolder

/**
 * 单布局的 ViewBindingViewHolder 的适配器基类，用于提升适配器的复用性和开发效率。
 *
 * 子类仅需重写 [initLayoutId] 方法，和 [onBindViewData] 或 [onBindViewDataBinding] 方法，实现数据绑定逻辑，无需重复编写样板代码。
 *
 * @author 新一
 * @date 2025/4/21 9:22
 */
abstract class BaseViewBindingAdapter<M, VDB : ViewDataBinding> : BaseAdapter<M, ViewBindingViewHolder<VDB>> {

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
    override fun onCreateView(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<VDB> {
        // 创建ViewHolder
        return ViewBindingViewHolder(initLayoutId(), parent)
    }

    /**
     * 绑定数据到 ViewHolder 上，用于将指定位置的数据项与其视图进行绑定。
     */
    override fun onBindViewData(holder: ViewBindingViewHolder<VDB>, item: M, position: Int) {
        onBindViewDataBinding(holder.binding, item, position)
    }

    /**
     * 绑定数据到 ViewHolder 上，用于将指定位置的数据项与其视图进行绑定。
     *
     * 番外话：想在Adapter中绑定数据就直接这里写，不强制在ViewHolder做，如果ViewHolder封装了绑定方法，也可以调用它自己的onBindViewData。
     *
     * @param binding 当前条目的 ViewHolder
     * @param item 当前条目的数据
     * @param position 当前条目的初始 position
     */
    open fun onBindViewDataBinding(binding: VDB, item: M, position: Int) { }
}