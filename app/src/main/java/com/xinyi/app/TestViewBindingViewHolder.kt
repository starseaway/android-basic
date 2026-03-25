package com.xinyi.app

import android.view.ViewGroup
import com.xinyi.androidbasic.base.adapter.with.BaseViewBindingViewHolder
import com.xinyi.app.databinding.TestItemBinding

/**
 * 测试 ViewBindingViewHolder
 *
 * @author 新一
 * @date 2025/6/5 10:49
 */
class TestViewBindingViewHolder(parent: ViewGroup) : BaseViewBindingViewHolder<TestItemBinding>(R.layout.test_item, parent) {

    fun onBindViewData(item: String, position: Int) {

    }
}