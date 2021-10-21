package io.agora.chat.uikit;

import android.content.Context;

import io.agora.chat.ChatOptions;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;


public class EaseUIKit {
    private static EaseUIKit instance;
    /**
     * user info provider
     */
    private EaseUserProfileProvider userProvider;
    private EaseUIKit() {}

    public static EaseUIKit getInstance() {
        if(instance == null) {
            synchronized (EaseUIKit.class) {
                if(instance == null) {
                    instance = new EaseUIKit();
                }
            }
        }
        return instance;
    }

    /**
     * get user profile provider
     * @return
     */
    public EaseUserProfileProvider getUserProvider() {
        return userProvider;
    }

    /**
     * set user profile provider
     * @param userProvider
     * @return
     */
    public EaseUIKit setUserProvider(EaseUserProfileProvider userProvider) {
        this.userProvider = userProvider;
        return this;
    }

    public boolean isMainProcess(Context context) {
        return false;
    }

    public boolean init(Context context, ChatOptions options) {
        return false;
    }

    public EaseAvatarOptions getAvatarOptions() {
        return null;
    }
}
