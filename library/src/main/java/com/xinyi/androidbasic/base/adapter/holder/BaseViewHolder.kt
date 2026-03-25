package com.xinyi.androidbasic.base.adapter.holder

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xinyi.androidbasic.action.ClickAction

/**
 * RecyclerView.ViewHolder的基类
 *
 * 主要用来配合 BaseAdapter 使用，封装了常见的 ViewHolder 逻辑，
 * 简化写法、减少重复代码，你只需要专注在数据和视图的绑定上就好。
 *
 * @author 新一
 * @date 2025/4/19 15:00
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ClickAction {

    /**
     * 布局资源 ID 创建 ViewHolder 的构造函数
     *
     * @param layoutId 布局资源 ID（R.layout.xxx）
     * @param parent 父布局，用于获取 Context 和作为布局的根容器
     */
    constructor(
        @LayoutRes layoutId: Int,
        parent: ViewGroup
    ) : this(
        LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )
    )

    /**
     * 当前 ViewHolder 是否拥有一个有效的 Adapter position
     */
    open fun hasValidPosition(): Boolean {
        return adapterPosition != RecyclerView.NO_POSITION
    }

    override fun <V : View> findViewById(id: Int): V {
        return itemView.findViewById(id)
    }

    /**
     * 设置 TextView 的文本
     *
     * @param viewId TextView 的资源 ID
     * @param value 要设置的文本内容
     */
    open fun setText(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
        findViewById<TextView>(viewId).text = value
        return this
    }

    /**
     * 设置 TextView 的文本资源
     *
     * @param viewId TextView 的资源 ID
     * @param strId 字符串资源 ID
     */
    open fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
        findViewById<TextView>(viewId).setText(strId)
        return this
    }

    /**
     * 设置 TextView 的文字颜色
     *
     * @param viewId TextView 的资源 ID
     * @param color 颜色值（ColorInt）
     */
    open fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        findViewById<TextView>(viewId).setTextColor(color)
        return this
    }

    /**
     * 设置 TextView 的文字颜色（资源 ID）
     *
     * @param viewId TextView 的资源 ID
     * @param colorRes 颜色资源 ID
     */
    open fun setTextColorRes(@IdRes viewId: Int, @ColorRes colorRes: Int): BaseViewHolder {
        findViewById<TextView>(viewId).setTextColor(ContextCompat.getColor(itemView.context, colorRes))
        return this
    }

    /**
     * 设置 ImageView 的图片资源
     *
     * @param viewId ImageView 的资源 ID
     * @param imageResId 图片资源 ID
     */
    open fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseViewHolder {
        findViewById<ImageView>(viewId).setImageResource(imageResId)
        return this
    }

    /**
     * 设置 ImageView 的 Drawable
     *
     * @param viewId ImageView 的资源 ID
     * @param drawable 要设置的 Drawable
     */
    open fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BaseViewHolder {
        findViewById<ImageView>(viewId).setImageDrawable(drawable)
        return this
    }

    /**
     * 设置 ImageView 的 Bitmap
     *
     * @param viewId ImageView 的资源 ID
     * @param bitmap 要设置的 Bitmap
     */
    open fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BaseViewHolder {
        findViewById<ImageView>(viewId).setImageBitmap(bitmap)
        return this
    }

    /**
     * 设置 View 的背景颜色
     *
     * @param viewId View 的资源 ID
     * @param color 背景颜色值（ColorInt）
     */
    open fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        findViewById<View>(viewId).setBackgroundColor(color)
        return this
    }

    /**
     * 设置 View 的背景资源
     *
     * @param viewId View 的资源 ID
     * @param backgroundRes 背景资源 ID
     */
    open fun setBackgroundResource(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseViewHolder {
        findViewById<View>(viewId).setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * 设置 View 的可见性为 VISIBLE 或 INVISIBLE
     *
     * @param viewId View 的资源 ID
     * @param isVisible 是否可见（true = VISIBLE，false = INVISIBLE）
     */
    open fun setVisible(@IdRes viewId: Int, isVisible: Boolean): BaseViewHolder {
        val view = findViewById<View>(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        return this
    }

    /**
     * 设置 View 的可见性为 GONE 或 VISIBLE
     *
     * @param viewId View 的资源 ID
     * @param isGone 是否隐藏（true = GONE，false = VISIBLE）
     */
    open fun setGone(@IdRes viewId: Int, isGone: Boolean): BaseViewHolder {
        val view = findViewById<View>(viewId)
        view.visibility = if (isGone) View.GONE else View.VISIBLE
        return this
    }

    /**
     * 设置 View 的启用状态
     *
     * @param viewId View 的资源 ID
     * @param isEnabled 是否启用
     */
    open fun setEnabled(@IdRes viewId: Int, isEnabled: Boolean): BaseViewHolder {
        findViewById<View>(viewId).isEnabled = isEnabled
        return this
    }
}