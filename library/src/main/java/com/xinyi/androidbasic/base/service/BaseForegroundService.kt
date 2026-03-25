package com.xinyi.androidbasic.base.service

import android.app.Notification
import android.os.Build

/**
 * 前台服务基类，自动启动前台通知以提升存活率。
 *
 * 子类需实现通知内容，并提供唯一通知 ID。
 *
 * 继承自 [BaseService]，支持复杂的线程任务派发。
 *
 * @author 新一
 * @date 2025/6/3 14:07
 */
abstract class BaseForegroundService : BaseService() {

    override fun onCreate() {
        super.onCreate()
        // 启动前台服务，防止服务被系统杀死
        // Android 8.0（API 26）及以上系统中，必须调用 [startForeground], 否则系统将抛出异常。
        startForeground(getForegroundId(), createForegroundNotification())
    }

    /**
     * 获取前台服务通知 ID，用于标识通知。
     *
     * @return 通知 ID，默认返回 hashCode()
     */
    protected open fun getForegroundId(): Int = hashCode()

    /**
     * 创建前台通知内容。
     *
     * @return 用于前台服务的通知对象
     */
    protected abstract fun createForegroundNotification(): Notification

    /**
     * 启动前台服务，设置通知（通常在 onCreate 或需要更新通知时调用）
     *
     * @param notification 新的通知对象
     */
    protected fun startForeground(notification: Notification) {
        startForeground(getForegroundId(), notification)
    }

    /**
     * 更新通知内容
     *
     * ⚠️ 注意：调用时请确保线程安全，避免阻塞主线程。
     *
     * @param notification 新的通知对象
     */
    protected fun updateNotification(notification: Notification) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as? android.app.NotificationManager
        nm?.notify(getForegroundId(), notification)
    }

    override fun onDestroy() {
        // 兼容 Android 13+ stopForeground API，移除通知并停止前台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        super.onDestroy()
    }
}