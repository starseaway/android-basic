package com.xinyi.androidbasic.action

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 软键盘相关的操作接口
 *
 * @author 新一
 * @date 2025/4/17 17:27
 */
interface KeyboardAction {

    /**
     * 显示软键盘，默认是隐式显示（系统判断是否弹出），如果是在 Activity Create，那么需要延迟一段时间
     *
     * @param view 要获取焦点并弹出软键盘的视图
     * @param force 是否强制弹出（慎用：可能造成键盘残留，已在 Android 13 起废弃）
     */
    fun showKeyboard(view: View?, force: Boolean = false) {
        if (view == null) {
            return
        }
        // 确保 view 有焦点
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        val flag = if (force) InputMethodManager.SHOW_FORCED else InputMethodManager.SHOW_IMPLICIT
        imm.showSoftInput(view, flag)
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * 切换软键盘状态（显示/隐藏）
     */
    fun toggleSoftInput(view: View?) {
        if (view == null) {
            return
        }
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        if (imm.isActive(view)) {
            // 如果键盘已激活，尝试隐藏
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } else {
            // 如果未激活，尝试显示
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}