package com.xinyi.androidbasic.base.activity

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Activity view 的视图绑定基类
 *
 * @author 新一
 * @date 2024/9/30 14:40
 */
abstract class BaseViewBindingActivity<VDB : ViewDataBinding> : BaseResultActivity() {

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VDB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VDB get() = varBinding

    override fun bindContentView() {
        varBinding = DataBindingUtil.setContentView(this, initLayoutId())
        varBinding.root.setOnClickListener {
            // 隐藏软键，避免内存泄漏
            hideKeyboard(currentFocus)
        }
        initObserveUI()
    }

    /**
     * 初始化UI观察
     */
    open fun initObserveUI() { }
}