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
     * Set custom top extension menu.
     * @param menu
     */
    void setCustomTopExtendMenu(IChatTopExtendMenu menu);

    /**
     * Hide extended area (including emoticons and extended menu)
     */
    void hideExtendContainer();

    /**
     * Hide input menu exclude top extend menu.
     */
    void hideInputMenu();

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
     * Whether to show the top extension menu
     * @param isShow
     */
    void showTopExtendMenu(boolean isShow);

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
     * Get the top extension menu
     * @return
     */
    IChatTopExtendMenu getChatTopExtendMenu();

    /**
     * Click back
     * @return
     */
    boolean onBackPressed();
}
