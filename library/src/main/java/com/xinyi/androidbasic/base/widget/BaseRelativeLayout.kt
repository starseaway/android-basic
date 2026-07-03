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
 * 自定义 RelativeLayout 基类
 *
 * 主要封装了 Handler 消息处理和线程操作以及生命周期管理
 *
 * @author 新一
 * @date 2024/10/8 9:17
 */
abstract class BaseRelativeLayout @JvmOverloads constructor(
    context: Context,
    private val attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr),
    Handler.Callback,
    ActivityAction,
    ThreadHandlerProxy {

    /**
     * 是否已完成初始化
     */
    private var isInitialized = false

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    /**
     * 是否处于 Resume 状态
     */
    private var isResume: Boolean = false

    init {
        inflateLayoutContentView()
    }

    /**
     * 加载布局内容
     */
    protected open fun inflateLayoutContentView() {
        inflate(context, initLayoutId(), this)
    }

    /**
     * 当 View 及其 XML 中声明的所有子 View 完成 Inflate 后调用
     *
     * 此时当前对象及子类成员均已完成初始化，且布局层级已经构建完成。
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        performInitialize()
    }

    /**
     * 完成初始化，仅执行一次
     */
    private fun performInitialize() {
        if (isInitialized) {
            return
        }

        isInitialized = true

        initStyledAttributes(attributeSet)
        initViews()
        initParams()
        initListeners()
    }

    /**
     * 初始化样式属性
     *
     * @param attrs 属性集
     */
    protected open fun initStyledAttributes(attrs: AttributeSet?) {}

    /**
     * 初始化布局 ID
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
     * 初始化 UI 观察
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
     * 是否启用线程处理器
     *
     * 默认关闭，子类可按需开启。
     */
    protected open fun isThreadHandlerEnabled(): Boolean = false

    /**
     * 在视图附加到窗口时调用
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // 兼容代码创建 View 的场景（代码创建不会回调 onFinishInflate）
        performInitialize()

        if (isThreadHandlerEnabled() && mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(
                this,
                this::class.java.simpleName
            )
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

        if (isThreadHandlerEnabled()) {
            mThreadHandler?.quitSafely()
            mThreadHandler = null
        }

        onDestroy()
    }

    /**
     * 处理 Handle 消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}