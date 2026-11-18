package com.xinyi.app.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.xinyi.androidbasic.base.widget.BaseConstraintLayout
import com.xinyi.app.R

/**
 * main 源码集：BaseConstraintLayout（无 VB）烟测
 *
 * @author 杨耿雷
 * @date 2026/11/18
 */
class MainPlainCardView : BaseConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun initLayoutId(): Int = R.layout.view_main_plain

    override fun initViews() {
        setBackgroundResource(R.drawable.bg_item_card)
        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
        findViewById<TextView>(R.id.tvPlain)?.text =
            "main 源码集 · BaseConstraintLayout（无 VB）"
    }
}