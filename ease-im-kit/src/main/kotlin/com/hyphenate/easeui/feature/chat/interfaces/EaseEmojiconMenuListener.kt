package com.hyphenate.easeui.feature.chat.interfaces

interface EaseEmojiconMenuListener {
    /**
     * on emojicon clicked
     * @param emojiIcon
     */
    fun onExpressionClicked(emojiIcon: Any?)

    /**
     * on delete image clicked
     */
    fun onDeleteImageClicked() {}

    /**
     * On send icon clicked
     */
    fun onSendIconClicked() {}
}