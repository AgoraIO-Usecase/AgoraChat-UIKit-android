package io.agora.chat.uikit.chat.viewholder;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowCustom;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowFile;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowImage;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowText;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowUnknown;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVideo;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowVoice;

public class EaseChatViewHolderFactory {
    public static EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage> createViewHolder(@NonNull ViewGroup parent, EaseChatType viewType
            , MessageListItemClickListener listener) {

        switch (viewType) {
            case VIEW_TYPE_MESSAGE_TXT_ME:
            case VIEW_TYPE_MESSAGE_TXT_OTHER:
                return new EaseTextViewHolder(new EaseChatRowText(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_TXT_ME), listener);
            case VIEW_TYPE_MESSAGE_IMAGE_ME:
            case VIEW_TYPE_MESSAGE_IMAGE_OTHER:
                return new EaseTextViewHolder(new EaseChatRowImage(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_IMAGE_ME), listener);
            case VIEW_TYPE_MESSAGE_VIDEO_ME:
            case VIEW_TYPE_MESSAGE_VIDEO_OTHER:
                return new EaseTextViewHolder(new EaseChatRowVideo(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_VIDEO_ME), listener);
            case VIEW_TYPE_MESSAGE_VOICE_ME:
            case VIEW_TYPE_MESSAGE_VOICE_OTHER:
                return new EaseTextViewHolder(new EaseChatRowVoice(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_VOICE_ME), listener);
            case VIEW_TYPE_MESSAGE_FILE_ME:
            case VIEW_TYPE_MESSAGE_FILE_OTHER:
                return new EaseTextViewHolder(new EaseChatRowFile(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_FILE_ME), listener);
            case VIEW_TYPE_MESSAGE_CUSTOM_ME:
            case VIEW_TYPE_MESSAGE_CUSTOM_OTHER:
                return new EaseTextViewHolder(new EaseChatRowCustom(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_CUSTOM_ME), listener);
            case VIEW_TYPE_MESSAGE_UNKNOWN_ME:
            case VIEW_TYPE_MESSAGE_UNKNOWN_OTHER:
                return new EaseUnknownViewHolder(new EaseChatRowUnknown(parent.getContext(), viewType == EaseChatType.VIEW_TYPE_MESSAGE_UNKNOWN_ME), listener);
            default:
                return new EaseUnknownViewHolder(new EaseChatRowUnknown(parent.getContext(), false), listener);
        }
    }

    public static int getViewType(@NonNull ChatMessage message) {
        return getChatType(message).getValue();
    }

    public static EaseChatType getChatType(@NonNull ChatMessage message) {
        EaseChatType type;
        ChatMessage.Type messageType = message.getType();
        ChatMessage.Direct direct = message.direct();
        if (messageType == ChatMessage.Type.TXT) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_TXT_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_TXT_OTHER;
            }
        } else if (messageType == ChatMessage.Type.IMAGE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_IMAGE_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_IMAGE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.VIDEO) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_VIDEO_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_VIDEO_OTHER;
            }
        } else if (messageType == ChatMessage.Type.LOCATION) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_LOCATION_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_LOCATION_OTHER;
            }
        } else if (messageType == ChatMessage.Type.VOICE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_VOICE_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_VOICE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.FILE) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_FILE_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_FILE_OTHER;
            }
        } else if (messageType == ChatMessage.Type.CMD) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_CMD_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_CMD_OTHER;
            }
        } else if (messageType == ChatMessage.Type.CUSTOM) {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_CUSTOM_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER;
            }
        } else {
            if(direct == ChatMessage.Direct.SEND) {
                type = EaseChatType.VIEW_TYPE_MESSAGE_UNKNOWN_ME;
            }else {
                type = EaseChatType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER;
            }
        }
        return type;
    }
}
