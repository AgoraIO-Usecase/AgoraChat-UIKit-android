package io.agora.uikit.feature.group.config

import io.agora.uikit.widget.EaseImageView

data class EaseGroupListConfig(
    var itemNameTextSize: Int = -1,
    var itemNameTextColor: Int = -1,
    var isShowDivider: Boolean = true,
    var avatarSize: Int = -1,
    var avatarShape: EaseImageView.ShapeType = EaseImageView.ShapeType.NONE,
    var avatarRadius: Int = -1,
    var avatarBorderWidth: Int = -1,
    var avatarBorderColor: Int = -1,
    var itemHeight: Float = -1f,
)