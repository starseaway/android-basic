package com.xinyi.androidbasic.extensions

/**
 * Any 扩展函数集合
 *
 * @author 新一
 * @date 2026/3/25 14:17
 */
object AnyExtension  {

    /**
     * 获取一个对象的独一无二的字符串标记（运行期唯一）
     */
    @JvmStatic
    fun Any.getUniqueTag(): String {
        // 对象所在的包名 @ 对象的内存地址
        return "${this.javaClass.name}@${Integer.toHexString(System.identityHashCode(this))}"
    }

    /**
     * 获取对象的调试信息
     *
     * 输出格式：ClassName@hash: toString()
     */
    @JvmStatic
    fun Any?.debugInfo(): String {
        if (this == null) return "null"
        return buildString {
            append(this@debugInfo.javaClass.simpleName)
            append("@")
            append(Integer.toHexString(System.identityHashCode(this@debugInfo)))
            append(": ")
            append(this@debugInfo.toString())
        }
    }

    /**
     * 安全执行代码块，捕获异常并返回 null
     *
     * @param block 执行逻辑
     * @return 成功返回结果，失败返回 null
     */
    @JvmStatic
    inline fun <T, R> T.runSafely(block: (T) -> R): R? {
        return try {
            block(this)
        } catch (throwable: Throwable) {
            throwable.printStackTrace(System.err)
            null
        }
    }

    /**
     * 执行操作并保证 finally 一定执行
     *
     * @param block 主逻辑
     * @param finallyBlock 收尾逻辑
     */
    @JvmStatic
    inline fun <T, R> T.runWithFinally(block: (T) -> R, finallyBlock: () -> Unit): R {
        return try {
            block(this)
        } finally {
            finallyBlock()
        }
    }

    /**
     * 判断对象是否为指定类型
     */
    @JvmStatic
    inline fun <reified T> Any?.isType(): Boolean = this is T

    /**
     * 当对象是指定类型时执行逻辑
     *
     * @param block 类型匹配后的操作
     */
    @JvmStatic
    inline fun <reified T> Any?.doIfInstance(block: (T) -> Unit) {
        if (this is T) block(this)
    }

    /**
     * 安全转换类型，失败返回默认值
     *
     * @param default 默认值
     */
    @JvmStatic
    inline fun <reified T> Any?.castOr(default: T): T {
        return this as? T ?: default
    }

    /**
     * 断言对象不为 null，否则抛出异常
     *
     * @param message 错误信息
     * @throws IllegalStateException 当对象为 null 时
     */
    @JvmStatic
    fun <T : Any> T?.requireNotNull(message: String = "Value should not be null"): T {
        return this ?: throw IllegalStateException(message)
    }

    /**
     * 条件执行操作，并返回自身（支持链式调用）
     *
     * @param condition 条件
     * @param block 执行逻辑
     */
    @JvmStatic
    inline fun <T> T.doIf(condition: Boolean, block: (T) -> Unit): T {
        if (condition) block(this)
        return this
    }

    /**
     * 执行操作并返回当前对象
     *
     * 类似 also，但语义更偏向“副作用”
     */
    @JvmStatic
    inline fun <T> T.tap(block: (T) -> Unit): T {
        block(this)
        return this
    }

    /**
     * 根据条件决定是否替换当前对象
     *
     * @param condition 条件
     * @param newValue 新值生成逻辑
     */
    @JvmStatic
    inline fun <T> T.replaceIf(condition: Boolean, newValue: () -> T): T {
        return if (condition) newValue() else this
    }

    /**
     * 将对象转换为字符串
     *
     * @param nullValue 当对象为 null 时返回值
     */
    @JvmStatic
    fun Any?.toStringOr(nullValue: String = ""): String {
        return this?.toString() ?: nullValue
    }

    /**
     * 安全比较两个对象是否相等
     */
    @JvmStatic
    fun Any?.isEquals(other: Any?): Boolean {
        return this == other
    }
}