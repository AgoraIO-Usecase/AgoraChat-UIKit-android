package io.agora.chat.uikit.interfaces;

import java.util.List;

import io.agora.MessageListener;
import io.agora.chat.ChatMessage;

public abstract class EaseMessageListener implements MessageListener {
    @Override
    public abstract void onMessageReceived(List<ChatMessage> messages);

    @Override
    public void onCmdMessageReceived(List<ChatMessage> messages) {

    }

    @Override
    public void onMessageRead(List<ChatMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<ChatMessage> messages) {

    }

    @Override
    public abstract void onMessageRecalled(List<ChatMessage> messages);

    @Override
    public abstract void onMessageChanged(ChatMessage message, Object change);
}
