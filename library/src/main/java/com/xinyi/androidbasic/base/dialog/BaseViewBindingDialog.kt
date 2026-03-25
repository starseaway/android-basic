package com.xinyi.androidbasic.base.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 弹窗 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/9/30 16:20
 */
abstract class BaseViewBindingDialog<VDB : ViewDataBinding> : BaseDialog {

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    constructor(context: Context, theme: Int) : super(context, theme)

    constructor(context: Context) : super(context)

    private lateinit var _binding: VDB

    /**
     * 获取ViewBinding对象
     */
    val binding: VDB get() = _binding

    override fun setDialogContentView() {
        // 初始化ViewBinding
        _binding = DataBindingUtil.inflate(LayoutInflater.from(context), initLayoutId(), null, false)
        setContentView(_binding.root)
        initObserveUI()
    }

    /**
     * 初始化 UI 观察
     */
    open fun initObserveUI() { }
}