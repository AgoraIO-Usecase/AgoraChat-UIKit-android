package io.agora.chat.uikit;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

import io.agora.ConnectionListener;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.uikit.interfaces.OnEaseChatConnectionListener;
import io.agora.chat.uikit.manager.EaseChatPresenter;
import io.agora.chat.uikit.manager.EaseNotifier;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.provider.EaseActivityProvider;
import io.agora.chat.uikit.provider.EaseGroupInfoProvider;
import io.agora.chat.uikit.options.EaseReactionOptions;
import io.agora.chat.uikit.provider.EaseEmojiconInfoProvider;
import io.agora.chat.uikit.provider.EaseFileIconProvider;
import io.agora.chat.uikit.provider.EaseGroupInfoProvider;
import io.agora.chat.uikit.provider.EaseSettingsProvider;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;


public class EaseUIKit {
    private static final String TAG = EaseUIKit.class.getSimpleName();
    private static EaseUIKit instance;
    /**
     * user info provider
     */
    private EaseUserProfileProvider userProvider;
    /**
     * Message system settings
     */
    private EaseSettingsProvider settingsProvider;
    /**
     * conversation info settings
     */
    private EaseGroupInfoProvider groupInfoProvider;
    /**
     * chat avatar options which we can easily control the style
     */
    private EaseAvatarOptions avatarOptions;
    private EaseEmojiconInfoProvider mEmojiconInfoProvider;
    /**
     * The file icon provider
     */
    private EaseFileIconProvider fileIconProvider;
    /**
     * Activity provider
     */
    private EaseActivityProvider activitiesProvider;
    /**
     * the notifier
     */
    private EaseNotifier notifier = null;
    /**
     * Chat presenter which implement {@link io.agora.MessageListener}
     */
    private EaseChatPresenter presenter;
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;
    /**
     * application context
     */
    private Context appContext = null;
    /**
     * Whether to send image message as original picture
     */
    private boolean sendOriginalImage;
    private OnEaseChatConnectionListener chatConnectionListener;

    /**
     * Whether to  show reaction view
     */
    private EaseReactionOptions reactionOptions;

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

    public boolean isMainProcess(@NonNull Context context) {
        String processName;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            processName = getProcessNameByApplication();
        }else {
            processName = getProcessNameByReflection();
        }
        return context.getApplicationInfo().packageName.equals(processName);
    }

    private String getProcessNameByReflection() {
        String processName = null;
        try {
            final Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                    .getDeclaredMethod("currentProcessName", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, new Object[0]);
            if (invoke instanceof String) {
                processName = (String) invoke;
            }
        } catch (Throwable e) {
        }
        return processName;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private String getProcessNameByApplication() {
        return Application.getProcessName();
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
        ChatClient.getInstance().init(context, options);
        // add connection listener
        ChatClient.getInstance().addConnectionListener(connectionListener);
        initNotifier();
        presenter = new EaseChatPresenter();
        presenter.attachApp(appContext);
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
        options.setRequireDeliveryAck(true);

        return options;
    }

    public Context getContext() {
        return appContext;
    }

    private void initNotifier(){
        notifier = new EaseNotifier(appContext);
    }

    public void addChatPresenter(EaseChatPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachApp(appContext);
    }

    public EaseChatPresenter getChatPresenter() {
        return presenter;
    }

    public EaseUIKit setAvatarOptions(EaseAvatarOptions avatarOptions) {
        this.avatarOptions = avatarOptions;
        return this;
    }

    public EaseAvatarOptions getAvatarOptions() {
        return this.avatarOptions;
    }

    public EaseNotifier getNotifier(){
        return notifier;
    }

    public EaseUIKit setSendMessageAsOriginalImage(boolean sendOriginalImage) {
        this.sendOriginalImage = sendOriginalImage;
        return this;
    }

    public boolean isSendMessageAsOriginalImage() {
        return this.sendOriginalImage;
    }

    /**
     * get conversation info provider
     * @return
     */
    public EaseGroupInfoProvider getGroupInfoProvider() {
        return groupInfoProvider;
    }

    /**
     * set conversation provider
     * @param provider
     * @return
     */
    public EaseUIKit setGroupInfoProvider(EaseGroupInfoProvider provider) {
        this.groupInfoProvider = provider;
        return this;
    }

    /**
     * get file icon provider
     * @return
     */
    public EaseFileIconProvider getFileIconProvider() {
        return fileIconProvider;
    }

    /**
     * set file icon provider
     * @param provider
     * @return
     */
    public EaseUIKit setFileIconProvider(EaseFileIconProvider provider) {
        this.fileIconProvider = provider;
        return this;
    }

    /**
     * get emojicon provider
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return mEmojiconInfoProvider;
    }

    /**
     * set emojicon provider
     * @param emojiconInfoProvider
     * @return
     */
    public EaseUIKit setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        mEmojiconInfoProvider = emojiconInfoProvider;
        return this;
    }

    @Deprecated
    public EaseUIKit setOnEaseChatConnectionListener(OnEaseChatConnectionListener listener) {
        this.chatConnectionListener = listener;
        return this;
    }

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnected() {
            if(EaseUIKit.this.chatConnectionListener != null) {
                chatConnectionListener.onConnected();
            }
        }

        @Override
        public void onDisconnected(int error) {
            if(chatConnectionListener != null) {
                chatConnectionListener.onDisconnect(error);
            }
            if (error == Error.USER_REMOVED
                    || error == Error.USER_LOGIN_ANOTHER_DEVICE
                    || error == Error.SERVER_SERVICE_RESTRICTED
                    || error == Error.USER_KICKED_BY_CHANGE_PASSWORD
                    || error == Error.USER_KICKED_BY_OTHER_DEVICE) {
                if(chatConnectionListener != null) {
                    chatConnectionListener.onAccountLogout(error);
                }
            }
        }

        @Override
        public void onTokenExpired() {
            if(chatConnectionListener != null) {
                chatConnectionListener.onTokenExpired();
            }
        }

        @Override
        public void onTokenWillExpire() {
            if(chatConnectionListener != null) {
                chatConnectionListener.onTokenWillExpire();
            }
        }
    };

    /**
     * get settings provider
     * @return
     */
    public EaseSettingsProvider getSettingsProvider() {
        if(settingsProvider == null) {
            settingsProvider = getDefaultSettingsProvider();
        }
        return settingsProvider;
    }

    /**
     * set settting provider
     * @param settingsProvider
     * @return
     */
    public EaseUIKit setSettingsProvider(EaseSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
        return this;
    }

    private EaseSettingsProvider getDefaultSettingsProvider() {
        return new EaseSettingsProvider() {
            @Override
            public boolean isMsgNotifyAllowed(ChatMessage message) {
                return false;
            }

            @Override
            public boolean isMsgSoundAllowed(ChatMessage message) {
                return false;
            }

            @Override
            public boolean isMsgVibrateAllowed(ChatMessage message) {
                return false;
            }

            @Override
            public boolean isSpeakerOpened() {
                return false;
            }
        };
    }

    /**
     * Set to reaction options
     *
     * @param reactionOptions
     */
    public void setReactionOptions(EaseReactionOptions reactionOptions) {
        this.reactionOptions = reactionOptions;
    }

    public EaseReactionOptions getReactionOptions() {
        return reactionOptions;
    }

    /**
     * get file icon provider
     * @return
     */
    public EaseActivityProvider getActivitiesProvider() {
        return activitiesProvider;
    }

    /**
     * set file icon provider
     * @param provider
     * @return
     */
    public EaseUIKit setActivityProvider(EaseActivityProvider provider) {
        this.activitiesProvider = provider;
        return this;
    }
}
