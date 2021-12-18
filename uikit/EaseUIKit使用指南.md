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
```xml
<!-- IM SDK required start -->
<!-- 允许程序振动 -->
<uses-permission android:name="android.permission.VIBRATE" />
<!-- 访问网络权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- 麦克风权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<!-- 获取运营商信息，用于支持提供运营商信息相关的接口-->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<!-- 写入扩展存储权限-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<!-- 这个权限用于访问GPS定位(用于定位消息，如果不用定位相关可以移除) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<!-- api 21后被标记为deprecated -->
<uses-permission android:name="android.permission.GET_TASKS" />
<!-- 用于访问wifi网络信息-->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<!-- 用于获取wifi的获取权限 -->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<!-- 允许程序在手机屏幕关闭后后台进程仍然运行 -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
<!-- 允许程序修改声音设置信息 -->
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<!-- 允许程序访问电话状态 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!-- 允许程序开机自动运行 -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<!-- IM SDK required end -->
```
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
        ... // Other options you want to set
        EaseUIKit.getInstance().init(this, options);
    }

}
```
注意：如果您选择在 AndroidManifest.xml设置 appKey ，可以不在ChatOptions中配置，即如下：
```xml
<meta-data android:name="EASEMOB_APPKEY"  android:value="Your AppKey" />
```

## 快速搭建
### 快速创建聊天页面
EaseIMKit 提供了 EaseChatFragment ，添加到 Activity 中并传递相应的参数即可使用。
示例如下：
```java
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        // conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
        // 1: single chat; 2: group chat; 3: chat room
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fl_fragment, 
                                            new EaseChatFragment.Builder(conversationID, 1)
                                                                .build())
                                   .commit();
    }
}
```
运行后，如下图：
// todo：添加图片

### 快速创建聊天页面
EaseIMKit 提供了 EaseConversationListFragment ，添加到 Activity 中即可使用。
示例如下：
```java
public class ConversationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fl_fragment,
                                            new EaseConversationListFragment.Builder()
                                                                .build())
                                   .commit();
    }
}
```
注意：如果接收到新消息，删除会话联系人等事件，需要调用 EaseConversationListFragment#refreshList 方法刷新列表。
运行后，如下图：
// todo：添加图片

## 高级定制
### 聊天页面相关
#### 通过 EaseChatFragment.Builder 自定义设置
EaseChatFragment 提供了 Builder 构建方式，方便开发者进行一些自定义设置，目前提供的设置项如下：
```java
// conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
// 1: single chat; 2: group chat; 3: chat room
new EaseChatFragment.Builder(conversationID, SINGLE_CHAT)
        .useHeader(true)
        .setHeaderTitle("title")
        .enableHeaderPressBack(true)
        .setHeaderBackPressListener(onBackPressListener)
        .getHistoryMessageFromServerOrLocal(false)
        .setOnChatExtendMenuItemClickListener(onChatExtendMenuItemClickListener)
        .setOnChatInputChangeListener(onChatInputChangeListener)
        .setOnMessageItemClickListener(onMessageItemClickListener)
        .setOnMessageSendCallBack(onMessageSendCallBack)
        .setOnAddMsgAttrsBeforeSendEvent(onAddMsgAttrsBeforeSendEvent)
        .setOnChatRecordTouchListener(onChatRecordTouchListener)
        .setMsgTimeTextColor(msgTimeTextColor)
        .setMsgTimeTextSize(msgTimeTextSize)
        .setReceivedMsgBubbleBackground(receivedMsgBubbleBackground)
        .setSentBubbleBackground(sentBubbleBackground)
        .showNickname(false)
        .setMessageListShowStyle(EaseChatMessageListLayout.ShowType.LEFT_RIGHT)
        .hideReceiverAvatar(false)
        .hideSenderAvatar(true)
        .setChatBackground(chatBackground)
        .setChatInputMenuStyle(EaseInputMenuStyle.All)
        .setChatInputMenuBackground(inputMenuBackground)
        .setChatInputMenuHint(inputMenuHint)
        .sendMessageByOriginalImage(true)
        .setEmptyLayout(R.layout.layout_conversation_empty)
        .setCustomAdapter(customAdapter)
        .setCustomFragment(myChatFragment)
        .build();
```
#### 通过继承 EaseChatFragment 进行自定义设置

### 会话列表页面相关
#### 通过 EaseConversationListFragment.Builder 自定义设置
EaseConversationListFragment 提供了 Builder 构建方式，方便开发者进行一些自定义设置，目前提供的设置项如下：
```java
new EaseConversationListFragment.Builder()
        .useHeader(true)
        .setHeaderTitle("title")
        .enableHeaderPressBack(true)
        .setHeaderBackPressListener(onBackPressListener)
        .hideUnread(false)
        .setUnreadStyle(EaseConversationSetStyle.UnreadStyle.NUM)
        .setUnreadPosition(EaseConversationSetStyle.UnreadDotPosition.RIGHT)
        .setItemClickListener(onItemClickListener)
        .setConversationChangeListener(conversationChangeListener)
        .setEmptyLayout(R.layout.layout_conversation_empty)
        .setCustomAdapter(customAdapter)
        .setCustomFragment(myConversationListFragment)
        .build();
```