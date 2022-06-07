package io.agora.chat.uikit.chatthread.presenter;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IChatThreadCreateView extends ILoadDataView {

    /**
     * Failed to send message
     * @param message
     */
    void sendMessageFail(String message);

    /**
     * Before sending a message, add message attributes, such as setting ext, etc.
     * @param message
     */
    void addMsgAttrBeforeSend(ChatMessage message);

    /**
     * message send success
     * @param message
     */
    void onPresenterMessageSuccess(ChatMessage message);

    /**
     * message send fail
     * @param message
     * @param code
     * @param error
     */
    void onPresenterMessageError(ChatMessage message, int code, String error);

    /**
     * message in sending progress
     * @param message
     * @param progress
     */
    void onPresenterMessageInProgress(ChatMessage message, int progress);

    /**
     * Complete the message sending action
     * @param message
     */
    void sendMessageFinish(ChatMessage message);

    /**
     * Create thread success
     * @param thread
     * @param message
     */
    void onCreateThreadSuccess(ChatThread thread, ChatMessage message);

    /**
     * Create thread failed
     * @param errorCode
     * @param message
     */
    void onCreateThreadFail(int errorCode, String message);
}
