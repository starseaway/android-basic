package com.xinyi.app

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xinyi.androidbasic.base.adapter.BaseAdapter
import com.xinyi.androidbasic.base.adapter.with.ViewBindingViewHolder
import com.xinyi.app.databinding.TestItem1Binding
import com.xinyi.app.databinding.TestItem2Binding
import com.xinyi.app.databinding.TestItem3Binding

/**
 * 测试多个 ViewHolder 对象的示例
 *
 * @author 新一
 * @date 2025/6/5 10:19
 */
class TestMultiHolderAdapter(context: Context?) : BaseAdapter<String, RecyclerView.ViewHolder>(context) {

    companion object {
        const val TYPE_1 = 0
        const val TYPE_2 = 1
        const val TYPE_3 = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            "1" -> TYPE_1
            "2" -> TYPE_2
            else -> TYPE_3
        }
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_1 -> ViewBindingViewHolder<TestItem1Binding>(R.layout.test_item_1, parent)
            TYPE_2 -> ViewBindingViewHolder<TestItem2Binding>(R.layout.test_item_2, parent)
            TYPE_3 -> TestViewBindingViewHolder(parent)
            else -> ViewBindingViewHolder<TestItem3Binding>(R.layout.test_item_3, parent)
        }
    }

    override fun onBindViewData(holder: RecyclerView.ViewHolder, item: String, position: Int) {
        when (holder) {
            is ViewBindingViewHolder<*> -> {
                when (holder.binding) {
                    is TestItem1Binding -> {

                    }
                    is TestItem2Binding -> {

                    }
                    is TestItem3Binding -> {

                    }
                }
            }
            is TestViewBindingViewHolder -> holder.onBindViewData(item, position)
        }
    }
}