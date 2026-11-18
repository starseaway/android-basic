package com.xinyi.app.widget

import android.content.Context
import android.util.AttributeSet
import com.xinyi.androidbasic.base.widget.BaseViewBindingConstraintLayout
import com.xinyi.app.R
import com.xinyi.app.databinding.ViewStoreHighlightBinding

/**
 * store 源码集：BaseViewBindingConstraintLayout 烟测
 *
 * @author 杨耿雷
 * @date 2026/11/18
 */
class StoreHighlightCardView : BaseViewBindingConstraintLayout<ViewStoreHighlightBinding> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun initLayoutId(): Int = R.layout.view_store_highlight

    override fun onBindingCreated(binding: ViewStoreHighlightBinding) {
        setBackgroundResource(R.drawable.bg_item_tip)
        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
    }

    fun setHighlight(text: CharSequence) {
        binding.tvHighlight.text = text
    }
}