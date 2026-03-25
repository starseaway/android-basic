package com.xinyi.androidbasic.base.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 封装 Activity 回传基类
 *
 * @author 新一
 * @date 2025/4/15 18:30
 */
abstract class BaseResultActivity : BaseActivity() {

    companion object {

        /** 请求码键 */
        private const val EXTRA_REQUEST_CODE = "request_code"
    }

    /** Activity 回调集合  */
    private var mActivityCallbacks: SparseArray<OnActivityCallback>? = null


    /**
     * Activity 结果回调
     */
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 Activity 结果回调 [新版的跳转回传最低支持到SDK21]
        val startActForResult = ActivityResultContracts.StartActivityForResult()
        mActivityResultLauncher = registerForActivityResult(startActForResult, ::onActivityResult)
    }

    /**
     * 启动 Activity（基于 Class），并接收返回结果
     *
     * 对 startActivityForResult 的简化封装，内部自动构建 Intent
     *
     * @param clazz 目标 Activity 类型
     * @param callback 结果回调
     */
    open fun startActivityForResult(clazz: Class<out Activity?>?, callback: OnActivityCallback?) {
        startActivityForResult(Intent(this, clazz), callback)
    }

    /**
     * 启动 Activity（基于 Intent），并接收返回结果
     *
     * @param intent 启动参数
     * @param callback 结果回调
     */
    open fun startActivityForResult(intent: Intent, callback: OnActivityCallback?) {
        startActivityForResult(intent, null, callback)
    }

    /**
     * 启动 Activity（支持 options），并接收返回结果
     *
     * 为每次请求分配唯一 requestCode，并建立回调映射关系。
     *
     * @param intent 启动参数
     * @param options 启动附加参数（如动画等）
     * @param callback 结果回调
     */
    open fun startActivityForResult(intent: Intent, options: Bundle?, callback: OnActivityCallback?) {
        if (mActivityCallbacks == null) {
            mActivityCallbacks = SparseArray<OnActivityCallback>(1)
        }
        // 生成唯一的请求代码
        val requestCode = (System.currentTimeMillis() % 65536).toInt()
        mActivityCallbacks?.put(requestCode, callback)
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode)
        mActivityResultLauncher.launch(intent)
    }

    /**
     * 处理 Activity 返回结果
     *
     * 会根据 requestCode 分发对应的回调
     *
     * @param result ActivityResult 回调结果
     */
    open fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            // 获取数据并通过请求码找到对应的回调
            val data = result.data
            mActivityCallbacks?.let { callbacks ->
                // 通过从返回数据中获取请求码来找到对应的回调
                val requestCode = data?.getIntExtra(EXTRA_REQUEST_CODE, -1) ?: -1
                val callback = callbacks[requestCode]
                callback?.onActivityResult(result.resultCode, data)
            }
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
        fun onActivityResult(resultCode: Int, data: Intent?)
    }
}