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
     * Bind to conversation
     * @param conversation
     */
    public void setupWithConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public abstract void joinChatRoom(String username);

    /**
     * Load local messages
     * @param pageSize
     */
    public abstract void loadLocalMessages(int pageSize);

    /**
     * Load more local messages
     * @param pageSize
     */
    public abstract void loadMoreLocalMessages(String msgId, int pageSize);

    /**
     * Load more historical data locally
     * @param msgId
     * @param pageSize
     * @param direction
     */
    public abstract void loadMoreLocalHistoryMessages(String msgId, int pageSize, Conversation.SearchDirection direction);

    /**
     * Load data from the server
     * @param pageSize
     */
    public abstract void loadServerMessages(int pageSize);
    
    /**
     * Load data from the server
     * @param pageSize
     */
    public abstract void loadServerMessages(int pageSize, Conversation.SearchDirection direction);
    
    /**
     * Load more data from the server
     * @param msgId 消息id
     * @param pageSize
     */
    public abstract void loadMoreServerMessages(String msgId, int pageSize);
    
    /**
     * Load more data from the server
     * @param msgId 消息id
     * @param pageSize
     */
    public abstract void loadMoreServerMessages(String msgId, int pageSize, Conversation.SearchDirection direction);

    /**
     * Refresh current conversation
     */
    public abstract void refreshCurrentConversation();

    /**
     * Refresh the current session and move to the latest
     */
    public abstract void refreshToLatest();
}

