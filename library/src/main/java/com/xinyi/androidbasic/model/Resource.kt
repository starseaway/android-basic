package com.xinyi.androidbasic.model

/**
 * 资源类用于表示数据加载的状态。
 * @param T 表示成功状态时返回的数据类型。
 *
 * 该密封类具有三个子类：
 *
 * - [Loading] 表示数据正在加载中
 * - [Success] 包含加载成功的数据
 * - [Fail] 包含加载失败时的异常信息
 *
 * @author 新一
 * @date 2024/9/24 21:10
 */
sealed class Resource<out T> {

    /**
     * 表示正在加载状态
     */
    data object Loading : Resource<Nothing>()

    /**
     * 表示加载成功，携带成功返回的值
     *
     * @param value 成功加载的数据
     */
    data class Success<out T>(val value: T) : Resource<T>()

    /**
     * 表示加载失败，携带异常信息
     *
     * @param ex 加载过程中发生的异常
     */
    data class Fail(val ex: Throwable) : Resource<Nothing>()
}