package io.agora.chat.uikit.options;

/**
 * Created by wei on 2016/11/29.
 */

public class EaseAvatarOptions {
    private int avatarShape;
    private int avatarRadius;
    private int avatarBorderColor;
    private int avatarBorderWidth;

    public int getAvatarShape() {
        return avatarShape;
    }

    /**
     * Set the shape of the avatar in the item
     * 0：default，1：round，2：rectangle
     * @param avatarShape
     */
    public void setAvatarShape(int avatarShape) {
        this.avatarShape = avatarShape;
    }

    public int getAvatarRadius() {
        return avatarRadius;
    }

    /**
     * Set chamfer
     *
     * @param avatarRadius
     */
    public void setAvatarRadius(int avatarRadius) {
        this.avatarRadius = avatarRadius;
    }

    public int getAvatarBorderColor() {
        return avatarBorderColor;
    }

    /**
     * Set the border color of the avatar control
     *
     * @param avatarBorderColor
     */
    public void setAvatarBorderColor(int avatarBorderColor) {
        this.avatarBorderColor = avatarBorderColor;
    }

    public int getAvatarBorderWidth() {
        return avatarBorderWidth;
    }

    /**
     * Set the border width of the avatar control
     *
     * @param avatarBorderWidth
     */
    public void setAvatarBorderWidth(int avatarBorderWidth) {
        this.avatarBorderWidth = avatarBorderWidth;
    }

}
