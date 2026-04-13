package com.xinyi.androidbasic.extension

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * 按类型区分的Context扩展函数
 * 
 * @author 新一
 * @date 2025/3/3 17:34
 */
object ContextExtension {

    /**
     * 获取屏幕宽度
     */
    @JvmStatic
    val Context.screenWidth
        get() = resources.displayMetrics.widthPixels

    /**
     * 获取屏幕高度
     */
    @JvmStatic
    val Context.screenHeight
        get() = resources.displayMetrics.heightPixels

    /**
     * 获取资源颜色
     */
    @JvmStatic
    fun Context.color(@ColorRes color: Int): Int {
        return ContextCompat.getColor(this, color)
    }

    /**
     * 获取资源 drawable
     */
    @JvmStatic
    fun Context.drawable(@DrawableRes drawable: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawable)
    }

    /**
     * 获取屏幕方向是否竖屏
     */
    @JvmStatic
    fun Context.portrait(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 获取屏幕方向是否横屏
     */
    @JvmStatic
    fun Context.landscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 注册本地广播接收器
     */
    @JvmStatic
    fun Context.registerLocalReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
        // 注册广播接收器
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    /**
     * 取消注册本地广播接收器
     */
    @JvmStatic
    fun Context.unregisterLocalReceiver(receiver: BroadcastReceiver) {
        // 取消注册广播接收器
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    /**
     * 发送本地广播
     */
    @JvmStatic
    fun Context.sendLocalBroadcast(intent: Intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * 获取应用的版本名
     *
     * @return 应用的版本名
     */
    @JvmStatic
    fun Context.getAppVersionName(): String? = packageManager.getPackageInfo(packageName, 0).versionName

    /**
     * 从任意 [Context] 递归提取 [Activity]
     *
     * Android 中许多组件（例如 Dialog、View）常常被包裹在多层
     * [android.content.ContextWrapper] 之中，例如：
     * ```
     *   ContextThemeWrapper -> DecorContext -> Activity
     * ```
     *
     * 此方法会沿着 baseContext 链向上查找，直到遇到 Activity 为止
     *
     * @receiver 起始上下文对象
     * @return 若能找到关联的 Activity 则返回之，否则返回 null
     */
    @JvmStatic
    tailrec fun Context?.findActivity(): Activity? {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.findActivity()
            else -> null
        }
    }
}