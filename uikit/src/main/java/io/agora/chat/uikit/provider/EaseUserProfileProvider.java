package io.agora.chat.uikit.provider;

import io.agora.chat.uikit.domain.EaseUser;

/**
 * User profile provider
 * @author wei
 *
 */
public interface EaseUserProfileProvider {
    /**
     * return EaseUser for input username
     * @param username
     * @return
     */
    EaseUser getUser(String username);
}