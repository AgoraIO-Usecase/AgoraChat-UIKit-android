package io.agora.chat.uikit.chathistory.presenter;


import io.agora.chat.ChatMessage;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseChatHistoryPresenter extends EaseBasePresenter {
    public IChatHistoryLayoutView mView;
    public Conversation conversation;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IChatHistoryLayoutView) view;
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
     * Download and parse combine message
     * @param combinedMessage
     */
    public abstract void downloadCombineMessage(ChatMessage combinedMessage);

    /**
     * Download image thumbnail.
     * @param message
     * @param position
     */
    public abstract void downloadThumbnail(ChatMessage message, int position);

    /**
     * Download voice attachment.
     * @param message
     * @param position
     */
    public abstract void downloadVoice(ChatMessage message, int position);
}

