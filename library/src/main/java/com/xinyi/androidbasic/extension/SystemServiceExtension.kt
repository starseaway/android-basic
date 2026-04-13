package com.xinyi.androidbasic.extension

import android.app.*
import android.app.job.JobScheduler
import android.content.ClipboardManager
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.PowerManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

/**
 * 系统服务扩展函数
 *
 * @author 新一
 * @date 2025/3/3 17:35
 */
object SystemServiceExtension {

    /**
     * 获取系统服务，返回指定类型的系统服务
     * @param T 系统服务的类型
     * 
     * @return 对应类型的系统服务实例
     */
    @JvmStatic
    inline fun <reified T> Context.getSystemService(): T? =
        ContextCompat.getSystemService(this, T::class.java)

    /**
     * 获取系统服务：窗口管理
     */
    @JvmStatic
    val Context.windowManager get() = getSystemService<WindowManager>()

    /**
     * 获取系统服务：剪贴板管理
     */
    @JvmStatic
    val Context.clipboardManager get() = getSystemService<ClipboardManager>()

    /**
     * 获取系统服务：布局填充器
     */
    @JvmStatic
    val Context.layoutInflater get() = getSystemService<LayoutInflater>()

    /**
     * 获取系统服务：活动管理器
     */
    @JvmStatic
    val Context.activityManager get() = getSystemService<ActivityManager>()

    /**
     * 获取系统服务：电源管理
     */
    @JvmStatic
    val Context.powerManager get() = getSystemService<PowerManager>()

    /**
     * 获取系统服务：闹钟管理
     */
    @JvmStatic
    val Context.alarmManager get() = getSystemService<AlarmManager>()

    /**
     * 获取系统服务：通知管理
     */
    @JvmStatic
    val Context.notificationManager get() = getSystemService<NotificationManager>()

    /**
     * 获取系统服务：锁屏管理
     */
    @JvmStatic
    val Context.keyguardManager get() = getSystemService<KeyguardManager>()

    /**
     * 获取系统服务：位置管理
     */
    @JvmStatic
    val Context.locationManager get() = getSystemService<LocationManager>()

    /**
     * 获取系统服务：搜索管理
     */
    @JvmStatic
    val Context.searchManager get() = getSystemService<SearchManager>()

    /**
     * 获取系统服务：存储管理
     */
    @JvmStatic
    val Context.storageManager get() = getSystemService<StorageManager>()

    /**
     * 获取系统服务：振动器
     */
    @JvmStatic
    val Context.vibrator get() = getSystemService<Vibrator>()

    /**
     * 获取系统服务：网络连接管理
     */
    @JvmStatic
    val Context.connectivityManager get() = getSystemService<ConnectivityManager>()

    /**
     * 获取系统服务：WiFi管理
     */
    @JvmStatic
    val Context.wifiManager get() = getSystemService<WifiManager>()

    /**
     * 获取系统服务：音频管理
     */
    @JvmStatic
    val Context.audioManager get() = getSystemService<AudioManager>()

    /**
     * 获取系统服务：媒体路由
     */
    @JvmStatic
    val Context.mediaRouter get() = getSystemService<MediaRouter>()

    /**
     * 获取系统服务：电话管理
     */
    @JvmStatic
    val Context.telephonyManager get() = getSystemService<TelephonyManager>()

    /**
     * 获取系统服务：传感器管理
     */
    @JvmStatic
    val Context.sensorManager get() = getSystemService<SensorManager>()

    /**
     * 获取系统服务：订阅管理
     */
    @JvmStatic
    val Context.subscriptionManager get() = getSystemService<SubscriptionManager>()

    /**
     * 获取系统服务：运营商配置管理
     */
    @JvmStatic
    val Context.carrierConfigManager get() = getSystemService<CarrierConfigManager>()

    /**
     * 获取系统服务：输入法管理
     */
    @JvmStatic
    val Context.inputMethodManager get() = getSystemService<InputMethodManager>()

    /**
     * 获取系统服务：UI模式管理
     */
    @JvmStatic
    val Context.uiModeManager get() = getSystemService<UiModeManager>()

    /**
     * 获取系统服务：下载管理
     */
    @JvmStatic
    val Context.downloadManager get() = getSystemService<DownloadManager>()

    /**
     * 获取系统服务：电池管理
     */
    @JvmStatic
    val Context.batteryManager get() = getSystemService<BatteryManager>()

    /**
     * 获取系统服务：任务调度器
     */
    val Context.jobScheduler get() = getSystemService<JobScheduler>()

    /**
     * 获取系统服务：无障碍管理
     */
    @JvmStatic
    val Context.accessibilityManager get() = getSystemService<AccessibilityManager>()
}