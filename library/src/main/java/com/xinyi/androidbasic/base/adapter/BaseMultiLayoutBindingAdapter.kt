package com.xinyi.androidbasic.base.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.xinyi.androidbasic.base.adapter.itembinding.ViewBindingItem
import com.xinyi.androidbasic.base.adapter.with.LambdaViewBindingItem
import com.xinyi.androidbasic.base.adapter.with.ViewBindingViewHolder

/**
 * 支持给 RecyclerViewHolder 提供多种布局的 RecyclerView 适配器基类
 * 本类将关注布局的创建和ViewDataBinding的绑定，适配器的复用性和开发效率大大提高
 *
 * 该类基于 viewType 机制，为不同类型的数据项提供灵活的布局支持
 * 子类只需实现对应的 viewType 和布局 ID 映射，即可轻松实现多条目展示
 *
 * 子类不无需要关注 [onCreateView] [onBindViewData] 方法的实现
 * 通常情况下，子类只需实现 [getViewTypeForPosition] 和 [onRegisterViewBindingType] 方法即可
 *
 * @author 新一
 * @date 2025/6/4 14:21
 */
abstract class BaseMultiLayoutBindingAdapter<M> : BaseAdapter<M, ViewBindingViewHolder<ViewDataBinding>> {

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    constructor(context: Context?) : super(context) {
        // 注册 ViewBinding 条目类型
        onRegisterViewBindingType()
    }

    /**
     * 构造函数，带初始数据集合
     *
     * @param listBeans 初始数据列表
     * @param context 上下文对象
     */
    constructor(listBeans: MutableList<M>, context: Context?) : super(listBeans, context) {
        // 注册 ViewBinding 条目类型
        onRegisterViewBindingType()
    }

    /**
     * 不同的 viewType 对应不同的 item
     *
     * key: viewType
     * value: ViewDataBinding
     */
    private val mItemTypeMap = mutableMapOf<Int, ViewBindingItem<M, out ViewDataBinding>>()

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
     * 子类需实现该方法，在此方法中调用 [registerViewType] 注册所有的 ViewType 与其对应的布局绑定逻辑
     *
     * 推荐在该方法中对每个 ViewType 使用唯一常量标识，并与具体的 [ViewBindingItem] 实例进行绑定
     */
    abstract fun onRegisterViewBindingType()

    /**
     * 注册某个 ViewType 对应的布局绑定器
     *
     * 在 [onRegisterViewBindingType] 方法中调用，用于为不同的 ViewType 设置对应的绑定行为
     *
     * @param viewType 唯一的视图类型标识（通常使用 Int 常量）
     * @param layoutId 当前视图类型的布局 ID
     * @param onBindViewData 当前视图类型的数据绑定逻辑
     */
    fun <VDB : ViewDataBinding> registerViewType(
        viewType: Int,
        layoutId: Int,
        onBindViewData: (binding: VDB, item: M, position: Int) -> Unit,
    ) {
        registerViewType(viewType, LambdaViewBindingItem(layoutId, onBindViewData))
    }

    /**
     * 注册某个 ViewType 对应的布局绑定器
     *
     * 在 [onRegisterViewBindingType] 方法中调用，用于为不同的 ViewType 设置对应的绑定行为
     *
     * @param viewType 唯一的视图类型标识（通常使用 Int 常量）
     * @param item 当前视图类型的绑定行为，封装了布局 ID 与数据绑定逻辑
     */
    fun <VDB : ViewDataBinding> registerViewType(viewType: Int, item: LambdaViewBindingItem<M, VDB>) {
        mItemTypeMap[viewType] = item
    }

    /**
     * 注册某个 ViewType 对应的布局绑定器
     *
     * 在 [onRegisterViewBindingType] 方法中调用，用于为不同的 ViewType 设置对应的绑定行为
     *
     * @param viewType 唯一的视图类型标识（通常使用 Int 常量）
     * @param item 当前视图类型的绑定行为，封装了布局 ID 与数据绑定逻辑
     */
    fun <VDB : ViewDataBinding> registerViewType(viewType: Int, item: ViewBindingItem<M, VDB>) {
        mItemTypeMap[viewType] = item
    }

    /**
     * 获取指定 ViewType 对应的布局绑定器
     *
     * @param viewType 已注册的视图类型
     * @return 对应的 [ViewBindingItem] 实例
     * @throws IllegalArgumentException 如果未注册该类型则抛出异常
     */
    fun getViewBindingItem(viewType: Int): ViewBindingItem<M, out ViewDataBinding> {
        return mItemTypeMap[viewType] ?: throw IllegalArgumentException("ViewType $viewType is not registered")
    }


    /**
     * 根据 [getItemViewType] 返回指定的 viewType 创建对应的 ViewHolder
     *
     * @param parent 父容器 ViewGroup
     * @param viewType 当前 item 类型
     * @return 包裹 ViewBinding 的 ViewHolder
     */
    override fun onCreateView(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<ViewDataBinding> {
        val item = getViewBindingItem(viewType)
        return ViewBindingViewHolder(item.initLayoutId(), parent)
    }

    /**
     * 将数据绑定到指定位置的 ViewHolder
     *
     * @param holder 当前绑定的 ViewHolder，内部持有 ViewBinding 实例
     * @param item 当前数据项
     * @param position 当前项在列表中的位置
     */
    override fun onBindViewData(holder: ViewBindingViewHolder<ViewDataBinding>, item: M, position: Int) {
        val viewType = getItemViewType(position)
        val viewItem = getViewBindingItem(viewType)

        // 绑定数据到对应的条目类型上
        bindItem(viewItem, holder.binding, item, position)
    }

    /**
     * 执行数据绑定的具体操作
     *
     * 这段代码的安全性建立在以下几个前提：
     * 1. `viewItem` 在 `onRegisterViewBindingType` 中已经明确指定了对应的 `VDB` 类型
     * 2. `bindItem` 方法在实际调用时，会传入与注册时类型一致的 `ViewBindingItem` 和 `ViewDataBinding`
     *
     * @param viewItem 注册的条目绑定器，封装了视图类型、数据绑定逻辑
     * @param binding 当前项的 ViewBinding 实例（此时为 ViewDataBinding 类型）
     * @param item 当前数据项
     * @param position 当前项在列表中的位置
     */
    @Suppress("UNCHECKED_CAST")
    private fun <VDB : ViewDataBinding> bindItem(viewItem: ViewBindingItem<M, VDB>, binding: ViewDataBinding, item: M, position: Int) {
        // 由于泛型擦除，binding 类型在运行时只能是 ViewDataBinding，
        // 所以此处通过强制类型转换恢复为注册时指定的具体 Binding 类型
        // 这里也是唯一的 as，但受控安全
        viewItem.onBindViewData(binding as VDB, item, position)
    }
}