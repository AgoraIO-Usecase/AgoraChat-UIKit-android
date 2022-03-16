package io.agora.chat.uikit.thread.presenter;

import io.agora.chat.ChatThread;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IThreadChatView extends ILoadDataView {

    /**
     * Get thread info success
     * @param thread
     */
    void onGetThreadInfoSuccess(ChatThread thread);

    /**
     * Get thread info failed
     * @param error
     * @param errorMsg
     */
    void onGetThreadInfoFail(int error, String errorMsg);
}
