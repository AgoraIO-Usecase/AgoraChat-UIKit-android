package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.model.ChatUIKitEmojiconGroupEntity
interface IChatEmojiconMenu {
    /**
     * Add emoticon group
     * @param groupEntity
     */
    fun addEmojiconGroup(groupEntity: ChatUIKitEmojiconGroupEntity)

    /**
     * Add emoticons list
     * @param groupEntitieList
     */
    fun addEmojiconGroup(groupEntitieList: List<ChatUIKitEmojiconGroupEntity>?)

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
    fun setEmojiconMenuListener(listener: ChatUIKitEmojiconMenuListener?)
}