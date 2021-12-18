package io.agora.chat.uikit.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.model.EaseInputMenuStyle;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.conversation.EaseConversationListFragment;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.widget.EaseTitleBar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String conversationID = "";
        int SINGLE_CHAT = 1;
        EaseTitleBar.OnBackPressListener onBackPressListener;
        // conversationID: Agora Chat ID: 1v1 is peer's userID, group chat is groupID, chat room is chatRoomID
        // 1: single chat; 2: group chat; 3: chat room
//        new EaseChatFragment.Builder(conversationID, SINGLE_CHAT)
//                .useHeader(true)
//                .setHeaderTitle("title")
//                .enableHeaderPressBack(true)
//                .setHeaderBackPressListener(onBackPressListener)
//                .getHistoryMessageFromServerOrLocal(false)
//                .setOnChatExtendMenuItemClickListener(onChatExtendMenuItemClickListener)
//                .setOnChatInputChangeListener(onChatInputChangeListener)
//                .setOnMessageItemClickListener(onMessageItemClickListener)
//                .setOnMessageSendCallBack(onMessageSendCallBack)
//                .setOnAddMsgAttrsBeforeSendEvent(onAddMsgAttrsBeforeSendEvent)
//                .setOnChatRecordTouchListener(onChatRecordTouchListener)
//                .setMsgTimeTextColor(msgTimeTextColor)
//                .setMsgTimeTextSize(msgTimeTextSize)
//                .setReceivedMsgBubbleBackground(receivedMsgBubbleBackground)
//                .setSentBubbleBackground(sentBubbleBackground)
//                .showNickname(false)
//                .setMessageListShowStyle(EaseChatMessageListLayout.ShowType.LEFT_RIGHT)
//                .hideReceiverAvatar(false)
//                .hideSenderAvatar(true)
//                .setChatBackground(chatBackground)
//                .setChatInputMenuStyle(EaseInputMenuStyle.All)
//                .setChatInputMenuBackground(inputMenuBackground)
//                .setChatInputMenuHint(inputMenuHint)
//                .sendMessageByOriginalImage(true)
//                .setEmptyLayout(R.layout.layout_conversation_empty)
//                .setCustomAdapter(customAdapter)
//                .setCustomFragment(myChatFragment)
//                .build();
//
//        new EaseConversationListFragment.Builder()
//                .useHeader(true)
//                .setHeaderTitle("title")
//                .enableHeaderPressBack(true)
//                .setHeaderBackPressListener(onBackPressListener)
//                .setUnreadStyle(EaseConversationSetStyle.UnreadStyle.NUM)
//                .setUnreadPosition(EaseConversationSetStyle.UnreadDotPosition.RIGHT)
//                .setEmptyLayout(R.layout.layout_conversation_empty)
//                .setItemClickListener(onItemClickListener)
//                .setConversationChangeListener(conversationChangeListener)
//                .setCustomAdapter(customAdapter)
//                .setCustomFragment(myConcersationListFragment)
//                .build();
    }
}