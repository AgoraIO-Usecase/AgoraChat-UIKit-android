# EaseIMKit 使用指南

// 前提条件
// 集成 Agora Chat UIKit
// 快速搭建
    // 创建会话页面
    // 创建聊天页面
// 高级定制

## 导入EaseIMKit
### 开发环境要求
- Android Studio 3.2以上
- Gradle4.6以上
- targetVersion 26以上
- Android SDK API 19以上
- Java JDK 1.8以上

### 集成说明
EaseIMKit支持Gradle接入和 Module源码集成

#### Gradle接入集成

#### Module源码集成

### 权限
### 初始化 Agora Chat SDK
使用 Agora Chat SDK之前，需要初始化，可以使用 EaseUIKit 提供的初始化方法 EaseUIKit#init 。
示例代码：
```java
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ChatOptions options = new ChatOptions();
        options.setAppKey("Your AppKey");
        EaseUIKit.getInstance().init(this, options);
    }

}
```
注意：如果您选择在 AndroidManifest.xml设置 appKey，可以不在ChatOptions中配置。

### 延迟初始化 Agora Chat SDK
根据场景不同，可以选择延迟初始化 Agora Chat SDK。

## 快速搭建
### 快速创建会话列表页面
