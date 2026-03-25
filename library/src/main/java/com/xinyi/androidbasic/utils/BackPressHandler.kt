package com.xinyi.androidbasic.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.fragment.app.Fragment
import com.xinyi.androidbasic.extension.findActivity

/**
 * 系统返回键事件监听与处理的工具类
 *
 * 自动根据 Android 版本选择最佳实现方案：
 * - Android 13+ 使用 [OnBackInvokedDispatcher]
 * - Android 12 及以下使用 [androidx.activity.OnBackPressedDispatcher]
 *
 * @author 新一
 * @date 2025/10/10 10:59
 */
object BackPressHandler {

    /**
     * 注册返回键监听
     *
     * @param host 任意支持的宿主类型（Activity、Fragment、Dialog、View、Context（必须是activity的））
     * @param onBack 当用户按下返回键时执行的回调逻辑：
     *               返回 true 表示拦截（消费）事件；
     *               返回 false 表示放行（继续执行系统返回）。
     *
     * @return 一个注销函数，可用于移除回调
     * @throws IllegalArgumentException 若传入对象不受支持
     */
    fun register(host: Any, onBack: () -> Boolean): BackPressHandle {
        val activity = resolveActivity(host)
            ?: error("Unable to resolve Activity from host: ${host.javaClass.name}")
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                registerSystemBack(activity, onBack)
            }
            activity is ComponentActivity -> {
                registerCompatBack(activity, onBack)
            }
            else -> {
                error("Activity must extend ComponentActivity or run on Android 13+.")
            }
        }
    }

    /**
     * 根据传入对象自动解析对应的 [Activity]
     *
     * 若无法解析出 Activity，返回 null
     */
    private fun resolveActivity(host: Any): Activity? {
        val activity = when (host) {
            is Activity -> host
            is Fragment -> host.activity
            is Dialog -> host.context.findActivity()
            is View -> host.context.findActivity()
            is Context -> host.findActivity()
            else -> null
        }
        return activity
    }

    /**
     * 使用 Android 13+ 提供的系统级 [OnBackInvokedDispatcher] 注册回调
     *
     * 当用户执行 “预测返回手势” 时，系统会触发此回调
     *
     * @param activity 当前宿主 [Activity]
     * @param onBack 当返回事件发生时执行的回调逻辑
     * @return 一个注销函数，可用于移除回调
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun registerSystemBack(activity: Activity, onBack: () -> Boolean): BackPressHandle {
        val dispatcher: OnBackInvokedDispatcher = activity.onBackInvokedDispatcher

        val callback = OnBackInvokedCallback {
            val intercepted = onBack()
            if (!intercepted) {
                // 未消费 -> 尝试执行系统默认返回逻辑
                when (activity) {
                    is ComponentActivity -> {
                        // 调用 AndroidX 的分发逻辑（支持 Fragment 回退）
                        activity.onBackPressedDispatcher.onBackPressed()
                    }
                    else -> {
                        // 保底逻辑
                        activity.onBackPressed()
                    }
                }
            }
        }

        dispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT,
            callback
        )

        // 返回注销函数
        return BackPressHandle {
            dispatcher.unregisterOnBackInvokedCallback(callback)
        }
    }

    /**
     * 使用 AndroidX [androidx.activity.OnBackPressedDispatcher] 注册回调
     *
     * @param activity 当前宿主 [Activity]（必须继承自 [ComponentActivity]）
     * @param onBack 当返回事件发生时执行的回调逻辑
     * @return 一个注销函数，可用于移除回调
     */
    fun registerCompatBack(activity: ComponentActivity, onBack: () -> Boolean): BackPressHandle {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intercepted = onBack()
                if (!intercepted) {
                    // 放行 -> 暂时禁用再触发系统默认返回
                    isEnabled = false
                    activity.onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }

        activity.onBackPressedDispatcher.addCallback(activity, callback)
        return BackPressHandle { callback.remove() }
    }

    /**
     * 返回键回调句柄
     *
     * 用于控制已注册的返回键监听
     */
    fun interface BackPressHandle {

        /**
         * 注销当前返回键监听
         */
        fun unregister()
    }
}