package io.agora.chat.uikit.conversation.interfaces;


import java.util.List;

import io.agora.chat.uikit.conversation.model.EaseConversationInfo;

public interface OnConversationLoadListener {
    /**
     * Call back after loading
     * @param data
     */
    void loadDataFinish(List<EaseConversationInfo> data);

    /**
     * Call back after failed to load data
     * @param message
     */
    default void loadDataFail(String message) {}

}
