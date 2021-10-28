package io.agora.chat.uikit.chat.presenter;


import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IHandleMessageView extends ILoadDataView {
    /**
     * 生成视频封面失败
     * @param message
     */
    void createThumbFileFail(String message);

    /**
     * 在发送消息前，添加消息属性，如设置ext等
     * @param message
     */
    void addMsgAttrBeforeSend(ChatMessage message);

    /**
     * 发送消息失败
     * @param message
     */
    void sendMessageFail(String message);

    /**
     * 完成发送消息动作
     * @param message
     */
    void sendMessageFinish(ChatMessage message);

    /**
     * 删除本地消息
     * @param message
     */
    void deleteLocalMessageSuccess(ChatMessage message);

    /**
     * 完成撤回消息
     * @param message
     */
    void recallMessageFinish(ChatMessage message);

    /**
     * 撤回消息失败
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
}
