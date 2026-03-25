package com.xinyi.androidbasic.base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.util.forEach
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xinyi.androidbasic.R

/**
 * RecyclerView 适配器的基础抽象类。
 *
 * 本类将 ViewHolder 的创建与绑定逻辑进一步封装，降低模板代码冗余，提高使用便捷性，使适配器实现更加轻量高效。
 *
 * @param M 数据类型
 * @param VH RecyclerView.ViewHolder 类型
 *
 * @author 新一
 * @date 2024/9/30 16:06
 */
abstract class BaseAdapter<M, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH> {

    /** 数据集 */
    private var mListBeans: MutableList<M> = mutableListOf()

    /** 上下文对象  */
    private var mContext: Context? = null

    /** RecyclerView 对象  */
    private var mRecyclerView: RecyclerView? = null

    /** 条目点击监听器  */
    private var mItemClickListener: OnItemClickListener? = null

    /** 条目长按监听器  */
    private var mItemLongClickListener: OnItemLongClickListener? = null

    /** 条目子 View 点击监听器  */
    private var mChildClickListeners: SparseArray<OnChildClickListener?>? = null

    /** 条目子 View 长按监听器  */
    private var mChildLongClickListeners: SparseArray<OnChildLongClickListener?>? = null

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    constructor(context: Context?) {
        this.mContext = context
    }

    /**
     * 构造函数，带初始数据集合
     *
     * @param listBeans 初始数据列表
     * @param context 上下文对象
     */
    constructor(listBeans: MutableList<M>, context: Context?) {
        this.mContext = context
        this.mListBeans = listBeans
    }

    /**
     * 创建ViewHolder对象
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 当前 item 类型
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // 未来在这里增加上拉加载和下拉刷新的逻辑
        return onCreateView(parent, viewType)
    }

    /**
     * 创建ViewHolder对象，用户自定义创建
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 当前 item 类型
     */
    protected abstract fun onCreateView(parent: ViewGroup, viewType: Int): VH

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any?>) {
        onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewClickListener(holder)
        onBindViewData(holder, getItem(position), position)
    }

    /**
     * 为当前 ViewHolder 绑定点击、长按、子视图点击等事件监听。
     *
     * @param holder 当前条目的 ViewHolder
     */
    private fun onBindViewClickListener(holder: VH) {
        mItemClickListener?.let { listener ->
            bindItemClick(holder, listener)
        }
        mItemLongClickListener?.let { listener ->
            bindItemLongClick(holder, listener)
        }
        mChildClickListeners?.let { listeners ->
            bindChildClick(holder, listeners)
        }
        mChildLongClickListeners?.let { listeners ->
            bindChildLongClick(holder, listeners)
        }
        setRippleBackground(holder)
    }

    /**
     * 绑定 item 的点击事件
     *
     * @param holder 当前 ViewHolder
     */
    open fun bindItemClick(holder: VH, listener: OnItemClickListener) {
        holder.itemView.setOnClickListener {
            val safePosition = getSafeAdapterPosition(holder)
            if (safePosition != null) {
                listener.onItemClick(mRecyclerView, holder.itemView, safePosition)
            }
        }
    }

    /**
     * 绑定 item 的长按事件
     *
     * @param holder 当前 ViewHolder
     */
    open fun bindItemLongClick(holder: VH, listener: OnItemLongClickListener) {
        holder.itemView.setOnLongClickListener {
            val safePosition = getSafeAdapterPosition(holder)
            if (safePosition != null) {
                listener.onItemLongClick(mRecyclerView, holder.itemView, safePosition)
            }
            false
        }
    }

    /**
     * 绑定子 View 的点击事件
     *
     * 遍历所有设置了监听的子 View（通过 View ID 注册），为其绑定点击回调。
     *
     * @param holder 当前 ViewHolder
     */
    open fun bindChildClick(holder: VH, listeners: SparseArray<OnChildClickListener?>) {
        listeners.forEach { key, value ->
            value?.let {
                val childView = holder.itemView.findViewById<View>(key)
                childView?.setOnClickListener { view ->
                    val safePosition = getSafeAdapterPosition(holder)
                    if (safePosition != null) {
                        value.onChildClick(mRecyclerView, view, safePosition)
                    }
                }
            }
        }
    }

    /**
     * 绑定子 View 的长按事件
     *
     * 遍历所有设置了监听的子 View（通过 View ID 注册），为其绑定长按回调。
     *
     * @param holder 当前 ViewHolder
     */
    open fun bindChildLongClick(holder: VH, listeners: SparseArray<OnChildLongClickListener?>) {
        listeners.forEach { key, value ->
            value?.let {
                val childView = holder.itemView.findViewById<View>(key)
                childView?.setOnLongClickListener { view ->
                    val safePosition = getSafeAdapterPosition(holder)
                    if (safePosition != null) {
                        value.onChildLongClick(mRecyclerView, view, safePosition)
                    }
                    false
                }
            }
        }
    }

    /**
     * 设置一个默认的水波纹点击动画, ⚠️ xml的ripple最低适配只在SDK 21
     */
    open fun setRippleBackground(holder: VH) {
        if (holder.itemView.background == null) {
            holder.itemView.setBackgroundResource(R.drawable.ripple_white_gray)
        }
    }

    /**
     * 获取当前 ViewHolder 的有效位置，如果无效则返回 null。
     *
     * ⚠️ 使用 adapterPosition 获取的是 **当前 ViewHolder 对应的数据在 Adapter 中的位置**。
     *
     * ✅ 它在大多数场景中都够用，但需要注意：
     *    - 如果在 RecyclerView 触发 notify 系列方法后立刻访问，它可能暂时返回 RecyclerView.NO_POSITION（-1）。
     *    - 对于使用 ConcatAdapter 或 PagingAdapter 的情况，它可能不反映在总 Adapter 中的位置。
     *
     * 👉 若使用的是 RecyclerView 1.2.0 及以上，可以使用 bindingAdapterPosition：
     *    - bindingAdapterPosition 能正确反映 **当前 ViewHolder 在绑定时在 Adapter 中的位置**。
     *    - 它更安全，避免某些异步场景中 adapterPosition 不准确的情况。
     *
     * 可根据项目依赖，重写getSafeAdapterPosition方法，升级到 bindingAdapterPosition。
     */
    open fun getSafeAdapterPosition(holder: VH): Int? {
        val pos = holder.adapterPosition
        return if (pos != RecyclerView.NO_POSITION) {
            pos
        } else {
            null
        }
    }

    /**
     * 绑定数据到 ViewHolder 上，用于将指定位置的数据项与其视图进行绑定。
     * 此方法在每次绑定条目视图时回调，子类应实现具体的数据展示逻辑。
     *
     * 注意：该方法在绑定点击事件之后调用，确保点击行为和数据同步。
     *
     * 番外话：想在Adapter中绑定数据就直接这里写，不强制在ViewHolder做，如果ViewHolder封装了绑定方法，也可以调用它自己的onBindViewData。
     *
     * @param holder 当前条目的 ViewHolder
     * @param item 当前条目的数据
     * @param position 当前条目的初始 position（用于首次绑定数据）
     */
    protected abstract fun onBindViewData(holder: VH, item: M, position: Int)

    /**
     * @return 获取列表长度
     */
    override fun getItemCount(): Int = mListBeans.size

    /**
     * @return 获取上下文对象
     */
    fun getContext(): Context? {
        return mContext
    }

    /**
     * @return 获取列表所有数据
     */
    fun getListBeans(): MutableList<M> {
        return mListBeans
    }

    /**
     * @param mList 添加一个集合
     */
    fun setListBeans(mList: MutableList<M>) {
        mListBeans = mList
    }

    /**
     * 获取指定位置的数据
     */
    fun getItem(position: Int): M {
        require(position >= 0 && position < mListBeans.size) {
            "Position $position 必须介于 0 和 ${mListBeans.size - 1} 之间的范围"
        }
        return mListBeans[position]
    }

    /**
     * 添加一条数据
     */
    fun appendBean(bean: M) {
        mListBeans.add(bean)
    }

    /**
     * @param bean 插入一条数据到第一个
     */
    fun appendFirstBean(bean: M) {
        mListBeans.add(0, bean)
    }

    /**
     * @param mList 添加一个集合 默认添加到后面
     */
    fun appendList(mList: MutableList<M>) {
        appendCollection(mList, false)
    }

    /**
     * @param mList 添加一个集合 默认插入到前面
     */
    fun appendListByFirstElement(mList: MutableList<M>) {
        appendCollection(mList, true)
    }

    /**
     * @param mList 添加一个集合
     * @param first true：插入最前面，false：添加到后面
     */
    private fun appendCollection(mList: MutableList<M>, first: Boolean) {
        if (first) {
            mListBeans.addAll(0, mList)
        } else {
            mListBeans.addAll(mList)
        }
    }

    /**
     * 删除指定位置的数据
     */
    fun removeItem(position: Int) {
        require(position >= 0 && position < mListBeans.size) {
            "Position $position 必须介于 0 和 ${mListBeans.size - 1} 之间的范围"
        }
        mListBeans.removeAt(position)
    }

    /**
     * 删除指定数据
     */
    fun removeBean(bean: M) {
        mListBeans.remove(bean)
    }

    /**
     * 更新指定位置的数据
     */
    open fun updateItem(position: Int, bean: M) {
        require(position >= 0 && position < mListBeans.size) {
            "Position $position 必须介于 0 和 ${mListBeans.size - 1} 之间的范围"
        }
        mListBeans[position] = bean
    }

    /**
     * 插入到列表第一个，并刷新适配器位置
     */
    open fun notifyItemInsertedFirst(bean: M) {
        appendFirstBean(bean)
        notifyItemInserted(0)
    }

    /**
     * 插入列表最后一个，并刷新适配器数据
     */
    open fun notifyItemInserted(bean: M) {
        appendBean(bean)
        notifyItemInserted(itemCount - 1)
    }

    /**
     * 指定`position` 位置插入某条数据，并刷新适配器数据
     */
    open fun notifyItemInserted(position: Int, bean: M) {
        mListBeans.add(position, bean)
        notifyItemInserted(position)
    }

    /**
     * 插入一个List集合至列表的最前面，并刷新适配器数据
     */
    open fun notifyItemInsertedFirst(list: MutableList<M>) {
        if (list.isEmpty()) {
            return
        }
        appendListByFirstElement(list)
        notifyItemRangeInserted(0, list.size)
    }

    /**
     * 插入一个List集合到最后面，并刷新适配器数据
     */
    open fun notifyItemInserted(list: MutableList<M>) {
        if (list.isEmpty()) {
            return
        }
        appendList(list)
        notifyItemRangeInserted(itemCount - list.size, list.size)
    }

    /**
     * 更新并刷新适配器数据
     */
    open fun notifyItemUpdated(position: Int, bean: M) {
        updateItem(position, bean)
        notifyItemChanged(position)
    }

    /**
     * 删除并刷新适配器数据
     */
    open fun notifyItemRemovedAt(position: Int) {
        removeItem(position)
        notifyItemRemoved(position)
    }

    /**
     * 清空并刷新适配器数据
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun notifyItemCleared() {
        clear()
        // 更直接、避免潜在索引越界
        notifyDataSetChanged()
    }

    /**
     * 统一刷新整个列表
     *
     * ⚠️ 注意：请勿在首次加载时调用
     * - 因为这个方法的行为是：刷新已有条目的 UI，但不触发数据集合的变更逻辑，也不会重建 ViewHolder 数量
     *
     * @param payload 局部变化标识，为 null 则会 “完整” 更新
     */
    open fun notifyItemAllRangeChanged(payload: Any? = null) {
        if (itemCount > 0) {
            notifyItemRangeChanged(0, itemCount, payload)
        }
    }

    /**
     * 使用 DiffUtil 更新列表数据，自动计算差异并刷新 UI，
     * 它高效地对比旧数据和新数据，仅在数据内容有差异时才更新对应的条目，
     * 从而避免全量刷新带来的性能浪费和视觉抖动，尤其适合大列表或频繁更新场景。
     *
     * ⚠️ 模型类需正确实现 equals 方法，以确保差异计算准确。
     *
     * 注意：DiffUtil 会自动识别增删改的差异，因此无需手动调用 notify 系列方法。
     * @param newList 新的数据列表，将替换当前列表内容
     */
    open fun updateListWithDiff(newList: MutableList<M>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int {
                // 旧数据大小
                return itemCount
            }

            override fun getNewListSize(): Int {
                // 新数据大小
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // 判断两项是否为同一个对象（如有唯一ID，建议使用ID比较）
                return mListBeans[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // 判断两个对象的内容是否一致
                return mListBeans[oldItemPosition] == (newList[newItemPosition])
            }
        })

        // 更新数据源
        setListBeans(ArrayList(newList))
        // 通知 RecyclerView 执行差异刷新
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * 清空所有数据
     */
    fun clear() {
        mListBeans.clear()
    }

    /**
     * 由 RecyclerView 在开始观察此 Adapter 时调用。
     * 请注意，多个 RecyclerView 可能会观察到同一个适配器。
     *
     * @param recyclerView 开始观察此适配器的 RecyclerView 实例。
     * @see onDetachedFromRecyclerView(RecyclerView)
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        // 判断当前的布局管理器是否为空，如果为空则设置默认的布局管理器
        if (mRecyclerView?.layoutManager == null) {
            val layoutManager = generateDefaultLayoutManager(mContext)
            mRecyclerView?.layoutManager = layoutManager
        }
    }

    /**
     * 由 RecyclerView 在停止观察此 Adapter 时调用。
     *
     * @param recyclerView 停止观察此适配器的 RecyclerView 实例。
     * @see onAttachedToRecyclerView(RecyclerView)
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = null
    }

    /**
     * 生成默认的布局摆放器，如果需要自定义，重写此方法。
     *
     * @param context 上下文对象
     */
    open fun generateDefaultLayoutManager(context: Context?): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * 设置 RecyclerView 条目点击监听
     *
     * @param listener 点击事件回调
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mItemClickListener = listener
    }

    /**
     * 设置 RecyclerView 条目长按监听
     *
     * @param listener 长按事件回调
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        mItemLongClickListener = listener
    }

    /**
     * 设置 RecyclerView 条目子 View 点击监听
     *
     * @param viewId 子 View 的资源 ID
     * @param listener 点击事件回调
     */
    fun setOnChildClickListener(@IdRes viewId: Int, listener: OnChildClickListener?) {
        if (mChildClickListeners == null) {
            mChildClickListeners = SparseArray()
        }
        mChildClickListeners?.put(viewId, listener)
    }

    /**
     * 设置 RecyclerView 条目子 View 长按监听
     *
     * @param viewId 子 View 的资源 ID
     * @param listener 长按事件回调
     */
    fun setOnChildLongClickListener(@IdRes viewId: Int, listener: OnChildLongClickListener?) {
        if (mChildLongClickListeners == null) {
            mChildLongClickListeners = SparseArray()
        }
        mChildLongClickListeners?.put(viewId, listener)
    }

    /**
     * RecyclerView 条目点击监听类
     */
    interface OnItemClickListener {

        /**
         * 当 RecyclerView 某个条目被点击时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param itemView          被点击的条目对象
         * @param safePosition      被点击的条目位置
         */
        fun onItemClick(recyclerView: RecyclerView?, itemView: View, safePosition: Int)
    }

    /**
     * RecyclerView 条目长按监听类
     */
    interface OnItemLongClickListener {

        /**
         * 当 RecyclerView 某个条目被长按时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param itemView          被点击的条目对象
         * @param safePosition      被点击的条目位置
         * @return                  是否拦截事件
         */
        fun onItemLongClick(recyclerView: RecyclerView?, itemView: View, safePosition: Int): Boolean
    }

    /**
     * RecyclerView 条目子 View 点击监听类
     */
    interface OnChildClickListener {

        /**
         * 当 RecyclerView 某个条目 子 View 被点击时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param childView         被点击的条目子 View
         * @param safePosition      被点击的条目位置
         */
        fun onChildClick(recyclerView: RecyclerView?, childView: View, safePosition: Int)
    }

    /**
     * RecyclerView 条目子 View 长按监听类
     */
    interface OnChildLongClickListener {

        /**
         * 当 RecyclerView 某个条目子 View 被长按时回调
         *
         * @param recyclerView      RecyclerView 对象
         * @param childView         被点击的条目子 View
         * @param safePosition      被点击的条目位置
         */
        fun onChildLongClick(recyclerView: RecyclerView?, childView: View, safePosition: Int): Boolean
    }
}