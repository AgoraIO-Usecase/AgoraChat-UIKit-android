package io.agora.chat.uikit;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatOptions;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;


public class EaseUIKit {
    private static final String TAG = EaseUIKit.class.getSimpleName();
    private static EaseUIKit instance;
    /**
     * user info provider
     */
    private EaseUserProfileProvider userProvider;
    /**
     * chat avatar options which we can easily control the style
     */
    private EaseAvatarOptions avatarOptions;
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;
    /**
     * application context
     */
    private Context appContext = null;
    /**
     * Configuration Management
     */
    private EaseConfigsManager configsManager;
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
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return context.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    public boolean init(Context context, ChatOptions options) {
        if(sdkInited) {
            return true;
        }
        appContext = context.getApplicationContext();
        // if there is application has remote service, application:onCreate() maybe called twice
        // this check is to make sure SDK will initialized only once
        // return if process name is not application's name since the package name is the default process name
        if (!isMainProcess(appContext)) {
            Log.e(TAG, "enter the service process!");
            return false;
        }
        if(options == null) {
            options = initChatOptions();
        }
        configsManager = new EaseConfigsManager(context);
        ChatClient.getInstance().init(context, options);
        //initNotifier();
        sdkInited = true;
        return true;
    }

    protected ChatOptions initChatOptions(){
        Log.d(TAG, "init Agora chat Options");

        ChatOptions options = new ChatOptions();
        // change to need confirm contact invitation
        options.setAcceptInvitationAlways(false);
        // set if need read ack
        options.setRequireAck(true);
        // set if need delivery ack
        options.setRequireDeliveryAck(false);

        return options;
    }

    public EaseUIKit setAvatarOptions(EaseAvatarOptions avatarOptions) {
        this.avatarOptions = avatarOptions;
        return this;
    }

    public EaseAvatarOptions getAvatarOptions() {
        return this.avatarOptions;
    }
}
