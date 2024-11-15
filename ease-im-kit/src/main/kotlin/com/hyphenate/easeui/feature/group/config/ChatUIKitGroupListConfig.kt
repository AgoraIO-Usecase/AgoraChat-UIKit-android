package com.hyphenate.easeui.feature.group.config

import com.hyphenate.easeui.widget.ChatUIKitImageView

data class ChatUIKitGroupListConfig(
    var itemNameTextSize: Int = -1,
    var itemNameTextColor: Int = -1,
    var isShowDivider: Boolean = true,
    var avatarSize: Int = -1,
    var avatarShape: ChatUIKitImageView.ShapeType = ChatUIKitImageView.ShapeType.NONE,
    var avatarRadius: Int = -1,
    var avatarBorderWidth: Int = -1,
    var avatarBorderColor: Int = -1,
    var itemHeight: Float = -1f,
)