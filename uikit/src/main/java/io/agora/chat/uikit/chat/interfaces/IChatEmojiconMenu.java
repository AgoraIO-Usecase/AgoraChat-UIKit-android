package io.agora.chat.uikit.chat.interfaces;



import java.util.List;

import io.agora.chat.uikit.models.EaseEmojiconGroupEntity;

public interface IChatEmojiconMenu {
    /**
     * 添加表情
     * @param groupEntity
     */
    void addEmojiconGroup(EaseEmojiconGroupEntity groupEntity);

    /**
     * 添加表情列表
     * @param groupEntitieList
     */
    void addEmojiconGroup(List<EaseEmojiconGroupEntity> groupEntitieList);

    /**
     * 移除表情
     * @param position
     */
    void removeEmojiconGroup(int position);

    /**
     * 设置TabBar是否可见
     * @param isVisible
     */
    void setTabBarVisibility(boolean isVisible);

    /**
     * 设置表情监听
     * @param listener
     */
    void setEmojiconMenuListener(EaseEmojiconMenuListener listener);
}
