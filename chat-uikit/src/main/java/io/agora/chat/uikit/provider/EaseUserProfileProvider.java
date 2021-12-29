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
}