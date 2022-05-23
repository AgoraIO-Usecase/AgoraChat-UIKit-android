package io.agora.chat.uikit.lives;


import java.util.List;

import io.agora.chat.ChatMessage;

public interface OnLiveMessageListener {
    default void onMessageReceived(List<ChatMessage> messages) {
    }

    void onGiftMessageReceived(ChatMessage message);

    default void onPraiseMessageReceived(ChatMessage message) {
    }

    default void onBarrageMessageReceived(ChatMessage message) {
    }
}
