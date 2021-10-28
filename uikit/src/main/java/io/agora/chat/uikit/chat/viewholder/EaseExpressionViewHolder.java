package io.agora.chat.uikit.chat.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowBigExpression;
import io.agora.exceptions.ChatException;


public class EaseExpressionViewHolder extends EaseChatRowViewHolder {

    public EaseExpressionViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    @Override
    protected void handleReceiveMessage(ChatMessage message) {
        super.handleReceiveMessage(message);
        if(!EaseConfigsManager.enableSendChannelAck()) {
            //此处不再单独发送read_ack消息，改为进入聊天页面发送channel_ack
            //新消息在聊天页面的onReceiveMessage方法中，排除视频，语音和文件消息外，发送read_ack消息
            if (!message.isAcked() && message.getChatType() == ChatMessage.ChatType.Chat) {
                try {
                    ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (ChatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
