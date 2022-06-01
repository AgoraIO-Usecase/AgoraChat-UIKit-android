package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseVoiceLengthUtils;
import io.agora.util.EMLog;


public class EaseChatRowVoice extends EaseChatRowFile {
    private static final String TAG = EaseChatRowVoice.class.getSimpleName();
    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;
    private AnimationDrawable voiceAnimation;

    public EaseChatRowVoice(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowVoice(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ? R.layout.ease_row_received_voice
                : R.layout.ease_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        int padding = 0;
        if (len > 0) {
            padding = EaseVoiceLengthUtils.getVoiceLength(getContext(), len);
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (!showSenderType) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
            voiceLengthView.setPadding(padding, 0, 0, 0);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
            voiceLengthView.setPadding(0, 0, padding, 0);
        }

        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            FileMessageBody.EMDownloadStatus downloadStatus = voiceBody.downloadStatus();
            if(downloadStatus == FileMessageBody.EMDownloadStatus.PENDING &&
                    ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                ChatClient.getInstance().chatManager().downloadAttachment(message);
            }
            if(readStatusView != null) {
                if (message.isListened()) {
                    // hide the unread icon
                    readStatusView.setVisibility(View.INVISIBLE);
                } else {
                    readStatusView.setVisibility(View.VISIBLE);
                }
            }

            EMLog.d(TAG, "it is receive msg");
            if (voiceBody.downloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() == FileMessageBody.EMDownloadStatus.PENDING) {
                if (ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else {
            // hide the unread icon
            readStatusView.setVisibility(View.INVISIBLE);
        }

        // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
        EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
        if (voicePlayer.isPlaying() && message.getMsgId().equals(voicePlayer.getCurrentPlayingId())) {
            startVoicePlayAnimation();
        }
    }

    @Override
    protected void onViewUpdate(ChatMessage msg) {
        super.onViewUpdate(msg);

        // Only the received message has the attachment download status.
        if (message.direct() == ChatMessage.Direct.SEND) {
            return;
        }

        VoiceMessageBody voiceBody = (VoiceMessageBody) msg.getBody();
        if (voiceBody.downloadStatus() == FileMessageBody.EMDownloadStatus.DOWNLOADING ||
                voiceBody.downloadStatus() == FileMessageBody.EMDownloadStatus.PENDING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void startVoicePlayAnimation() {
        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceImageView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
        voiceAnimation.start();

        // Hide the voice item not listened status view.
        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            readStatusView.setVisibility(View.INVISIBLE);
        }
    }

    public void stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }

        if (message.direct() == ChatMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }
    }
}
