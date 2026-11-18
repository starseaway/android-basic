package com.xinyi.app

import android.app.Application
import com.xinyi.androidbasic.app.ActivityManager
import com.xinyi.androidbasic.app.AppContext

/**
 * 样例 Application：初始化基础库上下文
 *
 * @author 杨耿雷
 * @date 2026/11/18 13:32
 */
class WenAnApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        ActivityManager.instance.init(this)
    }
}