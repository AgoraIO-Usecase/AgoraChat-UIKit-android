package io.agora.chat.uikit.chat.viewholder;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.uikit.activities.EaseShowVideoActivity;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.util.EMLog;


public class EaseVideoViewHolder extends EaseChatRowViewHolder {
    private static final String TAG = EaseVideoViewHolder.class.getSimpleName();

    public EaseVideoViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
        EMLog.d(TAG, "video view is on click");
        if(ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {

        }else{
            if(videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.PENDING ||
                    videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.FAILED){
                // retry download with click event of user
                ChatClient.getInstance().chatManager().downloadThumbnail(message);
                return;
            }
        }
        Intent intent = new Intent(getContext(), EaseShowVideoActivity.class);
        intent.putExtra("msg", message);
        if (message != null && message.direct() == ChatMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == ChatMessage.ChatType.Chat) {
            try {
                ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getContext().startActivity(intent);
    }
}
