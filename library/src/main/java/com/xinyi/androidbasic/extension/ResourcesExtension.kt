package com.xinyi.androidbasic.extension

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.xinyi.androidbasic.app.AppContext

/**
 * id资源获取的扩展函数
 * 
 * @author 新一
 * @date 2025/3/3 17:35
 */

/**
 * 通过资源ID获取strings.xml字符串
 */
public fun @receiver:StringRes Int.getString(): String {
    return AppContext.sApplication.getString(this)
}

/**
 * 通过资源ID获取strings.xml字符串
 *
 * @param formatArgs 格式化参数
 */
public fun @receiver:StringRes Int.getString(vararg formatArgs: String): String {
    return AppContext.sApplication.getString(this, *formatArgs)
}

/**
 * 获取颜色
 */
public fun @receiver:ColorRes Int.getColor(): Int {
    return ContextCompat.getColor(AppContext.sApplication, this)
}

/**
 * 获取Drawable
 */
public fun @receiver:DrawableRes Int.drawable(): Drawable? {
    return ContextCompat.getDrawable(AppContext.sApplication, this)
}

/**
 * 获取 font
 */
public fun @receiver:FontRes Int.getFont(): Typeface? {
    return ResourcesCompat.getFont(AppContext.sApplication, this)
}