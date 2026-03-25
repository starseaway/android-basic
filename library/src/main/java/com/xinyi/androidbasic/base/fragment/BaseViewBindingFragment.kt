package com.xinyi.androidbasic.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Fragment的ViewBinding基类
 *
 * @author 新一
 * @date 2024/9/30 15:41
 */
abstract class BaseViewBindingFragment<VDB: ViewDataBinding> : BaseFragment() {

    private lateinit var _binding: VDB

    /**
     * 获取ViewBinding对象
     */
    val binding: VDB get() = _binding

    override fun setContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = DataBindingUtil.inflate(LayoutInflater.from(context), initLayoutId(), null, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObserveUI()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 初始化UI观察
     */
    open fun initObserveUI() { }
}