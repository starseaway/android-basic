package com.xinyi.androidbasic.utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment 切换工具类
 *
 * <p> 使用 show/hide 方式管理 Fragment 切换。</p>
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
     * Fragment Tag 前缀
     */
    private static final String FRAGMENT_TAG_PREFIX = "FragmentSwitchHelper_";

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
     * @param containerId Fragment 容器 ID
     * @param fragmentFactory Fragment 创建工厂
     */
    public FragmentSwitchHelper(FragmentManager fragmentManager, int containerId, FragmentFactory fragmentFactory) {
        this.mFragmentManager = fragmentManager;
        this.mContainerId = containerId;
        this.mFragmentFactory = fragmentFactory;
    }

    /**
     * 初始化 Fragment
     *
     * <p>
     *   优先恢复上次选中的 Tab，否则显示指定的默认 Tab。
     *   初始化时同步提交事务，确保首屏显示时 Fragment 已完成添加。
     * </p>
     *
     * @param savedInstanceState 存储上次活动状态的 Bundle
     * @param defaultTab 默认显示的 Tab
     */
    public void initialize(Bundle savedInstanceState, int defaultTab) {
        int tab = savedInstanceState == null
                ? defaultTab
                : savedInstanceState.getInt(SELECTED_TAB, defaultTab);

        switchFragment(tab, true);
    }

    /**
     * 切换到指定 Fragment
     *
     * @param itemId 被选中的 Tab ID
     */
    public void switchFragment(int itemId) {
        switchFragment(itemId, false);
    }

    /**
     * 切换到指定 Fragment
     *
     * @param itemId 被选中的 Tab ID
     * @param commitNow 是否同步提交事务
     */
    private void switchFragment(int itemId, boolean commitNow) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        Fragment targetFragment = obtainFragment(itemId, transaction);

        hideOtherFragments(transaction, targetFragment);

        if (targetFragment.isAdded()) {
            transaction.show(targetFragment);
        }

        mCurrentFragment = targetFragment;

        if (commitNow) {
            transaction.commitNow();
        } else {
            transaction.commit();
        }
    }

    /**
     * 预加载指定的 Fragment。
     *
     * <p>
     *   Fragment 会提前创建并加入 FragmentManager，同时保持隐藏状态，不影响当前显示的 Fragment。
     *   后续切换时可直接显示，避免首次切换时产生额外的创建开销。
     * </p>
     *
     * @param itemIds 需要预加载的 Tab ID
     */
    public void preload(int... itemIds) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        boolean changed = false;

        for (int itemId : itemIds) {
            Fragment fragment = mFragmentCache.get(itemId);

            if (fragment == null) {
                String tag = getFragmentTag(itemId);
                fragment = mFragmentManager.findFragmentByTag(tag);

                if (fragment == null) {
                    fragment = mFragmentFactory.createFragment(itemId);
                    transaction.add(mContainerId, fragment, tag);

                    // Fragment 尚未真正加入 FragmentManager，
                    // 但可以在同一事务中直接设置为隐藏状态。
                    transaction.hide(fragment);

                    changed = true;
                } else if (!fragment.isHidden()) {
                    transaction.hide(fragment);
                    changed = true;
                }

                mFragmentCache.put(itemId, fragment);
            } else if (fragment != mCurrentFragment && fragment.isAdded() && !fragment.isHidden()) {
                transaction.hide(fragment);
                changed = true;
            }
        }

        if (changed) {
            transaction.commit();
        }
    }

    /**
     * 获取指定 Tab 对应的 Fragment
     *
     * <p>
     *   优先从缓存获取，其次从 FragmentManager 恢复，
     *   最后通过工厂创建新的 Fragment。
     * </p>
     */
    private Fragment obtainFragment(int itemId, FragmentTransaction transaction) {
        Fragment fragment = mFragmentCache.get(itemId);

        if (fragment == null) {
            String tag = getFragmentTag(itemId);
            fragment = mFragmentManager.findFragmentByTag(tag);

            if (fragment == null) {
                fragment = mFragmentFactory.createFragment(itemId);
                transaction.add(mContainerId, fragment, tag);
            }

            mFragmentCache.put(itemId, fragment);
        }

        return fragment;
    }

    /**
     * 隐藏除目标 Fragment 外的其它 Fragment
     */
    private void hideOtherFragments(FragmentTransaction transaction, Fragment targetFragment) {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (isManagedFragment(fragment) && fragment != targetFragment) {
                transaction.hide(fragment);
            }
        }
    }

    /**
     * 判断 Fragment 是否由当前工具类管理
     */
    private boolean isManagedFragment(Fragment fragment) {
        String tag = fragment.getTag();
        return tag != null && tag.startsWith(FRAGMENT_TAG_PREFIX);
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
     */
    private int getCurrentTabId() {
        for (Map.Entry<Integer, Fragment> entry : mFragmentCache.entrySet()) {
            if (entry.getValue() == mCurrentFragment) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * 根据 Tab ID 生成对应的 Fragment Tag
     *
     * @param itemId Tab ID
     */
    private String getFragmentTag(int itemId) {
        return FRAGMENT_TAG_PREFIX + itemId;
    }

    /**
     * Fragment 创建工厂
     */
    public interface FragmentFactory {

        /**
         * 根据 Tab ID 创建对应的 Fragment
         *
         * @param itemId 选中的 Tab ID
         */
        Fragment createFragment(int itemId);
    }
}