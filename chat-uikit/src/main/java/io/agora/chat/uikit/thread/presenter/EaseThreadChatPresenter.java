package io.agora.chat.uikit.thread.presenter;

import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseThreadChatPresenter extends EaseBasePresenter {
    protected IThreadChatView mView;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IThreadChatView) view;
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

    public abstract void getThreadInfo(String threadId);
}
