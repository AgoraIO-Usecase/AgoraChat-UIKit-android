package io.agora.chat.uikit.chatthread.presenter;

import java.util.List;

import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseChatThreadListPresenter extends EaseBasePresenter {
    protected IChatThreadListView mView;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IChatThreadListView) view;
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
     * Get joined thread list by parent id
     * @param parentId  Usually is group id
     * @param limit
     * @param cursor
     */
    public abstract void getJoinedThreadList(String parentId, int limit, String cursor);

    /**
     * Get more joined thread list by parent id
     * @param parentId  Usually is group id
     * @param limit
     * @param cursor
     */
    public abstract void getMoreJoinedThreadList(String parentId, int limit, String cursor);

    /**
     * Get thread list by parent id
     * @param parentId  Usually is group id
     * @param limit
     * @param cursor
     */
    public abstract void getThreadList(String parentId, int limit, String cursor);

    /**
     * Get more thread list by parent id
     * @param parentId Usually is group id
     * @param limit
     * @param cursor
     */
    public abstract void getMoreThreadList(String parentId, int limit, String cursor);

    /**
     * Get thread latest messages
     * @param threadIds
     */
    public abstract void getThreadLatestMessages(List<String> threadIds);

    /**
     * Get thread's parent, first from local, if local is null, will get from server, usually is a group
     * @param parentId  Usually is group id
     */
    public abstract void getThreadParent(String parentId);
}
