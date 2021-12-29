package io.agora.chat.uikit.chat.interfaces;



import java.util.List;

import io.agora.chat.uikit.models.EaseEmojiconGroupEntity;

public interface IChatEmojiconMenu {
    /**
     * Add emoticon group
     * @param groupEntity
     */
    void addEmojiconGroup(EaseEmojiconGroupEntity groupEntity);

    /**
     * Add emoticons list
     * @param groupEntitieList
     */
    void addEmojiconGroup(List<EaseEmojiconGroupEntity> groupEntitieList);

    /**
     * Remove emoticon group
     * @param position
     */
    void removeEmojiconGroup(int position);

    /**
     * Set whether the TabBar is visible
     * @param isVisible
     */
    void setTabBarVisibility(boolean isVisible);

    /**
     * Set up emoticon monitoring
     * @param listener
     */
    void setEmojiconMenuListener(EaseEmojiconMenuListener listener);
}
