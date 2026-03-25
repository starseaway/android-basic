package com.xinyi.androidbasic.app

import android.app.Application
import android.content.Context
import com.xinyi.androidbasic.action.HandlerAction

/**
 * 全局上下文
 *
 * @author 新一
 * @date 2024/9/30 13:41
 */
object AppContext : HandlerAction {

    /**
     * Application instance
     */
    private lateinit var _sApplication: Application

    /**
     * 是否已经初始化了Application。
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
    }
}