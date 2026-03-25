package com.xinyi.app

import android.content.Context
import com.xinyi.androidbasic.base.adapter.BaseViewBindingAdapter
import com.xinyi.app.databinding.TestItemBinding
import com.xinyi.beehive.TaskBeehive

/**
 * 测试 ViewBinding 适配器, 单布局
 *
 * @author 新一
 * @date 2025/4/19 16:04
 */
class TestViewBindingAdapter(context: Context?) : BaseViewBindingAdapter<String, TestItemBinding>(context) {

    override fun initLayoutId(): Int {
        return R.layout.test_item
    }

    override fun onBindViewDataBinding(binding: TestItemBinding, item: String, position: Int) {

    }
}