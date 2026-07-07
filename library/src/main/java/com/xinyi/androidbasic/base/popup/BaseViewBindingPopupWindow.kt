package com.xinyi.androidbasic.base.popup

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.base.binding.BindingInflaters
import com.xinyi.androidbasic.utils.LogUtil
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy
import razerdp.basepopup.BasePopupWindow

/**
 * popup Binding基类
 *
 * @author 新一
 * @date 2024/9/30 14:03
 */
abstract class BaseViewBindingPopupWindow<VB : ViewBinding> : BasePopupWindow, Handler.Callback,
    ThreadHandlerProxy {

    /**
     * 可变 binding
     */
    protected var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    init {
        varBinding = inflateBinding(LayoutInflater.from(context))
        onBindingCreated(varBinding)
    }

    constructor(fragment: Fragment) : super(fragment) {
        contentView = varBinding.root
    }

    constructor(context: Context) : super(context) {
        contentView = varBinding.root
    }

    constructor(dialog: Dialog) : super(dialog) {
        contentView = varBinding.root
    }

    /**
     * 加载 ViewBinding，子类可重写以自定义 inflate 逻辑
     *
     * @param inflater 布局加载器
     * @return 绑定的 ViewBinding 对象
     */
    protected open fun inflateBinding(inflater: LayoutInflater): VB {
        return BindingInflaters.inflate(context, initLayoutId(), inflater, null, false)
    }

    /**
     * Binding 创建完成后的回调
     */
    protected open fun onBindingCreated(binding: VB) {}

    /**
     * 初始化布局文件
     */
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)

        initViews()
        initParams()
        initListeners()

        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this::class.java.simpleName)
        }
    }

    override fun getThreadHandler(): ThreadHandler? {
        return mThreadHandler
    }

    /**
     * 初始化布局文件
     */
    protected abstract fun initLayoutId(): Int

    /**
     * 初始化视图
     */
    protected open fun initViews() { }

    /**
     * 初始化参数
     */
    protected open fun initParams() {
        // 设置点击外部区域关闭弹窗
        setAutoMirrorEnable(true)
    }

    /**
     * 初始化监听
     */
    protected open fun initListeners() {}

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }

    /**
     * 是否点击返回已经触摸屏幕空白区域关闭弹窗
     */
    override fun onShowError(ex: java.lang.Exception?) {
        super.onShowError(ex)
        LogUtil.d("popup异常捕获：${ex?.message}")
    }

    /**
     * 处理 Handler 消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}