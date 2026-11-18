package com.xinyi.app

import android.annotation.SuppressLint
import android.os.Bundle
import com.xinyi.androidbasic.base.activity.BaseViewBindingActivity
import com.xinyi.app.databinding.ActivityHomeBinding

/**
 * beta 渠道入口：验证 main 源码集 + BaseViewBindingActivity / 自定义控件
 *
 * @author 杨耿雷
 * @date 2026/11/18 13:39
 */
class HomeActivity : BaseViewBindingActivity<ActivityHomeBinding>() {

    override fun initLayoutId(): Int = R.layout.activity_home

    @SuppressLint("SetTextI18n")
    override fun onBindingCreated(binding: ActivityHomeBinding) {
        binding.tvChannel.text = "${getString(R.string.flavor_label)} · $packageName"
        binding.mainHighlightCard.setHighlight("main · BaseViewBindingConstraintLayout")
    }

    override fun initViews() = Unit

    override fun initListeners() = Unit

    override fun initParams(savedInstanceState: Bundle?) = Unit
}