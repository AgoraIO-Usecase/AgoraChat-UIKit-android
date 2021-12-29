package io.agora.chat.uikit.chat.interfaces;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;


public interface IChatMessageItemSet {
    /**
     * Set default avatar
     * @param src
     */
    void setAvatarDefaultSrc(Drawable src);

    /**
     * Set avatar shape
     * @param shapeType
     */
    void setAvatarShapeType(int shapeType);

    /**
     * Whether to show nickname
     * @param showNickname
     */
    void showNickname(boolean showNickname);

    /**
     * Set the background of the item sender
     * @param bgDrawable
     */
    void setItemSenderBackground(Drawable bgDrawable);

    /**
     * Set the receiver's background
     * @param bgDrawable
     */
    void setItemReceiverBackground(Drawable bgDrawable);

    /**
     * Set text message font size
     * @param textSize
     */
    void setItemTextSize(int textSize);

    /**
     * Set text message font color
     * @param textColor
     */
    void setItemTextColor(@ColorInt int textColor);

    /**
     * Set the timeline text size
     * @param textSize
     */
    void setTimeTextSize(int textSize);

    /**
     * Set the timeline text color
     * @param textColor
     */
    void setTimeTextColor(int textColor);

    /**
     * Set the timeline background
     * @param bgDrawable
     */
    void setTimeBackground(Drawable bgDrawable);

    /**
     * Set the display style of the chat list
     * @param type
     */
    void setItemShowType(EaseChatMessageListLayout.ShowType type);

    /**
     * Hide receiver's avatar, default is false
     * @param hide
     */
    void hideChatReceiveAvatar(boolean hide);

    /**
     * Hide sender's avatar, default is false
     * @param hide
     */
    void hideChatSendAvatar(boolean hide);
}
