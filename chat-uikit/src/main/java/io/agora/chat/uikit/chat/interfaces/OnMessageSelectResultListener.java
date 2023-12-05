package io.agora.chat.uikit.chat.interfaces;

import android.view.View;

import java.util.List;

/**
 * Listen to the result of the selected messages in chat message list.
 */
public interface OnMessageSelectResultListener {

    enum SelectType {
        DELETE,
        FORWARD
    }

    /**
     * Callback when the selected messages are deleted or forwarded.
     *
     * @param view             The EaseChatMultiSelectView object.
     * @param type             The type of the selected messages.
     * @param msgIdList        The list of the selected messages.
     * @return
     */
    boolean onSelectResult(View view, SelectType type, List<String> msgIdList);

}
