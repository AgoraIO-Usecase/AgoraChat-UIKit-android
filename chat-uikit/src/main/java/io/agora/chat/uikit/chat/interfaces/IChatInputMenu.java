package io.agora.chat.uikit.chat.interfaces;

public interface IChatInputMenu {

    /**
     * Set custom menu
     * @param menu
     */
    void setCustomPrimaryMenu(IChatPrimaryMenu menu);

    /**
     * Set up a custom emoji
     * @param menu
     */
    void setCustomEmojiconMenu(IChatEmojiconMenu menu);

    /**
     * Set up a custom extended menu
     * @param menu
     */
    void setCustomExtendMenu(IChatExtendMenu menu);

    /**
     * Hide extended area (including emoticons and extended menu)
     */
    void hideExtendContainer();

    /**
     * Whether to show the emoji menu
     * @param show
     */
    void showEmojiconMenu(boolean show);

    /**
     * Whether to show the extended menu
     * @param show
     */
    void showExtendMenu(boolean show);

    /**
     * Hide soft keyboard
     */
    void hideSoftKeyboard();

    /**
     * Set menu listener
     * @param listener
     */
    void setChatInputMenuListener(ChatInputMenuListener listener);

    /**
     * Get menu
     * @return
     */
    IChatPrimaryMenu getPrimaryMenu();

    /**
     * Get emoji menu
     * @return
     */
    IChatEmojiconMenu getEmojiconMenu();

    /**
     * Get extended menu
     * @return
     */
    IChatExtendMenu getChatExtendMenu();

    /**
     * Click back
     * @return
     */
    boolean onBackPressed();
}
