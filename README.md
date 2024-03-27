# Agora Chat UIKit Readme


## Sample project

Agora provides an open-source [AgoraChat-android](https://github.com/AgoraIO-Usecase/AgoraChat-android) sample project on GitHub. You can download the sample to try it out or view the source code.

## Import Agora Chat UIKit

### Prerequisites

- Android Studio 3.2 or later
- Gradle 4.6 or later
- targetVersion 26 or later
- Android SDK API 21 or later
- Java JDK 1.8 or later

### Integrate Agora Chat UIKit

Integrate the Agora Chat SDK into your project with Maven Central or manually download it.

#### Integrate with Maven Central

In /app/build.gradle, add the following lines to add the Maven Central dependency:

```java
buildscript {
    repositories {
        ...
        mavenCentral()
    }
}
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

In /app/build.gradle add the following lines to integrate the Agora Chat UIKit into your Android project:

```java
android {

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    ...
    // Replace X.Y.Z with the latest version of the Chat UIKit.
    implementation 'io.agora.rtc:chat-uikit:X.Y.Z'
}
```
<div class="alert note"><ul><li>For the latest uikit version, go to <a href="https://search.maven.org/search?q=a:chat-uikit">Sonatype</a>.</li></ul></div>

#### Manually download the CODE

Mannually download the [Agora Chat UIKit](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android),and follow the steps below.

```java
implementation project(':uikit')
```

#### Prevent code obfuscation

In app/proguard-rules.pro, add the following line:

```java
-keep class io.agora.** {*;}
-dontwarn  io.agora.**
```

### Add permissions for network and device access

In /app/Manifests/AndroidManifest.xml, add the following permissions after </application>:

```xml
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 <uses-permission android:name="android.permission.WAKE_LOCK"/>
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 <uses-permission android:name="android.permission.CAMERA"/>
 <uses-permission android:name="android.permission.RECORD_AUDIO"/>
```

These are the minimum permissions you need to add to start Agora Chat. You can also add other permissions according to your use case.

### Initialize the Agora Chat SDK

Initialize the SDK with EaseUIKit#init as follows:

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
## Implementation

### Create the chat UI

Agora Chat UIKit provides EaseChatFragment and you can add it in Activity as follows:

```java
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        // conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
        // chatType can be EaseChatType#SINGLE_CHAT, EaseChatType#GROUP_CHAT, EaseChatType#CHATROOM
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fl_fragment,
                                            new EaseChatFragment.Builder(conversationID, chatType)
                                                                .build())
                                   .commit();
    }
}
```

Run.

<img src="https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android/raw/dev/images/Chat.jpg" style="zoom:30%;" />

### Create the conversation UI

Agora Chat UIKit provides EaseConversationListFragment and you can add it in Activity as follows.

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

Alerts:

You need to add `EaseConversationListFragment#refreshList` to refresh the UI if there is events like new messages or contacts been deleted.

<img src="https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android/raw/dev/images/ConversationList.jpg" style="zoom:30%;" />

## Advanced implementation

### Chat UI

Chat UI is partitioned and named as follows:

[![avatar](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android/raw/dev/images/ChatMenuNote.png)](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-android/blob/dev/images/ChatMenuNote.png)

#### Customize UI with EaseChatFragment.Builder

EaseChatFragment provides Builder. You can customize UI as follows:

```java
// conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
// easeChatType: SINGLE_CHAT, GROUP_CHAT, CHATROOM
new EaseChatFragment.Builder(conversationID, easeChatType)
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

The methods in EaseChatFragment#Builder.

| Methods                                    | Description                                               |
| -------------------------------------- | ---------------------------------------------------- |
| useHeader()                            | Sets whether to use the default title bar（EaseTitleBar）. <br/> - True: Yes. <br/> - (Default) False: No.       |
| setHeaderTitle()                       | Sets the header title.                                     |
| enableHeaderPressBack()                | Sets whether to enable header pressback.<br/> - True: Yes. <br/> - (Default) False: No.                    |
| setHeaderBackPressListener(）           | Sets the event listner of header back press.                        |
| getHistoryMessageFromServerOrLocal(）   | Sets whether to get history messages from server or local.<br/> - True: Yes. <br/> - False: No.                       |
| setOnChatExtendMenuItemClickListener() | Sets the item click event listening of the extended function                            |
| setOnChatInputChangeListener()         | Sets the listner of text changes in the menu.                                |
| setOnMessageItemClickListener()        | Sets the click event listner of message entries, including the click and long press events of bubble areas and avatars. |
| setOnMessageSendCallBack()             | Sets the result callback listener of sending messages.                              |
| setOnAddMsgAttrsBeforeSendEvent()      | Sets the callback to add the message extension property before sending the message.              |
| setOnChatRecordTouchListener()         | Sets the touch event callback of the recording button.                               |
| setMsgTimeTextColor()                  | Sets the color of timeline text.                                     |
| setMsgTimeTextSize()                   | Sets the font size of timeline text.                                  |
| setReceivedMsgBubbleBackground()       | Sets the background of the receiving message bubble area                             |
| setSentBubbleBackground()              | Sets the background of the sending message bubble area                               |
| showNickname()                         | Sets whether to display nicknames. <br/> - True: Yes. <br/> - (Default) False: No.                                |
| setMessageListShowStyle()              | Sets the display style of the message list, which is divided into left_right and all_ Left two styles. |
| hideReceiverAvatar()                   | Sets not to display the receiver's Avatar, and display the receiver's Avatar by default.                             |
| hideSenderAvatar()                     | Sets not to display the sender's Avatar, and display the sender's Avatar by default.                        |
| setChatBackground()                    | Sets the background of the chat list area.                                   |
| setChatInputMenuStyle()                | Sets the menu style. See EaseInputMenuStyle for details.               |
| setChatInputMenuBackground()           | Sets the background of the menu area.                                      |
| setChatInputMenuHint()                 | Sets the prompt text of the input text box in the menu area                          |
| sendMessageByOriginalImage()           | Sets whether the image message sends the original image. <br/> - True: Yes. <br/> - (Default) False: No.             |
| setEmptyLayout()                       | Sets the blank page of the chat list.                                 |
| setCustomAdapter()                     | Sets a custom adapter, which is EaseMessageAdapter by default.            |
| setCustomFragment()                    | Sets a custom chat fragment, which needs to be inherited from EaseChatFragment.       |

#### Add custom message layout

You can use EaseMessageAdapter, EaseChatRowViewHolder, EaseChatRow to complete CustomMessageAdapter, CustomChatTypeViewViewHolder and CustomTypeChatRow. And then set CustomMessageAdapter into EaseChatFragment#Builder#setCustomAdapter.

（1）Create CustomMessageAdapter, which is inherited from EaseMessageAdapter, overrides getViewHolder and getItemNotEmptyViewType methods.

```java
public class CustomMessageAdapter extends EaseMessageAdapter {

    @Override
    public int getItemNotEmptyViewType(int position) {
        // Set your itemViewType according to the message type.
        // If you want to use the default, return super.getItemNotEmptyViewType(position).
        return CUSTOM_YOUR_MESSAGE_TYPE;
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        // Return the corresponding ViewHolder according to the returned viewType.
        // Return to the customized ViewHolder or use the default super getViewHolder(parent, viewType)
        return new CUSTOM_VIEWHOLDER();
    }
}
```

（2）Create CustomTypeChatRow, which is inherited from EaseChatRow.

```java
public class CustomTypeChatRow extends EaseChatRow {
    private TextView contentView;

    public CustomTypeChatRow(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.layout_row_received_custom_type
                : R.layout.layout_row_sent_custom_type, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        if(txtBody != null){
            contentView.setText(txtBody.getMessage());
        }
    }
}
```

（3）Create CustomChatTypeViewViewHolder, which is inherited from EaseChatRowViewHolder.

```java
public class CustomChatTypeViewViewHolder extends EaseChatRowViewHolder {

    public CustomChatTypeViewViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        // Add click event
    }

}
```

（4）Complete CustomMessageAdapter 。

```java
public class CustomMessageAdapter extends EaseMessageAdapter {
    private static final String CUSTOM_TYPE = "custom_type";
    private static final int VIEW_TYPE_MESSAGE_CUSTOM_VIEW_ME = 1000;
    private static final int VIEW_TYPE_MESSAGE_CUSTOM_VIEW_OTHER = 1001;

    @Override
    public int getItemNotEmptyViewType(int position) {
        ChatMessage chatMessage = mData.get(position);
        String type = chatMessage.getStringAttribute("type", "");
        if(TextUtils.equals(type, CUSTOM_TYPE)) {
            if(chatMessage.direct() == ChatMessage.Direct.SEND) {
                return VIEW_TYPE_MESSAGE_CUSTOM_VIEW_ME;
            }else {
                return VIEW_TYPE_MESSAGE_CUSTOM_VIEW_OTHER;
            }
        }
        return super.getItemNotEmptyViewType(position);
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_MESSAGE_CUSTOM_VIEW_ME || viewType == VIEW_TYPE_MESSAGE_CUSTOM_VIEW_OTHER) {
            return new CustomChatTypeViewViewHolder(
                    new CustomTypeChatRow(parent.getContext(), viewType == VIEW_TYPE_MESSAGE_CUSTOM_VIEW_ME),
                    listener);
        }
        return super.getViewHolder(parent, viewType);
    }
}
```

（5）Add CustomMessageAdapter in EaseChatFragment#Builder。

```java
builder.setCustomAdapter(customMessageAdapter);
```

#### Customize settings with EaseChatFragment

Create a custom CustomChatFragment, inherit from EaseChatFragment, and set it into eEaseChatFragment#Builder.

```java
builder.setCustomFragment(customChatFragment);
```

（1）List control related function settings

Get the `EaseChatMessageListLayout`:

```java
EaseChatMessageListLayout chatMessageListLayout = chatLayout.getChatMessageListLayout();
```

The following methods are provided in EaseChatMessageListLayout:

| Method                         | Description                                                 |
| ------------------------------ | ---------------------------------------------------- |
| setPresenter()                 | UIKit provides the default data implementation EaseChatMessagePresenterImpl, and you can inherit EaseChatMessagePresenter and add their own data logic. |
| getMessageAdapter()            | Gets the adapter that returns the message list. |
| addHeaderAdapter()             | Adds the adapter of the header layout of the message list. |
| addFooterAdapter()             | Adds the adapter of the tail layout of the message list. |
| removeAdapter()                | Removes the specified adapter. |
| addRVItemDecoration()          | Adds decorator of message list. |
| removeRVItemDecoration()       | Removes the decorator of the message list. |
| setAvatarDefaultSrc()          | Sets the default avatar of the entry. |
| setAvatarShapeType()           | Sets the style of the avatar, which is divided into three styles: the default ImageView style, circular and rectangular. |
| showNickname()                 | Sets whether to display the nickname of the entry.，EaseChatFragment#Builder also provides the setting method of this function.         |
| setItemSenderBackground()      | Sets the background of the sender. EaseChatFragment#Builder also provides the setting method of this function.  |
| setItemReceiverBackground()    | Sets the background of the receiver. EaseChatFragment#Builder also provides the setting method of this function.           |
| setItemTextSize()              | Sets the font size of text messages.                                                |
| setItemTextColor()             | Sets the font color of text messages.                                                |
| setTimeTextSize()              | Sets the font size of timeline text. EaseChatFragment#Builder also provides the setting method of this function.      |
| setTimeTextColor()             | Sets the color of timeline text. EaseChatFragment#Builder also provides the setting method of this function.       |
| setTimeBackground()            | Sets the background of the timeline.                                                   |
| setItemShowType()              | Sets the display style of the message list. EaseChatFragment#Builder also provides the setting method of this function.     |
| hideChatReceiveAvatar()        | Sets that the receiver's Avatar is not displayed, and it is displayed by default. EaseChatFragment#Builder also provides the setting method of this function.|
| hideChatSendAvatar()           | Sets that the sender's Avatar is not displayed, and it is displayed by default.EaseChatFragment#Builder also provides the setting method of this function. |
| setOnChatErrorListener()       | Sets the error callback when sending messages. EaseChatFragment#Builder also provides the setting method of this function.    |

（2）Set extended function

```java
IChatExtendMenu chatExtendMenu = chatLayout.getChatInputMenu().getChatExtendMenu();
```

After getting the chatExtendMenu object, you can add, remove, sort and handle the click events of the extended function.

Method explanation provided by IChatExtendMenu:


| Method                                 | Description                                         |
| -------------------------------------- | --------------------------------------------------- |
| clear()                                | Clears all extended menu items.                      |
| setMenuOrder()                         | Sorts the specified menu items                       |
| registerMenuItem()                     | Adds a new menu item.                                |

- Listen for extension entry click events

You can listen by EaseChatFragment#Builder#setOnChatExtendMenuItemClickListener or override the onChatExtendMenuItemClick method in a custom Fragment.

```java
@Override
public boolean onChatExtendMenuItemClick(View view, int itemId) {
    if(itemId == CUSTOM_YOUR_EXTEND_MENU_ID) {
    // Handle your own click event logic.
    // To handle click events, return true.
        return true;
    }
    return super.onChatExtendMenuItemClick(view, itemId);
}
```

(3) Long press menu function setting

- Add custom menu items

```java
chatLayout.addItemMenu(Menu.NONE, CUSTOM_YOUR_LONG_LICK_MENU_ID,
                       CUSTOM_YOUR_LONG_LICK_MENU_ORDER,
                       CUSTOM_YOUR_LONG_LICK_MENU_ITEM_TITLE);
```

Long press menu method provided by EaseChatLayout:

| Method                               | Description                                                       |
| -------------------------------------- | ---------------------------------------------------- |
| showItemDefaultMenu()               | Sets whether to display the default shortcut menu in UIKit. <br/> - (Default)True: Yes；<br/>- False: No. You need to handle MessageListItemClickListener#onBubbleLongClick by yourself. |
| clearMenu()                         | Clears menu items.                                        |
| addItemMenu()                       | Adds a new menu item.                                       |
| findItem()                          | Searches for the menu item by specifying itemId, and SDK returns null if it is not found.             |
| findItemVisible()                   | Sets the visibility of menu items by specifying itemid.                           |
| setOnPopupWindowItemClickListener() | Sets the click event listening of the menu item, which is set in EaseChatFragment.  |

- Handle menu events

Override the following methods in the customized fragment:

```java
@Override
public void onPreMenu(EasePopupWindowHelper helper, ChatMessage message) {
    // For the callback event before menu display, you can set whether menu items are displayed here through the helper object.
}

@Override
public boolean onMenuItemClick(MenuItemBean item, ChatMessage message) {
    //  You need to set the returning as true to intercept a click event.
    return false;
}

@Override
public void onDismiss(PopupWindow menu) {
    // Hidden events of shortcut menus can be handled here.
}
```

（4）Set input menu related properties

- Get the EaseChatInputMenu object

```java
EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
```

The following methods are provided by EaseChatInputMenu:

| Method                               | Description                                                 |
| -------------------------- | ------------------------------------------------------------ |
| setCustomPrimaryMenu()     | Sets customized menu items, which supports View and Fragment.         |
| setCustomEmojiconMenu()    | Sets the custom expression function, which supports View and Fragment. |
| setCustomExtendMenu()      | Sets custom extension functions, which supports View, Dialog and Fragment. |
| hideExtendContainer()      | Hides extended areas, including expression areas and extended function areas. |
| showEmojiconMenu()         | Displays expression function area. |
| showExtendMenu()           | Displays the extended function area. |
| setChatInputMenuListener()  | Sets input menu listening. |
| getPrimaryMenu()            | Gets menu item interface. |
| getEmojiconMenu()            | Gets the expression function menu interface. |
| getChatExtendMenu()            | Gets the extended function interface. |

- Get menu item object

```
IChatPrimaryMenu primaryMenu = chatLayout.getChatInputMenu().getPrimaryMenu();
```

The following methods are provided by IChatPrimaryMenu:

| Method                               | Description                                   |
| ------------------- | ----------------------------------------- |
| setMenuShowType()   | Sets menu style. For style, see EaseInputMenuStyle. |
| onTextInsert()            | Inserts text at the cursor. |
| getEditText()            | Gets the menu input box object. |
| setMenuBackground()            | Sets the background of the menu. |

- Get emoticon menu object

```java
IChatEmojiconMenu emojiconMenu = chatLayout.getChatInputMenu().getEmojiconMenu();
```

The following methods are provided by IChatEmojiconMenu:

| Method                               | Description               |
| --------------------- | ------------------ |
| addEmojiconGroup()    | Adds a custom expression.     |
| removeEmojiconGroup() | Removes the specified Emoji group.  |
| setTabBarVisibility() | Sets the visibility of TabBar. |
| setMenuBackground()   | Sets the background of the emoticon menu. |

Add a custom emoticon.

```java
chatLayout.getChatInputMenu().getEmojiconMenu().addEmojiconGroup(EmojiconExampleGroupData.getData());
```

### Conversation list UI related

#### Customize settings with EaseConversationListFragment.Builder

EaseConversationListFragment provides Builder. You can customize UI as follows:

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

Method explanation provided by EaseConversationListFragment#Builder:

| Method                               | Description                                                      |
| -------------------------------- | ------------------------------------------------------------ |
| useHeader()                      | Sets whether to use the default title bar(EaseTitleBar).<br/>- True: Yes. <br/>- (Default)False: No.           |
| setHeaderTitle()                 | Sets the title of the title bar.                                            |
| enableHeaderPressBack()          | Sets whether to support the display of the return button.<br/>- True: Yes. <br/>- (Default)False: No.                |
| setHeaderBackPressListener(）    | Sets the listener that clicks the title bar return button.                             |
| hideUnread(）                    | Sets whether to hide the unread message flag.                                    |
| setUnreadStyle(）                | Sets the style of unread messages. See EaseConversationSetStyle#UnreadStyle for the style. |
| setUnreadPosition(）             | Sets the location of unread messages. For the style, see EaseConversationSetStyle#UnreadDotPosition. |
| setItemClickListener(）          | Sets the item click event listener.                                       |
| setConversationChangeListener(） | Sets the listener of conversation change.                                       |
| setEmptyLayout(）                | Sets the blank page of the session list.                                 |
| setCustomAdapter(）              | Sets a custom adapter, which defaults to EaseConversationListAdapter.      |
| setCustomFragment(）             | Sets a custom chat Fragment, which needs to be inherited from EaseConversationListFragment. |

#### Add custom session layout

You can inherit the EaseConversationListAdapter to implement your own  CustomConversationListAdapter, and then set the CustomConversationListAdapter to the EaseConversationListFragment#Builder#setCustomAdapter.

（1）Create a custom adapter CustomConversationListAdapter, which inherits from EaseConversationListAdapter and overrides getViewHolder and getItemNotEmptyViewType methods.

```java
public class CustomConversationListAdapter extends EaseConversationListAdapter {

    @Override
    public int getItemNotEmptyViewType(int position) {
        // Set the custom itemViewType according to the message type.
        // If you use the default itemViewTyp, return super.getItemNotEmptyViewType(position).
        return CUSTOM_YOUR_CONVERSATION_TYPE;
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        // Return the corresponding ViewHolder according to the returned viewType.
        // Return the customized ViewHolder or the default setting of super.getViewHolder(parent, viewType).
        return new CUSTOM_YOUR_VIEWHOLDER();
    }
}
```

（2）Add CustomConversationListAdapter to EaseConversationListFragment#Builder.

```java
builder.setCustomAdapter(customConversationListAdapter);
```

#### Customize settings by inheriting EaseConversationListFragment

Create a custom CustomConversationListFragment, which inherits from EaseConversationListFragment, and needs to be set into EaseConversationListFragment#Builder.

```
builder.setCustomFragment(customConversationListFragment);
```

You can get the object of EaseConversationListLayout from CustomConversationListFragment, which enables more detailed custom settings.

Method explanation provided by EaseConversationListLayout:

| Method                               | Description                                                    |
| -------------------------------------- | ---------------------------------------------------------------- |
| setPresenter()                         | UIKit provides the default data implementation for EaseConversationPresenterImpl. You need to inherit EaseConversationPresenter to add your own data logic. |
| showItemDefaultMenu()                  | Sets whether to display the default shortcut menu in UIKit. <br/>- True: Yes. <br/>- False: No. You need to handle EaseConversationListLayout#setOnItemLongClickListener by your own.|
| setListAdapter()                       | Sets custom conversation list adapter. |
| getListAdapter()                       | Gets the conversation list adapter. |
| getItem()                              | Gets the data of the specified location. |
| makeConversionRead()                   | Sets the conversation at the specified location as read. |
| makeConversationTop()                  | Sets the conversation at the specified location to the top. |
| cancelConversationTop()                | Cancels the top setting operation at the specified position. |
| deleteConversation()                   | Deletes the session in the top position. |
| setOnConversationChangeListener()      | Sets the listening of conversation changes. The corresponding methods are provided in EaseConversationListFragment#Builder. |
| addHeaderAdapter()                     | Adds the adapter of the header layout of the conversation list. |
| addFooterAdapter()                     | Adds the adapter of the tail layout of the conversation list. |
| removeAdapter()                        | Removes the specified adapter. |
| addRVItemDecoration()                  | Adds decorator of conversation list. |
| removeRVItemDecoration()               | Removes the decorator of the conversation list. |
| setOnItemClickListener()               | Sets the item click listening of the conversation list. The corresponding methods are provided in EaseConversationListFragment#Builder. |
| setOnItemLongClickListener()           | Sets the entry of the conversation list to long press and listen. |
| setItemBackGround()                    | Sets the background of the item. |
| setItemHeight()                        | Sets the height of the item. |
| hideUnreadDot()                        | Sets whether to hide the unread message flag. The corresponding methods are provided in EaseConversationListFragment#Builder. |
| showUnreadDotPosition()                | Sets the location of unread messages. For the style, see EaseConversationSetStyle#UnreadDotPosition. The corresponding methods are provided in EaseConversationListFragment#Builder. |
| setUnreadStyle()                       | Sets the style of unread messages. For the style, see EaseConversationSetStyle#UnreadStyle. The corresponding methods are provided in EaseConversationListFragment#Builder. |
| setAvatarDefaultSrc()                  | Sets the default avatar of the entry. |
| setAvatarSize()                        | Sets the size of the entry Avatar. |
| setAvatarShapeType()                   | Sets the style of the item's Avatar, which contains three styles: (Default)ImageView, circular and rectangular.|
| setAvatarRadius()                      | Sets the fillet radius of the entry avatar, which is valid when the style is set to rectangle. |
| setAvatarBorderWidth()                 | Sets the width of the Avatar border. |
| setAvatarBorderColor()                 | Sets the color of the Avatar border. |
| setTitleTextSize()                     | Sets the text size of the conversation entry title. |
| setTitleTextColor()                    | Sets the text color of the conversation entry title. |
| setContentTextSize()                   | Sets the text size of the conversation entry content. |
| setContentTextColor()                  | Sets the text color of the conversation entry content. |
| setDateTextSize()                      | Sets the text size of the conversation entry date. |
| setDateTextColor()                     | Sets the text color of the session entry date. |
| clearMenu()                            | Clears the long press menu item. |
| addItemMenu()                          | Adds the long press menu item. |
| findItemVisible()                      | Sets whether the specified menu item is visible. |

### Thread message UI related

Chat UIKit provides three pages: EaseChatThreadActivity，EaseChatThreadCreateActivity and EaseChatThreadListActivity, which have been published in AndroidManifest.xml.

You can add their own logic by inheritance. The default Activity in UIKit can be replaced by the following methods:

```java
EaseUIKit.getInstance()
        .setActivityProvider(new EaseActivityProvider() {
            @Override
            public Class getActivity(String activityName) {
                if(TextUtils.equals(activityName, EaseChatThreadActivity.class.getSimpleName())) {
                    return ChatThreadActivity.class;
                }else if(TextUtils.equals(activityName, EaseChatThreadCreateActivity.class.getSimpleName())) {
                    return ChatThreadCreateActivity.class;
                }
                return null;
            }
        });
```

Currently, only the replacement of EaseChatThreadActivity and EaseChatThreadCreateActivity is supported.

#### The UI of thread

Agora chat UIKit provides EaseChatThreadFragment. You can inherit or use EaseChatThreadFragment#Builder to set customized options, add them to Activity and pass corresponding parameters.

```java
public class ChatThreadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_thread);
        // parentMsgId: message thread's parent message ID
        // conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
        // parentId: group ID to which message thread belongs
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fl_fragment,
                                            new EaseChatThreadFragment.Builder(parentMsgId, conversationID, parentId)
                                                                      .build())
                                   .commit();
    }
}
```

You can customize settings with `EaseChatThreadFragment.Builder`

EaseChatThreadFragment inherits from EaseChatFragment, EaseChatThreadFragment provides Builder method as follows:

```java
// parentMsgId: parent message ID, which is the message create the chat thread
// conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
// parentId: group ID to which is the chat thread belongs
new EaseChatThreadFragment.Builder(parentMsgId, conversationID, parentId)
        .hideHeader(false)
        .setThreadParentMsgViewProvider(threadParentMsgView)
        .setThreadPresenter(chatThreadPresenter)
        .setHeaderBackPressListener(onBackPressListener)
        .setOnJoinThreadResultListener(onJoinThreadResultListener)
        .setOnThreadRoleResultCallback(onThreadRoleResultCallback)
        .build();
```

Method explanation provided by EaseChatFragment#Builder:

| Method                               | Description                                                |
| -------------------------------- | ------------------------------------------------------------ |
| hideHeader()                     | Sets whether to hide the header layout of the sub area chat page. <br/>- True: Yes.<br/>- (Default)False: No.                |
| setThreadParentMsgViewProvider() | Sets the parent message layout provider, and you can set a custom parent message layout.             |
| setThreadPresenter()             | The Agora Chat UIKit provides the default data implementation EaseChatThreadPresenterImpl. You can inherit EaseChatThreadPresenter and add your own data logic. |
| setOnJoinThreadResultListener(） | Sets the result listening of user join Chat Thread.                             |
| setOnThreadRoleResultCallback(） | Sets the result callback of the role in Chat Thread.                        |

#### Thread creation page

Agora Chat UIKit provides EaseChatThreadCreateFragment. You can inherit the EaseChatThreadCreateFragment#Builder, and add into Activity passing corresponding parameters.

```java
public class ChatThreadCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_thread);
        // parentId: group ID to which message thread belongs
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fl_fragment,
                                            new EaseChatThreadCreateFragment.Builder(parentId, parentMsgId)
                                                                            .build())
                                   .commit();
    }
}
```

You can customize settings with `EaseChatThreadCreateFragment.Builder`.

EaseChatThreadCreateFragment provides Builder method for you as follows:

```java
new EaseChatThreadCreateFragment.Builder()
        .useHeader(true)
        .setHeaderTitle("title")
        .enableHeaderPressBack(true)
        .setHeaderBackPressListener(onBackPressListener)
        .setThreadMention("mention")
        .setThreadInputHint("chatTheadHint")
        .sendMessageByOriginalImage(true)
        .setThreadParentMsgViewProvider(threadParentMsgView)
        .setOnChatExtendMenuItemClickListener(onChatExtendMenuItemClickListener)
        .setOnMessageItemClickListener(onMessageItemClickListener)
        .setOnMessageSendCallBack(onMessageSendCallBack)
        .setOnAddMsgAttrsBeforeSendEvent(onAddMsgAttrsBeforeSendEvent)
        .setOnChatRecordTouchListener(onChatRecordTouchListener)
        .setOnThreadCreatedResultListener(onThreadCreatedResultListener)
        .setCustomPresenter(chatThreadCreatePresenter)
        .setCustomFragment(myConversationListFragment)
        .build();
```

Method explanation provided by EaseConversationListFragment#Builder:

| Method                               | Description                                                       |
| -------------------------------------- | ------------------------------------------------------------ |
| useHeader()                            | Sets whether to use the default title bar（EaseTitleBar）. <br/>- True: Yes.<br/>- (Default)False: No.      |
| setHeaderTitle()                       | Sets the title of the title bar.                                             |
| enableHeaderPressBack()                | Sets whether to support the display of the return button.<br/>- True: Yes.<br/>- (Default)False: No.              |
| setHeaderBackPressListener(）          | Sets the listener that clicks the title bar return button.                               |
| setThreadMention(）                    | Sets the prompt information under the message input box.                                   |
| setThreadInputHint(）                  | Sets the prompt information of the message input box.                                 |
| sendMessageByOriginalImage(）          | Sets whether to send the original image. <br/>- True: Yes.<br/>- (Default)False: No.                        |
| setThreadParentMsgViewProvider(）      | Sets the parent message layout provider, and you can set a custom parent message layout.           |
| setOnChatExtendMenuItemClickListener() | Sets the item click event listening of the extended function.                          |
| setOnMessageItemClickListener()        | Sets the click event monitoring of message entries, including the click and long press events of bubble areas and avatars. |
| setOnMessageSendCallBack()             | Sets the result callback listening of sending messages.                                  |
| setOnAddMsgAttrsBeforeSendEvent()      | Sets the callback to add the message extension property before sending the message.              |
| setOnChatRecordTouchListener()         | Sets the touch event callback of the recording button.                        |
| setOnThreadCreatedResultListener()     | Sets subarea creation result listener.                                  |
| setCustomPresenter()                   | Sets custom creation sub area logic, which needs to inherit EaseChatThreadCreatePresenter. UIKit provides the default EaseChatThreadCreatePresenterImpl.  |
| setCustomFragment(）                   | Sets a custom chat Fragment. which needs to be inherited from EaseConversationListFragment.  |

### Set the default title block

Both EaseChatFragment and EaseConversationListFragment provide（EaseTitleBar）a default title bar. You can get an instance of the title bar to make more customized settings.

Methods provided in EaseTitleBar:

| Method                               | Description                                                    |
| -------------------------------------- | ---------------------------------------------------------------- |
| setToolbarCustomColor()                | Sets the color of the ToolBar return button, and the incoming parameter is color resource.  |
| setToolbarCustomColorDefault()         | Sets the color of the ToolBar return button, and the incoming parameter is the color value. |
| setLeftImageResource()                 | Sets the left image resource. |
| setRightImageResource()                | Sets the right picture resource. |
| setRightTitleResource()                | Sets the right Title Resource. |
| setRightTitle()                        | Sets the right title. |
| setIcon()                              | Sets the picture with the title, which is generally located in the middle of the title, and the position depends on the setTitlePosition method setting.  |
| setTitlePosition()                     | Sets the position of the title. See TitlePosition. |
| setLeftLayoutVisibility()              | Sets whether the left layout is visible. <br/>- True: Yes.<br/>- (Default)False: No. |
| setRightLayoutVisibility()             | Sets whether the right layout is visible. <br/>- True: Yes.<br/>- (Default)False: No. |
| setTitle()                             | Sets title. |
| setTitleSize()                         | Sets the text size of the title. |
| setDisplayHomeAsUpEnabled()            | Sets whether the return button is visible in the ToolBar. <br/>- (Default)True: Yes.<br/>- False: No. |
| setBackgroundColor()                   | Sets the background of the title bar. |
| setOnBackPressListener()               | Sets the event listener for clicking the return button, and it can also listen for clicking events in the left area.|
| setOnRightClickListener()              | Sets the click event listener in the right area. |
| setOnIconClickListener()               | Sets the click event listener of the title icon. |
| getTitle()                             | Gets the Title control. |
| getLeftLayout()                        | Gets the left layout control. |
| getRightLayout()                       | Gets the right layout control. |
| getRightImage()                        | Gets the right picture control. |
| getRightText()                         | Gets the right Title Control. |
| getToolbar()                           | Gets the ToolBar control. |
| getIcon()                              | Gets the title icon control. |

### UIKit providers

#### Set avatar and nickname

You can provide customized avatars and nicknames through `EaseUserProfileProvider`.

You need to set `EaseUserProfileProvider` when appropriate. For example:

```java
EaseUIKit.getInstance().setUserProvider(new EaseUserProfileProvider() {
    @Override
    public EaseUser getUser(String userID) {
        // According to the user ID, the previously saved user information is retrieved from the database or memory. For example, the user object retrieved from the database is DemoUserBean.
        DemoUserBean bean = getUserFromDbOrMemery(userID);
        EaseUser user = new EaseUser(userID);
        ......
        // Set the nickname
        user.setNickname(bean.getNickname());
        // Set the avatar address
        user.setAvatar(bean.getAvatar());
        // Finally, return the built EaseUser object
        return user;
    }
});
```

The judgment of EaseUserProfileProvider has been added to the conversation list and chat list in UIKit. When displaying data, the avatar and nickname data will be obtained from EaseUserProfileProvider first. If there is one, it will be displayed. If there is no avatar, the default avatar will be used, and the nickname will be displayed as Agora chat ID.

#### Set avatar style uniformly


UIKit provides EaseAvatarOptions, which is used to globally configure the style of avatars, including shape, fillet radius, stroke width and stroke color. Support for EaseAvatarOptions has been added to conversations and chats.

```java
// Sets the avatar configuration properties.
EaseUIKit.getInstance().setAvatarOptions(getAvatarOptions());
......
/**
 * Unifies Avatar.
 * @return EaseAvatarOptions
 */
private EaseAvatarOptions getAvatarOptions() {
    EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
    // Sets the avatar shape, 1 for circle and 2 for square. It is set as a circle here.
    avatarOptions.setAvatarShape(1);
    return avatarOptions;
}
```

When using, you can directly call the setUserAvatarStyle(EaseImageView imageView) method in EaseUserUtils to set.

#### Set the configuration of local message notification

The Agora Chat UIKit provides the EaseNotifier to build message notifications and the EaseSettingsProvider to customize local notifications.

EaseSettingsProvider provides the following methods:：

| Method                               | Description              |
| -------------------------------------- | -------------------- |
| isMsgNotifyAllowed()                   | Whether to send local message notifications. By default, local message notifications are not sent. |
| isMsgSoundAllowed()                    | Whether to make a sound when receiving a message. By default, no sound is made when a message is received. |
| isMsgVibrateAllowed()                  | Whether to vibrate when receiving a message. By default, Yes. You need to add the vibration permission before you enable vibration.|
| isSpeakerOpened()                      | Whether to enable the speaker. By default, the speaker is disabled.|

#### Set the avatar and nickname for a group

The Agora Chat UIKit for iSO provides the EaseGroupInfoProvider method to set the avatar and nickname for a group or chat room.

You need to set EaseGroupInfoProvider when appropriate.

```java
EaseUIKit.getInstance().setGroupInfoProvider(new EaseGroupInfoProvider() {
    @Override
    public EaseGroupInfo getGroupInfo(String groupId, int type) {
        if(type == Conversation.ConversationType.GroupChat.ordinal()) {
            EaseGroupInfo info = new EaseGroupInfo();
            // Sets the avatar of the group
            info.setIcon(ContextCompat.getDrawable(context, R.drawable.group_avatar));
            // Sets the display style of the group
            EaseGroupInfo.AvatarSettings settings = new EaseGroupInfo.AvatarSettings();
            settings.setAvatarShapeType(2);
            settings.setAvatarRadius(1);
            info.setAvatarSettings(settings);
            return info;
        }
        return null;
    }
});
```

#### Provides icons by file type

When file messages are sent, developers may need to provide different icons for different types of file. For the purpose, the Agora Chat UIKit for iOS provides EaseFileIconProvider to provide different drawables for files with different file name extensions.

You need to set EaseFileIconProvider when appropriate.

```java
EaseUIKit.getInstance().setFileIconProvider(new EaseFileIconProvider() {
    @Override
    public Drawable getFileIcon(String filename) {
        if(!TextUtils.isEmpty(filename)) {
            Drawable drawable = null;
            Context context = DemoApplication.getInstance();
            Resources resources = context.getResources();
            if(EaseCompat.checkSuffix(filename, resources.getStringArray(io.agora.chat.uikit.R.array.ease_image_file_suffix))) {
                drawable = ContextCompat.getDrawable(context, R.drawable.file_type_image);
            }else if(EaseCompat.checkSuffix(filename, resources.getStringArray(io.agora.chat.uikit.R.array.ease_video_file_suffix))) {
                drawable = ContextCompat.getDrawable(context, R.drawable.file_type_video);
            }
            ...
            return drawable;
        }
        return null;
    }
});
```

### Live Streaming Chat Room

#### Chat display page

EaseChatRoomMessagesView implements the page for displaying messages in the live streaming chat room. It allows users to send text messages and receive text messages when joining the chatroom and the callback for the user joining the chat room. You can set custom attributes in the layout file (.xml).

EaseChatRoomMessagesView provides the following attributes:

| Attribute value | Description                    |
| ----------------------------------------- | -------------------------- |
| ease_live_input_edit_margin_bottom        | The distance from the message input prompt box to the bottom edge of the parent layout. |
| ease_live_input_edit_margin_end           | The distance between the input prompt box to the end of the parent layout. |
| ease_live_message_list_margin_end         | The distance between the message list to the end of the parent layout.     |
| ease_live_message_list_background         | The message list background.               |
| ease_live_message_item_text_color         | The color of message content text.           |
| ease_live_message_item_text_size          | The size of the message content text.         |
| ease_live_message_item_bubbles_background | The background of each message in the message list.   |
| ease_live_message_nickname_text_color     | The nickname text color.           |
| ease_live_message_nickname_text_size      | The nickname text size.               |
| ease_live_message_show_nickname           | Whether to display nickname, which is displayed by default.     |
| ease_live_message_show_avatar             | Whether to display avatar, which is displayed by default.    |
| ease_live_message_avatar_shape_type       | The avatar shape type, which is fillet by default. |

EaseChatRoomMessagesView provides the following methods:

| Method      | Description                             |
| ------------------------ | ------------------------------ |
| init                     | Initializes the information of a live streaming chat room.               |
| updateChatRoomInfo       | Updates the information of a live streaming chat room.                 |
| setVisibility            | Sets whether to display the page.             |
| getVisibility            | Gets the page display status value.           |
| getInputView             | Gets the input box for message-sending.             |
| getMessageListView       | Gets the message list view.               |
| getInputTipView          | Gets the input prompt box for message-sending.           |
| enableInputView          | Sets whether to enable the message input box.             |
| setMessageViewListener   | Sets View callback listening.              |
| setMessageStopRefresh    | Sets whether to stop refreshing the message list.     |
| refresh                  | Refreshes the message list.                 |
| setInputEditMarginBottom | Sets the distance between the message input prompt box and the bottom. |
| setInputEditMarginEnd    | Sets the distance between the message input prompt box and the end. |
| setMessageListMarginEnd  | Sets the distance between the message list and the end.      |

EaseChatRoomMessagesView.MessageViewListener provides the following methods:

| Method      | Description                     |
| ---------------------------------- | -------------------- |
| onSendTextMessageSuccess           | Succeeds in sending the text message.    |
| onSendTextMessageError             | Fails to send the text message. |
| onChatRoomMessageItemClickListener | Callback for clicking an item on the message list. |
| onHiderBottomBar                   | Whether to hide the bottom bar.  |

#### Send and receive messages in the live streaming chat room

EaseLiveMessageHelper is a message management tool class for live streaming chat rooms. It implements how to send and receive messages in live streaming chat rooms. EaseLiveMessageHelper provides the following methods:

| Method      | Description                |
| ------------------------- | ---------------------- |
| init                      | Initializes the information of the chat room.    |
| addLiveMessageListener    | Adds a message listener for the live streaming chat room.    |
| removeLiveMessageListener | Removes a message listener for the live streaming chat room.    |
| sendTxtMsg                | Sends a text message.         |
| sendGiftMsg               | Sends a gift message.        |
| sendCustomMsg             | Sends a custom message.        |
| getMsgGiftId              | Gets the ID of the gift delivered in the git message.  |
| getMsgGiftNum             | Gets the number of gifts delivered in the gift message. |
| isGiftMsg                 | Checks whether it is a gift message.    |
| getCustomEvent            | Gets the custom message event.      |
| getCustomMsgParams        | Gets the custom message parameters.    |
| getCustomMsgType          | Gets the custom message type.      |

##### Initialize the live streaming chat room

```Java
EaseLiveMessageHelper.getInstance().init(chatroomId);
```

##### Add and remove a message listener for the live streaming chat room

```Java
EaseLiveMessageHelper.getInstance().addLiveMessageListener(new OnLiveMessageListener() {
    @Override
    public void onGiftMessageReceived(ChatMessage message) {

    }
});
EaseLiveMessageHelper.getInstance().removeLiveMessageListener(this);
```
