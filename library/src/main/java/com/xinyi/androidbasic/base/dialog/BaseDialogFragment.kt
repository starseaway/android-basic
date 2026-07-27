package com.xinyi.androidbasic.base.dialog

import android.os.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.xinyi.androidbasic.R
import com.xinyi.androidbasic.action.BundleAction
import com.xinyi.androidbasic.action.ClickAction
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy

/**
 * Dialog 弹窗碎片基类
 *
 * @author 新一
 * @date 2024/9/30 15:43
 */
abstract class BaseDialogFragment : DialogFragment(), Handler.Callback, ThreadHandlerProxy, BundleAction, ClickAction {

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    /**
     * 根布局
     */
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this.javaClass.simpleName)
        }
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = bindContentView(inflater, container)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow()

        initViews(view)
        initParams(savedInstanceState)
        initListeners()
    }

    /**
     * 初始化窗口属性
     */
    protected fun initWindow() {
        // 设置弹窗样式
        setStyle(STYLE_NO_TITLE, setDialogStyleTheme())

        setBackgroundDrawableResource(R.color.transparent)
        setGravity()

        setCanceledOnTouchOutside(canceledOnTouchOutside())
        setCancelable(cancelable())
    }

    /**
     * 设置内容视图
     *
     * @param inflater 布局加载器
     * @param container 容器
     */
    protected open fun bindContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(initLayoutId(), container, false)
    }

    override fun getView(): View? {
        return rootView
    }

    override fun <V : View> findViewById(id: Int): V? {
        return rootView?.findViewById(id)
    }

    /**
     * 初始化布局 ID
     */
    protected abstract fun initLayoutId(): Int

    /**
     * 初始化视图
     */
    protected open fun initViews(view: View) {}

    /**
     * 初始化参数
     */
    protected open fun initParams(savedInstanceState: Bundle?) { }

    /**
     * 初始化监听器
     */
    protected open fun initListeners() { }

    /**
     * 处理消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    override fun getBundle(): Bundle? {
        return arguments
    }

    /**
     * 设置弹窗样式
     */
    protected open fun setDialogStyleTheme(): Int = R.style.BaseDialogTheme

    /**
     * 设置弹窗在外部触摸时是否可以取消
     */
    protected open fun canceledOnTouchOutside(): Boolean = true

    /**
     * 设置弹窗背景
     *
     * @param resId 资源 ID
     */
    open fun setBackgroundDrawableResource(resId: Int) {
        dialog?.window?.setBackgroundDrawableResource(resId)
    }

    /**
     * 设置弹窗在外部触摸时是否可以取消
     *
     * @param isCanceled 是否可以取消
     */
    open fun setCanceledOnTouchOutside(isCanceled: Boolean) {
        dialog?.setCanceledOnTouchOutside(isCanceled)
    }

    /**
     * 设置 Dialog 重心
     */
    open fun setGravity(gravity: Int = Gravity.CENTER) {
        dialog?.window?.setGravity(gravity)
    }

    /**
     * 设置弹窗是否可以通过物理返回键取消
     */
    protected open fun cancelable(): Boolean = true

    /**
     * 设置 Dialog 宽度 (WRAP_CONTENT / MATCH_PARENT)
     */
    fun setWidth(width: Int) {
        val params = dialog?.window?.attributes
        params?.width = width
        dialog?.window?.attributes = params
    }

    /**
     * 设置 Dialog 高度 (WRAP_CONTENT / MATCH_PARENT)
     */
    fun setHeight(height: Int) {
        val params = dialog?.window?.attributes
        params?.height = height
        dialog?.window?.attributes = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }
}