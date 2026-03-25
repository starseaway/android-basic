package com.xinyi.androidbasic.action

import android.view.View
import androidx.annotation.IdRes

/**
 * ClickAction 接口用于简化点击事件绑定操作，提供批量设置点击监听器的方法。
 * 实现类需提供 findViewById 实现，用于查找目标 View。
 *
 * @author 新一
 * @date 2022/09/15 16:37
 */
interface ClickAction : View.OnClickListener {

    /**
     * 查找指定 ID 的 View
     *
     * @param id View 的资源 ID
     * @return 指定类型的 View 实例
     */
    fun <V : View> findViewById(@IdRes id: Int): V

    /**
     * 为多个 View ID 设置点击事件监听器（默认为当前对象）
     *
     * @param ids 需要设置点击事件的 View ID
     */
    fun setOnClickListener(vararg ids: Int) {
        setOnClickListener(listener = this, *ids)
    }

    /**
     * 为多个 View ID 设置指定的点击事件监听器
     *
     * @param listener 点击事件监听器，可为空
     * @param ids 需要设置点击事件的 View ID
     */
    fun setOnClickListener(listener: View.OnClickListener?, vararg ids: Int) {
        for (id in ids) {
            findViewById<View>(id).setOnClickListener(listener)
        }
    }

    /**
     * 为多个 View 设置点击事件监听器（默认为当前对象）
     *
     * @param views 需要设置点击事件的 View
     */
    fun setOnClickListener(vararg views: View) {
        setOnClickListener(listener = this, *views)
    }

    /**
     * 为多个 View 设置指定的点击事件监听器
     *
     * @param listener 点击事件监听器，可为空
     * @param views 需要设置点击事件的 View
     */
    fun setOnClickListener(listener: View.OnClickListener?, vararg views: View) {
        for (view in views) {
            view.setOnClickListener(listener)
        }
    }

    /**
     * 点击事件回调函数，默认不做任何操作
     * 实现类可重写此方法以处理点击逻辑
     *
     * @param view 被点击的 View
     */
    override fun onClick(view: View) {
        // 默认实现为空，由子类实现具体逻辑
    }
}