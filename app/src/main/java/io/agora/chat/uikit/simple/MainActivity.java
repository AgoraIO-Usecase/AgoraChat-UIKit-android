package io.agora.chat.uikit.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.agora.chat.Conversation;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.model.EaseInputMenuStyle;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.conversation.EaseConversationListFragment;
import io.agora.chat.uikit.conversation.interfaces.OnConItemClickListener;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseTitleBar;


public class MainActivity extends AppCompatActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment,
                        new EaseConversationListFragment.Builder()
                                .setItemClickListener(new OnConItemClickListener<EaseConversationInfo>() {
                                    @Override
                                    public void onItemClick(View view, EaseConversationInfo conversation, int position) {
                                        Object info = conversation.getInfo();
                                        if(info instanceof Conversation) {
                                            ChatActivity.actionStart(MainActivity.this
                                                    , ((Conversation) info).conversationId(), EaseUtils.getChatType((Conversation) info));
                                        }
                                    }
                                })
                                .build())
                .commit();
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