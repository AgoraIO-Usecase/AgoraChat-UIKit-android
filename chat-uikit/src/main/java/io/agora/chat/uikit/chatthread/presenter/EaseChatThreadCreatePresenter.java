package io.agora.chat.uikit.chatthread.presenter;

import android.net.Uri;
import android.widget.EditText;


import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.chat.presenter.IBaseHandleMessage;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.ILoadDataView;
import io.agora.chat.uikit.menu.EaseChatType;

public abstract class EaseChatThreadCreatePresenter extends EaseBasePresenter implements IBaseHandleMessage {
    protected IChatThreadCreateView mView;
    protected EaseChatType chatType = EaseChatType.GROUP_CHAT;
    protected EditText etInput;
    protected String toChatUsername;
    protected String parentId;
    protected String messageId;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IChatThreadCreateView) view;
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
     * @param parentId
     * @param messageId
     */
    public void setupWithToUser(String parentId, String messageId, EditText etInput) {
        this.parentId = parentId;
        this.messageId = messageId;
        this.etInput = etInput;
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
     * Send group ding message
     * @param message
     */
    public abstract void sendGroupDingMessage(ChatMessage message);

    /**
     * Add extension fields to the message
     * @param message
     */
    public abstract void addMessageAttributes(ChatMessage message);

    public abstract void createThread(String threadName, ChatMessage message);

    /**
     * Determine whether it is a group chat
     * @return
     */
    public boolean isGroupChat() {
        return chatType == EaseChatType.GROUP_CHAT;
    }

}
