package com.xinyi.androidbasic.utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment 切换工具类，使用 show/hide 方式管理 Fragment 切换
 *
 * @author 新一
 * @date 2025/3/12 16:13
 */
public class FragmentSwitchHelper {

    /**
     * 保存选中的 Tab ID 的 Key
     */
    private static final String SELECTED_TAB = "SELECTED_TAB";

    /**
     * Fragment 管理器
     */
    private final FragmentManager mFragmentManager;

    /**
     * 当前显示的 Fragment
     */
    private Fragment mCurrentFragment;

    /**
     * Fragment 缓存
     */
    private final Map<Integer, Fragment> mFragmentCache = new HashMap<>();

    /**
     * 容器 ID
     */
    private final int mContainerId;

    /**
     * Fragment 创建工厂
     */
    private final FragmentFactory mFragmentFactory;

    /**
     * 构造方法
     *
     * @param fragmentManager Fragment 管理器
     * @param containerId 容器 ID
     * @param fragmentFactory Fragment 创建工厂
     */
    public FragmentSwitchHelper(FragmentManager fragmentManager, int containerId, FragmentFactory fragmentFactory) {
        this.mFragmentManager = fragmentManager;
        this.mContainerId = containerId;
        this.mFragmentFactory = fragmentFactory;
    }

    /**
     * 初始化，处理第一次进入时的 Fragment 显示
     *
     * @param savedInstanceState 存储上次活动状态的 bundle
     * @param defaultTab 默认显示的 Tab
     */
    public void initialize(Bundle savedInstanceState, int defaultTab) {
        if (savedInstanceState == null) {
            // 第一次加载，显示默认Fragment
            switchFragment(defaultTab);
        } else {
            // 恢复选中的Tab
            int selectedTab = savedInstanceState.getInt(SELECTED_TAB, defaultTab);
            switchFragment(selectedTab);
        }
    }

    /**
     * 切换 Fragment
     *
     * @param itemId 被选中的 Tab ID
     */
    public void switchFragment(int itemId) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        String tag = getFragmentTag(itemId);
        // 获取当前Fragment
        Fragment targetFragment = mFragmentCache.get(itemId);

        // 如果没有缓存Fragment，则创建
        if (targetFragment == null) {
            // 从 FragmentManager 中查找是否已存在该 Fragment
            targetFragment = mFragmentManager.findFragmentByTag(tag);
            if (targetFragment == null) {
                // 不存在则创建并添加
                targetFragment = mFragmentFactory.createFragment(itemId);
                transaction.add(mContainerId, targetFragment, tag);
            }
            // 保存到缓存中
            mFragmentCache.put(itemId, targetFragment);
        }

        // 遍历 FragmentManager 中所有使用了该工具的 Fragment，隐藏非目标 Fragment
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (fragment != null && fragment.getTag() != null && fragment != targetFragment) {
                transaction.hide(fragment);
            }
        }

        // 如果目标Fragment已经添加过，则直接显示它
        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        }

        // 更新当前Fragment
        mCurrentFragment = targetFragment;

        transaction.commit();
    }

    /**
     * 保存当前选中的 Tab ID
     *
     * @param outState 存储实例状态
     */
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentFragment != null) {
            outState.putInt(SELECTED_TAB, getCurrentTabId());
        }
    }

    /**
     * 获取当前显示的 Fragment 对应的 Tab ID
     *
     * @return 当前选中的Tab ID
     */
    private int getCurrentTabId() {
        for (Map.Entry<Integer, Fragment> entry : mFragmentCache.entrySet()) {
            if (entry.getValue().equals(mCurrentFragment)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * 根据 Tab ID 生成对应的 Fragment Tag
     *
     * @param itemId Tab ID
     * @return Fragment Tag
     */
    private String getFragmentTag(int itemId) {
        return "FragmentSwitchHelper_" + itemId;
    }

    /**
     * 设置 Fragment 创建工厂
     */
    public interface FragmentFactory {
        /**
         * 根据 Tab ID 创建对应的Fragment
         *
         * @param itemId 选中的 Tab ID
         * @return 创建的 Fragment
         */
        Fragment createFragment(int itemId);
    }
}