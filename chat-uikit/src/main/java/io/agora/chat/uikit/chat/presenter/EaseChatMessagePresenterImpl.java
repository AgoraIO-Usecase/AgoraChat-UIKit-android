package io.agora.chat.uikit.chat.presenter;

import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.CursorResult;
import io.agora.chat.TextMessageBody;
import io.agora.util.EMLog;

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
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        List<ChatMessage> messages = null;
        try {
            messages = conversation.loadMoreMsgFromDB(null, pageSize, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(messages == null || messages.isEmpty()) {
            if(isActive()) {
                runOnUI(()->mView.loadNoLocalMsg());
            }
            return;
        }
        if(isActive()) {
            checkMessageStatus(messages);
            List<ChatMessage> finalMessages = messages;
            runOnUI(()->mView.loadLocalMsgSuccess(finalMessages));
        }
    }

    @Override
    public void loadMoreLocalMessages(String msgId, int pageSize) {
        loadMoreLocalMessages(msgId, pageSize, Conversation.SearchDirection.UP);
    }

    @Override
    public void loadMoreLocalMessages(String msgId, int pageSize, Conversation.SearchDirection direction) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        if(!isMessageId(msgId)) {
            throw new IllegalArgumentException("please check if set correct msg id");
        }
        List<ChatMessage> moreMsgs = null;
        try {
            moreMsgs = conversation.loadMoreMsgFromDB(msgId, pageSize, direction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(moreMsgs == null || moreMsgs.isEmpty()) {
            if(isActive()) {
                runOnUI(()->mView.loadNoMoreLocalMsg());
            }
            return;
        }
        if(isActive()) {
            checkMessageStatus(moreMsgs);
            List<ChatMessage> finalMoreMsgs = moreMsgs;
            runOnUI(()->mView.loadMoreLocalMsgSuccess(finalMoreMsgs));
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
        ChatMessage message = conversation.getMessage(msgId, true);
        List<ChatMessage> messages = conversation.searchMsgFromDB(message.getMsgTime() - 1,
                                                                pageSize, direction);
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
        conversation.markAllMessagesAsRead();
        // Not use cache data when is chat thread conversation
        if(conversation.isThread()) {
            return;
        }
        List<ChatMessage> allMessages = conversation.getAllMessages();
        if(isActive()) {
            runOnUI(()->mView.refreshCurrentConSuccess(allMessages, false));
        }
    }

    @Override
    public void refreshToLatest() {
        refreshToLatest(true);
    }

    @Override
    public void refreshToLatest(boolean useCacheData) {
        if(conversation == null) {
            throw new NullPointerException("should first set up with conversation");
        }
        conversation.markAllMessagesAsRead();
        if(useCacheData) {
            if(conversation.isThread()) {
                ChatMessage lastMessage = conversation.getLastMessage();
                if(isActive()) {
                    mView.insertMessageToLast(lastMessage);
                }
                return;
            }
            List<ChatMessage> allMessages = conversation.getAllMessages();
            if(isActive()) {
                runOnUI(()->mView.refreshCurrentConSuccess(allMessages, true));
            }
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
}

