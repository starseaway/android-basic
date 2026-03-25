package com.xinyi.androidbasic.base.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xinyi.androidbasic.utils.LogUtil
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy
import java.lang.ref.WeakReference

/**
 * Fragment基类，鉴于handler在日常开发中使用频率较高，因此在基类中封装了handler的使用
 *
 * @author 新一
 * @date 2024/9/30 15:35
 */
abstract class BaseFragment : Fragment(), ThreadHandlerProxy {

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
        mThreadHandler = ThreadHandler.createHandler(mHandlerCallback, this::class.java.simpleName)
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setContentView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initParams(savedInstanceState)
        initListeners()
    }

    /**
     * 设置内容视图
     */
    protected open fun setContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(initLayoutId(), container, false)
    }

    /**
     * 初始化布局文件
     */
    protected abstract fun initLayoutId(): Int

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
     * 线程处理器消息的回调实现类
     *
     * @param mThreadHandler 对 BaseThreadHandlerHook 的弱引用
     */
    class HandlerCallback(mThreadHandler: BaseFragment) : Handler.Callback {

        private val mWeakThreadHandler: WeakReference<BaseFragment> = WeakReference(mThreadHandler)

        /**
         * 处理接收到的消息
         *
         * @param msg 接收到的消息对象
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
    protected open fun handleMessage(msg: Message?) {}

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mHandlerCallback?.clear()
        mThreadHandler = null
        mHandlerCallback = null
    }
}