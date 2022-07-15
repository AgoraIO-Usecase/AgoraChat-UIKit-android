package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import io.agora.chat.ChatMessage;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.provider.EaseFileIconProvider;
import io.agora.chat.uikit.utils.EaseFileUtils;
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
        fileStateView.setVisibility(GONE);
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
    protected void onMessageCreate() {
        super.onMessageCreate();
        progressBar.setVisibility(View.VISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
        if (message.direct() == ChatMessage.Direct.SEND)
            if(fileStateView != null) {
                fileStateView.setText(R.string.ease_have_uploaded);
            }
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onMessageInProgress() {
        super.onMessageInProgress();
        if(progressBar.getVisibility() != VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (percentageView != null) {
            percentageView.setVisibility(View.VISIBLE);
            percentageView.setText(message.progress() + "%");
        }
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    private void setFileIcon(String fileName) {
        EaseFileIconProvider provider = EaseUIKit.getInstance().getFileIconProvider();
        if(provider != null) {
            Drawable icon = provider.getFileIcon(fileName);
            if(icon != null) {
                ivFileIcon.setImageDrawable(icon);
            }
        }
    }
}
