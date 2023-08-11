package io.agora.chat.uikit.chathistory.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import io.agora.CallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowFile;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowImage;
import io.agora.util.EMLog;


/**
 * image for row
 */
public class EaseChatRowHistoryImage extends EaseChatRowImage {

    public EaseChatRowHistoryImage(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowHistoryImage(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.ease_row_history_picture, this);
    }

    @Override
    protected void onSetUpView() {
        if(bubbleLayout != null) {
            bubbleLayout.setBackground(null);
        }
        imgBody = (ImageMessageBody) message.getBody();
        // If local file exits, show image directly
        if(EaseFileUtils.isFileExistByUri(context, imgBody.getLocalUri()) || EaseFileUtils.isFileExistByUri(context, imgBody.thumbnailLocalUri())) {
            showImageView(message);
            return;
        }
        ViewGroup.LayoutParams params = EaseImageUtils.getImageShowSize(context, message);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        imageView.setImageResource(R.drawable.ease_default_image);
        // If auto transfer message attachments to Chat Server, then download attachments
        if(ChatClient.getInstance().getOptions().getAutoTransferMessageAttachments()) {
            if(imgBody.downloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING
                    || imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING) {
                setMessageDownloadCallback();
                return;
            }
            downloadAttachment(!TextUtils.isEmpty(imgBody.getThumbnailUrl()));
        }else {
            showImageView(message);
        }
    }

    @Override
    public void setOtherTimestamp(ChatMessage preMessage) {
        timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
        timeStampView.setVisibility(View.VISIBLE);
    }
}
