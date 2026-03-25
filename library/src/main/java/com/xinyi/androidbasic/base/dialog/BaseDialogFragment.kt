package com.xinyi.androidbasic.base.dialog

import android.os.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.xinyi.androidbasic.R
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy

/**
 * Dialog 弹窗碎片基类
 *
 * @author 新一
 * @date 2024/9/30 15:43
 */
abstract class BaseDialogFragment : DialogFragment(), Handler.Callback, ThreadHandlerProxy {

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置弹窗样式
        setStyle(STYLE_NO_TITLE, setDialogStyleTheme())
        // 初始化Handler
        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this.javaClass.simpleName)
        }
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(initLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dealIntent()
        initViews(view)
        initParams(savedInstanceState)
        initListeners()
    }

    /**
     * 初始化布局文件
     */
    protected abstract fun initLayoutId(): Int

    /**
     * 获取弹窗视图宽度
     */
    abstract fun getWindowWidth(): Int

    /**
     * 获取弹窗视图高度
     */
    abstract fun getWindowHeight(): Int

    /**
     * 设置弹窗样式
     */
    protected open fun setDialogStyleTheme(): Int = R.style.BaseDialogTheme

    /**
     * 设置弹窗在外部触摸时是否可以取消
     */
    protected open fun canceledOnTouchOutside(): Boolean = true

    /**
     * 设置弹窗是否可以通过物理返回键取消
     */
    protected open fun cancelable(): Boolean = true

    /**
     * 初始化视图
     */
    protected open fun initViews(view: View) {}

    /**
     * 初始化参数
     */
    protected open fun initParams(savedInstanceState: Bundle?) {
        dialog?.apply {
            // 设置弹窗在外部触摸时是否可以取消
            // 如果返回 true，用户点击弹窗外部时会关闭弹窗
            setCanceledOnTouchOutside(canceledOnTouchOutside())

            // 设置弹窗是否可以通过物理返回键取消
            // 如果返回 true，用户按下返回键时会关闭弹窗
            setCancelable(cancelable())

            //自定义弹窗视图宽高度
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                decorView.setPadding(0, 0, 0, 0)
                val wlp = attributes.apply {
                    gravity = Gravity.CENTER
                    width = getWindowWidth()
                    height = getWindowHeight()
                }
                attributes = wlp
            }
        }
    }

    /**
     * 处理Intent
     */
    protected open fun dealIntent() {}

    /**
     * 初始化监听器
     */
    protected open fun initListeners() {}

    /**
     * 处理消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}