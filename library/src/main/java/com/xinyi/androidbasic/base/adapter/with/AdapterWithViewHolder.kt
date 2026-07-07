package com.xinyi.androidbasic.base.adapter.with

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.adapter.holder.BaseViewHolder
import com.xinyi.androidbasic.base.adapter.itembinding.ViewBindingItem
import com.xinyi.androidbasic.base.binding.BindingInflaters

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
open class ViewBindingViewHolder<VB : ViewBinding>(
    layoutId: Int,
    parent: ViewGroup,
) : BaseViewBindingViewHolder<VB>(layoutId, parent)

/**
 * ViewHolder 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2025/6/4 10:41
 */
abstract class BaseViewBindingViewHolder<VB : ViewBinding>(val binding: VB) :
    BaseViewHolder(binding.root) {

    /**
     * 布局资源 ID 创建 ViewHolder 的构造函数
     *
     * @param layoutId 布局资源 ID
     * @param parent 父布局，用于获取 Context 和作为布局的根容器
     */
    constructor(layoutId: Int, parent: ViewGroup) : this(
        BindingInflaters.inflate(
            parent.context,
            layoutId,
            LayoutInflater.from(parent.context),
            parent,
            false,
        ),
    )
}

/**
 * 一个基于 lambda 封装的 ViewBinding 条目实现类。
 *
 * 用于通过函数方式注册 item，减少模板代码，增强适配器的灵活性。
 *
 * @param M 数据模型类型
 * @param VB 具体的 ViewBinding 类型
 * @param layoutId 当前条目的布局资源 ID
 * @param onBindViewData 绑定逻辑，接收 binding、数据项和位置
 *
 * @author 新一
 * @date 2025/6/4 14:32
 */
class LambdaViewBindingItem<M, VB : ViewBinding>(
    private val layoutId: Int,
    private val onBindViewData: (binding: VB, item: M, position: Int) -> Unit,
) : ViewBindingItem<M, VB> {

    override fun initLayoutId(): Int = layoutId

    override fun onBindViewData(binding: VB, item: M, position: Int) {
        onBindViewData.invoke(binding, item, position)
    }
}