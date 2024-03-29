package io.agora.chat.uikit.chat.viewholder;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.util.EMLog;


public class EaseImageViewHolder extends EaseChatRowViewHolder {

    public EaseImageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();

        if(!EaseConfigsManager.enableSendChannelAck()) {
            //Here no longer send read_ack message separately, instead enter the chat page to send channel_ack
            //New messages are sent in the onReceiveMessage method of the chat page, except for video
            // , voice and file messages, and send read_ack messages
            if (message != null && message.direct() == ChatMessage.Direct.RECEIVE && !message.isAcked()
                    && message.getChatType() == ChatMessage.ChatType.Chat) {
                try {
                    ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Uri imgUri = imgBody.getLocalUri();
        EaseFileUtils.takePersistableUriPermission(getContext(), imgUri);
        EMLog.e("Tag", "big image uri: " + imgUri + "  exist: "+EaseFileUtils.isFileExistByUri(getContext(), imgUri));
        if(EaseFileUtils.isFileExistByUri(getContext(), imgUri)) {
            EaseActivityProviderHelper.startToLocalImageActivity(getContext(), imgUri);
        } else{
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            EaseActivityProviderHelper.startToLocalImageActivity(getContext(), message.getMsgId(), imgBody.getFileName());
        }
    }

    @Override
    protected void handleReceiveMessage(ChatMessage message) {
        super.handleReceiveMessage(message);
        getChatRow().updateView(message);
    }
}
