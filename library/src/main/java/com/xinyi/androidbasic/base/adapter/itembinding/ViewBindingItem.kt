package com.xinyi.androidbasic.base.adapter.itembinding

import androidx.databinding.ViewDataBinding

/**
 * ViewBinding 条目的接口，用于多类型列表中描述每一项的布局、绑定创建和数据绑定逻辑。
 *
 * 允许子类的通过 [out VDB] 的泛型协变性，替代为父类类型为具体的 ViewDataBinding 类型
 *
 * @param M 数据模型的类型
 * @param VDB 具体的 ViewDataBinding 子类，用于绑定当前布局
 *
 * @author 新一
 * @date 2025/6/4 14:49
 */
interface ViewBindingItem<M, VDB : ViewDataBinding> {

    /**
     * 当前条目的布局资源 ID
     */
    fun initLayoutId(): Int

    /**
     * 将数据绑定到当前条目的视图上
     *
     * @param binding 与当前布局关联的 ViewDataBinding
     * @param item 当前条目对应的数据项
     * @param position 当前条目的位置
     */
    fun onBindViewData(binding: VDB, item: M, position: Int)
}