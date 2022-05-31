package io.agora.chat.uikit.chat.presenter;


import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;
import io.agora.util.EMLog;

public abstract class EaseChatMessagePresenter extends EaseBasePresenter {
    public IChatMessageListView mView;
    public Conversation conversation;
    protected ChatMessage reachFlagMessage;
    /**
     * The flag whether the current conversation is reach the first flag message
     */
    protected boolean isReachFirstFlagMessage = false;

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
        EMLog.e("EaseChatMessagePresenter", "conversation isThread: "+conversation.isChatThread()+"conversationId: "+conversation.conversationId());
        // Chat thread conversation should clear cache data
        if(conversation != null && conversation.isChatThread()) {
            conversation.clear();
        }
    }

    public abstract void joinChatRoom(String username);

    /**
     * Load local messages
     * @param pageSize
     */
    public abstract void loadLocalMessages(int pageSize);

    /**
     * Load local messages
     * @param pageSize
     * @param direction
     */
    public abstract void loadLocalMessages(int pageSize, Conversation.SearchDirection direction);

    /**
     * Load more local messages
     * @param msgId
     * @param pageSize
     */
    public abstract void loadMoreLocalMessages(String msgId, int pageSize);

    /**
     * Load more local messages
     * @param msgId
     * @param pageSize
     * @param direction
     */
    public abstract void loadMoreLocalMessages(String msgId, int pageSize, Conversation.SearchDirection direction);

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

    /**
     * Set current conversation flag message used for chat thread conversation
     * @param message
     */
    public void setSendOrReceiveMessage(ChatMessage message) {
        if(reachFlagMessage == null) {
            reachFlagMessage = message;
        }
    }
}

