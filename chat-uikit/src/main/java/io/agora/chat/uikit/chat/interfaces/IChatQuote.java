package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.ChatMessage;

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
}
