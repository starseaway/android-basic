package com.xinyi.androidbasic.base.repository

import com.xinyi.androidbasic.R
import com.xinyi.androidbasic.extension.getString
import com.xinyi.androidbasic.model.Resource
import com.xinyi.androidbasic.model.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

/**
 * Repository 基类
 *
 * 用于统一封装数据请求的执行流程，将结果转换为 [Resource] 状态流，
 * 从而在上层（ViewModel/UI）实现统一的状态消费（加载 / 成功 / 失败）。
 *
 * @author 新一
 * @date 2024/9/24 21:26
 */
abstract class BaseRepository {

    /**
     * 标准接口调用封装（适用于返回 [ResponseData] 的接口）
     *
     * 函数会将接口响应转换为 [Resource]，可根据业务字段判断成功或失败
     *
     * @param T 数据实体类型
     * @param apiCall 挂起函数，返回统一响应结构 [ResponseData]
     *
     * @return Flow<Resource<T>> 状态流：
     * - [Resource.Loading] 加载中
     * - [Resource.Success] 成功（包含 data）
     * - [Resource.Fail] 失败（包含异常信息）
     */
    fun <T> apiCallWithResponseData(apiCall: suspend () -> ResponseData<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        val response = apiCall()
        if (response.isSuccess && (response.code == 0 || response.code == 200)) {
            emit(Resource.Success(response.data))
        } else {
            emit(Resource.Fail(IOException(response.message)))
        }
    }.catch { ex ->
        emit(Resource.Fail(ex))
    }.flowOn(Dispatchers.IO) // 在 IO 线程上执行流

    /**
     * 通用数据调用封装（适用于非统一响应体的场景）
     *
     * 函数会将返回结果转换为 [Resource]，通过判空决定成功或失败
     *
     * @param T 数据类型
     * @param apiCall 挂起函数，返回可空数据（T?）
     *
     * @return Flow<Resource<T>> 状态流：
     * - [Resource.Loading] 加载中
     * - [Resource.Success] 成功（包含 data）
     * - [Resource.Fail] 失败（包含异常信息）
     */
    fun <T> apiCallWithGenericType(apiCall: suspend () -> T?): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        val response = apiCall()
        if (response != null) {
            emit(Resource.Success(response))
        } else {
            emit(Resource.Fail(IOException(R.string.read_local_null_text.getString())))
        }
    }.catch { ex ->
        emit(Resource.Fail(ex))
    }.flowOn(Dispatchers.IO) // 在 IO 线程上执行流
}