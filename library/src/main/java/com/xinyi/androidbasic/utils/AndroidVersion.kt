package com.xinyi.androidbasic.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * Android API 等级工具类
 *
 * @author 新一
 * @date 2026/3/25 11:17
 */
object AndroidVersion {

    /**
     * Android 4.4（API 19）
     */
    const val ANDROID_4_4 = Build.VERSION_CODES.KITKAT

    /**
     * Android 4.4W（API 20）
     */
    const val ANDROID_4_4W = Build.VERSION_CODES.KITKAT_WATCH

    /**
     * Android 5.0（API 21）
     */
    const val ANDROID_5 = Build.VERSION_CODES.LOLLIPOP

    /**
     * Android 5.1（API 22）
     */
    const val ANDROID_5_1 = Build.VERSION_CODES.LOLLIPOP_MR1

    /**
     * Android 6.0（API 23）
     */
    const val ANDROID_6 = Build.VERSION_CODES.M

    /**
     * Android 7.0（API 24）
     */
    const val ANDROID_7 = Build.VERSION_CODES.N

    /**
     * Android 7.1（API 25）
     */
    const val ANDROID_7_1 = Build.VERSION_CODES.N_MR1

    /**
     * Android 8.0（API 26）
     */
    const val ANDROID_8 = Build.VERSION_CODES.O

    /**
     * Android 8.1（API 27）
     */
    const val ANDROID_8_1 = Build.VERSION_CODES.O_MR1

    /**
     * Android 9（API 28）
     */
    const val ANDROID_9 = Build.VERSION_CODES.P

    /**
     * Android 10（API 29）
     */
    const val ANDROID_10 = Build.VERSION_CODES.Q

    /**
     * Android 11（API 30）
     */
    const val ANDROID_11 = Build.VERSION_CODES.R

    /**
     * Android 12（API 31）
     */
    const val ANDROID_12 = Build.VERSION_CODES.S

    /**
     * Android 12L（API 32）
     */
    const val ANDROID_12_1 = Build.VERSION_CODES.S_V2

    /**
     * Android 13（API 33）
     */
    const val ANDROID_13 = Build.VERSION_CODES.TIRAMISU

    /**
     * Android 14（API 34）
     */
    const val ANDROID_14 = Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    /**
     * Android 15（API 35）
     */
    const val ANDROID_15 = Build.VERSION_CODES.VANILLA_ICE_CREAM

    /**
     * Android 16（API 36）
     */
    const val ANDROID_16 = Build.VERSION_CODES.BAKLAVA

    /**
     * 当前设备的 API 等级
     */
    @JvmStatic
    val current: Int get() = Build.VERSION.SDK_INT

    /**
     * 判断当前系统是否 >= 指定 API
     *
     * @param api 目标 API 等级
     * @return 是否满足版本要求
     */
    @ChecksSdkIntAtLeast(parameter = 0)
    @JvmStatic
    fun isAtLeast(api: Int): Boolean {
        return current >= api
    }

    /** Android 4.4 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_4_4)
    @JvmStatic
    fun isAtLeastAndroid4_4() = isAtLeast(ANDROID_4_4)

    /** Android 4.4W 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_4_4W)
    @JvmStatic
    fun isAtLeastAndroid4_4W() = isAtLeast(ANDROID_4_4W)

    /** Android 5.0 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_5)
    @JvmStatic
    fun isAtLeastAndroid5() = isAtLeast(ANDROID_5)

    /** Android 5.1 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_5_1)
    @JvmStatic
    fun isAtLeastAndroid5_1() = isAtLeast(ANDROID_5_1)

    /** Android 6.0 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_6)
    @JvmStatic
    fun isAtLeastAndroid6() = isAtLeast(ANDROID_6)

    /** Android 7.0 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_7)
    @JvmStatic
    fun isAtLeastAndroid7() = isAtLeast(ANDROID_7)

    /** Android 7.1 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_7_1)
    @JvmStatic
    fun isAtLeastAndroid7_1() = isAtLeast(ANDROID_7_1)

    /** Android 8.0 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_8)
    @JvmStatic
    fun isAtLeastAndroid8() = isAtLeast(ANDROID_8)

    /** Android 8.1 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_8_1)
    @JvmStatic
    fun isAtLeastAndroid8_1() = isAtLeast(ANDROID_8_1)

    /** Android 9 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_9)
    @JvmStatic
    fun isAtLeastAndroid9() = isAtLeast(ANDROID_9)

    /** Android 10 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_10)
    @JvmStatic
    fun isAtLeastAndroid10() = isAtLeast(ANDROID_10)

    /** Android 11 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_11)
    @JvmStatic
    fun isAtLeastAndroid11() = isAtLeast(ANDROID_11)

    /** Android 12 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_12)
    @JvmStatic
    fun isAtLeastAndroid12() = isAtLeast(ANDROID_12)

    /** Android 12L 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_12_1)
    @JvmStatic
    fun isAtLeastAndroid12L() = isAtLeast(ANDROID_12_1)

    /** Android 13 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_13)
    @JvmStatic
    fun isAtLeastAndroid13() = isAtLeast(ANDROID_13)

    /** Android 14 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_14)
    @JvmStatic
    fun isAtLeastAndroid14() = isAtLeast(ANDROID_14)

    /** Android 15 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_15)
    @JvmStatic
    fun isAtLeastAndroid15() = isAtLeast(ANDROID_15)

    /** Android 16 及以上 */
    @ChecksSdkIntAtLeast(api = ANDROID_16)
    @JvmStatic
    fun isAtLeastAndroid16() = isAtLeast(ANDROID_16)
}