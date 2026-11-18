package com.xinyi.androidbasic.base.binding

import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ViewBinding 泛型类型解析器
 *
 * 沿宿主类继承链查找具体的 ViewBinding 泛型参数。
 *
 * @author 杨耿雷
 * @date 2026/11/18 11:12
 */
object ViewBindingTypeResolver {

    /**
     * 解析宿主实例对应的 ViewBinding Class
     *
     * @param owner 声明了具体 Binding 泛型的宿主实例
     * @return Binding Class
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> resolve(owner: Any): Class<VB> {
        var current: Class<*>? = owner.javaClass
        while (current != null) {
            val superType = current.genericSuperclass
            if (superType is ParameterizedType) {
                for (argument in superType.actualTypeArguments) {
                    val argumentClass = unwrapToClass(argument) ?: continue
                    if (ViewBinding::class.java.isAssignableFrom(argumentClass) &&
                        argumentClass != ViewBinding::class.java
                    ) {
                        return argumentClass as Class<VB>
                    }
                }
            }
            current = current.superclass
        }

        throw IllegalStateException(
            "无法从 ${owner.javaClass.name} 解析 ViewBinding 泛型。" +
                    "请在具体子类上写明 Binding 类型，或重写 inflateBinding 显式 inflate。",
        )
    }

    /**
     * 将泛型 Type 解包为 Class
     *
     * @param type 泛型 Type
     */
    private fun unwrapToClass(type: Type): Class<*>? {
        return when (type) {
            is Class<*> -> type
            is ParameterizedType -> type.rawType as? Class<*>
            else -> null
        }
    }
}