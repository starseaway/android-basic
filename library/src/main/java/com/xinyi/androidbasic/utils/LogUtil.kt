package com.xinyi.androidbasic.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * 使用[LogUtil.init]方法进行初始化，设置是否显示日志。
 * 使用[LogUtil.d]方法进行调试，仅输出debug级别的调试信息。可以通过Logcat标签进行过滤选择输出的信息。
 * 使用[LogUtil.e]方法进行调试，输出error级别的错误信息，以红色显示。需要认真分析并查看栈信息。
 * 使用[LogUtil.v]方法进行调试，输出任何消息，即verbose级别。平时使用Log.v("", "")。
 * 使用[LogUtil.i]方法进行调试，输出提示性的信息，即information级别。不会输出Log.v和Log.d的信息，但会显示i、w和e级别的信息。
 * 使用[LogUtil.w]方法进行调试，输出警告信息，即warning级别。需要注意优化Android代码，并会输出Log.e级别的信息。
 * 使用[LogUtil.wtf]方法进行调试，(what the fuck?)，形容正常情况下永远不会发生bug？
 * 使用[LogUtil.json]方法进行调试，打印json数据。
 *
 * @author 新一
 * @date 2024/9/30 13:39
 */
object LogUtil {

    // 日志等级常量
    private const val V = 0x01
    private const val D = 0x02
    private const val I = 0x03
    private const val W = 0x04
    private const val E = 0x05
    private const val WTF = 0x06
    private const val JSON = 0x07
    private const val JSON_INDENT = 4 // JSON 缩进
    private var IS_SHOW_LOG = true // 是否显示日志，默认为true
    private const val DEFAULT_MESSAGE = "execute" // 默认的消息内容

    // 默认的日志消息统一标签标签
    private var mMsgTag: String? = null

    /**
     * 默认的日志消息统一标签标签，该标签会被拼接在每条日志消息的前方，方便过滤日志
     */
    @JvmStatic
    fun setMsgTag(tag: String) {
        mMsgTag = tag
    }

    /**
     * 初始化方法，用于设置是否显示日志
     */
    @JvmStatic
    fun init(isShowLog: Boolean) {
        IS_SHOW_LOG = isShowLog
    }

    /**
     * Debug级别的日志输出方法
     *
     * Debug-level log output method
     */
    @JvmStatic
    fun d() {
        printLog(D, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun d(msg: Any?) {
        printLog(D, null, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: Any?) {
        printLog(D, tag, msg)
    }

    /**
     * Error级别的日志输出方法
     */
    @JvmStatic
    fun e() {
        printLog(E, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun e(msg: Any?) {
        printLog(E, null, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: Any?) {
        printLog(E, tag, msg)
    }

    @JvmStatic
    fun e(tag: String, msg: Any?, throwable: Throwable) {
        if (!IS_SHOW_LOG) {
            return
        }
        logMessage(E, tag, "$msg", throwable)
    }

    /**
     * Verbose级别的日志输出方法
     */
    @JvmStatic
    fun v() {
        printLog(V, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun v(msg: Any?) {
        printLog(V, null, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?) {
        printLog(V, tag, msg)
    }

    /**
     * Info级别的日志输出方法
     */
    @JvmStatic
    fun i() {
        printLog(I, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun i(msg: Any?) {
        printLog(I, null, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: Any?) {
        printLog(I, tag, msg)
    }

    /**
     * Warning级别的日志输出方法
     */
    @JvmStatic
    fun w() {
        printLog(W, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun w(msg: Any?) {
        printLog(W, null, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: Any?) {
        printLog(W, tag, msg)
    }

    /**
     * Assert级别的日志输出方法
     */
    @JvmStatic
    fun wtf() {
        printLog(WTF, null, DEFAULT_MESSAGE)
    }

    @JvmStatic
    fun wtf(msg: Any?) {
        printLog(WTF, null, msg)
    }

    @JvmStatic
    fun wtf(tag: String?, msg: Any?) {
        printLog(WTF, tag, msg)
    }

    /**
     * 输出格式化的JSON日志
     */
    @JvmStatic
    fun json(jsonFormat: String?) {
        printLog(JSON, null, jsonFormat, false)
    }

    @JvmStatic
    fun json(jsonFormat: String?, isFormatJson: Boolean) {
        printLog(JSON, null, jsonFormat, isFormatJson)
    }

    @JvmStatic
    fun json(tag: String?, jsonFormat: String?) {
        printLog(JSON, tag, jsonFormat)
    }

    @JvmStatic
    fun json(tag: String?, jsonFormat: String?, isFormatJson: Boolean) {
        printLog(JSON, tag, jsonFormat, isFormatJson)
    }

    /**
     * 打印日志的核心方法。
     *
     * @param type 日志类型
     * @param tagStr 标签
     * @param objectMsg 日志消息
     * @param isFormatJson 是否格式化 JSON 内容
     */
    private fun printLog(type: Int, tagStr: String?, objectMsg: Any?, isFormatJson: Boolean = false) {
        if (!IS_SHOW_LOG) {
            return
        }
        val pair = getStackTraceElement(tagStr)
        val tag = pair.first
        val logStr = pair.second
        val message = objectMsg?.toString() ?: "Log with null Object"

        if (type != JSON) {
            logMessage(type, tag, "$logStr => $message")
        } else {
            try {
                if (message.startsWith("{")) {
                    val jsonObject = JSONObject(message)
                    val jsonContent = if (isFormatJson) {
                        jsonObject.toString(JSON_INDENT) // 格式化 JSON 内容
                    } else {
                        jsonObject.toString()
                    }
                    printJsonContent(tag, logStr, jsonContent)
                } else if (message.startsWith("[")) {
                    val jsonArray = JSONArray(message)
                    val jsonContent = if (isFormatJson) {
                        jsonArray.toString(JSON_INDENT) // 格式化 JSON 内容
                    } else {
                        jsonArray.toString()
                    }
                    printJsonContent(tag, logStr, jsonContent)
                }
            } catch (e: JSONException) {
                logMessage(E, tag, "${e.cause?.message} \n $message")
                return
            }
        }
    }

    /**
     * 获取堆栈中类名和方法名的元素
     */
    private fun getStackTraceElement(tagStr: String?): Pair<String, String> {
        val stackTrace = Thread.currentThread().stackTrace

        // 寻找调用该方法的堆栈元素
        val callerIndex = stackTrace.indexOfLast { it.className == LogUtil::class.java.name } + 1

        // 获取标签，如果为空，则取调用位置的文件名作为标签
        val tag = tagStr ?: stackTrace[callerIndex].fileName

        // 格式化堆栈元素，获取类名、方法名和行号
        val logStr = formatStackTraceElement(stackTrace[callerIndex])
        return Pair(tag, logStr)
    }

    /**
     * 打印日志信息。
     *
     * @param type 日志类型
     * @param tag 标签
     * @param message 日志消息
     * @param throwable 异常信息
     */
    private fun logMessage(type: Int, tag: String, message: String, throwable: Throwable? = null) {
        val msg = if (mMsgTag == null) {
            message
        } else {
            "《$mMsgTag》".plus(message)
        }
        when (type) {
            V -> Log.v(tag, msg) // 输出 verbose 级别的日志
            D -> Log.d(tag, msg) // 输出 debug 级别的日志
            I -> Log.i(tag, msg) // 输出 info 级别的日志
            W -> Log.w(tag, msg) // 输出 warning 级别的日志
            E -> {  // 输出 error 级别的日志
                if (throwable == null) {
                    Log.e(tag, msg)
                } else {
                    Log.e(tag, msg, throwable)
                }
            }
            WTF -> Log.wtf(tag, msg) // 输出 assert 级别的日志
        }
    }

    /**
     * 格式化堆栈元素，返回类名、方法名和行号组成的字符串。
     *
     * @param stackTraceElement 堆栈元素
     * @return 类名、方法名和行号组成的字符串
     */
    private fun formatStackTraceElement(stackTraceElement: StackTraceElement): String {
        val className = stackTraceElement.fileName // 获取类名
        val methodName = stackTraceElement.methodName // 获取方法名
        val lineNumber = stackTraceElement.lineNumber // 获取行号

        return "($className:$lineNumber)#$methodName"
    }

    /**
     * 打印 JSON 内容。
     *
     * @param tag 标签
     * @param logStr 日志信息
     * @param jsonContent JSON 内容
     */
    private fun printJsonContent(tag: String, logStr: String, jsonContent: String) {
        if (jsonContent.isEmpty()) {
            logMessage(E, tag, "Empty or Null json content")
            return
        }

        printLine(tag, true)

        val message = "$logStr${"\n"}$jsonContent"

        // 将消息按行分割为数组
        val lines = message.split("\n").toTypedArray()

        val formattedJsonContent = StringBuilder()
        for (line in lines) {
            // 添加表格线格式的消息内容
            formattedJsonContent.append("║ ").append(line).append("\n")
        }

        // 如果消息内容超过 3200 字符
        if (formattedJsonContent.length > 3200) {
            logMessage(W, tag, "jsonContent.length = ${formattedJsonContent.length}")
            val chunkCount = formattedJsonContent.length / 3200 // 计算消息内容分块数量
            var i = 0
            while (i <= chunkCount) {
                val start = 3200 * i
                val end = minOf(3200 * (i + 1), formattedJsonContent.length)
                logMessage(W, tag, formattedJsonContent.substring(start, end)) // 按块输出消息内容
                i++
            }
        } else {
            logMessage(W, tag, formattedJsonContent.toString())
        }

        printLine(tag, false)
    }

    /**
     * 打印表格线。
     *
     * @param tag 标签
     * @param isStart 是否是起始位置
     */
    private fun printLine(tag: String, isStart: Boolean) {
        val lineSymbol = if (isStart)
            "╔═══════════════════════════════════════════════════════════════════════════════════════"
        else
            "╚═══════════════════════════════════════════════════════════════════════════════════════"
        logMessage(D, tag, lineSymbol)
    }
}