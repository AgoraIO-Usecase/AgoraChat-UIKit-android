package io.agora.chat.uikit.chat.presenter;

import android.text.TextUtils;

import io.agora.Error;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.FileMessageBody;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.LocationMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseChatQuotePresenterImpl extends EaseChatQuotePresenter {
    @Override
    public void showQuoteMessageInfo(ChatMessage message) {
        if(message == null || message.getBody() == null) {
            mView.onShowError(Error.GENERAL_ERROR, "Message or body cannot be null.");
            return;
        }
        EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
        String from = "";
        if (user == null){
            from = message.getFrom();
        }else {
            if (TextUtils.isEmpty(user.getNickname())){
                from = user.getUsername();
            }else {
                from = user.getNickname();
            }
        }
        mView.showQuoteMessageNickname(from);
        StringBuilder builder = new StringBuilder();
        String localPath = null;
        String remoteUrl = null;
        switch (message.getType()){
            case TXT:
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    builder.append(mView.context().getResources().getString(R.string.ease_emoji));
                }else {
                    TextMessageBody textBody = (TextMessageBody) message.getBody();
                    builder.append(EaseSmileUtils.getSmiledText(mView.context(), textBody != null ? from + ": " + textBody.getMessage() : "").toString());
                }
                break;
            case VOICE:
                VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
                builder.append(mView.context().getResources().getString(R.string.ease_voice)).append(":").append(((voiceBody != null && voiceBody.getLength() > 0)? voiceBody.getLength() : 0) + "\"");
                mView.showQuoteMessageAttachment(ChatMessage.Type.VOICE, null, null, R.drawable.ease_chatfrom_voice_playing);
                break;
            case VIDEO:
                builder.append(mView.context().getResources().getString(R.string.ease_video));
                VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
                if(videoBody != null) {
                    if(!TextUtils.isEmpty(videoBody.getLocalThumb()) && EaseFileUtils.isFileExistByUri(mView.context(), videoBody.getLocalThumbUri())) {
                        localPath = videoBody.getLocalThumb();
                    }
                    remoteUrl = videoBody.getThumbnailUrl();
                }
                mView.showQuoteMessageAttachment(ChatMessage.Type.VIDEO, localPath, remoteUrl, R.drawable.ease_default_image);
                break;
            case FILE:
                FileMessageBody fileBody = (FileMessageBody) message.getBody();
                builder.append(mView.context().getResources().getString(R.string.ease_file)).append(fileBody != null ? ": "+fileBody.getFileName() : "");
                mView.showQuoteMessageAttachment(ChatMessage.Type.FILE, null, null, R.drawable.ease_chat_quote_message_attachment);
                break;
            case IMAGE:
                builder.append(mView.context().getResources().getString(R.string.ease_picture));
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                if(imageBody != null) {
                    if(!TextUtils.isEmpty(imageBody.getThumbnailUrl()) && EaseFileUtils.isFileExistByUri(mView.context(), imageBody.getLocalUri())) {
                        localPath = imageBody.getLocalUrl();
                    }
                    remoteUrl = imageBody.getRemoteUrl();
                }
                mView.showQuoteMessageAttachment(ChatMessage.Type.IMAGE, localPath, remoteUrl, R.drawable.ease_default_image);
                break;
            case LOCATION:
                LocationMessageBody locationBody = (LocationMessageBody) message.getBody();
                builder.append(mView.context().getResources().getString(R.string.ease_location));
                if(locationBody != null && !TextUtils.isEmpty(locationBody.getAddress())) {
                    builder.append(": ").append(locationBody.getAddress());
                }
                break;
            case CUSTOM:
                builder.append(mView.context().getResources().getString(R.string.ease_custom));
                break;
            case COMBINE:
                builder.append(mView.context().getResources().getString(R.string.ease_combine));
                break;
            default:
                break;
        }
        mView.showQuoteMessageContent(builder);
    }
}
