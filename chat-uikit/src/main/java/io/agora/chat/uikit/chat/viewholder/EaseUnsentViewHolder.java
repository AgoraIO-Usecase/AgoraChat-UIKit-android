package io.agora.chat.uikit.chat.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.exceptions.ChatException;

public class EaseUnsentViewHolder extends EaseChatRowViewHolder {

    public EaseUnsentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

}
