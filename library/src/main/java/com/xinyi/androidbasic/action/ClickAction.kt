package com.xinyi.androidbasic.action

import android.view.View
import androidx.annotation.IdRes

/**
 * 点击事件绑定等相关操作接口
 *
 * 提供批量设置点击监听器的方法，实现类需提供 findViewById 实现，用于查找目标 View
 *
 * @author 新一
 * @date 2022/09/15 16:37
 */
interface ClickAction : View.OnClickListener {

    /**
     * 查找指定 ID 的 View
     *
     * @param id View 的资源 ID
     */
    fun <V : View> findViewById(@IdRes id: Int): V?

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
            findViewById<View>(id)?.setOnClickListener(listener)
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
     * 点击事件回调函数
     *
     * @param view 被点击的 View
     */
    override fun onClick(view: View) { }
}