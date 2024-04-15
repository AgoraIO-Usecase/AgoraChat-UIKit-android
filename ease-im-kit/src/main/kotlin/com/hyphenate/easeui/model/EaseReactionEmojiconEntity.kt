package com.hyphenate.easeui.model

data class EaseReactionEmojiconEntity(
    var emojicon: EaseEmojicon? = null,
    var count: Int = 0,
    var userList: List<String>? = null,
    var isAddedBySelf: Boolean = false
)