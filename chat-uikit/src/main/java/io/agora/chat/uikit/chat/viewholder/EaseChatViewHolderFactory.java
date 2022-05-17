package io.agora.chat.uikit.chat.viewholder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.chatthread.widget.EaseChatRowThreadNotify;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowCustom;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowFile;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowImage;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowText;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowUnknown;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVideo;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVoice;

public class EaseChatViewHolderFactory {
    public static EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage> createViewHolder(@NonNull ViewGroup parent, EaseMessageViewType viewType
            , MessageListItemClickListener listener) {

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_TXT_ME:
            case VIEW_TYPE_MESSAGE_TXT_OTHER:
                return new EaseTextViewHolder(new EaseChatRowText(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME), listener);
            case VIEW_TYPE_MESSAGE_IMAGE_ME:
            case VIEW_TYPE_MESSAGE_IMAGE_OTHER:
                return new EaseImageViewHolder(new EaseChatRowImage(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME), listener);
            case VIEW_TYPE_MESSAGE_VIDEO_ME:
            case VIEW_TYPE_MESSAGE_VIDEO_OTHER:
                return new EaseVideoViewHolder(new EaseChatRowVideo(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME), listener);
            case VIEW_TYPE_MESSAGE_VOICE_ME:
            case VIEW_TYPE_MESSAGE_VOICE_OTHER:
                return new EaseVoiceViewHolder(new EaseChatRowVoice(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME), listener);
            case VIEW_TYPE_MESSAGE_FILE_ME:
            case VIEW_TYPE_MESSAGE_FILE_OTHER:
                return new EaseFileViewHolder(new EaseChatRowFile(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME), listener);
            case VIEW_TYPE_MESSAGE_CUSTOM_ME:
            case VIEW_TYPE_MESSAGE_CUSTOM_OTHER:
                return new EaseCustomViewHolder(new EaseChatRowCustom(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME), listener);
            case VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY:
                return new EaseThreadNotifyViewHolder(new EaseChatRowThreadNotify(parent.getContext(), false), listener);
            case VIEW_TYPE_MESSAGE_UNKNOWN_ME:
            case VIEW_TYPE_MESSAGE_UNKNOWN_OTHER:
                return new EaseUnknownViewHolder(new EaseChatRowUnknown(parent.getContext(), viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME), listener);
            default:
                return new EaseUnknownViewHolder(new EaseChatRowUnknown(parent.getContext(), false), listener);
        }
    }

    public static int getViewType(@NonNull ChatMessage message) {
        return getChatType(message).getValue();
    }

    public static EaseMessageViewType getChatType(@NonNull ChatMessage message) {
        EaseMessageViewType type;
        ChatMessage.Type messageType = message.getType();
        ChatMessage.Direct direct = message.direct();
        if (messageType == ChatMessage.Type.TXT) {
            boolean isThreadNotify = message.getBooleanAttribute(EaseConstant.EASE_THREAD_NOTIFICATION_TYPE, false);
            if(isThreadNotify) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY;
            }else {
                if(direct == ChatMessage.Direct.SEND) {
                    type = EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME;
                }else {
                    type = EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER;
                }
            }
        } else if (messageType == ChatMessage.Type.IMAGE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.VIDEO) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER;
            }
        } else if (messageType == ChatMessage.Type.LOCATION) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_OTHER;
            }
        } else if (messageType == ChatMessage.Type.VOICE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.FILE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.CMD) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_CMD_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_CMD_OTHER;
            }
        } else if (messageType == ChatMessage.Type.CUSTOM) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER;
            }
        } else {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME;
            }else {
                type = EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER;
            }
        }
        return type;
    }
}
