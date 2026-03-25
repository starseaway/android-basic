package com.xinyi.androidbasic.base.adapter

import android.content.Context
import android.view.ViewGroup
import com.xinyi.androidbasic.base.adapter.with.RecyclerViewHolder

/**
 * 支持给 RecyclerViewHolder 提供多种布局的 RecyclerView 适配器基类。
 *
 * 该类基于 viewType 机制，为不同类型的数据项提供灵活的布局支持。
 * 子类只需实现对应的 viewType 和布局 ID 映射，即可轻松实现多条目展示。
 *
 * 子类不无需要关注 [onCreateView] [onBindViewData] 方法的实现。
 * 通常情况下，子类只需实现 [onBindViewTypeData] 方法即可
 *
 * @author 新一
 * @date 2025/6/4 13:53
 */
abstract class BaseMultiLayoutAdapter<M> : BaseAdapter<M, RecyclerViewHolder> {

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
     * 返回当前 position 的 item 的 viewType
     */
    override fun getItemViewType(position: Int): Int {
        return getViewTypeForPosition(position, getItem(position))
    }

    /**
     * 子类实现，根据 item 或 position 返回对应的类型标识
     */
    abstract fun getViewTypeForPosition(position: Int, item: M): Int

    /**
     * 根据 viewType 返回对应的 layoutId
     */
    abstract fun getLayoutIdForViewType(viewType: Int): Int

    /**
     * 根据不同的 viewType 创建不同布局的 RecyclerViewHolder 对象
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 当前 item 类型
     */
    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // 创建ViewHolder
        val layoutId = getLayoutIdForViewType(viewType)
        return RecyclerViewHolder(layoutId, parent)
    }

    override fun onBindViewData(holder: RecyclerViewHolder, item: M, position: Int) {
        // 绑定数据
        onBindViewTypeData(getItemViewType(position), holder, item, position)
    }

    /**
     * 根据不同的 viewType 绑定数据
     *
     * ViewDataBinding 可以自行实现，最好不要在这里进行绑定，因为onBindViewTypeData 会频繁被调用，会导致性能问题。
     *
     * ```
     *  // Kotlin 示例
     *  val binding = DataBindingUtil.bind<VDB>(holder.itemView)
     *
     *  // Java 示例
     *  VDB binding = DataBindingUtil.bind(holder.itemView);
     * ```
     */
    abstract fun onBindViewTypeData(viewType: Int, holder: RecyclerViewHolder, item: M, position: Int)
}