package com.xinyi.app.widget

import android.content.Context
import android.util.AttributeSet
import com.xinyi.androidbasic.base.widget.BaseViewBindingConstraintLayout
import com.xinyi.app.R
import com.xinyi.app.databinding.ViewMainHighlightBinding

/**
 * main 源码集：BaseViewBindingConstraintLayout 烟测
 *
 * @author 杨耿雷
 * @date 2026/11/18
 */
class MainHighlightCardView : BaseViewBindingConstraintLayout<ViewMainHighlightBinding> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun initLayoutId(): Int = R.layout.view_main_highlight

    override fun onBindingCreated(binding: ViewMainHighlightBinding) {
        setBackgroundResource(R.drawable.bg_item_tip)
        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
    }

    fun setHighlight(text: CharSequence) {
        binding.tvHighlight.text = text
    }
}