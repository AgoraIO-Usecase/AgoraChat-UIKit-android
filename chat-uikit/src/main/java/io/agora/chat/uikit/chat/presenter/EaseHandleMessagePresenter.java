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
     * Bind sender id
     * @param chatType
     * @param toChatUsername
     */
    public void setupWithToUser(int chatType, @NonNull String toChatUsername) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        conversation = ChatClient.getInstance().chatManager().getConversation(toChatUsername, EaseUtils.getConversationType(chatType), true);
    }

    /**
     * Send text message
     * @param content
     */
    public abstract void sendTextMessage(String content);

    /**
     * Send text message
     * @param content
     * @param isNeedGroupAck Whether need a group receipt
     */
    public abstract void sendTextMessage(String content, boolean isNeedGroupAck);

    /**
     * Send @ message
     * @param content
     */
    public abstract void sendAtMessage(String content);

    /**
     * Send GIF message
     * @param name
     * @param identityCode
     */
    public abstract void sendBigExpressionMessage(String name, String identityCode);

    /**
     * Send voice message
     * @param filePath
     * @param length
     */
    public abstract void sendVoiceMessage(Uri filePath, int length);

    /**
     * Send image message
     * @param imageUri
     */
    public abstract void sendImageMessage(Uri imageUri);

    /**
     * Send image message
     * @param imageUri
     * @param sendOriginalImage
     */
    public abstract void sendImageMessage(Uri imageUri, boolean sendOriginalImage);

    /**
     * Send location message
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    public abstract void sendLocationMessage(double latitude, double longitude, String locationAddress);

    /**
     * Send video message
     * @param videoUri
     * @param videoLength
     */
    public abstract void sendVideoMessage(Uri videoUri, int videoLength);

    /**
     * Send file message
     * @param fileUri
     */
    public abstract void sendFileMessage(Uri fileUri);

    /**
     * Add extension fields to the message
     * @param message
     */
    public abstract void addMessageAttributes(ChatMessage message);

    /**
     * Send message
     * @param message
     */
    public abstract void sendMessage(ChatMessage message);

    /**
     * Send cmd message
     * @param action
     */
    public abstract void sendCmdMessage(String action);

    /**
     * Resend message
     * @param message
     */
    public abstract void resendMessage(ChatMessage message);

    /**
     * Delete local message
     * @param message
     */
    public abstract void deleteMessage(ChatMessage message);

    /**
     * Withdraw message
     * @param message
     */
    public abstract void recallMessage(ChatMessage message);

    /**
     * Determine whether it is a group chat
     * @return
     */
    public boolean isGroupChat() {
        return chatType == EaseConstant.CHATTYPE_GROUP;
    }

    /**
     * add reaction
     *
     * @param message
     * @param reaction
     */
    public abstract void addReaction(ChatMessage message, String reaction);

    /**
     * remove reaction
     *
     * @param message
     * @param reaction
     */
    public abstract void removeReaction(ChatMessage message, String reaction);
}

