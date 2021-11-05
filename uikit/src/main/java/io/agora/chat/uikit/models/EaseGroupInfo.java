package io.agora.chat.uikit.models;

import android.graphics.drawable.Drawable;

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
}
