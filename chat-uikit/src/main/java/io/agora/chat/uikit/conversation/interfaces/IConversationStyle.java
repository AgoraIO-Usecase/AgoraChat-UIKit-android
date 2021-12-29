package io.agora.chat.uikit.conversation.interfaces;

import android.graphics.drawable.Drawable;

import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.interfaces.IAvatarSet;


public interface IConversationStyle extends IAvatarSet, IConversationTextStyle {

    void setItemBackGround(Drawable backGround);

    void setItemHeight(int height);

    /**
     * Whether to display unread red dots
     * @param hide
     */
    void hideUnreadDot(boolean hide);

    /**
     * Unread display position
     * Currently supports left and right
     * @param position
     */
    void showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition position);

    /**
     * Set unread view's style , see {@link io.agora.chat.uikit.conversation.model.EaseConversationSetStyle.UnreadStyle}
     * @param style
     */
    void setUnreadStyle(EaseConversationSetStyle.UnreadStyle style);
}
