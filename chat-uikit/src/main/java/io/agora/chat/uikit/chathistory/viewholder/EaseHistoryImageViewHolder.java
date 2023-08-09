package io.agora.chat.uikit.chathistory.viewholder;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.uikit.chat.viewholder.EaseChatRowViewHolder;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.util.EMLog;


public class EaseHistoryImageViewHolder extends EaseChatRowViewHolder {

    public EaseHistoryImageViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        if(ChatClient.getInstance().getOptions().getAutodownloadThumbnail()){
            if(imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.FAILED){
                getChatRow().updateView(message);
                // retry download with click event of user
                ChatClient.getInstance().chatManager().downloadThumbnail(message);
            }
        } else{
            if(imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.PENDING ||
                    imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.FAILED){
                // retry download with click event of user
                ChatClient.getInstance().chatManager().downloadThumbnail(message);
                getChatRow().updateView(message);
                return;
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
