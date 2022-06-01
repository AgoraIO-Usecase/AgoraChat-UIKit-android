package io.agora.chat.uikit.chatthread.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.activities.EaseShowBigImageActivity;
import io.agora.chat.uikit.activities.EaseShowNormalFileActivity;
import io.agora.chat.uikit.activities.EaseShowVideoActivity;
import io.agora.chat.uikit.chat.interfaces.OnMessageItemClickListener;
import io.agora.chat.uikit.databinding.EaseLayoutChatThreadParentMsgBinding;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseFileIconProvider;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.utils.EaseVoiceLengthUtils;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVoicePlayer;
import io.agora.util.EMLog;
import io.agora.util.TextFormater;

public class EaseChatThreadParentMsgView extends ConstraintLayout {
    private static final String TAG = EaseChatThreadParentMsgView.class.getSimpleName();
    private EaseLayoutChatThreadParentMsgBinding binding;
    private OnMessageItemClickListener itemClickListener;
    private ChatMessage message;
    private EaseChatRowVoicePlayer voicePlayer;
    private AnimationDrawable voiceAnimation;

    public EaseChatThreadParentMsgView(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatThreadParentMsgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatThreadParentMsgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View root = LayoutInflater.from(context).inflate(R.layout.ease_layout_chat_thread_parent_msg, this);
        binding = EaseLayoutChatThreadParentMsgBinding.bind(root);

        // set avatar uniformly
        EaseUserUtils.setUserAvatarStyle(binding.avatar);

        setListener();
    }

    private void setListener() {
        binding.avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null && message != null) {
                    itemClickListener.onUserAvatarClick(message.getFrom());
                }
            }
        });

        binding.avatar.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(itemClickListener != null && message != null) {
                    itemClickListener.onUserAvatarLongClick(message.getFrom());
                    return true;
                }
                return false;
            }
        });

        binding.llContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null && message != null) {
                    itemClickListener.onBubbleClick(message);
                    return;
                }
                clickEvent(message);
            }
        });

        binding.llContent.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(itemClickListener != null && message != null) {
                    itemClickListener.onBubbleLongClick(v, message);
                    return true;
                }
                return false;
            }
        });
    }

    private void clickEvent(ChatMessage message) {
        if(message == null) {
            return;
        }
        ChatMessage.Type type = message.getType();
        switch (type) {
            case IMAGE :
                openImage(message);
                break;
            case VIDEO :
                openVideo(message);
                break;
            case LOCATION :
                openLocation(message);
                break;
            case VOICE :
                playVoice(message);
                break;
            case FILE :
                openFile(message);
                break;
        }
    }

    private void openImage(ChatMessage message) {
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        Intent intent = new Intent(getContext(), EaseShowBigImageActivity.class);
        Uri imgUri = imgBody.getLocalUri();
        EaseFileUtils.takePersistableUriPermission(getContext(), imgUri);
        EMLog.e("Tag", "big image uri: " + imgUri + "  exist: "+EaseFileUtils.isFileExistByUri(getContext(), imgUri));
        if(EaseFileUtils.isFileExistByUri(getContext(), imgUri)) {
            intent.putExtra("uri", imgUri);
        } else{
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            String msgId = message.getMsgId();
            intent.putExtra("messageId", msgId);
            intent.putExtra("filename", imgBody.getFileName());
        }
        getContext().startActivity(intent);
    }

    private void openVideo(ChatMessage message) {
        Intent intent = new Intent(getContext(), EaseShowVideoActivity.class);
        intent.putExtra("msg", message);
        getContext().startActivity(intent);
    }

    private void openLocation(ChatMessage message) {

    }

    private void playVoice(ChatMessage message) {
        if(voicePlayer == null) {
            voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
        }
        if (voicePlayer.isPlaying()) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer.stop();

            // If the playing voice item is this item, only need stop play.
            String playingId = voicePlayer.getCurrentPlayingId();
            if (message.getMsgId().equals(playingId)) {
                return;
            }
        }
        voicePlayer.play(message, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Stop the voice play animation.
                stopVoicePlayAnimation();
            }
        });
        // Start the voice play animation.
        startVoicePlayAnimation();
    }

    private void startVoicePlayAnimation() {
        binding.ivVoice.setImageResource(R.drawable.voice_from_icon);
        voiceAnimation = (AnimationDrawable) binding.ivVoice.getDrawable();
        voiceAnimation.start();
    }

    private void stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        binding.ivVoice.setImageResource(R.drawable.ease_chatfrom_voice_playing);
    }

    private void openFile(ChatMessage message) {
        NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        Uri filePath = fileMessageBody.getLocalUri();
        EaseFileUtils.takePersistableUriPermission(getContext(), filePath);
        if(EaseFileUtils.isFileExistByUri(getContext(), filePath)){
            EaseCompat.openFile(getContext(), filePath);
        } else {
            // download the file
            getContext().startActivity(new Intent(getContext(), EaseShowNormalFileActivity.class).putExtra("msg", message));
        }
    }

    public void setMessage(ChatMessage message) {
        if(message == null) {
            return;
        }
        this.message = message;
        String nickname = message.getFrom();
        do {
            EaseUserProfileProvider userProvider = EaseUIKit.getInstance().getUserProvider();
            if(userProvider == null) {
                break;
            }
            EaseUser user = userProvider.getUser(message.getFrom());
            if(user == null) {
                break;
            }
            nickname = user.getNickname();
            EaseUserUtils.setUserAvatar(getContext(), message.getFrom(), binding.avatar);
        } while (false);
        binding.name.setText(nickname);
        binding.time.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
        ChatMessage.Type type = message.getType();
        switch (type) {
            case TXT :
                setTxtMessage(message);
                break;
            case IMAGE :
                setImageMessage(message);
                break;
            case VIDEO :
                setVideoMessage(message);
                break;
            case LOCATION :
                setLocationMessage(message);
                break;
            case VOICE :
                setVoiceMessage(message);
                break;
            case FILE :
                setFileMessage(message);
                break;
            case CUSTOM :
                setCustomMessage(message);
                break;
        }
    }

    private void setTxtMessage(ChatMessage message) {
        hideAllBubble();
        binding.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseUtils.getMessageDigest(message, getContext())));
        binding.message.setVisibility(VISIBLE);
    }

    private void setImageMessage(ChatMessage message) {
        hideAllBubble();
        EaseImageUtils.showImage(getContext(), binding.image, message);
        binding.bubblePicture.setVisibility(VISIBLE);
    }

    private void setVideoMessage(ChatMessage message) {
        hideAllBubble();
        ViewGroup.LayoutParams params = EaseImageUtils.showVideoThumb(getContext(), binding.chattingContentIv, message);
        ViewGroup.LayoutParams bubbleParams = binding.bubbleVideo.getLayoutParams();
        bubbleParams.width = params.width;
        bubbleParams.height = params.height;

        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();

        if (videoBody.getDuration() > 0) {
            String time;
            if(videoBody.getDuration() > 1000) {
                time = EaseDateUtils.toTime(videoBody.getDuration());
            }else {
                time = EaseDateUtils.toTimeBySecond(videoBody.getDuration());
            }
            binding.chattingLengthIv.setText(time);
        }

        if (videoBody.getVideoFileLength() > 0) {
            String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
            binding.chattingSizeIv.setText(size);
        }

        binding.bubbleVideo.setVisibility(VISIBLE);
    }

    private void setLocationMessage(ChatMessage message) {
        hideAllBubble();
    }

    private void setVoiceMessage(ChatMessage message) {
        hideAllBubble();
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        int padding = 0;
        if (len > 0) {
            padding = EaseVoiceLengthUtils.getVoiceLength(getContext(), len);
            binding.tvLength.setText(voiceBody.getLength() + "\"");
            binding.tvLength.setVisibility(View.VISIBLE);
        } else {
            binding.tvLength.setVisibility(View.INVISIBLE);
        }
        binding.ivVoice.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        binding.tvLength.setPadding(padding, 0, 0, 0);
        binding.bubbleVoice.setVisibility(VISIBLE);
    }

    private void setFileMessage(ChatMessage message) {
        hideAllBubble();
        NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
        binding.tvFileName.setText(fileMessageBody.getFileName());
        binding.tvFileSize.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        setFileIcon(fileMessageBody.getFileName());
        binding.tvFileState.setText("");
        binding.bubbleFile.setVisibility(VISIBLE);
    }

    private void setCustomMessage(ChatMessage message) {
        hideAllBubble();
    }

    private void setFileIcon(String fileName) {
        EaseFileIconProvider provider = EaseUIKit.getInstance().getFileIconProvider();
        if(provider != null) {
            Drawable icon = provider.getFileIcon(fileName);
            if(icon != null) {
                binding.ivFileIcon.setImageDrawable(icon);
            }
        }
    }

    private void hideAllBubble() {
        binding.message.setVisibility(GONE);
        binding.bubbleVoice.setVisibility(GONE);
        binding.bubblePicture.setVisibility(GONE);
        binding.bubbleVideo.setVisibility(GONE);
        binding.bubbleFile.setVisibility(GONE);
        binding.bubbleBigExpression.setVisibility(GONE);
    }

    public ImageView getAvatarView() {
        return binding.avatar;
    }

    public TextView getUsernameView() {
        return binding.name;
    }

    public TextView getTimeView() {
        return binding.time;
    }

    public ViewGroup getBubbleParent() {
        return binding.llContent;
    }

    public void setBottomDividerVisible(boolean visible) {
        binding.viewBottomDivider.setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Set thread parent message's click listener
     * @param itemClickListener
     */
    public void setOnMessageItemClickListener(OnMessageItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
