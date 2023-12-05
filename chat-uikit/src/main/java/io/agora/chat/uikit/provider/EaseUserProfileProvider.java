package io.agora.chat.uikit.provider;

import io.agora.chat.uikit.models.EaseUser;

/**
 * User profile provider
 * @author wei
 *
 */
public interface EaseUserProfileProvider {
    /**
     * return EaseUser for input username
     * @param userID
     * @return
     */
    EaseUser getUser(String userID);
    /**
     * If not overridden will return the info from {@link #getUser(String)}
     * @param groupId
     * @param userId
     * @return
     */
    default EaseUser getGroupUser(String groupId,String userId){
        return getUser(userId);
    }
}