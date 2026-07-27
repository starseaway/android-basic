package com.xinyi.androidbasic.base.popup

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xinyi.androidbasic.action.ClickAction
import com.xinyi.androidbasic.base.binding.BindingInflaters
import com.xinyi.androidbasic.utils.LogUtil
import com.xinyi.beehive.core.ThreadHandler
import com.xinyi.beehive.proxy.ThreadHandlerProxy
import razerdp.basepopup.BasePopupWindow

/**
 * popup Binding 基类
 *
 * [onViewCreated] 在父类构造中执行，目前无法解决子类在 [initParams] 获取构参为空的 BUG。
 * 之后将会考虑重新替换底层 Popup 的抽象封装
 *
 * @author 新一
 * @date 2024/9/30 14:03
 */
abstract class BaseViewBindingPopupWindow<VB : ViewBinding> : BasePopupWindow, Handler.Callback,
    ThreadHandlerProxy, ClickAction {

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VB get() = varBinding

    /**
     * 是否已经绑定过 ContentView
     */
    private var _isContentViewBound = false
    val isContentViewBound: Boolean
        get() = _isContentViewBound

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    constructor(fragment: Fragment) : super(fragment) {
        initialize()
    }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(dialog: Dialog) : super(dialog) {
        initialize()
    }

    /**
     * 构造初始化
     */
    private fun initialize() {
        if (!isContentViewBound) {
            bindContentView()
        }
        initWindow()
    }

    /**
     * 绑定弹窗内容视图
     */
    protected open fun bindContentView() {
        varBinding = inflateBinding(LayoutInflater.from(context))
        contentView = varBinding.root
        onBindingCreated(varBinding)
    }

    /**
     * 初始化窗口属性
     */
    protected fun initWindow() {
        // 设置点击外部区域关闭弹窗
        setAutoMirrorEnable(true)
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
    protected open fun onBindingCreated(binding: VB) { }

    /**
     * 初始化布局文件
     */
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        if (mThreadHandler == null) {
            mThreadHandler = ThreadHandler.createHandler(this, this::class.java.simpleName)
        }

        initViews()
        initParams()
        initListeners()
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
    protected open fun initParams() { }

    /**
     * 初始化监听
     */
    protected open fun initListeners() { }

    override fun onDestroy() {
        super.onDestroy()
        mThreadHandler?.quitSafely()
        mThreadHandler = null
    }

    override fun onShowError(ex: java.lang.Exception?) {
        super.onShowError(ex)
        LogUtil.d("${this::class.java.simpleName} 显示异常：${ex?.message}")
    }

    /**
     * 处理 Handler 消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}