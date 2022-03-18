package io.agora.chat.uikit.thread.presenter;

import java.util.List;

import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseThreadListPresenter extends EaseBasePresenter {
    protected IThreadListView mView;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IThreadListView) view;
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
     * Get joined thread list
     * @param limit
     * @param cursor
     */
    public abstract void getJoinedThreadList(String groupId, int limit, String cursor);

    /**
     * Get more joined thread list
     * @param limit
     * @param cursor
     */
    public abstract void getMoreJoinedThreadList(String groupId, int limit, String cursor);

    /**
     * Get thread list by group id
     * @param groupId
     * @param limit
     * @param cursor
     */
    public abstract void getThreadList(String groupId, int limit, String cursor);

    /**
     * Get more thread list by group id
     * @param groupId
     * @param limit
     * @param cursor
     */
    public abstract void getMoreThreadList(String groupId, int limit, String cursor);

    /**
     * Get thread latest messages
     * @param threadIds
     */
    public abstract void getThreadLatestMessages(List<String> threadIds);

    /**
     * Get thread's parent, first from local, if local is null, will get from server, usually is a group
     * @param parentId
     */
    public abstract void getThreadParent(String parentId);
}
