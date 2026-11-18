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
     * @param layoutId 布局资源 ID
     * @param parent 父布局
     */
    constructor(layoutId: Int, parent: ViewGroup) : super(layoutId, parent)
}

/**
 * 通用的 `ViewBindingViewHolder`
 *
 * @author 新一
 * @date 2025/4/21 9:14
 */
open class ViewBindingViewHolder<VB : ViewBinding> : BaseViewBindingViewHolder<VB> {

    companion object {

        /**
         * 以 reified 泛型创建
         *
         * 用法：`ViewBindingViewHolder.create<TestItemBinding>(parent)`
         *
         * @param parent 父布局
         */
        inline fun <reified VB : ViewBinding> create(parent: ViewGroup): ViewBindingViewHolder<VB> {
            return ViewBindingViewHolder(VB::class.java, parent)
        }
    }

    /**
     * ViewBinding 实例创建 ViewHolder
     *
     * @param binding ViewBinding 实例
     */
    constructor(binding: VB) : super(binding)

    /**
     * Binding Class 创建 ViewHolder
     *
     * @param bindingClass 具体 ViewBinding 类型
     * @param parent 父布局
     */
    constructor(bindingClass: Class<VB>, parent: ViewGroup) : super(bindingClass, parent)

    /**
     * 布局资源 ID 创建 ViewHolder
     *
     * @param layoutId 布局资源 ID
     * @param parent 父布局
     */
    constructor(layoutId: Int, parent: ViewGroup) : super(layoutId, parent)
}

/**
 * ViewHolder 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2025/6/4 10:41
 */
abstract class BaseViewBindingViewHolder<VB : ViewBinding>(val binding: VB) :
    BaseViewHolder(binding.root) {

    /**
     * Binding Class 创建 ViewHolder
     *
     * @param bindingClass 具体 ViewBinding 类型
     * @param parent 父布局
     */
    constructor(bindingClass: Class<VB>, parent: ViewGroup) : this(
        BindingInflaters.inflate(
            bindingClass,
            LayoutInflater.from(parent.context),
            parent,
            false,
        ),
    )

    /**
     * 布局资源 ID 创建 ViewHolder
     *
     * @param layoutId 布局资源 ID
     * @param parent 父布局
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
 * @param bindingClass 当前条目的 ViewBinding Class
 * @param onBindViewData 绑定逻辑，接收 binding、数据项和位置
 *
 * @author 新一
 * @date 2025/6/4 14:32
 */
class LambdaViewBindingItem<M, VB : ViewBinding>(
    private val layoutId: Int,
    private val bindingClass: Class<VB>,
    private val onBindViewData: (binding: VB, item: M, position: Int) -> Unit,
) : ViewBindingItem<M, VB> {

    override fun initLayoutId(): Int = layoutId

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): VB {
        return BindingInflaters.inflate(bindingClass, inflater, parent, false)
    }

    override fun onBindViewData(binding: VB, item: M, position: Int) {
        onBindViewData.invoke(binding, item, position)
    }
}