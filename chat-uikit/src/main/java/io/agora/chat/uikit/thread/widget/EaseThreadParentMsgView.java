package io.agora.chat.uikit.thread.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.OnMessageItemClickListener;
import io.agora.chat.uikit.databinding.EaseLayoutChatThreadParentMsgBinding;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseFileIconProvider;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseVoiceLengthUtils;
import io.agora.util.TextFormater;

public class EaseThreadParentMsgView extends FrameLayout {
    private static final String TAG = EaseThreadParentMsgView.class.getSimpleName();
    private EaseLayoutChatThreadParentMsgBinding binding;
    private OnMessageItemClickListener itemClickListener;
    private ChatMessage message;

    public EaseThreadParentMsgView(@NonNull Context context) {
        this(context, null);
    }

    public EaseThreadParentMsgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseThreadParentMsgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Log.e(TAG, "init");
        View root = LayoutInflater.from(context).inflate(R.layout.ease_layout_chat_thread_parent_msg, this);
        binding = EaseLayoutChatThreadParentMsgBinding.bind(root);

        // set avatar uniformly
        EaseUserUtils.setUserAvatarStyle(binding.image);

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
                }
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
        TextMessageBody body = (TextMessageBody) message.getBody();
        String content = body.getMessage();
        binding.message.setText(content);
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
            String time = EaseDateUtils.toTime(videoBody.getDuration());
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

    /**
     * Set thread parent message's click listener
     * @param itemClickListener
     */
    public void setOnMessageItemClickListener(OnMessageItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
