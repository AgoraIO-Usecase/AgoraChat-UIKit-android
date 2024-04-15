package com.hyphenate.easeui.configs

import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.widget.EaseImageView

/**
 * Used to control the display style of user avatars in the SDK.
 * @param avatarShape The shape of the avatar, the default is [EaseAvatarShape.DEFAULT].
 * @param avatarRadius The radius of the avatar, the default is 0.
 * @param avatarBorderColor The border color of the avatar, the default is 0.
 * @param avatarBorderWidth The border width of the avatar, the default is 0.
 */
data class EaseAvatarConfig(
    var avatarShape: EaseImageView.ShapeType = EaseImageView.ShapeType.NONE,
    var avatarRadius: Int = -1,
    var avatarBorderColor: Int = -1,
    var avatarBorderWidth: Int = -1,
) {
    fun getAvatarShapeIncludeDefault(): EaseImageView.ShapeType {
        return if (avatarShape != EaseImageView.ShapeType.NONE) {
            avatarShape
        } else {
            if (EaseIM.isInited()) {
                when (EaseIM.getContext()?.resources?.getInteger(R.integer.ease_avatar_shape_type) ?: 0) {
                    0 -> EaseImageView.ShapeType.NONE
                    1 -> EaseImageView.ShapeType.ROUND
                    2 -> EaseImageView.ShapeType.RECTANGLE
                    else -> EaseImageView.ShapeType.NONE
                }
            } else {
                avatarShape
            }
        }
    }

    fun getAvatarRadiusIncludeDefault(): Int {
        return if (avatarRadius != -1) {
            avatarRadius
        } else {
            if (EaseIM.isInited()) {
                EaseIM.getContext()?.resources?.getDimensionPixelSize(R.dimen.ease_avatar_round_radius) ?: avatarRadius
            } else {
                avatarRadius
            }
        }
    }

    fun getAvatarBorderColorIncludeDefault(): Int {
        return if (avatarBorderColor != -1) {
            avatarBorderColor
        } else {
            if (EaseIM.isInited()) {
                if (EaseIM.getContext() == null) {
                    avatarBorderColor
                } else {
                    ContextCompat.getColor(EaseIM.getContext()!!, R.color.ease_avatar_border_color)
                }
            } else {
                avatarBorderColor
            }
        }
    }

    fun getAvatarBorderWidthIncludeDefault(): Int {
        return if (avatarBorderWidth != -1) {
            avatarBorderWidth
        } else {
            if (EaseIM.isInited()) {
                EaseIM.getContext()?.resources?.getDimensionPixelSize(R.dimen.ease_avatar_border_width) ?: avatarBorderWidth
            } else {
                avatarBorderWidth
            }
        }
    }
}

/**
 * Set avatar by [EaseAvatarConfig].
 */
fun EaseAvatarConfig.setAvatarStyle(avatar: EaseImageView?) {
    if (avatar == null) return
    if (getAvatarShapeIncludeDefault() != EaseImageView.ShapeType.NONE) avatar.setShapeType(getAvatarShapeIncludeDefault())
    if (getAvatarRadiusIncludeDefault() != -1) avatar.setRadius(getAvatarRadiusIncludeDefault())
    if (getAvatarBorderWidthIncludeDefault() != -1) {
        avatar.setBorderWidth(getAvatarBorderWidthIncludeDefault())
        if (getAvatarBorderColorIncludeDefault() != -1) avatar.setBorderColor(getAvatarBorderColorIncludeDefault())
    }
}

/**
 * Set status by [EaseAvatarConfig].
 */
fun EaseAvatarConfig.setStatusStyle(avatar: EaseImageView?,borderWidth:Int,@ColorInt color:Int){
    if (avatar == null) return
    avatar.setShapeType(EaseImageView.ShapeType.ROUND)
    if (getAvatarBorderWidthIncludeDefault() != -1) {
        avatar.setBorderWidth(borderWidth)
        avatar.setBorderColor(color)
    }
}