package com.hyphenate.easeui.feature.group.config

import android.util.TypedValue
import android.view.View
import com.hyphenate.easeui.databinding.EaseLayoutGroupListItemBinding
import com.hyphenate.easeui.widget.EaseImageView


fun EaseGroupListConfig.bindView(binding: EaseLayoutGroupListItemBinding){
    with(binding) {
        if (itemNameTextSize != -1) groupName.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemNameTextSize.toFloat())
        if (itemNameTextColor != -1) groupName.setTextColor(itemNameTextColor)
        if (avatarSize != -1) {
            val layoutParams = groupAvatar.layoutParams
            layoutParams.height = avatarSize
            layoutParams.width = avatarSize
        }
        if (avatarShape != EaseImageView.ShapeType.NONE) {
            groupAvatar.setShapeType(avatarShape)
        }
        if (avatarBorderWidth != -1) {
            groupAvatar.setBorderWidth(avatarBorderWidth)
        }
        if (avatarBorderColor != -1) {
            groupAvatar.setBorderColor(avatarBorderColor)
        }
        if (itemHeight != -1f) {
            val layoutParams = root.layoutParams
            layoutParams.height = itemHeight.toInt()
        }
        if (!isShowDivider) divider.visibility = View.GONE
    }
}