package com.xinyi.androidbasic.base.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.binding.BindingInflaters

/**
 * 弹窗碎片的 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/9/30 15:57
 */
abstract class BaseViewBindingDialogFragment<VB : ViewBinding> : BaseDialogFragment() {

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        varBinding = inflateBinding(inflater, container)
        onBindingCreated(varBinding)
        return varBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObserveUI()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 加载 ViewBinding，子类可重写以自定义 inflate 逻辑
     *
     * @param inflater 布局加载器
     * @param container 父容器
     * @return 绑定的 ViewBinding 对象
     */
    protected open fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return BindingInflaters.inflate(requireContext(), initLayoutId(), inflater, container, false)
    }

    /**
     * Binding 创建完成后的回调
     */
    protected open fun onBindingCreated(binding: VB) { }

    /**
     * 初始化UI观察
     */
    open fun initObserveUI() { }
}