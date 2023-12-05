package io.agora.chat.uikit.interfaces;

import java.util.List;

import io.agora.chat.ChatMessage;

/**
 * Download and parse combine message attachment's listener.
 */
public interface OnCombineMessageDownloadAndParseListener {
    /**
     * Download and parse combine message attachment successfully.
     */
    void onDownloadAndParseSuccess(List<ChatMessage> messageList);

    /**
     * Failed to download and parse combined message.
     * @param message
     * @param code
     * @param errorMsg
     */
    void onDownloadOrParseFailed(ChatMessage message, int code, String errorMsg);
}
