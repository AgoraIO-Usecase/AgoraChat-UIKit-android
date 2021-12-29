package io.agora.chat.uikit.provider;

import io.agora.chat.uikit.models.EaseGroupInfo;

public interface EaseGroupInfoProvider {
    /**
     * Provide group info
     * @param groupId
     * @param type 1: group, 2:chatroom, 3:system chat
     * @return
     */
    EaseGroupInfo getGroupInfo(String groupId, int type);
}
