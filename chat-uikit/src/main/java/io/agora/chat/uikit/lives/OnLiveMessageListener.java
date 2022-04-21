package io.agora.chat.uikit.lives;


import java.util.List;

import io.agora.chat.ChatMessage;

public interface OnLiveMessageListener {
    void onMessageReceived(List<ChatMessage> messages);

    void onGiftMessageReceived(ChatMessage message);

    void onPraiseMessageReceived(ChatMessage message);

    void onBarrageMessageReceived(ChatMessage message);

    void onMessageChanged();

}
