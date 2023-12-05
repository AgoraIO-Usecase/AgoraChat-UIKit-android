package io.agora.chat.uikit.chathistory.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.uikit.chat.viewholder.EaseChatRowViewHolder;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.util.EMLog;


public class EaseHistoryVideoViewHolder extends EaseChatRowViewHolder {
    private static final String TAG = EaseHistoryVideoViewHolder.class.getSimpleName();

    public EaseHistoryVideoViewHolder(@NonNull View itemView) {
        super(itemView);
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
        EaseActivityProviderHelper.startToDownloadVideoActivity(getContext(), message);
    }
}
