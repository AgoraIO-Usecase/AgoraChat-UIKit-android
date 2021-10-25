package io.agora.chat.uikit.models;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * The bean is used to provider custom set by developer
 */
public class EaseConvSet implements Serializable {
    private Drawable icon;
    private Drawable background;
    private String name;
    private String id;

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
}
