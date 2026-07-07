package com.xinyi.androidbasic.base.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.binding.BindingInflaters

/**
 * 弹窗 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/9/30 16:20
 */
abstract class BaseViewBindingDialog<VB : ViewBinding> : BaseDialog {

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener,
    ) : super(context, cancelable, cancelListener)

    constructor(context: Context, theme: Int) : super(context, theme)

    constructor(context: Context) : super(context)

    /**
     * 可变 binding
     */
    protected var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    init {
        varBinding = inflateBinding(LayoutInflater.from(context))
        setContentView(varBinding.root)
        onBindingCreated(varBinding)
        initObserveUI()
    }

    /**
     * 加载 ViewBinding，子类可重写以自定义 inflate 逻辑
     *
     * @param inflater 布局加载器
     * @return 绑定的 ViewBinding 对象
     */
    protected open fun inflateBinding(inflater: LayoutInflater): VB {
        return BindingInflaters.inflate(context, initLayoutId(), inflater, null, false)
    }

    /**
     * Binding 创建完成后的回调
     */
    protected open fun onBindingCreated(binding: VB) { }

    /**
     * 初始化 UI 观察
     */
    open fun initObserveUI() { }
}