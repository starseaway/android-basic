package com.xinyi.androidbasic.base.widget

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.CallSuper
import com.xinyi.androidbasic.action.ActivityAction
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy

/**
 * 自定义 RelativeLayout 基类, 主要封装了 Handler 消息处理和线程操作以及生命周期管理
 *
 * @author 新一
 * @date 2024/10/8 9:17
 */
abstract class BaseRelativeLayout: RelativeLayout, Handler.Callback, ActivityAction,
    ThreadHandlerProxy {

    constructor(context: Context?) : super(context!!) {
        initWidget(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initWidget(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        initWidget(attrs)
    }

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    /**
     * 是否处于 Resume 状态
     */
    private var isResume: Boolean = false

    private fun initWidget(attrs: AttributeSet?) {
        setLayoutContentView()

        initViews()
        initStyledAttributes(attrs)
        initParams()
        initListeners()
    }

    /**
     * 设置布局内容视图
     */
    protected open fun setLayoutContentView() {
        inflate(context, initLayoutId(), this)
    }

    /**
     * 初始化样式属性
     * @param attrs 属性集
     */
    protected open fun initStyledAttributes(attrs: AttributeSet?) {}

    /**
     * 初始化布局 ID
     *
     * @return 返回 ID
     */
    protected abstract fun initLayoutId(): Int

    /**
     * 初始化组件
     */
    protected open fun initViews() {}

    /**
     * 参数设置
     */
    protected open fun initParams() {}

    /**
     * 监听设置
     */
    protected open fun initListeners() {}

    /**
     * 初始化UI观察
     */
    @CallSuper
    protected open fun onResume() {
        isResume = true
    }

    /**
     * 视图暂停时调用
     */
    @CallSuper
    protected open fun onPause() {
        isResume = false
    }

    /**
     * 视图销毁时调用
     */
    protected open fun onDestroy() {}

    /**
     * 是否处于 Resume 状态
     */
    fun isResume(): Boolean {
        return isResume
    }

    /**
     * 在视图附加到窗口时调用
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this::class.java.simpleName)
        }
        onResume()
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    /**
     * 当视图从窗口分离时调用
     */
    override fun onDetachedFromWindow() {
        onPause()
        super.onDetachedFromWindow()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
        onDestroy()
    }

    /**
     * 处理消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}