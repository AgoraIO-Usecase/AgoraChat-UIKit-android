package io.agora.chat.uikit.chat.viewholder;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.File;


import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVoice;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVoicePlayer;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;

public class EaseVoiceViewHolder extends EaseChatRowViewHolder{
    private EaseChatRowVoicePlayer voicePlayer;

    public EaseVoiceViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
        voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
    }

    @Override
    public void onBubbleClick(ChatMessage message) {
        super.onBubbleClick(message);
        String msgId = message.getMsgId();

        if (voicePlayer.isPlaying()) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer.stop();
            // Stop the voice play animation.
            ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();

            // If the playing voice item is this item, only need stop play.
            String playingId = voicePlayer.getCurrentPlayingId();
            if (msgId.equals(playingId)) {
                return;
            }
        }

        if (message.direct() == ChatMessage.Direct.SEND) {
            // Play the voice
            String localPath = ((VoiceMessageBody) message.getBody()).getLocalUrl();
            File file = new File(localPath);
            if (file.exists() && file.isFile()) {
                playVoice(message);
                // Start the voice play animation.
                ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();
            } else {
                asyncDownloadVoice(message);
            }
        } else {
            final String st = getContext().getResources().getString(R.string.ease_is_download_voice_click_later);
            if (message.status() == ChatMessage.Status.SUCCESS) {
                if (ChatClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    play(message);
                } else {
                    VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
                    EMLog.i("TAG", "Voice body download status: " + voiceBody.downloadStatus());
                    switch (voiceBody.downloadStatus()) {
                        case PENDING:// Download not begin
                        case FAILED:// Download failed
                            getChatRow().updateView(message);
                            asyncDownloadVoice(message);
                            break;
                        case DOWNLOADING:// During downloading
                            Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESSED:// Download success
                            play(message);
                            break;
                    }
                }
            } else if (message.status() == ChatMessage.Status.INPROGRESS) {
                Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
            } else if (message.status() == ChatMessage.Status.FAIL) {
                Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
                asyncDownloadVoice(message);
            }
        }
    }

    private void playVoice(ChatMessage msg) {
        voicePlayer.play(msg, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Stop the voice play animation.
                ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();
            }
        });
    }

    private void asyncDownloadVoice(final ChatMessage message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ChatClient.getInstance().chatManager().downloadAttachment(message);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                getChatRow().updateView(message);
            }
        }.execute();
    }

    private void play(ChatMessage message) {
        String localPath = ((VoiceMessageBody) message.getBody()).getLocalUrl();
        File file = new File(localPath);
        if (file.exists() && file.isFile()) {
            ackMessage(message);
            playVoice(message);
            // Start the voice play animation.
            ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();
        } else {
            EMLog.e("TAG", "file not exist");
        }
    }

    private void ackMessage(ChatMessage message) {
        ChatMessage.ChatType chatType = message.getChatType();
        if (!message.isAcked() && chatType == ChatMessage.ChatType.Chat) {
            try {
                ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (ChatException e) {
                e.printStackTrace();
            }
        }
        if (!message.isListened()) {
            ChatClient.getInstance().chatManager().setVoiceMessageListened(message);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(voicePlayer.isPlaying()) {
            voicePlayer.stop();
            voicePlayer.release();
        }
    }
}
