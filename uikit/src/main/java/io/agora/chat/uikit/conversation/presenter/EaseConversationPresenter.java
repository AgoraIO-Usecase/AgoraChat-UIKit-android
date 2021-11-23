package io.agora.chat.uikit.conversation.presenter;


import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.utils.EaseUtils;

public abstract class EaseConversationPresenter extends EaseBasePresenter {
    public IEaseConversationListView mView;
    public boolean showSystemMessage;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IEaseConversationListView) view;
        showSystemMessage = EaseConfigsManager.isShowSysNotificationForConversation();
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
     * Whether to display notification messages
     * @param showSystemMessage
     */
    public void setShowSystemMessage(boolean showSystemMessage) {
        this.showSystemMessage = showSystemMessage;
    }

    /**
     * load data
     */
    public abstract void loadData(boolean fetchConfig);

    /**
     * Sort data
     * @param data
     */
    public abstract void sortData(List<EaseConversationInfo> data);

    /**
     * Make conversation read
     * @param position
     * @param info
     */
    public abstract void makeConversionRead(int position, EaseConversationInfo info);

    /**
     * Pin conversation
     * @param position
     * @param info
     */
    public abstract void makeConversationTop(int position, EaseConversationInfo info);

    /**
     * Unpin conversation
     * @param position
     * @param info
     */
    public abstract void cancelConversationTop(int position, EaseConversationInfo info);

    /**
     * Delete conversation
     * @param position
     * @param info
     */
    public abstract void deleteConversation(int position, EaseConversationInfo info);

}
