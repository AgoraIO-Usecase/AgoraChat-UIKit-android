package io.agora.chat.uikit.chat.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.exceptions.ChatException;


public class EaseTextViewHolder extends EaseChatRowViewHolder{

    public EaseTextViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        if (!EaseDingMessageHelper.get().isDingMessage(message) ||
                message.getChatType() != ChatMessage.ChatType.GroupChat ||
                message.direct() != ChatMessage.Direct.SEND) {
            return;
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        // TODO: 2021/10/27  
//        Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
//        i.putExtra("msg", message);
//        getContext().startActivity(i);
    }

    @Override
    protected void handleReceiveMessage(ChatMessage message) {
        super.handleReceiveMessage(message);
        if(!EaseConfigsManager.enableSendChannelAck()) {
            //Here no longer send read_ack message separately, instead enter the chat page to send channel_ack
            //New messages are sent in the onReceiveMessage method of the chat page, except for video
            // , voice and file messages, and send read_ack messages
            if (!message.isAcked() && message.getChatType() == ChatMessage.ChatType.Chat) {
                try {
                    ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (ChatException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message);
    }
}
