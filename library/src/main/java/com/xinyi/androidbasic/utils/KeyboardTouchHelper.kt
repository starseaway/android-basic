package com.xinyi.androidbasic.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 点击输入框外隐藏软键盘的工具类
 *
 * 在 Activity 的 [Activity.dispatchTouchEvent] 中调用 [onDispatchTouch] 即可
 *
 * @author 杨耿雷
 * @date 2026/7/8 14:12
 */
object KeyboardTouchHelper {

    /**
     * 输入框焦点区域，用于判断点击是否落在输入框外
     */
    private val mFocusRect = Rect()

    /**
     * 在 dispatchTouchEvent 中调用，处理点击输入框外隐藏软键盘
     *
     * @param activity 当前 Activity
     * @param ev 触摸事件
     * @param enabled 是否启用
     */
    fun onDispatchTouch(activity: Activity, ev: MotionEvent, enabled: Boolean = true) {
        if (!enabled || ev.action != MotionEvent.ACTION_DOWN) {
            return
        }
        hideOnTouch(activity, ev)
    }

    /**
     * 点击输入框外时隐藏软键盘
     *
     * @param activity 当前 Activity
     * @param ev 触摸事件
     */
    fun hideOnTouch(activity: Activity, ev: MotionEvent) {
        val focus = activity.currentFocus as? EditText ?: return
        if (!focus.getGlobalVisibleRect(mFocusRect) ||
            mFocusRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
            return
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        imm.hideSoftInputFromWindow(focus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        focus.clearFocus()
    }
}