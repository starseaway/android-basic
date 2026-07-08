package com.xinyi.androidbasic.base.activity

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.binding.BindingInflaters

/**
 * Activity view 的视图绑定基类
 *
 * @author 新一
 * @date 2024/9/30 14:40
 */
abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseResultActivity() {

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    override fun bindContentView() {
        varBinding = inflateBinding(layoutInflater)
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
        return BindingInflaters.inflate(this, initLayoutId(), inflater, null, false)
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