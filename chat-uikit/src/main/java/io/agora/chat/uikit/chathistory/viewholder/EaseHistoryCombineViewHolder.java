package io.agora.chat.uikit.chathistory.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.chat.viewholder.EaseChatRowViewHolder;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;


public class EaseHistoryCombineViewHolder extends EaseChatRowViewHolder {

    public EaseHistoryCombineViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        skipToCombine(message);
    }

    private void skipToCombine(ChatMessage message) {
        EaseActivityProviderHelper.startToChatHistoryActivity(getContext(), message);
    }

}
