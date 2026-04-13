package com.xinyi.androidbasic.extension

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * FragmentActivity 扩展，用于 Fragment 的替换、添加、移除等操作
 *
 * @author 新一
 * @date 2025/3/4 11:35
 */
object FragmentActivityExtension {
    
    /**
     * 替换碎片
     *
     * @param frameId 容器id
     * @param fragment 碎片
     */
    @JvmStatic
    fun FragmentActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
        supportFragmentManager.transact {
            replace(frameId, fragment)
        }
    }

    /**
     * 替换碎片
     *
     * @param fragment 碎片
     * @param frameId 容器id
     * @param enter 进入动画
     * @param exit 出去动画
     */
    @JvmStatic
    fun FragmentActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int, enter: Int, exit: Int) {
        supportFragmentManager.transact {
            setCustomAnimations(enter, exit)
            replace(frameId, fragment)
        }
    }

    /**
     * 添加碎片
     *
     * @param fragment 碎片
     * @param tag 标签
     */
    @JvmStatic
    fun FragmentActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
        supportFragmentManager.transact {
            add(fragment, tag)
        }
    }

    /**
     * 添加碎片
     *
     * @param fragment 碎片
     * @param tag 标签
     * @param frameId 容器id
     */
    @JvmStatic
    fun FragmentActivity.addFragmentToActivity(fragment: Fragment, tag: String, @IdRes frameId: Int) {
        supportFragmentManager.transact {
            add(frameId, fragment, tag)
        }
    }

    /**
     * 移除碎片
     *
     * @param fragment 碎片
     */
    @JvmStatic
    fun FragmentActivity.removeFragmentInActivity(fragment: Fragment) {
        supportFragmentManager.transact {
            remove(fragment)
        }
    }

    /**
     * 显示和隐藏碎片
     *
     * @param show 显示的碎片
     * @param hides 需要隐藏的碎片
     */
    @JvmStatic
    fun FragmentActivity.showAndHideFragmentInFragment(show: Fragment, vararg hides: Fragment?) {
        supportFragmentManager.transact {
            hides.forEach { hideFragment -> hideFragment?.let { hide(it) } }
            show(show)
        }
    }

    /**
     * 碎片事务提交
     */
    @JvmStatic
    inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            action()
        }.commit()
    }   
}