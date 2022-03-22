package io.agora.chat.uikit.thread.presenter;

import io.agora.chat.ChatThread;
import io.agora.chat.Group;
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

    /**
     * Join thread success or have joined
     */
    void OnJoinThreadSuccess();

    /**
     * Join thread failed
     * @param error
     * @param errorMsg
     */
    void OnJoinThreadFail(int error, String errorMsg);

    /**
     * Get group info success
     * @param group
     */
    void onGetGroupInfoSuccess(Group group);

    /**
     * Get group info failed
     * @param error
     * @param errorMsg
     */
    void onGetGroupInfoFail(int error, String errorMsg);
}
