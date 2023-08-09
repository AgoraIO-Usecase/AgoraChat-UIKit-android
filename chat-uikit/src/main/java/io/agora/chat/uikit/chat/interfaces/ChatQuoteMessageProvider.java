package io.agora.chat.uikit.chat.interfaces;

import android.text.SpannableString;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.interfaces.IUIKitInterface;

/**
 * Provide quote message content.
 */
public interface ChatQuoteMessageProvider extends IUIKitInterface {
    /**
     * Provide quote message content by SpannableString.
     * @param quoteMessage
     * @param quoteMsgType
     * @param quoteSender
     * @param quoteContent
     * @return
     */
    SpannableString provideQuoteContent(ChatMessage quoteMessage, ChatMessage.Type quoteMsgType, String quoteSender, String quoteContent);
}