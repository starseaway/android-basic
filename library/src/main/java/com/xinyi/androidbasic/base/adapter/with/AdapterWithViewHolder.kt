package com.xinyi.androidbasic.base.adapter.with

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.xinyi.androidbasic.base.adapter.holder.BaseViewHolder
import com.xinyi.androidbasic.base.adapter.itembinding.ViewBindingItem

/**
 * 默认的 `ViewHolder` 实现类。用于简化 `BaseAdapter` 的使用。
 *
 * @author 新一
 * @date 2025/4/19 15:54
 */
open class RecyclerViewHolder : BaseViewHolder {

    /**
     * View 创建 ViewHolder 的构造函数。
     *
     * @param itemView 布局 View
     */
    constructor(itemView: View) : super(itemView)

    /**
     * 布局资源 ID 创建 ViewHolder 的构造函数。
     *
     * @param layoutId 布局资源 ID（R.layout.xxx）
     * @param parent 父布局，用于获取 Context 和作为布局的根容器
     */
    constructor(layoutId: Int, parent: ViewGroup) : super(layoutId, parent)
}

/**
 * 通用的 `ViewBindingViewHolder`
 *
 * @param layoutId 布局资源 ID（R.layout.xxx）
 * @param parent 父布局，用于获取 Context 和作为布局的根容器
 *
 * @author 新一
 * @date 2025/4/21 9:14
 */
open class ViewBindingViewHolder<VDB : ViewDataBinding>(
    layoutId: Int,
    parent: ViewGroup,
) : BaseViewBindingViewHolder<VDB>(layoutId, parent)

/**
 * ViewHolder 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2025/6/4 10:41
 */
abstract class BaseViewBindingViewHolder<VDB : ViewDataBinding>(val binding: VDB) :
    BaseViewHolder(binding.root) {

    /**
     * 布局资源 ID 创建 ViewHolder 的构造函数。
     *
     * @param layoutId 布局资源 ID（R.layout.xxx）
     * @param parent 父布局，用于获取 Context 和作为布局的根容器
     */
    constructor(layoutId: Int, parent: ViewGroup) : this(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
    )
}

/**
 * 一个基于 lambda 封装的 ViewBinding 条目实现类。
 *
 * 用于通过函数方式注册 item，减少模板代码，增强适配器的灵活性。
 *
 * @param M 数据模型类型
 * @param VDB 具体的 ViewDataBinding 类型
 * @param layoutId 当前条目的布局资源 ID
 * @param onBindViewData 绑定逻辑，接收 binding、数据项和位置
 *
 * @author 新一
 * @date 2025/6/4
 */
class LambdaViewBindingItem<M, VDB : ViewDataBinding>(
    private val layoutId: Int,
    private val onBindViewData: (binding: VDB, item: M, position: Int) -> Unit
) : ViewBindingItem<M, VDB> {

    override fun initLayoutId(): Int = layoutId

    override fun onBindViewData(binding: VDB, item: M, position: Int) {
        onBindViewData.invoke(binding, item, position)
    }
}