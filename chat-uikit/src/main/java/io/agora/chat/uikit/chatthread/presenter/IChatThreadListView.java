package io.agora.chat.uikit.chatthread.presenter;

import java.util.List;
import java.util.Map;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.CursorResult;
import io.agora.chat.Group;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IChatThreadListView extends ILoadDataView {

    /**
     * Get thread list success
     * @param result
     */
    void getJoinedThreadListSuccess(CursorResult<ChatThread> result);

    /**
     * Get no data of joined thread list
     */
    void getNoJoinedThreadListData();

    /**
     * Get thread list fail
     * @param code
     * @param message
     */
    void getJoinedThreadListFail(int code, String message);

    /**
     * Get more thread list success
     * @param result
     */
    void getMoreJoinedThreadListSuccess(CursorResult<ChatThread> result);

    /**
     * Get no more joined thread list
     */
    void getNoMoreJoinedThreadList();

    /**
     * Get more thread list fail
     * @param code
     * @param message
     */
    void getMoreJoinedThreadListFail(int code, String message);

    /**
     * Get thread list success
     * @param result
     */
    void getThreadListSuccess(CursorResult<ChatThread> result);

    /**
     * Get no data of thread list
     */
    void getNoThreadListData();

    /**
     * Get thread list fail
     * @param code
     * @param message
     */
    void getThreadListFail(int code, String message);

    /**
     * Get more thread list success
     * @param result
     */
    void getMoreThreadListSuccess(CursorResult<ChatThread> result);

    /**
     * Get no more data of thread list
     */
    void getNoMoreThreadList();

    /**
     * Get more thread list fail
     * @param code
     * @param message
     */
    void getMoreThreadListFail(int code, String message);

    /**
     * Get thread id list
     * @param threadIds
     */
    void getThreadIdList(List<String> threadIds);

    /**
     * Get thread latest message success
     * @param latestMessageMap
     */
    void getLatestThreadMessagesSuccess(Map<String, ChatMessage> latestMessageMap);

    /**
     * Get no data of latest thread messages
     */
    void getNoDataLatestThreadMessages();
    /**
     * Get thread latest message failed
     * @param code
     * @param message
     */
    void getLatestThreadMessagesFail(int code, String message);

    /**
     * Get group info success
     * @param group
     */
    void getThreadParentInfoSuccess(Group group);

    /**
     * Get group info fail
     * @param code
     * @param message
     */
    void getThreadParentInfoFail(int code, String message);

}
