package com.xinyi.androidbasic.action

import android.os.Handler
import android.os.Looper

/**
 * 主线程 Handler 的常用操作处理
 *
 * @author 新一
 * @date 2024/9/30 13:47
 */
interface HandlerAction {

    /**
     * 获取主线程 Handler
     */
    val mHandler: Handler get() = Handler(Looper.getMainLooper())

    /**
     * 执行任务
     */
    fun post(runnable: Runnable): Boolean {
        return mHandler.post(runnable)
    }

    /**
     * 延迟一段时间执行
     * @param delayMillis 单位毫秒
     */
    fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean {
        return mHandler.postDelayed(runnable, delayMillis)
    }

    /**
     * 在指定的时间执行
     * @param uptimeMillis 时间戳
     */
    fun postAtTime(runnable: Runnable, uptimeMillis: Long): Boolean {
        // 发送和当前对象相关的消息回调
        return mHandler.postAtTime(runnable, this, uptimeMillis)
    }

    /**
     * 移除单个消息回调
     */
    fun removeCallbacks(runnable: Runnable) {
        mHandler.removeCallbacks(runnable)
    }

    /**
     * 移除全部消息回调
     */
    fun removeCallbacks() {
        // 移除和当前对象相关的消息回调
        mHandler.removeCallbacksAndMessages(this)
    }
}