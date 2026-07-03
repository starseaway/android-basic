package com.xinyi.androidbasic.base.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.xinyi.androidbasic.action.ActivityAction
import com.xinyi.beehive.proxy.ThreadHandlerProxy

/**
 * BaseRelativeLayout 的 ViewBinding 基类
 *
 * @author 新一
 * @date 2024/10/8 9:54
 */
abstract class BaseViewBindingRelativeLayout<VDB : ViewDataBinding> @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRelativeLayout(context, attributeSet, defStyleAttr),
    Handler.Callback,
    ActivityAction,
    ThreadHandlerProxy {

    /**
     * 可变 binding
     */
    protected lateinit var varBinding: VDB

    /**
     * 获取 ViewBinding 对象
     */
    val binding: VDB get() = varBinding

    override fun inflateLayoutContentView() {
        // 通过 DataBindingUtil.inflate 方法将布局文件转换为 ViewDataBinding 对象
        varBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            initLayoutId(), this, true
        )
    }
}