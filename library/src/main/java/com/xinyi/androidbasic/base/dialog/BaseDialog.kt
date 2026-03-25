package com.xinyi.androidbasic.base.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import androidx.annotation.FloatRange
import androidx.annotation.StyleRes
import com.xinyi.androidbasic.R
import com.xinyi.androidbasic.action.ActivityAction
import androidx.core.graphics.drawable.toDrawable
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy

/**
 * Dialog 基类
 *
 * @author 新一
 * @date 2024/9/30 16:20
 */
abstract class BaseDialog: Dialog, Handler.Callback, ThreadHandlerProxy, ActivityAction {

    companion object {
        /** 没有动画效果  */
        const val ANIM_EMPTY = 0
        /** 缩放动画  */
        var ANIM_SCALE: Int = R.style.ScaleAnimStyle
        /** IOS 动画  */
        var ANIM_IOS: Int = R.style.IOSAnimStyle
        /** 顶部弹出动画  */
        var ANIM_TOP: Int = R.style.TopAnimStyle
        /** 底部弹出动画  */
        var ANIM_BOTTOM: Int = R.style.BottomAnimStyle
        /** 左边弹出动画  */
        var ANIM_LEFT: Int = R.style.LeftAnimStyle
        /** 右边弹出动画  */
        var ANIM_RIGHT: Int = R.style.RightAnimStyle
    }

    init {
        window?.attributes?.gravity = Gravity.CENTER
        window?.setBackgroundDrawable(0.toDrawable()) // 去除窗口透明部分显示的黑色
        initDialog()
    }

    /**
     * @param context 上下文
     * @param cancelable 是否可以取消
     * @param cancelListener 添加取消监听
     */
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    /**
     * @param context 上下文
     * @param theme 主题
     */
    constructor(context: Context, theme: Int) : super(context, theme)

    /**
     * @param context 上下文
     */
    constructor(context: Context) : super(context)

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    private fun initDialog() {
        setCanceledOnTouchOutside(canceled())
        setCancelable(canceled())
        setGravity() // 统一默认重心为居中

        setDialogContentView()
        initViews()
        initParams()
        initListeners()
    }

    /**
     * 设置弹窗的内容视图
     */
    protected open fun setDialogContentView() {
        setContentView(initLayoutId())
    }

    /**
     * 初始化布局ID
     *
     * @return 返回ID
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
     * 是否点击返回已经触摸屏幕空白区域关闭弹窗
     */
    open fun canceled(): Boolean {
        return true
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
     * 处理消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    /**
     * 设置 Dialog 宽度(WRAP_CONTENT/MATCH_PARENT)
     */
    fun setWidth(width: Int) {
        val params = window?.attributes
        params?.width = width
        window?.attributes = params
    }

    /**
     * 设置 Dialog 高度(WRAP_CONTENT/MATCH_PARENT)
     */
    fun setHeight(height: Int) {
        val params = window?.attributes
        params?.height = height
        window?.attributes = params
    }

    fun show(x: Int, y: Int) {
        onWindowAttributesChanged(
            x,
            y,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        show()
    }

    fun show(x: Int, y: Int, gravity: Int) {
        onWindowAttributesChanged(
            x,
            y,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            gravity
        )
        show()
    }

    /**
     * @param x 设置水平偏移
     * @param y 设置垂直偏移
     * @param width 宽
     * @param height 高
     * @param gravity 重心
     */
    private fun onWindowAttributesChanged(x: Int, y: Int, width: Int, height: Int, gravity: Int) {
        val params = window?.attributes
        params?.width = width
        params?.height = height
        params?.x = x
        params?.y = y
        params?.gravity = gravity
        onWindowAttributesChanged(params)
    }

    /**
     * 设置 Dialog 重心
     */
    open fun setGravity(gravity: Int = Gravity.CENTER) {
        window?.setGravity(gravity)
    }

    /**
     * 设置 Dialog 的动画
     */
    open fun setWindowAnimations(@StyleRes id: Int) {
        window?.setWindowAnimations(id)
    }

    /**
     * 设置窗口动画效果
     * @param animation 动画对象，用于定义窗口动画效果
     */
    open fun setWindowAnimations(animation: Animation) {
        // 设置窗口动画
        window?.setWindowAnimations(android.R.style.Animation_Dialog)
        window?.attributes?.windowAnimations = 0 // 防止双倍速度
        // 启动动画效果
        window?.decorView?.startAnimation(animation)
    }

    /**
     * 设置背景遮盖层开关(是否变暗)
     */
    fun setBackgroundDimEnabled(enabled: Boolean) {
        if (enabled) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    /**
     * 设置背景遮盖层的透明度（前提条件是背景遮盖层开关必须是为开启状态）
     */
    fun setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float) {
        window?.setDimAmount(dimAmount)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this::class.java.simpleName)
        }
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }
}