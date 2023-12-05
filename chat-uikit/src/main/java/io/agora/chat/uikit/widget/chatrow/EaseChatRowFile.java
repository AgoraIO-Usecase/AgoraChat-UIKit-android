package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import io.agora.CallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.provider.EaseFileIconProvider;
import io.agora.util.TextFormater;

/**
 * file for row
 */
public class EaseChatRowFile extends EaseChatRow {
    private static final String TAG = EaseChatRowFile.class.getSimpleName();
    /**
     * file name
     */
    protected TextView fileNameView;
    /**
     * file's size
     */
	protected TextView fileSizeView;
    /**
     * file state
     */
    protected TextView fileStateView;
    private NormalFileMessageBody fileMessageBody;
    private ImageView ivFileIcon;
    private boolean isDownloading = false;

    public EaseChatRowFile(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowFile(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
	protected void onInflateView() {
	    inflater.inflate(!showSenderType ? R.layout.ease_row_received_file
                : R.layout.ease_row_sent_file, this);
	}

	@Override
	protected void onFindViewById() {
	    fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
        ivFileIcon = findViewById(R.id.iv_file_icon);
	}

	@Override
	protected void onSetUpView() {
	    fileMessageBody = (NormalFileMessageBody) message.getBody();
//        Uri filePath = fileMessageBody.getLocalUri();
        if(fileStateView != null) {
            fileStateView.setVisibility(GONE);
        }
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        setFileIcon(fileMessageBody.getFileName());
//        if (message.direct() == ChatMessage.Direct.SEND){
//            if (EaseFileUtils.isFileExistByUri(context, filePath)
//                    && message.status() == ChatMessage.Status.SUCCESS) {
//                fileStateView.setText(R.string.ease_have_uploaded);
//            }else {
//                fileStateView.setText("");
//            }
//        }
//        if (message.direct() == ChatMessage.Direct.RECEIVE) {
//            if (EaseFileUtils.isFileExistByUri(context, filePath)) {
//                fileStateView.setText(R.string.ease_have_downloaded);
//            } else {
//                fileStateView.setText(R.string.ease_did_not_download);
//            }
//        }
	}

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        if (message.direct() == ChatMessage.Direct.SEND)
            if(fileStateView != null) {
                fileStateView.setText(R.string.ease_have_uploaded);
            }
    }

    protected void setFileIcon(String fileName) {
        EaseFileIconProvider provider = EaseUIKit.getInstance().getFileIconProvider();
        if(provider != null) {
            Drawable icon = provider.getFileIcon(fileName);
            if(icon != null) {
                ivFileIcon.setImageDrawable(icon);
            }
        }
    }

    /**
     * Download file or thumbnail.
     * @param isThumbnail   Whether to download thumbnail
     */
    protected void downloadAttachment(boolean isThumbnail) {
        if(message != null) {
            setMessageDownloadCallback();
            if(isThumbnail) {
                ChatClient.getInstance().chatManager().downloadThumbnail(message);
            }else {
                ChatClient.getInstance().chatManager().downloadAttachment(message);
            }
        }
    }

    @Override
    public void updateView(ChatMessage msg) {
        if(!isDownloading) {
            super.updateView(msg);
        }
    }

    /**
     * Set message download callback.
     */
    protected void setMessageDownloadCallback() {
        if(message != null) {
            isDownloading = true;
            Log.e("message", "setMessageDownloadCallback");
            message.setMessageStatusCallback(new CallBack() {
                @Override
                public void onSuccess() {
                    Log.e("message", "setMessageDownloadCallback onSuccess");
                    post(()->onDownloadAttachmentSuccess());
                    isDownloading = false;
                }

                @Override
                public void onError(int code, String error) {
                    Log.e("message", "setMessageDownloadCallback onError");
                    post(()->onDownloadAttachmentError(code, error));
                    isDownloading = false;
                }

                @Override
                public void onProgress(int progress, String status) {
                    Log.e("message", "setMessageDownloadCallback onProgress");
                    post(()->onDownloadAttachmentProgress(progress));
                }
            });
        }
    }

    protected void onDownloadAttachmentSuccess() {}

    protected void onDownloadAttachmentError(int code, String error) {}

    protected void onDownloadAttachmentProgress(int progress) {}
}
