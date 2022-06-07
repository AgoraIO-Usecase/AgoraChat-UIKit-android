package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface IRecyclerViewHandle {
    /**
     * Whether to use the default refresh
     * @param canUseRefresh
     */
    void canUseDefaultRefresh(boolean canUseRefresh);

    /**
     * Refresh messages
     */
    void refreshMessages();

    /**
     * Refresh and move to the latest piece of data
     */
    void refreshToLatest();

    /**
     * Refresh single message
     * @param message
     */
    void refreshMessage(ChatMessage message);

    /**
     * Refresh single message by id.
     * @param messageId
     */
    void refreshMessage(String messageId);

    /**
     * Delete single message
     * @param message
     */
    void removeMessage(ChatMessage message);

    /**
     * Move to the specified position
     * @param position
     */
    void moveToPosition(int position);
}

