package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.chat.presenter.EaseChatQuotePresenter;

public interface IChatQuote {
    /**
     * Set quote message.
     * @param message
     */
    void startQuote(ChatMessage message);

    /**
     * Hide quote view.
     */
    void hideQuoteView();

    /**
     * Set quote message presenter.
     * @param presenter
     */
    void setPresenter(EaseChatQuotePresenter presenter);
}
