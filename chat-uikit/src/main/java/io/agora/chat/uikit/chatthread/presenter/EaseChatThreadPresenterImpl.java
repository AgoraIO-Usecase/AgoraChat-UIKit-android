package io.agora.chat.uikit.chatthread.presenter;

import android.text.TextUtils;

import io.agora.CallBack;
import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatThread;
import io.agora.chat.Group;

public class EaseChatThreadPresenterImpl extends EaseChatThreadPresenter {

    @Override
    public void getThreadInfo(String threadId) {
        ChatClient.getInstance().chatThreadManager().getChatThreadFromServer(threadId, new ValueCallBack<ChatThread>() {
            @Override
            public void onSuccess(ChatThread value) {
                if(isActive()) {
                    runOnUI(()->mView.onGetThreadInfoSuccess(value));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isActive()) {
                    runOnUI(()->mView.onGetThreadInfoFail(error, errorMsg));
                }
            }
        });
    }

    @Override
    public void joinThread(String threadId) {
        ChatClient.getInstance().chatThreadManager().joinChatThread(threadId, new ValueCallBack<ChatThread>() {
            @Override
            public void onSuccess(ChatThread thread) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()->mView.OnJoinThreadSuccess(thread));
            }

            @Override
            public void onError(int code, String error) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()->mView.OnJoinThreadFail(code, error));
            }
        });
    }

    @Override
    public void getGroupInfo(String groupId) {
        Group group = ChatClient.getInstance().groupManager().getGroup(groupId);
        if(group != null && !TextUtils.isEmpty(group.getGroupName())) {
            if(isDestroy()) {
                return;
            }
            runOnUI(()->mView.onGetGroupInfoSuccess(group));
            return;
        }
        ChatClient.getInstance().groupManager().asyncGetGroupFromServer(groupId, new ValueCallBack<Group>() {
            @Override
            public void onSuccess(Group value) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()->mView.onGetGroupInfoSuccess(value));
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isDestroy()) {
                    return;
                }
                runOnUI(()->mView.onGetGroupInfoFail(error, errorMsg));
            }
        });
    }
}
