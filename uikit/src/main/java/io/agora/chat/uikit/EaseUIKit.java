package io.agora.chat.uikit;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatOptions;
import io.agora.chat.uikit.manager.EaseChatPresenter;
import io.agora.chat.uikit.manager.EaseNotifier;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.provider.EaseConversationInfoProvider;
import io.agora.chat.uikit.provider.EaseEmojiconInfoProvider;
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
    private EaseConversationInfoProvider conversationInfoProvider;
    /**
     * chat avatar options which we can easily control the style
     */
    private EaseAvatarOptions avatarOptions;
    private EaseEmojiconInfoProvider mEmojiconInfoProvider;
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
        ChatClient.getInstance().init(context, options);
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
        options.setRequireDeliveryAck(false);

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
    public EaseConversationInfoProvider getConversationInfoProvider() {
        return conversationInfoProvider;
    }

    /**
     * set conversation provider
     * @param provider
     * @return
     */
    public EaseUIKit setConversationInfoProvider(EaseConversationInfoProvider provider) {
        this.conversationInfoProvider = provider;
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
}
