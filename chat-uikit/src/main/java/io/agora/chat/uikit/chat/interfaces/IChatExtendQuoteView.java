package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IChatExtendQuoteView extends ILoadDataView {

    /**
     * Show nickname.
     * @param nickname
     */
    void showQuoteMessageNickname(String nickname);

    /**
     * Show content.
     * @param content
     */
    void showQuoteMessageContent(StringBuilder content);

    /**
     * Show attachment.
     * @param localPath
     * @param remotePath
     * @param defaultResource
     */
    void showQuoteMessageAttachment(ChatMessage.Type type, String localPath, String remotePath, int defaultResource);

    /**
     * Show error message.
     * @param code
     * @param message
     */
    void onShowError(int code, String message);
}
