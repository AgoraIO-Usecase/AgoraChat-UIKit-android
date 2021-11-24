package io.agora.chat.uikit.chat.interfaces;

import android.graphics.drawable.Drawable;
import android.widget.EditText;

import io.agora.chat.uikit.chat.model.EaseInputMenuStyle;


public interface IChatPrimaryMenu {
    /**
     * Set menu display type
     * @param style
     */
    void setMenuShowType(EaseInputMenuStyle style);

    void showNormalStatus();

    void showTextStatus();

    void showVoiceStatus();

    void showEmojiconStatus();

    void showMoreStatus();

    void hideExtendStatus();

    void hideSoftKeyboard();

   /**
     * Enter emoticon event
     * @param emojiContent
     */
    void onEmojiconInputEvent(CharSequence emojiContent);

    /**
     * Delete emoticon event
     */
    void onEmojiconDeleteEvent();

    /**
     * Insert text
     * @param text
     */
    void onTextInsert(CharSequence text);

    /**
     * Get EditText
     * @return
     */
    EditText getEditText();

    /**
     * Set the background of the input box
     * @param bg
     */
    void setMenuBackground(Drawable bg);

    /**
     * Set the send button background
     * @param bg
     */
    void setSendButtonBackground(Drawable bg);

    /**
     * Set up monitoring
     * @param listener
     */
    void setEaseChatPrimaryMenuListener(EaseChatPrimaryMenuListener listener);
}
