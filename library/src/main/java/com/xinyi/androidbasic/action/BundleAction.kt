package com.xinyi.androidbasic.action

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Bundle 参数读取能力接口
 *
 * @author 新一
 * @date 2025/3/12 20:01
 */
interface BundleAction {

    /**
     * 当前持有的 Bundle 数据源
     */
    fun getBundle(): Bundle?

    /** 获取 Int 参数，默认值为 0 */
    fun getInt(name: String): Int = getInt(name, 0)

    /** 获取 Int 参数，支持自定义默认值 */
    fun getInt(name: String, defaultValue: Int): Int =
        getBundle()?.getInt(name, defaultValue) ?: defaultValue

    /** 获取 Long 参数，默认值为 0 */
    fun getLong(name: String): Long = getLong(name, 0)

    /** 获取 Long 参数，支持自定义默认值 */
    fun getLong(name: String, defaultValue: Long): Long =
        getBundle()?.getLong(name, defaultValue) ?: defaultValue

    /** 获取 Float 参数，默认值为 0f */
    fun getFloat(name: String): Float = getFloat(name, 0f)

    /** 获取 Float 参数，支持自定义默认值 */
    fun getFloat(name: String, defaultValue: Float): Float =
        getBundle()?.getFloat(name, defaultValue) ?: defaultValue

    /** 获取 Double 参数，默认值为 0.0 */
    fun getDouble(name: String): Double = getDouble(name, 0.0)

    /** 获取 Double 参数，支持自定义默认值 */
    fun getDouble(name: String, defaultValue: Double): Double =
        getBundle()?.getDouble(name, defaultValue) ?: defaultValue

    /** 获取 Boolean 参数，默认值为 false */
    fun getBoolean(name: String): Boolean = getBoolean(name, false)

    /** 获取 Boolean 参数，支持自定义默认值 */
    fun getBoolean(name: String, defaultValue: Boolean): Boolean =
        getBundle()?.getBoolean(name, defaultValue) ?: defaultValue

    /** 获取 String 参数 */
    fun getString(name: String): String? =
        getBundle()?.getString(name)

    /** 获取 Parcelable 参数 */
    @Suppress("DEPRECATION")
    fun <P : Parcelable> getParcelable(name: String): P? =
        getBundle()?.getParcelable(name)

    /** 获取 Serializable 参数 */
    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    fun <S : Serializable> getSerializable(name: String): S? =
        getBundle()?.getSerializable(name) as? S

    /** 获取 String 类型的 ArrayList 参数 */
    fun getStringArrayList(name: String): ArrayList<String>? =
        getBundle()?.getStringArrayList(name)

    /** 获取 Int 类型的 ArrayList 参数 */
    fun getIntegerArrayList(name: String): ArrayList<Int>? =
        getBundle()?.getIntegerArrayList(name)
}