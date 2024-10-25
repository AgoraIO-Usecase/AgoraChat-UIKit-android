package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.model.EaseEmojiconGroupEntity
interface IChatEmojiconMenu {
    /**
     * Add emoticon group
     * @param groupEntity
     */
    fun addEmojiconGroup(groupEntity: EaseEmojiconGroupEntity)

    /**
     * Add emoticons list
     * @param groupEntitieList
     */
    fun addEmojiconGroup(groupEntitieList: List<EaseEmojiconGroupEntity>?)

    /**
     * Remove emoticon group
     * @param position
     */
    fun removeEmojiconGroup(position: Int)

    /**
     * Set whether the TabBar is visible
     * @param isVisible
     */
    fun setTabBarVisibility(isVisible: Boolean)

    /**
     * Set up emoticon monitoring
     * @param listener
     */
    fun setEmojiconMenuListener(listener: EaseEmojiconMenuListener?)
}