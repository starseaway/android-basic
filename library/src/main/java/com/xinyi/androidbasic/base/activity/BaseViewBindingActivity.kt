package com.xinyi.androidbasic.base.activity

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Activity view 的视图绑定基类
 *
 * @author 新一
 * @date 2024/9/30 14:40
 */
abstract class BaseViewBindingActivity<VDB : ViewDataBinding> : BaseActivity() {

    private lateinit var _binding: VDB

    /**
     * 获取ViewBinding对象
     */
    val binding: VDB get() = _binding

    override fun setContentView() {
        _binding = DataBindingUtil.setContentView(this, initLayoutId())
        _binding.root.setOnClickListener {
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