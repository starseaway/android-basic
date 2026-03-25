package com.xinyi.androidbasic.base.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Printer
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.xinyi.androidbasic.action.BundleAction
import com.xinyi.androidbasic.action.KeyboardAction
import com.xinyi.androidbasic.utils.LogUtil
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy
import java.lang.ref.WeakReference

/**
 * Activity基类，鉴于 handler 在日常开发中使用频率较高，因此在基类中封装了handler的使用
 *
 * @author 新一
 * @date 2024/9/30 14:27
 */
abstract class BaseActivity : AppCompatActivity(), ThreadHandlerProxy, KeyboardAction, BundleAction {

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    /**
     * HandlerCallback 对象，用于处理接收到的消息
     */
    private var mHandlerCallback: HandlerCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandlerCallback = HandlerCallback(this)
        mThreadHandler = ThreadHandler.createHandler(mHandlerCallback, MessagePrinter(), this::class.java.simpleName)

        dealIntent(intent)

        setContentView()
        initViews()
        initParams(savedInstanceState)
        initListeners()
    }

    override fun getBundle(): Bundle? {
        return intent.extras
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    /**
     * 设置内容视图
     */
    protected open fun setContentView() {
        if (initLayoutId() > 0) {
            setContentView(initLayoutId())
            initSoftKeyboard()
        }
    }

    /**
     * 获取内容视图
     */
    fun getContentView(): ViewGroup {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

    /**
     * 初始化布局文件
     */
    protected abstract fun initLayoutId(): Int

    /**
     * 处理 Intent
     */
    protected open fun dealIntent(mIntent: Intent?) {}

    /**
     * 初始化视图
     */
    protected open fun initViews() {}

    /**
     * 初始化参数
     */
    protected open fun initParams(savedInstanceState: Bundle?) {}

    /**
     * 初始化监听器
     */
    protected open fun initListeners() {}

    /**
     * 初始化软键盘
     */
    protected open fun initSoftKeyboard() {
        // 点击外部隐藏软键盘
        getContentView().setOnClickListener {
            // 隐藏软键，避免内存泄漏
            hideKeyboard(currentFocus)
        }
    }

    override fun finish() {
        super.finish()
        // 隐藏软键，避免内存泄漏
        hideKeyboard(currentFocus)
    }

    /**
     * 用于打印 Handler 消息队列中的消息的实现类
     */
    class MessagePrinter : Printer {

        /**
         * 打印消息
         */
        override fun println(x: String?) {
            LogUtil.d("消息入队：$x")
        }
    }

    /**
     * 线程处理器消息的回调实现类
     */
    class HandlerCallback(mThreadHandler: BaseActivity) : Handler.Callback {

        private val mWeakThreadHandler: WeakReference<BaseActivity> = WeakReference(mThreadHandler)

        /**
         * 处理接收到的消息
         *
         * @param msg 接收到的消息对象
         *
         * @return 如果消息已处理则返回 true，如果消息未处理则返回 false
         */
        override fun handleMessage(msg: Message): Boolean {
            LogUtil.i("Handler消息：$msg")
            mWeakThreadHandler.get()?.handleMessage(msg)
            return false
        }

        fun clear() {
            mWeakThreadHandler.clear()
        }
    }

    /**
     * 在工作线程上发送 Handler 消息
     *
     * @param messageWhat 消息的标识符
     */
    open fun sendWorkThreadMessage(messageWhat: Int) {
        sendWorkThreadMessage(Message.obtain().apply {
            what = messageWhat
        })
    }

    /**
     * 处理消息回调的方法。子类需要实现这个方法来处理接收到的消息。
     *
     * @param msg 接收到的消息对象
     */
    protected open fun handleMessage(msg: Message) {}

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mHandlerCallback?.clear()
        mThreadHandler = null
        mHandlerCallback = null
    }
}