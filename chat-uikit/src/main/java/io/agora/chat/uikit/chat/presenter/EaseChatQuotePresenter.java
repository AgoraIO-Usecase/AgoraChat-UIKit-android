package io.agora.chat.uikit.chat.presenter;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.base.EaseBasePresenter;
import io.agora.chat.uikit.chat.interfaces.IChatExtendQuoteView;
import io.agora.chat.uikit.interfaces.ILoadDataView;

public abstract class EaseChatQuotePresenter extends EaseBasePresenter {
    protected IChatExtendQuoteView mView;

    @Override
    public void attachView(ILoadDataView view) {
        mView = (IChatExtendQuoteView) view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    /**
     * Show quote message info.
     * @param message
     */
    public abstract void showQuoteMessageInfo(ChatMessage message);
}
