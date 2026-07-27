package com.xinyi.androidbasic.app

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.xinyi.androidbasic.action.HandlerAction
import com.xinyi.beehive.TaskBeehive

/**
 * 全局上下文
 *
 * @author 新一
 * @date 2024/9/30 13:41
 */
object AppContext {

    /**
     * Application instance
     */
    private lateinit var _sApplication: Application

    /**
     * 获取主线程 Handler
     */
    val mHandler: Handler get() = Handler(Looper.getMainLooper())

    /**
     * 是否已经初始化了 Application
     */
    @JvmStatic
    val isInitApplication: Boolean get() = ::_sApplication.isInitialized

    /**
     * 获取当前的保存的宿主应用的 Application 实例
     */
    @JvmStatic
    val sApplication: Application get() = _sApplication

    /**
     * 设置 Application 实例
     */
    @JvmStatic
    fun init(context: Context) {
        _sApplication = context.applicationContext as Application
        TaskBeehive.init(context)
    }

    /**
     * Ui 线程，执行任务
     */
    fun post(runnable: Runnable): Boolean {
        return mHandler.post(runnable)
    }

    /**
     * Ui 线程，延迟一段时间执行
     *
     * @param delayMillis 单位毫秒
     */
    fun postDelayed(runnable: Runnable, delayMillis: Long): Boolean {
        return mHandler.postDelayed(runnable, delayMillis)
    }

    /**
     * Ui 线程，在指定的时间执行
     *
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