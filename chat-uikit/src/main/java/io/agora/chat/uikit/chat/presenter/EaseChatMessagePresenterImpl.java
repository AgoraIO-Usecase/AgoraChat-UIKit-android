package io.agora.chat.uikit.chat.presenter;

import android.text.TextUtils;


import java.util.Iterator;
import java.util.List;

import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.CursorResult;

public class EaseChatMessagePresenterImpl extends EaseChatMessagePresenter {

    @Override
    public void joinChatRoom(String username) {
        ChatClient.getInstance().chatroomManager().joinChatRoom(username, new ValueCallBack<ChatRoom>() {
            @Override
            public void onSuccess(ChatRoom value) {
                runOnUI(()-> {
                    if(isActive()) {
                        mView.joinChatRoomSuccess(value);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUI(() -> {
                    if(isActive()) {
                        mView.joinChatRoomFail(error, errorMsg);
                    }
                });
            }
        });
    }

    @Override
    public void loadLocalMessages(int pageSize) {
        loadLocalMessages(pageSize, Conversation.SearchDirection.UP);
    }

    @Override
    public void loadLocalMessages(int pageSize, Conversation.SearchDirection direction) {
        List<ChatMessage> messages = privateLoadMoreLocalMessages(null, pageSize, direction);
        if(messages == null || messages.isEmpty()) {
            runOnUI(()->mView.loadNoLocalMsg());
            return;
        }
        checkMessageStatus(messages);
        runOnUI(()->mView.loadLocalMsgSuccess(messages));
    }

    @Override
    public void loadMoreLocalMessages(String msgId, int pageSize) {
        loadMoreLocalMessages(msgId, pageSize, Conversation.SearchDirection.UP);
    }

    @Override
    public void loadMoreLocalMessages(String msgId, int pageSize, Conversation.SearchDirection direction) {
        List<ChatMessage> moreMsgs = privateLoadMoreLocalMessages(msgId, pageSize, direction);
        if(moreMsgs == null || moreMsgs.isEmpty()) {
            runOnUI(()->mView.loadNoMoreLocalMsg());
            return;
        }
        checkMessageStatus(moreMsgs);
        runOnUI(()->mView.loadMoreLocalMsgSuccess(moreMsgs));
    }

    @Override
    public void loadMoreRetrievalsMessages(String msgId, int pageSize) {
        List<ChatMessage> moreMsgs = privateLoadMoreLocalMessages(msgId, pageSize, Conversation.SearchDirection.UP);
        if(moreMsgs == null || moreMsgs.isEmpty()) {
            runOnUI(()->mView.loadNoMoreLocalMsg());
            return;
        }
        runOnUI(()->mView.loadMoreRetrievalsMessagesSuccess(moreMsgs));
    }

    @Override
    public void loadLocalHistoryMessages(String msgId, int pageSize) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        if(!isMessageId(msgId)) {
            throw new IllegalArgumentException("please check if set correct msg id");
        }
        ChatMessage message = ChatClient.getInstance().chatManager().getMessage(msgId);
        List<ChatMessage> messages = conversation.searchMsgFromDB(message.getMsgTime() - 1,
                pageSize, Conversation.SearchDirection.DOWN);
        if(isActive()) {
            runOnUI(()-> {
                if(messages == null || messages.isEmpty()) {
                    mView.loadNoMoreLocalHistoryMsg();
                }else {
                    mView.loadMoreLocalHistoryMsgSuccess(messages, Conversation.SearchDirection.DOWN);
                }
            });

        }
    }

    @Override
    public void loadMoreLocalHistoryMessages(String msgId, int pageSize, Conversation.SearchDirection direction) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        if(!isMessageId(msgId)) {
            throw new IllegalArgumentException("please check if set correct msg id");
        }
        List<ChatMessage> messages = conversation.loadMoreMsgFromDB(msgId, pageSize, direction);
        if(isActive()) {
            runOnUI(()-> {
                if(messages == null || messages.isEmpty()) {
                    mView.loadNoMoreLocalHistoryMsg();
                }else {
                    mView.loadMoreLocalHistoryMsgSuccess(messages, direction);
                }
            });

        }
    }

    @Override
    public void loadServerMessages(int pageSize) {
        loadServerMessages(pageSize, Conversation.SearchDirection.UP);
    }

    @Override
    public void loadServerMessages(int pageSize, Conversation.SearchDirection direction) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        ChatClient.getInstance().chatManager().asyncFetchHistoryMessage(conversation.conversationId(),
                conversation.getType(), pageSize, "", direction,
                new ValueCallBack<CursorResult<ChatMessage>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatMessage> value) {
                        conversation.loadMoreMsgFromDB("", pageSize, direction);
                        if(conversation.isChatThread()) {
                            checkIfReachFirstSendMessage(value);
                        }
                        runOnUI(() -> {
                            if(isActive()) {
                                mView.loadServerMsgSuccess(value.getData(), value.getCursor());
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUI(() -> {
                            if(isActive()) {
                                mView.loadMsgFail(error, errorMsg);
                                loadLocalMessages(pageSize);
                            }
                        });
                    }
                });
    }

    private void checkIfReachFirstSendMessage(CursorResult<ChatMessage> cursorResult) {
        if(cursorResult == null || isReachFirstFlagMessage) {
            return;
        }
        // if not more data, make isReachFirstFlagMessage to ture
        if(TextUtils.isEmpty(cursorResult.getCursor())) {
            isReachFirstFlagMessage = true;
            isReachFirstFlagMessage();
            return;
        }
        List<ChatMessage> data = cursorResult.getData();
        if(data == null || data.isEmpty() || reachFlagMessage == null) {
            return;
        }
        for (ChatMessage message : data) {
            String msgId = message.getMsgId();
            String firstSendMessageMsgId = reachFlagMessage.getMsgId();
            if(TextUtils.equals(msgId, firstSendMessageMsgId)) {
                isReachFirstFlagMessage = true;
                isReachFirstFlagMessage();
                break;
            }
        }
    }

    private void isReachFirstFlagMessage() {
        runOnUI(() -> {
            if(isActive()) {
                mView.reachedLatestThreadMessage();
            }
        });
    }

    @Override
    public void loadMoreServerMessages(String msgId, int pageSize) {
        loadMoreServerMessages(msgId, pageSize, Conversation.SearchDirection.UP);
    }

    @Override
    public void loadMoreServerMessages(String msgId, int pageSize, Conversation.SearchDirection direction) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        if(!isMessageId(msgId)) {
            throw new IllegalArgumentException("please check if set correct msg id");
        }
        ChatClient.getInstance().chatManager().asyncFetchHistoryMessage(conversation.conversationId(),
                conversation.getType(), pageSize, msgId, direction,
                new ValueCallBack<CursorResult<ChatMessage>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatMessage> value) {
                        conversation.loadMoreMsgFromDB(msgId, pageSize, direction);
                        if(conversation.isChatThread()) {
                            checkIfReachFirstSendMessage(value);
                        }
                        runOnUI(() -> {
                            if(isActive()) {
                                mView.loadMoreServerMsgSuccess(value.getData(), value.getCursor());
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUI(() -> {
                            if(isActive()) {
                                mView.loadMsgFail(error, errorMsg);
                                loadMoreLocalMessages(msgId, pageSize);
                            }
                        });
                    }
                });
    }

    @Override
    public void refreshCurrentConversation() {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }

        List<ChatMessage> allMessages = conversation.getAllMessages();
        if(conversation.isChatThread() && reachFlagMessage != null && !isReachFirstFlagMessage) {
            removeNotReachedMessages(allMessages);
        }
        if(isActive()) {
            runOnUI(()->mView.refreshCurrentConSuccess(allMessages, false));
        }
    }

    /**
     * Delete messages with timestamps after the first sent message
     * @param allMessages
     */
    private void removeNotReachedMessages(List<ChatMessage> allMessages) {
        if(reachFlagMessage == null) {
            return;
        }
        long firstSendMsgTime  = reachFlagMessage.getMsgTime();
        Iterator<ChatMessage> iterator = allMessages.iterator();
        while (iterator.hasNext()) {
            long msgTime = iterator.next().getMsgTime();

            if(msgTime >= firstSendMsgTime) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshToLatest() {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        conversation.markAllMessagesAsRead();
        List<ChatMessage> allMessages = conversation.getAllMessages();
        if(conversation.isChatThread() && !isReachFirstFlagMessage) {
            removeNotReachedMessages(allMessages);
        }
        if(isActive()) {
            runOnUI(()->mView.refreshCurrentConSuccess(allMessages, true));
        }
    }

    /**
     * Determine whether it is a message id
     * @param msgId
     * @return
     */
    public boolean isMessageId(String msgId) {
        if(TextUtils.isEmpty(msgId)) {
            //Can allow message id to be empty
            return true;
        }
        ChatMessage message = conversation.getMessage(msgId, true);
        return message != null;
    }

    /**
     * Check message's status, if is not success or fail, set to {@link ChatMessage.Status#FAIL}
     * @param messages
     */
    private void checkMessageStatus(List<ChatMessage> messages) {
        if(messages == null || messages.isEmpty()) {
            return;
        }
        for (ChatMessage message : messages) {
            if(message.status() != ChatMessage.Status.SUCCESS && message.status() != ChatMessage.Status.FAIL) {
                message.setStatus(ChatMessage.Status.FAIL);
            }
        }
    }

    private List<ChatMessage> privateLoadMoreLocalMessages(String startMsgId, int pageSize, Conversation.SearchDirection direction) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        if(!isMessageId(startMsgId)) {
            throw new IllegalArgumentException("please check if set correct msg id");
        }
        List<ChatMessage> moreMsgs = null;
        try {
            moreMsgs = conversation.loadMoreMsgFromDB(startMsgId, pageSize, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moreMsgs;
    }
}

