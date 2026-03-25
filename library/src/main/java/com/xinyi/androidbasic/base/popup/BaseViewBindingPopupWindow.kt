package com.xinyi.androidbasic.base.popup

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
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
abstract class BaseViewBindingPopupWindow<VDB: ViewDataBinding> : BasePopupWindow, Handler.Callback,
    ThreadHandlerProxy {

    private var _binding: VDB

    /**
     * 获取viewBinding
     */
    val binding: VDB get() = _binding

    /**
     * 线程处理器
     */
    private var mThreadHandler: ThreadHandler? = null

    init {
        // 初始化布局文件
        _binding = DataBindingUtil.inflate(LayoutInflater.from(context), this.initLayoutId(), null, false)
    }

    constructor(fragment: Fragment): super(fragment) {
        contentView = _binding.root
    }

    constructor(context: Context): super(context) {
        contentView = _binding.root
    }

    constructor(dialog: Dialog): super(dialog) {
        contentView = _binding.root
    }

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
    protected open fun initViews() {}

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
    override fun onShowError(e: java.lang.Exception?) {
        super.onShowError(e)
        LogUtil.d("popup异常捕获：${e?.message}")
    }

    /**
     * 处理handle消息
     */
    override fun handleMessage(msg: Message): Boolean {
        return false
    }
}