package io.agora.chat.uikit.provider;

import io.agora.chat.uikit.models.EaseConvSet;

public interface EaseConversationInfoProvider {
    /**
     * 获取默认类型头像
     * @param conversationId
     * @return
     */
    EaseConvSet getConversationInfo(String conversationId);
}
