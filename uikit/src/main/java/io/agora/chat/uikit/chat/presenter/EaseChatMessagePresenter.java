package io.agora.chat.uikit.chat.presenter;


import io.agora.chat.Conversation;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseChatMessagePresenter extends EaseBasePresenter {
    public IChatMessageListView mView;
    public Conversation conversation;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IChatMessageListView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachView();
    }

    /**
     * 与会话绑定
     * @param conversation
     */
    public void setupWithConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public abstract void joinChatRoom(String username);

    /**
     * 加载本地数据
     * @param pageSize
     */
    public abstract void loadLocalMessages(int pageSize);

    /**
     * 加载更多本地数据
     * @param pageSize
     */
    public abstract void loadMoreLocalMessages(String msgId, int pageSize);

    /**
     * 从本地加载更多历史数据
     * @param msgId
     * @param pageSize
     * @param direction
     */
    public abstract void loadMoreLocalHistoryMessages(String msgId, int pageSize, Conversation.SearchDirection direction);

    /**
     * 从服务器加载数据
     * @param pageSize
     */
    public abstract void loadServerMessages(int pageSize);
    /**
     * 从服务器加载更多数据
     * @param msgId 消息id
     * @param pageSize
     */
    public abstract void loadMoreServerMessages(String msgId, int pageSize);

    /**
     * 刷新当前的会话
     */
    public abstract void refreshCurrentConversation();

    /**
     * 刷新当前会话，并移动到最新
     */
    public abstract void refreshToLatest();
}

