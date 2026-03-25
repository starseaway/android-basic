package com.xinyi.androidbasic.base.service

import android.app.Service
import android.os.Handler
import android.os.Message
import com.xinyi.scheduler.proxy.ThreadHandlerProxy
import com.xinyi.scheduler.thread.ThreadHandler

/**
 * Service基类, 主要封装工作线程的handler处理器
 *
 * @author 新一
 * @date 2025/6/3 9:38
 */
abstract class BaseService : Service(), ThreadHandlerProxy, Handler.Callback {

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    override fun onCreate() {
        super.onCreate()
        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this::class.java.simpleName)
        }
        initParams()
    }

    /**
     * 初始化参数
     */
    protected open fun initParams() {}

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    /**
     * 判断服务是否在运行中
     */
    open fun isRunning(): Boolean = mThreadHandler != null

    /**
     * 处理消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }
}