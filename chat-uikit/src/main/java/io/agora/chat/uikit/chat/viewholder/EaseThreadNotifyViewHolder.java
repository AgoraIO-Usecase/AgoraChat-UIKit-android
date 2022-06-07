package io.agora.chat.uikit.chat.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;


public class EaseThreadNotifyViewHolder extends EaseChatRowViewHolder {

    public EaseThreadNotifyViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        // Skip to Chat thread activity
        String parentMsgId = message.getStringAttribute(EaseConstant.EASE_THREAD_PARENT_MSG_ID, "");
        EaseActivityProviderHelper.startToChatThreadActivity(getContext(), message.getMsgId(), parentMsgId, message.getTo());
    }

}
