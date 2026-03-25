package com.xinyi.androidbasic.base.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 弹窗碎片的 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/9/30 15:57
 */
abstract class BaseViewBindingDialogFragment<VDB : ViewDataBinding> : BaseDialogFragment() {

    private lateinit var _binding: VDB

    /**
     * 获取ViewBinding对象
     */
    val binding: VDB get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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