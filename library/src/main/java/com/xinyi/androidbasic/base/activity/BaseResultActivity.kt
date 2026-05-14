package com.xinyi.androidbasic.base.activity

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat

/**
 * 封装 Activity 回传基类
 *
 * @author 新一
 * @date 2025/4/15 18:30
 */
abstract class BaseResultActivity : BaseActivity() {

    private companion object {

        /**
         * Activity 跳转结果契约
         *
         * 用于启动 Activity 并接收回传结果
         */
        val START_ACTIVITY_FOR_RESULT = ActivityResultContracts.StartActivityForResult()
    }

    /**
     * 当前 Activity 回调
     */
    private var mActivityCallback: OnActivityCallback? = null

    /**
     * Activity Result 启动器
     *
     * 新版 Activity 回传 API，最低支持 SDK21
     */
    private val mActivityResultLauncher = registerForActivityResult(START_ACTIVITY_FOR_RESULT) { result ->
        // 处理 Activity 返回结果
        mActivityCallback?.handleActivityResult(result.resultCode, result.data)
        // 防止内存泄漏
        mActivityCallback = null
    }

    /**
     * 启动 Activity（基于 Class）
     *
     * 对 startActivityForResult 的简化封装，内部自动构建 Intent
     *
     * @param clazz 目标 Activity 类型
     * @param callback 结果回调
     */
    open fun startActivityForResult(clazz: Class<out Activity>, callback: OnActivityCallback) {
        startActivityForResult(Intent(this, clazz), callback)
    }

    /**
     * 启动 Activity（基于 Intent）
     *
     * @param intent 启动参数
     * @param callback 结果回调
     */
    open fun startActivityForResult(intent: Intent, callback: OnActivityCallback) {
        startActivityForResult(intent, null, callback)
    }

    /**
     * 启动 Activity（支持 options）
     *
     * 当前采用单请求-单回传模型，同一时间仅允许存在一个待回传请求。
     * 如果上一次请求尚未完成时再次发起请求，会抛出 IllegalStateException 异常。
     *
     * 如需支持多并发回传：
     * - 建议业务层自行管理多个 ActivityResultLauncher
     * - 或基于业务场景实现请求队列机制
     *
     * @param intent 启动参数
     * @param options 启动附加参数
     * @param callback 结果回调
     */
    open fun startActivityForResult(intent: Intent, options: ActivityOptionsCompat?, callback: OnActivityCallback) {
        check(mActivityCallback == null) {
            "Another ActivityResult request is already in progress."
        }

        mActivityCallback = callback

        if (options != null) {
            mActivityResultLauncher.launch(intent, options)
        } else {
            mActivityResultLauncher.launch(intent)
        }
    }

    /**
     * Activity 跳转回传回调接口
     */
    interface OnActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode 结果码
         * @param data 数据
         */
        fun handleActivityResult(resultCode: Int, data: Intent?)
    }
}