package io.agora.chat.uikit.conversation.interfaces;

import android.graphics.drawable.Drawable;

import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.interfaces.IAvatarSet;


public interface IConversationStyle extends IAvatarSet, IConversationTextStyle {

    /**
     * 设置条目背景
     * @param backGround
     */
    void setItemBackGround(Drawable backGround);

    /**
     * 设置条目高度
     * @param height
     */
    void setItemHeight(int height);

    /**
     * 是否展示未读红点
     * @param hide
     */
    void hideUnreadDot(boolean hide);

    /**
     * 是否展示系统消息
     * @param show
     */
    void showSystemMessage(boolean show);

    /**
     * 未读数显示位置
     * 目前支持左侧和右侧两种
     * @param position
     */
    void showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition position);

    /**
     * Set unread view's style , see {@link io.agora.chat.uikit.conversation.model.EaseConversationSetStyle.UnreadStyle}
     * @param style
     */
    void setUnreadStyle(EaseConversationSetStyle.UnreadStyle style);
}
