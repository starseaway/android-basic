package com.xinyi.androidbasic.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * ConstraintLayout 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/10/8 9:39
 */
abstract class BaseViewBindingConstraintLayout <VDB : ViewDataBinding> : BaseConstraintLayout {

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr)

    private lateinit var _binding: VDB

    /**
     * 获取ViewBinding对象
     */
    val binding: VDB get() = _binding

    override fun setLayoutContentView() {
        // 通过 DataBindingUtil.inflate 方法将布局文件转换为 ViewDataBinding 对象
        _binding = DataBindingUtil.inflate(LayoutInflater.from(context), initLayoutId(), this, true)
    }
}