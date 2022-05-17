package io.agora.chat.uikit.widget.chatrow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.model.EaseChatItemStyleHelper;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;
import io.agora.util.TextFormater;


public class EaseChatRowVideo extends EaseChatRowFile {
    private static final String TAG = EaseChatRowVideo.class.getSimpleName();

    private ImageView imageView;
    private TextView sizeView;
    private TextView timeLengthView;
    private ImageView playView;

    public EaseChatRowVideo(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowVideo(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

	@Override
	protected void onInflateView() {
		inflater.inflate(!showSenderType ? R.layout.ease_row_received_video
                : R.layout.ease_row_sent_video, this);
	}

	@Override
	protected void onFindViewById() {
	    imageView = ((ImageView) findViewById(R.id.chatting_content_iv));
        sizeView = (TextView) findViewById(R.id.chatting_size_iv);
        timeLengthView = (TextView) findViewById(R.id.chatting_length_iv);
        playView = (ImageView) findViewById(R.id.chatting_status_btn);
        percentageView = (TextView) findViewById(R.id.percentage);
	}

	@Override
	protected void onSetUpView() {
        if(bubbleLayout != null) {
            bubbleLayout.setBackground(null);
        }
        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();

        if (videoBody.getDuration() >= 0) {
            String time;
            if(videoBody.getDuration() > 1000) {
                time = EaseDateUtils.toTime(videoBody.getDuration());
            }else {
                time = EaseDateUtils.toTimeBySecond(videoBody.getDuration());
            }
            timeLengthView.setText(time);
        }

        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                sizeView.setText(size);
            }
        } else {
            long videoFileLength = videoBody.getVideoFileLength();
            sizeView.setText(TextFormater.getDataSize(videoFileLength));
        }

        EMLog.d(TAG,  "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus());
        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING) {
                imageView.setImageResource(R.drawable.ease_default_image);
            } else {
                // System.err.println("!!!! not back receive, show image directly");
                imageView.setImageResource(R.drawable.ease_default_image);
                showVideoThumbView(message);
            }
        }else{
            if (videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.PENDING ||
                    videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.FAILED) {
                if(progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if(percentageView != null) {
                    percentageView.setVisibility(View.INVISIBLE);
                }
                if(videoBody.thumbnailDownloadStatus() == FileMessageBody.EMDownloadStatus.PENDING) {
                    showVideoThumbView(message);
                }else {
                    imageView.setImageResource(R.drawable.ease_default_image);
                }
            } else {
                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if(percentageView != null) {
                    percentageView.setVisibility(View.GONE);
                }
                imageView.setImageResource(R.drawable.ease_default_image);
                showVideoThumbView(message);
            }
        }

        setImageIncludeThread(imageView);
	}

    /**
     * show video thumbnails
     * @param message
     */
    @SuppressLint("StaticFieldLeak")
    private void showVideoThumbView(final ChatMessage message) {
        ViewGroup.LayoutParams params = EaseImageUtils.showVideoThumb(context, imageView, message);
        //setBubbleView(params.width, params.height);
    }

    private void setBubbleView(int width, int height) {
        ViewGroup.LayoutParams params = bubbleLayout.getLayoutParams();
        params.width = width;
        params.height = height;
    }

}
