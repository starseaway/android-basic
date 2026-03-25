package com.xinyi.app

import android.content.Context
import com.xinyi.androidbasic.base.adapter.BaseMultiLayoutBindingAdapter
import com.xinyi.androidbasic.base.adapter.with.LambdaViewBindingItem
import com.xinyi.app.databinding.TestItem1Binding
import com.xinyi.app.databinding.TestItem2Binding
import com.xinyi.app.databinding.TestItem3Binding

/**
 * 测试多布局适配器
 *
 * @author 新一
 * @date 2025/6/4 15:30
 */
class TestMultiLayoutBindingAdapter(context: Context?) : BaseMultiLayoutBindingAdapter<String>(context) {

    companion object {
        const val TYPE_1 = 0
        const val TYPE_2 = 1
        const val TYPE_3 = 2
    }

    override fun getViewTypeForPosition(position: Int, item: String): Int {
        return when (item) {
            "1" -> TYPE_1
            "2" -> TYPE_2
            else -> TYPE_3
        }
    }

    override fun onRegisterViewBindingType() {
        registerViewType<TestItem1Binding>(TYPE_1, R.layout.test_item_1) { binding, item, position ->

        }
        registerViewType(TYPE_2, LambdaViewBindingItem<String, TestItem2Binding>(
            layoutId = R.layout.test_item_2,
            onBindViewData = { binding, item, position ->

            }
        ))
        registerViewType(TYPE_3, LambdaViewBindingItem<String, TestItem3Binding>(
            layoutId = R.layout.test_item_3,
            onBindViewData = { binding, item, position ->

            }
        ))
    }
}