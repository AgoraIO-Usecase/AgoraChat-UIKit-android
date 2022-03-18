package io.agora.chat.uikit.thread.presenter;

import io.agora.CallBack;
import io.agora.ValueCallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatThread;

public class EaseThreadChatPresenterImpl extends EaseThreadChatPresenter {

    @Override
    public void getThreadInfo(String threadId) {
        ChatClient.getInstance().threadManager().getThreadFromServer(threadId, new ValueCallBack<ChatThread>() {
            @Override
            public void onSuccess(ChatThread value) {
                if(isActive()) {
                    mView.onGetThreadInfoSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(isActive()) {
                    mView.onGetThreadInfoFail(error, errorMsg);
                }
            }
        });
    }

    @Override
    public void joinThread(String threadId) {
        ChatClient.getInstance().threadManager().joinThread(threadId, new CallBack() {
            @Override
            public void onSuccess() {
                
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
}
