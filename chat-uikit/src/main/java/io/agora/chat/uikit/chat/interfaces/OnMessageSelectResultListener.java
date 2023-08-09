package io.agora.chat.uikit.chat.interfaces;

import android.view.View;

import java.util.List;

/**
 * Listen to the result of the selected messages in chat message list.
 */
public interface OnMessageSelectResultListener {

    /**
     * The event of deleting messages.
     *
     * @param view              The EaseChatMultiSelectView object.
     * @param deleteMsgIdList
     * @return
     */
    boolean onMessageDelete(View view, List<String> deleteMsgIdList);

    /**
     * The event of replying messages.
     *
     * @param view              The EaseChatMultiSelectView object.
     * @param replyMsgIdList
     * @return
     */
    boolean onMessageReply(View view, List<String> replyMsgIdList);

}
