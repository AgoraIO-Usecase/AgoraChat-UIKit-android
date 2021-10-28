package io.agora.chat.uikit.chat.presenter;

import android.net.Uri;

import androidx.annotation.NonNull;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.ILoadDataView;
import io.agora.chat.uikit.utils.EaseUtils;


public abstract class EaseHandleMessagePresenter extends EaseBasePresenter {
    protected IHandleMessageView mView;
    protected int chatType;
    protected String toChatUsername;
    protected Conversation conversation;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IHandleMessageView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachView();
    }

    /**
     * 绑定发送方id
     * @param chatType
     * @param toChatUsername
     */
    public void setupWithToUser(int chatType, @NonNull String toChatUsername) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        conversation = ChatClient.getInstance().chatManager().getConversation(toChatUsername, EaseUtils.getConversationType(chatType), true);
    }

    /**
     * 发送文本消息
     * @param content
     */
    public abstract void sendTextMessage(String content);

    /**
     * 发送文本消息
     * @param content
     * @param isNeedGroupAck 需要需要群回执
     */
    public abstract void sendTextMessage(String content, boolean isNeedGroupAck);

    /**
     * 发送@消息
     * @param content
     */
    public abstract void sendAtMessage(String content);

    /**
     * 发送大表情消息
     * @param name
     * @param identityCode
     */
    public abstract void sendBigExpressionMessage(String name, String identityCode);

    /**
     * 发送语音消息
     * @param filePath
     * @param length
     */
    public abstract void sendVoiceMessage(Uri filePath, int length);

    /**
     * 发送图片消息
     * @param imageUri
     */
    public abstract void sendImageMessage(Uri imageUri);

    /**
     * 发送图片消息
     * @param imageUri
     * @param sendOriginalImage
     */
    public abstract void sendImageMessage(Uri imageUri, boolean sendOriginalImage);

    /**
     * 发送定位消息
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    public abstract void sendLocationMessage(double latitude, double longitude, String locationAddress);

    /**
     * 发送视频消息
     * @param videoUri
     * @param videoLength
     */
    public abstract void sendVideoMessage(Uri videoUri, int videoLength);

    /**
     * 发送文件消息
     * @param fileUri
     */
    public abstract void sendFileMessage(Uri fileUri);

    /**
     * 为消息添加扩展字段
     * @param message
     */
    public abstract void addMessageAttributes(ChatMessage message);

    /**
     * 发送消息
     * @param message
     */
    public abstract void sendMessage(ChatMessage message);

    /**
     * 发送cmd消息
     * @param action
     */
    public abstract void sendCmdMessage(String action);

    /**
     * 重新发送消息
     * @param message
     */
    public abstract void resendMessage(ChatMessage message);

    /**
     * 删除消息
     * @param message
     */
    public abstract void deleteMessage(ChatMessage message);

    /**
     * 撤回消息
     * @param message
     */
    public abstract void recallMessage(ChatMessage message);

    /**
     * 是否是群聊
     * @return
     */
    public boolean isGroupChat() {
        return chatType == EaseConstant.CHATTYPE_GROUP;
    }
}

