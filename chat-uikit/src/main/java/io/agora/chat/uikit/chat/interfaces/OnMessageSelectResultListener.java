package io.agora.chat.uikit.chat.interfaces;

import java.util.List;

/**
 * Listen to the result of the selected messages in chat message list.
 */
public interface OnMessageSelectResultListener {

    /**
     * The event of deleting messages.
     * @param deleteMsgIdList
     * @return
     */
    boolean onMessageDelete(List<String> deleteMsgIdList);

    /**
     * The event of replying messages.
     * @param replyMsgIdList
     * @return
     */
    boolean onMessageReply(List<String> replyMsgIdList);
}
