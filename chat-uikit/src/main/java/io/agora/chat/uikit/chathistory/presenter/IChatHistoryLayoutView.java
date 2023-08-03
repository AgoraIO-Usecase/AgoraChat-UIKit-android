package io.agora.chat.uikit.chathistory.presenter;


import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IChatHistoryLayoutView extends ILoadDataView {

    /**
     * Download combine message successfully.
     * @param messageList   Parsed message list.
     */
    void downloadCombinedMessagesSuccess(List<ChatMessage> messageList);

    /**
     * Download or parse combine message failed.
     * @param error
     * @param errorMsg
     */
    void downloadCombinedMessagesFailed(int error, String errorMsg);

    /**
     * Download message attachment thumbnail successfully.
     * @param message
     * @param position
     */
    void downloadThumbnailSuccess(ChatMessage message, int position);

    /**
     * Download message attachment thumbnail failed.
     * @param message
     * @param position
     * @param error     error code.
     * @param errorMsg  error message.
     */
    void downloadThumbnailFailed(ChatMessage message, int position, int error, String errorMsg);

    /**
     * Download voice successfully.
     * @param message
     * @param position
     */
    void downloadVoiceSuccess(ChatMessage message, int position);

    /**
     * Download voice failed.
     * @param message
     * @param position
     * @param error
     * @param errorMsg
     */
    void downloadVoiceFailed(ChatMessage message, int position, int error, String errorMsg);

    /**
     * Refresh message list.
     */
    void refreshAll();

    /**
     * Refresh the specified item by position.
     * @param message   消息对象
     * @param position  消息对象所在列表的位置
     */
    void refreshItem(ChatMessage message, int position);

}
