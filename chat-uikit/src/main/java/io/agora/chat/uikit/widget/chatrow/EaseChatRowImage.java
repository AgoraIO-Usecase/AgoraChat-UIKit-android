package io.agora.chat.uikit.widget.chatrow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.util.EMLog;


/**
 * image for row
 */
public class EaseChatRowImage extends EaseChatRowFile {
    protected ImageView imageView;
    protected ImageMessageBody imgBody;

    private int retryTimes = 10;

    public EaseChatRowImage(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowImage(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_picture
                : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onSetUpView() {
        if(bubbleLayout != null) {
            bubbleLayout.setBackground(null);
        }
        imgBody = (ImageMessageBody) message.getBody();
        // If local file exits, show image directly
        if(EaseFileUtils.isFileExistByUri(context, imgBody.getLocalUri()) || EaseFileUtils.isFileExistByUri(context, imgBody.thumbnailLocalUri())) {
            showImageView(message, position);
            return;
        }
        retryTimes = 10;
        ViewGroup.LayoutParams params = EaseImageUtils.getImageShowSize(context, message);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        imageView.setImageResource(R.drawable.ease_default_image);
        checkAttachmentStatus(position);
    }

    private void checkAttachmentStatus(int position) {
        // If auto transfer message attachments to Chat Server, then download attachments
        if(ChatClient.getInstance().getOptions().getAutoTransferMessageAttachments()) {
            ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
            // received messages
            if (message.direct() == ChatMessage.Direct.RECEIVE) {
                if(ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    if(imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.SUCCESSED){
                        showImageView(message, position);
                        return;
                    }else if(imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING
                            || imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.PENDING) {
                        if(isInRetrying()) {
                            showInProgressStatus();
                            postDelayed(()->checkAttachmentStatus(position), 500);
                        }else {
                            showSuccessStatus();
                        }
                        return;
                    }
                }
            }
            if(message.status() != ChatMessage.Status.SUCCESS) {
                return;
            }
            if(imgBody.downloadStatus() == FileMessageBody.EMDownloadStatus.SUCCESSED
                    || imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.SUCCESSED) {
                showImageView(message, position);
            }else if(imgBody.downloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING
                    || imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING) {
                if(isInRetrying()) {
                    showInProgressStatus();
                    postDelayed(()-> checkAttachmentStatus(position), 500);
                }else {
                    showSuccessStatus();
                }
                return;
            }
            downloadAttachment(!TextUtils.isEmpty(imgBody.getThumbnailUrl()) && message.direct() == ChatMessage.Direct.RECEIVE);
        }else {
            showImageView(message, position);
        }
    }

    private boolean isInRetrying() {
        int times = retryTimes;
        retryTimes--;
        return times > 0;
    }

    @Override
    protected void onViewUpdate(ChatMessage msg) {
        super.onViewUpdate(msg);
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        //Even if it's the sender, it needs to be executed after 
        // it's successfully sent to prevent the image size from being wrong
        if(imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.SUCCESSED || imgBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.FAILED) {
            showImageView(message, position);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if(message.direct() == ChatMessage.Direct.SEND) {
            super.onMessageInProgress();
        }else {
            if(ChatClient.getInstance().getOptions().getAutodownloadThumbnail()){
                //imageView.setImageResource(R.drawable.ease_default_image);
            }else {
                if(progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if(percentageView != null) {
                    percentageView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDownloadAttachmentSuccess() {
        showImageView(message, position);
    }

    @Override
    protected void onDownloadAttachmentError(int code, String error) {
        EMLog.e(EaseChatRowImage.class.getSimpleName(), "onDownloadAttachmentError:" + code + ", error:" + error);
    }

    @Override
    protected void onDownloadAttachmentProgress(int progress) {
        showInProgressStatus();
    }

    /**
     * load image into image view
     *
     */
    @SuppressLint("StaticFieldLeak")
    protected void showImageView(final ChatMessage message, int position) {
        if(position == this.position && TextUtils.equals(message.getMsgId(), this.message.getMsgId())) {
            showSuccessStatus();
            EaseImageUtils.showImage(context, imageView, message);
            setImageIncludeThread(imageView);
        }
    }
}
