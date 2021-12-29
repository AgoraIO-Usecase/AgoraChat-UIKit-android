package io.agora.chat.uikit.interfaces;

import android.graphics.drawable.Drawable;

import io.agora.chat.uikit.widget.EaseImageView;


public interface IAvatarSet {
    /**
     * Set default avatar
     * @param src
     */
    default void setAvatarDefaultSrc(Drawable src){}

    /**
     * Set the size of the avatar, the length and width are the same
     * @param avatarSize
     */
    void setAvatarSize(float avatarSize);

    /**
     * Set avatar style
     * @param shapeType
     */
    void setAvatarShapeType(EaseImageView.ShapeType shapeType);

    /**
     * Set avatar radius
     * @param radius
     */
    void setAvatarRadius(int radius);

    /**
     * Set the width of the outer border
     * @param borderWidth
     */
    void setAvatarBorderWidth(int borderWidth);

    /**
     * Set the outer border color
     * @param borderColor
     */
    void setAvatarBorderColor(int borderColor);
}

