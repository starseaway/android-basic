# Android 项目基础库

<div align="center">
  <img src="android-basic-logo.svg" width="500" alt="android-basic-logo">
</div>

![Version](https://img.shields.io/badge/version-1.6.4-blue)
![License](https://img.shields.io/badge/license-Apache%202.0-green)
![API](https://img.shields.io/badge/API-19%2B-brightgreen)

## 一、模块简介

这是一个精简的 Android 基础模块库，不包含具体业务逻辑，主要对常用的基类、管理类、工具类以及开发能力做了一层统一封装。

目的就是帮你省去项目初始化时那些重复的搭建工作，让结构一开始就比较清晰、规范。直接在这个基础上扩展业务，也能更快进入开发状态。

---

## 特性

- 统一的 Activity / Fragment 等核心组件的基类体系
- 简单好用的 Fragment 多页面切换方案
- 可控的返回键事件分发机制
- 全局 Activity 栈管理
- 常用能力接口化（Action）
- 一些常用的 Kotlin 扩展函数，日常开发更轻松
- 内置一套实用的 RecyclerView 适配器，支持多布局 / 多类型

--- 

## 二、SDK 适用范围

- Android SDK 版本：Min SDK 19（Android 4.4）及以上

--- 

## 三、集成方式

### 1. 根据 Gradle 版本或项目配置自行选择在合适的位置添加仓库地址
```groovy
maven {
    // jitpack仓库
    url 'https://jitpack.io' 
}
```

### 2. 在 `build.gradle` (Module 级) 中添加依赖：
```groovy
dependencies {
    implementation 'com.github.starseaway:android-basic:1.6.4'
}
```

```kotlin
dependencies {
    implementation("com.github.starseaway:android-basic:1.6.4")
}
```

### 3. 初始化模块
```kotlin
class AppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化 AppContext
        AppContext.init(this)
        // 可选：初始化 Activity 栈管理
        ActivityManager.instance.init(this)
    }
}
```

---

## 四、核心能力说明

这里不再逐个展开所有类的示例教程，而是从“能力维度”做一个整体说明。

### 1. 基类能力

框架内提供了一套统一的 BaseActivity / BaseFragment 等体系，主要特性：
- 内置 ViewBinding / DataBinding 支持
- 生命周期结构统一（initViews / initListeners 等）
- 集成 HandlerThread 异步消息处理能力
- 支持消息回调与 UI 更新解耦

适用于页面结构标准化，以及轻量级异步任务处理场景。

### 2. Fragment 切换（多 Tab / 多页面）

提供 [FragmentSwitchHelper.java](library/src/main/java/com/xinyi/androidbasic/utils/FragmentSwitchHelper.java)，用于管理 Fragment 切换：
- 基于 show/hide，避免重复创建
- 支持状态恢复
- 统一管理 Fragment 实例

适用于底部 Tab、多页面容器切换场景。

### 3. Activity 栈管理

[ActivityManager.kt](library/src/main/java/com/xinyi/androidbasic/app/ActivityManager.kt) 提供全局 Activity 管控能力：
- 获取当前栈顶/前台 Activity
- 一键关闭单个/全部页面
- 前后台状态判断
- 生命周期监听与回调能力

### 4. 返回键事件分发

封装系统返回键处理逻辑
- 支持事件拦截
- 支持多级分发
- 可结合生命周期自动管理

适用于主页禁止返回至桌面，以及复杂页面返回控制（如嵌套 Fragment、弹层优先级等）

### 5. Action 行为接口

通过接口的方式为基类注入通用能力，例如：
- 键盘控制（显示 / 隐藏）
- Intent / Bundle 获取
- Activity 操作封装
- 等常用能力

本质是：把 “常用动作” 从业务代码中剥离出来

### 6. Kotlin 扩展函数

提供一批高频扩展：
- Context 快速获取资源（color / drawable 等）
- 快速启动 Activity
- 系统服务快捷访问
- 等常用扩展

目的：减少样板代码，提高可读性

## 五、快速上手（适配器示例）

适配器部分是这个模块里比较实用的一块，这里保留一个完整示例。

### 1.单布局 ViewBinding 适配器

```kotlin
/**
 * 测试 ViewBinding 适配器, 单布局
 *
 * @author 新一
 * @date 2025/4/19 16:04
 */
class TestViewBindingAdapter(context: Context?) : BaseViewBindingAdapter<String, TestItemBinding>(context) {

    override fun initLayoutId(): Int {
        return R.layout.test_item
    }

    override fun onBindViewDataBinding(binding: TestItemBinding, item: String, position: Int) {

    }
}
```

### 2. 单 ViewHolder 多布局

```kotlin

/**
 * 测试单类型 ViewHolder，但多布局的适配器，不需要 Binding 的可以用 BaseMultiLayoutAdapter 适配器
 *
 * @author 新一
 * @date 2025/6/4 15:30
 */
class TestMultiLayoutBindingAdapter(context: Context?) : BaseMultiLayoutBindingAdapter<String>(context) {

    companion object {
        const val TYPE_1 = 0
        const val TYPE_2 = 1
        const val TYPE_3 = 2
    }

    override fun getViewTypeForPosition(position: Int, item: String): Int {
        return when (item) {
            "1" -> TYPE_1
            "2" -> TYPE_2
            else -> TYPE_3
        }
    }

    override fun onRegisterViewBindingType() {
        registerViewType<TestItem1Binding>(TYPE_1, R.layout.test_item_1) { binding, item, position ->

        }
        registerViewType(TYPE_2, LambdaViewBindingItem<String, TestItem2Binding>(
            layoutId = R.layout.test_item_2,
            onBindViewData = { binding, item, position ->

            }
        ))
        registerViewType(TYPE_3, LambdaViewBindingItem<String, TestItem3Binding>(
            layoutId = R.layout.test_item_3,
            onBindViewData = { binding, item, position ->

            }
        ))
    }
}
```

### 3. 多 ViewHolder 模式（高自由度）

```kotlin

/**
 * 测试多个 ViewHolder 对象的示例
 *
 * @author 新一
 * @date 2025/6/5 10:19
 */
class TestMultiHolderAdapter(context: Context?) : BaseAdapter<String, RecyclerView.ViewHolder>(context) {

    companion object {
        const val TYPE_1 = 0
        const val TYPE_2 = 1
        const val TYPE_3 = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            "1" -> TYPE_1
            "2" -> TYPE_2
            else -> TYPE_3
        }
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_1 -> ViewBindingViewHolder<TestItem1Binding>(R.layout.test_item_1, parent)
            TYPE_2 -> ViewBindingViewHolder<TestItem2Binding>(R.layout.test_item_2, parent)
            TYPE_3 -> TestViewBindingViewHolder(parent)
            else -> ViewBindingViewHolder<TestItem3Binding>(R.layout.test_item_3, parent)
        }
    }

    override fun onBindViewData(holder: RecyclerView.ViewHolder, item: String, position: Int) {
        when (holder) {
            is ViewBindingViewHolder<*> -> {
                when (holder.binding) {
                    is TestItem1Binding -> {

                    }
                    is TestItem2Binding -> {

                    }
                    is TestItem3Binding -> {

                    }
                }
            }
            is TestViewBindingViewHolder -> holder.onBindViewData(item, position)
        }
    }
}
```

> 相比传统 Adapter 写法，这些就已经大幅减少模板代码
 
---

## 六、版本变更记录

### V1.6.4 (2026-04-10)
- 升级 kotlin 版本

### V1.6.2 (2026-03-31)
- build: 修改 agp 构建版本

### V1.6.0 (2026-03-25)
- 此版本正式在 GitHub 开源发布
- 新增 Fragment 切换工具类，简化页面切换逻辑
- 新增系统返回键事件监听与处理工具
- 新增 Android API 等级工具类
- 补充常用扩展函数

### V1.5.3 (2025-12-11)
- 修复多 Layout 布局的列表适配器，条目注册的条目时机有问题的bug

### V1.5.2 (2025-10-10)
- 封装局部刷新与 `payload` 相关的一些方法

### V1.5.1 (2025-09-05)
- 修复popup在kotlin的情况下，构造传入参数后在onViewCreated里未获取到值的bug

### V1.5.0 (2025-06-04)
- 重构旧版适配器，修复binding错乱的bug。
- 新增了多布局的适配器，支持单ViewHolder类型多布局，和多ViewHolder类型的使用

### V1.4.0 (2025-06-03)
- 新增Service系列基类封装
- 优化软键盘操作动作接口类

### V1.3.1 (2025-05-22)
- 更换BaseViewBindingAdapter里默认的ViewHolder的创建实现

### V1.3.0 (2025-04-21)
- 新增一个超级好用的RecyclerView的Adapter。但是保留了旧版的BaseAdapter，以便于向下兼容。

### V1.2.4 (2025-04-17)
- 添加了一个键盘意图的接口类

### V1.2.2 (2025-04-15)
- BaseActivity基类增加对initLayoutId的非零判断

### V1.2.1 (2025-04-9)
- 兼容性更新，降低对kotlin插件版本的要求，支持kotlin 1.6.10及以上版本。

### V1.1.3 (2025-04-9)
- 降级dataBinding库的版本，增强对低版本Android的兼容性。
- 降级material库的版本，增强对低版本Android的兼容性。
- 降级core-ktx库的版本，增强对低版本Android的兼容性。

### V1.1.1 (2025-04-09)
- 更新了任务调度模块的版本，修复了任务调度模块的一个bug。

### V1.1.0 (2025-03-31)
- 剔除模块自带的HandlerThread，改为使用任务调度模块提供的HandlerThread。

### V1.0.0 (2025-03-29)
- 初始化发布，包含基础的Activity、Fragment、Adapter等基类，以及ViewDataBinding的封装类等。
