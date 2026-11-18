package com.xinyi.app

import android.annotation.SuppressLint
import android.os.Bundle
import com.xinyi.androidbasic.base.activity.BaseViewBindingActivity
import com.xinyi.app.databinding.ActivityStoreHomeBinding

/**
 * store 渠道入口：验证 flavor 源码集 + 不同 applicationId 下的 VB / 自定义控件
 *
 * @author 杨耿雷
 * @date 2026/11/18
 */
class StoreHomeActivity : BaseViewBindingActivity<ActivityStoreHomeBinding>() {

    override fun initLayoutId(): Int = R.layout.activity_store_home

    @SuppressLint("SetTextI18n")
    override fun onBindingCreated(binding: ActivityStoreHomeBinding) {
        binding.tvChannel.text = "${getString(R.string.flavor_label)} · $packageName"
        binding.storeHighlightCard.setHighlight("store · BaseViewBindingConstraintLayout")
    }

    override fun initViews() = Unit

    override fun initListeners() = Unit

    override fun initParams(savedInstanceState: Bundle?) = Unit
}