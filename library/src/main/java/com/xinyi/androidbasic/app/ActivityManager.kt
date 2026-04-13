package com.xinyi.androidbasic.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.collection.ArrayMap
import com.xinyi.androidbasic.extension.AnyExtension.getUniqueTag
import com.xinyi.androidbasic.utils.LogUtil

/**
 * Activity 管理类
 *
 * @author 新一
 * @date 2024/9/30 14:44
 */
class ActivityManager private constructor() : ActivityLifecycleCallbacks {

    companion object {
        // 单例模式 + 同步锁
        @JvmStatic
        val instance: ActivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }

    /** Activity 存放集合 */
    private val mActivitySet = ArrayMap<String, Activity>()

    /** 应用生命周期回调 */
    private val mLifecycleCallbacks = ArrayList<ApplicationLifecycleCallback>()

    /** 栈顶的 Activity 对象 */
    private var _mTopActivity: Activity? = null
    val topActivity get() = _mTopActivity

    /** 前台并且可见的 Activity 对象 */
    private var _mResumedActivity: Activity? = null
    val resumedActivity get() = _mResumedActivity

    /**
     * 判断当前应用是否处于前台状态
     */
    fun isForeground(): Boolean = _mResumedActivity != null

    /**
     * 初始化
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 注册应用生命周期回调
     */
    fun registerApplicationLifecycleCallback(callback: ApplicationLifecycleCallback) {
        mLifecycleCallbacks.add(callback)
    }

    /**
     * 取消注册应用生命周期回调
     */
    fun unregisterApplicationLifecycleCallback(callback: ApplicationLifecycleCallback) {
        mLifecycleCallbacks.remove(callback)
    }

    /**
     * 销毁指定的 Activity
     */
    fun finishActivity(clazz: Class<out Activity?>?) {
        if (clazz == null) {
            return
        }
        mActivitySet.filter {
            // 过滤出指定的 Activity
            it.value.javaClass == clazz
        }.forEach {
            // 销毁 Activity
            it.value.finish()
            mActivitySet.remove(it.key)
        }
    }

    /**
     * 销毁所有的 Activity
     *
     * @param classArray 白名单 Activity
     */
    @SafeVarargs
    fun finishAllActivities(vararg classArray: Class<out Activity?> = emptyArray()) {
        val keys = mActivitySet.keys.toTypedArray()
        for (key in keys) {
            val activity = mActivitySet[key]
            if (activity == null || activity.isFinishing) {
                // 如果 Activity 为 null 或者已经销毁了就跳过
                continue
            }

            var whiteClazz = false
            for (clazz in classArray) {
                if (activity.javaClass == clazz) {
                    whiteClazz = true
                }
            }

            if (whiteClazz) {
                // 如果是白名单上面的 Activity 就跳过
                continue
            }

            // 如果不是白名单上面的 Activity 就销毁掉
            activity.finish()
            mActivitySet.remove(key)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogUtil.i("onCreate -> ${activity.javaClass.simpleName}")
        if (mActivitySet.isEmpty) {
            for (callback in mLifecycleCallbacks) {
                callback.onApplicationCreate(activity)
            }
            LogUtil.i("onApplicationCreate -> ${activity.javaClass.simpleName}")
        }
        mActivitySet[activity.getUniqueTag()] = activity
        _mTopActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        LogUtil.i("onStart -> ${activity.javaClass.simpleName}")
    }

    override fun onActivityResumed(activity: Activity) {
        LogUtil.i("onResume -> ${activity.javaClass.simpleName}")
        if (_mTopActivity === activity && _mResumedActivity == null) {
            for (callback in mLifecycleCallbacks) {
                callback.onApplicationForeground(activity)
            }
            LogUtil.i("onApplicationForeground -> ${activity.javaClass.simpleName}")
        }
        _mTopActivity = activity
        _mResumedActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        LogUtil.i("onPause -> ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        LogUtil.i("onStop -> ${activity.javaClass.simpleName}")
        if (_mResumedActivity === activity) {
            _mResumedActivity = null
        }
        if (_mResumedActivity == null) {
            for (callback in mLifecycleCallbacks) {
                callback.onApplicationBackground(activity)
            }
            LogUtil.i("onApplicationBackground -> ${activity.javaClass.simpleName}")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        LogUtil.i("onSaveInstanceState -> ${activity.javaClass.simpleName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        LogUtil.i("onDestroy -> ${activity.javaClass.simpleName}")
        mActivitySet.remove(activity.getUniqueTag())
        if (_mTopActivity === activity) {
            _mTopActivity = null
        }
        if (mActivitySet.isEmpty) {
            for (callback in mLifecycleCallbacks) {
                callback.onApplicationDestroy(activity)
            }
            LogUtil.i("onApplicationDestroy -> ${activity.javaClass.simpleName}")
        }
    }

    /**
     * 应用生命周期回调
     */
    interface ApplicationLifecycleCallback {

        /**
         * 第一个 Activity 创建了
         */
        fun onApplicationCreate(activity: Activity?)

        /**
         * 最后一个 Activity 销毁了
         */
        fun onApplicationDestroy(activity: Activity?)

        /**
         * 应用从前台进入到后台
         */
        fun onApplicationBackground(activity: Activity?)

        /**
         * 应用从后台进入到前台
         */
        fun onApplicationForeground(activity: Activity?)
    }
}