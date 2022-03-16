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


public abstract class EaseHandleMessagePresenter extends EaseBasePresenter implements IBaseHandleMessage{
    protected IHandleMessageView mView;
    protected int chatType;
    protected String toChatUsername;
    protected Conversation conversation;
    protected boolean isThread;

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
        setupWithToUser(chatType, toChatUsername, false);
    }
    
    /**
     * Bind sender id
     * @param chatType
     * @param toChatUsername
     */
    public void setupWithToUser(int chatType, @NonNull String toChatUsername, boolean isThread) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        this.isThread = isThread;
        conversation = ChatClient.getInstance().chatManager().getConversation(toChatUsername, EaseUtils.getConversationType(chatType), true);
    }

    /**
     * Send text message
     * @param content
     */
    public abstract void sendTextMessage(String content);

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
     * Send image message
     * @param imageUri
     */
    public abstract void sendImageMessage(Uri imageUri);

    /**
     * Add extension fields to the message
     * @param message
     */
    public abstract void addMessageAttributes(ChatMessage message);

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
}

