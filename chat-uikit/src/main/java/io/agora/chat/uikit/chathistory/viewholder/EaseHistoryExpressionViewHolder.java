package io.agora.chat.uikit.chathistory.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.chat.viewholder.EaseChatRowViewHolder;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.exceptions.ChatException;


public class EaseHistoryExpressionViewHolder extends EaseChatRowViewHolder {

    public EaseHistoryExpressionViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

}
