package com.xinyi.androidbasic.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.binding.BindingInflaters

/**
 * BaseRelativeLayout 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/10/8 9:54
 */
abstract class BaseViewBindingRelativeLayout<VB : ViewBinding> : BaseRelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    override fun inflateLayoutContentView() {
        varBinding = inflateBinding(LayoutInflater.from(context))
        onBindingCreated(varBinding)
    }

    /**
     * 加载 ViewBinding，子类可重写以自定义 inflate 逻辑
     *
     * @param inflater 布局加载器
     * @return 绑定的 ViewBinding 对象
     */
    protected open fun inflateBinding(inflater: LayoutInflater): VB {
        return BindingInflaters.inflate(context, initLayoutId(), inflater, this, true)
    }

    /**
     * Binding 创建完成后的回调
     */
    protected open fun onBindingCreated(binding: VB) {}
}