package io.agora.chat.uikit.chatthread.presenter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.CursorResult;
import io.agora.chat.Group;

public class EaseChatThreadListPresenterImpl extends EaseChatThreadListPresenter {
    @Override
    public void getJoinedThreadList(String parentId, int limit, String cursor) {
        ChatClient.getInstance().chatThreadManager().getJoinedChatThreadsFromServer(parentId, limit, cursor,
                new ValueCallBack<CursorResult<ChatThread>>() {
            @Override
            public void onSuccess(CursorResult<ChatThread> value) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(() -> {
                    if(value == null) {
                        mView.getNoJoinedThreadListData();
                        return;
                    }
                    mView.getJoinedThreadListSuccess(value);
                    getThreadIdList(value.getData());
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()-> {
                    mView.getJoinedThreadListFail(error, errorMsg);
                });
            }
        });
    }

    @Override
    public void getMoreJoinedThreadList(String parentId, int limit, String cursor) {
        ChatClient.getInstance().chatThreadManager().getJoinedChatThreadsFromServer(parentId, limit, cursor,
                new ValueCallBack<CursorResult<ChatThread>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatThread> value) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            if(value == null) {
                                mView.getNoMoreJoinedThreadList();
                                return;
                            }
                            List<ChatThread> data = value.getData();
                            if(data == null || data.size() == 0) {
                                mView.getNoMoreJoinedThreadList();
                                return;
                            }
                            mView.getMoreJoinedThreadListSuccess(value);
                            if(data.size() < limit) {
                                mView.getNoMoreJoinedThreadList();
                            }
                            getThreadIdList(data);
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            mView.getJoinedThreadListFail(error, errorMsg);
                        });
                    }
                });
    }

    @Override
    public void getThreadList(String parentId, int limit, String cursor) {
        ChatClient.getInstance().chatThreadManager().getChatThreadsFromServer(parentId, limit, cursor,
                new ValueCallBack<CursorResult<ChatThread>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatThread> value) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            if(value == null) {
                                mView.getNoThreadListData();
                                return;
                            }
                            List<ChatThread> data = value.getData();
                            if(data == null || data.size() == 0) {
                                mView.getNoThreadListData();
                                return;
                            }
                            mView.getThreadListSuccess(value);
                            getThreadIdList(data);
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            mView.getThreadListFail(error, errorMsg);
                        });
                    }
                });
    }

    @Override
    public void getMoreThreadList(String parentId, int limit, String cursor) {
        ChatClient.getInstance().chatThreadManager().getChatThreadsFromServer(parentId, limit, cursor,
                new ValueCallBack<CursorResult<ChatThread>>() {
                    @Override
                    public void onSuccess(CursorResult<ChatThread> value) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            if(value == null) {
                                mView.getNoMoreThreadList();
                                return;
                            }
                            List<ChatThread> data = value.getData();
                            if(data == null || data.size() == 0) {
                                mView.getNoMoreThreadList();
                                return;
                            }
                            mView.getMoreThreadListSuccess(value);
                            if(data.size() < limit) {
                                mView.getNoMoreThreadList();
                            }
                            getThreadIdList(data);
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if(isDestroy()) {
                            return;
                        }
                        runOnUI(()-> {
                            mView.getThreadListFail(error, errorMsg);
                        });
                    }
                });
    }

    @Override
    public void getThreadLatestMessages(List<String> threadIds) {
        ChatClient.getInstance().chatThreadManager().getChatThreadLatestMessage(threadIds, new ValueCallBack<Map<String, ChatMessage>>() {
            @Override
            public void onSuccess(Map<String, ChatMessage> value) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()-> {
                    if(value == null || value.isEmpty()) {
                        mView.getNoDataLatestThreadMessages();
                        return;
                    }
                    mView.getLatestThreadMessagesSuccess(value);
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()-> {
                    mView.getLatestThreadMessagesFail(error, errorMsg);
                });
            }
        });
    }

    @Override
    public void getThreadParent(String parentId) {
        Group group = ChatClient.getInstance().groupManager().getGroup(parentId);
        if(group == null || TextUtils.isEmpty(group.getGroupName())) {
            ChatClient.getInstance().groupManager().asyncGetGroupFromServer(parentId, new ValueCallBack<Group>() {
                @Override
                public void onSuccess(Group value) {
                    if(isDestroy()) {
                        return;
                    }
                    runOnUI(()-> {
                        mView.getThreadParentInfoSuccess(value);
                    });
                }

                @Override
                public void onError(int error, String errorMsg) {
                    if(isDestroy()) {
                        return;
                    }
                    runOnUI(()-> {
                        mView.getThreadParentInfoFail(error, errorMsg);
                    });
                }
            });
        }else {
            if(isDestroy()) {
                return;
            }
            runOnUI(()-> {
                mView.getThreadParentInfoSuccess(group);
            });
        }
    }

    private void getThreadIdList(List<ChatThread> data) {
        if(data == null || data.size() <= 0) {
            return;
        }
        List<String> threadIds = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            threadIds.add(data.get(i).getChatThreadId());
        }
        if(isDestroy()) {
            return;
        }
        mView.getThreadIdList(threadIds);
    }
}
