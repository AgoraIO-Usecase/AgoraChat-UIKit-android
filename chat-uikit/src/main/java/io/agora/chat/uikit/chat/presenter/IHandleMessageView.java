package io.agora.chat.uikit.chat.presenter;


import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IHandleMessageView extends ILoadDataView {
    /**
     * Failed to generate video cover
     * @param message
     */
    void createThumbFileFail(String message);

    /**
     * Before sending a message, add message attributes, such as setting ext, etc.
     * @param message
     */
    void addMsgAttrBeforeSend(ChatMessage message);

    /**
     * Failed to send message
     * @param message
     */
    void sendMessageFail(String message);

    /**
     * Complete the message sending action
     * @param message
     */
    void sendMessageFinish(ChatMessage message);

    /**
     * Delete local message
     * @param message
     */
    void deleteLocalMessageSuccess(ChatMessage message);

    /**
     * Complete withdrawal message
     * @param originalMessage The message was unsent
     * @param notification  The notification message
     */
    void recallMessageFinish(ChatMessage originalMessage, ChatMessage notification);

    /**
     * Failed to withdraw the message
     * @param code
     * @param message
     */
    void recallMessageFail(int code, String message);

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
     * add reaction success
     *
     * @param message
     */
    void addReactionMessageSuccess(ChatMessage message);

    /**
     * add reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    void addReactionMessageFail(ChatMessage message, int code, String error);

    /**
     * remove reaction success
     *
     * @param message
     */
    void removeReactionMessageSuccess(ChatMessage message);

    /**
     * remove reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    void removeReactionMessageFail(ChatMessage message, int code, String error);

}
