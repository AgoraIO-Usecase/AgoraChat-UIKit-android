package io.agora.chat.uikit.chat.presenter;


import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public interface IChatMessageListView extends ILoadDataView {
    /**
     * 获取当前会话
     * @return
     */
    Conversation getCurrentConversation();
    /**
     * 加入聊天室成功
     * @param value
     */
    void joinChatRoomSuccess(ChatRoom value);

    /**
     * 加入聊天室失败
     * @param error
     * @param errorMsg
     */
    void joinChatRoomFail(int error, String errorMsg);

    /**
     * 加载消息失败
     * @param error
     * @param message
     */
    void loadMsgFail(int error, String message);

    /**
     * 加载本地数据成功
     * @param data
     */
    void loadLocalMsgSuccess(List<ChatMessage> data);

    /**
     * 没有加载到本地数据
     */
    void loadNoLocalMsg();

    /**
     * 加载本地更多数据成功
     * @param data
     */
    void loadMoreLocalMsgSuccess(List<ChatMessage> data);

    /**
     * 没有加载到更多数据
     */
    void loadNoMoreLocalMsg();

    /**
     * 加载更多本地的历史数据
     * @param data
     */
    void loadMoreLocalHistoryMsgSuccess(List<ChatMessage> data, Conversation.SearchDirection direction);

    /**
     * 没有更多的本地历史数据
     */
    void loadNoMoreLocalHistoryMsg();

    /**
     * 加载漫游数据
     * @param data
     */
    void loadServerMsgSuccess(List<ChatMessage> data);

    /**
     * 加载更多漫游数据
     * @param data
     */
    void loadMoreServerMsgSuccess(List<ChatMessage> data);

    /**
     * 刷新当前会话
     * @param data
     */
    void refreshCurrentConSuccess(List<ChatMessage> data, boolean toLatest);
}
