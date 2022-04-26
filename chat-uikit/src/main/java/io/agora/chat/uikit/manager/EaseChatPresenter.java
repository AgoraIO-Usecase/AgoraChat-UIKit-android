package io.agora.chat.uikit.manager;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.agora.MessageListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.GroupReadAck;
import io.agora.chat.MessageReactionChange;
import io.agora.chat.uikit.EaseUIKit;

public class EaseChatPresenter implements MessageListener {
    private static final String TAG = EaseChatPresenter.class.getSimpleName();
    public Context context;

    public EaseChatPresenter() {
        ChatClient.getInstance().chatManager().addMessageListener(this);
    }


    public void attachApp(Context context) {
        this.context = context;
    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageReceived(List<ChatMessage> messages) {
        Log.e(TAG, "EaseChatPresenter onMessageReceived messages.size = "+messages.size());
        EaseAtMessageHelper.get().parseMessages(messages);
    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageRead(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageDelivered(List<ChatMessage> messages) {

    }

    /**
     * ChatMessageListener
     * @param messages
     */
    @Override
    public void onMessageRecalled(List<ChatMessage> messages) {
        
    }

    /**
     * ChatMessageListener
     * @param message
     * @param change
     */
    @Override
    public void onMessageChanged(ChatMessage message, Object change) {

    }

    @Override
    public void onGroupMessageRead(List<GroupReadAck> groupReadAcks) {
        for (GroupReadAck ack : groupReadAcks) {
            EaseDingMessageHelper.get().handleGroupReadAck(ack);
        }
    }

    @Override
    public void onReactionChanged(List<MessageReactionChange> list) {

    }

    public EaseNotifier getNotifier() {
        return EaseUIKit.getInstance().getNotifier();
    }
}
