package io.agora.chat.uikit.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import java.io.Serializable;

/**
 * The bean is used to provide custom settings by developer
 * Note: group chat type and chat room type can use
 */
public class EaseGroupInfo implements Serializable {
    private Drawable icon;
    /**
     * Icon url
     * Note: if you want to use local image, you can use like this:
     * For example:
     * EaseGroupInfo info = new EaseGroupInfo();
     * info.setIconUrl(String.valueOf(R.drawable.default_group_icon));
     */
    private String iconUrl;
    private Drawable background;
    private String name;
    private String id;
    private boolean isGroup;
    // avatar settings, you need to use EaseImageView
    private AvatarSettings avatarSettings;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public AvatarSettings getAvatarSettings() {
        return avatarSettings;
    }

    public void setAvatarSettings(AvatarSettings avatarSettings) {
        this.avatarSettings = avatarSettings;
    }

    public static class AvatarSettings implements Serializable{
        private int avatarShapeType;
        private int avatarBorderWidth;
        private @ColorInt int avatarBorderColor;
        private int avatarRadius;

        public int getAvatarShapeType() {
            return avatarShapeType;
        }

        /**
         * Set group's avatar shape
         * 0：default，1：round，2：rectangle
         * @param avatarShapeType
         */
        public void setAvatarShapeType(int avatarShapeType) {
            this.avatarShapeType = avatarShapeType;
        }

        public int getAvatarBorderWidth() {
            return avatarBorderWidth;
        }

        public void setAvatarBorderWidth(int avatarBorderWidth) {
            this.avatarBorderWidth = avatarBorderWidth;
        }

        public int getAvatarBorderColor() {
            return avatarBorderColor;
        }

        public void setAvatarBorderColor(@ColorInt int avatarBorderColor) {
            this.avatarBorderColor = avatarBorderColor;
        }

        public int getAvatarRadius() {
            return avatarRadius;
        }

        public void setAvatarRadius(int avatarRadius) {
            this.avatarRadius = avatarRadius;
        }
    }
}
