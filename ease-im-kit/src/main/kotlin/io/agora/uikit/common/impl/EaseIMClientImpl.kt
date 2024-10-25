package io.agora.uikit.common.impl

import android.content.Context
import io.agora.uikit.EaseIMConfig
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatConnectionListener
import io.agora.uikit.common.ChatContactListener
import io.agora.uikit.common.ChatConversationListener
import io.agora.uikit.common.ChatGroupChangeListener
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessageListener
import io.agora.uikit.common.ChatMultiDeviceListener
import io.agora.uikit.common.ChatOptions
import io.agora.uikit.common.ChatPresenceListener
import io.agora.uikit.common.ChatListenersWrapper
import io.agora.uikit.common.ChatRoomChangeListener
import io.agora.uikit.common.ChatThreadChangeListener
import io.agora.uikit.common.EaseIMCache
import io.agora.uikit.common.enums.EaseCacheType
import io.agora.uikit.common.extensions.isMainProcess
import io.agora.uikit.common.helper.EaseNotifier
import io.agora.uikit.common.helper.EasePreferenceManager
import io.agora.uikit.interfaces.EaseIMClient
import io.agora.uikit.interfaces.OnEventResultListener
import io.agora.uikit.model.EaseGroupProfile
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.provider.EaseCustomActivityRoute
import io.agora.uikit.provider.EaseGroupProfileProvider
import io.agora.uikit.provider.EaseEmojiconInfoProvider
import io.agora.uikit.provider.EaseSettingsProvider
import io.agora.uikit.provider.EaseUserProfileProvider
import java.util.concurrent.atomic.AtomicBoolean

internal class EaseIMClientImpl: EaseIMClient {
    private var isInit: AtomicBoolean = AtomicBoolean(false)
    private var groupProfileProvider: EaseGroupProfileProvider? = null
    private var userProvider: EaseUserProfileProvider? = null
    private var emojiconProvider: EaseEmojiconInfoProvider? = null
    private var settingsProvider: EaseSettingsProvider? = null
    private var activityRoute: EaseCustomActivityRoute? = null
    private var config: EaseIMConfig? = EaseIMConfig()
    private lateinit var context: Context
    private val cache: EaseIMCache by lazy { EaseIMCache() }
    private var user: EaseProfile? = null
    private val _notifier: EaseNotifier by lazy { EaseNotifier(context) }
    companion object {
        private const val TAG = "EaseIMClient"
    }
    override fun init(context: Context, options: ChatOptions?) {
        ChatLog.e(TAG, "UIKIt init")
        if (isInit.get()) {
            return
        }
        if (!context.isMainProcess()) {
            ChatLog.e(TAG, "Please init EaseIM in main process")
            return
        }
        this.context = context.applicationContext
        var chatOptions: ChatOptions? = null
        if (options == null) {
            chatOptions = ChatOptions().apply {
                // change to need confirm contact invitation
                acceptInvitationAlways = false
                // set if need read ack
                requireAck = true
                // set if need delivery ack
                requireDeliveryAck = false
            }
        } else {
            chatOptions = options
        }
        ChatClient.getInstance().init(context, chatOptions)
        addChatListenersWrapper()
        isInit.set(true)
        // If auto login, should init the cache.
        if (chatOptions.autoLogin && ChatClient.getInstance().isLoggedInBefore) {
            cache.init()
        }
        ChatLog.e(TAG, "UIKIt init end")
    }

    override fun login(userId: String, password: String, onSuccess: OnSuccess, onError: OnError) {
        this.user = EaseProfile(userId)
        ChatClient.getInstance().login(userId, password, CallbackImpl(onSuccess = {
            cache.init()
            cache.insertUser(user!!)
            onSuccess.invoke()
        }, onError))
    }

    override fun login(user: EaseProfile, token: String, onSuccess: OnSuccess, onError: OnError) {
        this.user = user
        ChatClient.getInstance().loginWithToken(user.id, token, CallbackImpl(onSuccess = {
            cache.init()
            cache.insertUser(user)
            onSuccess.invoke()
        }, onError))
    }

    override fun logout(unbindDeviceToken: Boolean, onSuccess: OnSuccess, onError: OnError) {
        val oldId = getCurrentUser()?.id
        ChatClient.getInstance().logout(unbindDeviceToken, CallbackImpl(onSuccess = {
            cache.clear(EaseCacheType.ALL)
            EasePreferenceManager.getInstance().removeLoadedContactDataStatus(oldId)
            onSuccess.invoke()
        }, onError))
    }

    override fun isInited(): Boolean {
        return isInit.get()
    }

    override fun isLoggedIn(): Boolean {
        return ChatClient.getInstance().isLoggedIn
    }

    override fun updateCurrentUser(user: EaseProfile) {
        this.user = user
        cache.insertUser(user)
    }

    override fun getCurrentUser(): EaseProfile? {
        return if (!ChatClient.getInstance().currentUser.isNullOrEmpty()) {
            cache.getUser(ChatClient.getInstance().currentUser)
                ?: EaseProfile(ChatClient.getInstance().currentUser)
        } else null
    }

    override fun setEmojiconInfoProvider(provider: EaseEmojiconInfoProvider) {
        emojiconProvider = provider
    }

    override fun setGroupProfileProvider(provider: EaseGroupProfileProvider) {
        groupProfileProvider = provider
    }

    override fun setUserProfileProvider(provider: EaseUserProfileProvider) {
        userProvider = provider
    }

    override fun updateGroupProfiles(profiles: List<EaseGroupProfile>) {
        cache.updateProfiles(profiles)
    }

    override fun updateUsersInfo(users: List<EaseProfile>) {
        cache.updateUsers(users)
    }

    override fun setSettingsProvider(provider: EaseSettingsProvider) {
        settingsProvider = provider
    }

    override fun setCustomActivityRoute(route: EaseCustomActivityRoute) {
        activityRoute = route
    }

    override fun setConfig(config: EaseIMConfig?) {
        if (config != null) {
            this.config = config
        }
    }

    override fun getContext(): Context? {
        if (!isInit.get()) {
            ChatLog.e(TAG, "please init UIKit SDK first!")
            return null
        }
        return context
    }

    override fun getEmojiconInfoProvider(): EaseEmojiconInfoProvider? {
        return emojiconProvider
    }

    override fun getGroupProfileProvider(): EaseGroupProfileProvider? {
        return groupProfileProvider
    }

    override fun getUserProvider(): EaseUserProfileProvider? {
        return userProvider
    }

    override fun getSettingsProvider(): EaseSettingsProvider? {
        return settingsProvider
    }

    override fun getCustomActivityRoute(): EaseCustomActivityRoute? {
        return activityRoute
    }

    override fun getConfig(): EaseIMConfig? {
        return config
    }

    override fun clearKitCache(type: EaseCacheType?) {
        cache.clear(type)
    }

    override fun getKitCache(): EaseIMCache {
        return cache
    }

    override fun getNotifier(): EaseNotifier? {
        if (!isInit.get()) {
            ChatLog.e(TAG, "please init UIKit SDK first!")
            return null
        }
        return _notifier
    }

    private fun addChatListenersWrapper() {
        ChatListenersWrapper.getInstance().addListeners()
    }

    fun removeChatListener() {
        ChatListenersWrapper.getInstance().removeListeners()
    }

    override fun addConnectionListener(listener:ChatConnectionListener) {
        ChatListenersWrapper.getInstance().addConnectionListener(listener)
    }

    override fun removeConnectionListener(listener: ChatConnectionListener) {
        ChatListenersWrapper.getInstance().removeConnectionListener(listener)
    }

    override fun addChatMessageListener(listener: ChatMessageListener) {
        ChatListenersWrapper.getInstance().addChatMessageListener(listener)
    }

    override fun removeChatMessageListener(listener: ChatMessageListener) {
        ChatListenersWrapper.getInstance().removeChatMessageListener(listener)
    }

    override fun addGroupChangeListener(listener: ChatGroupChangeListener) {
        ChatListenersWrapper.getInstance().addGroupChangeListener(listener)
    }

    override fun removeGroupChangeListener(listener: ChatGroupChangeListener) {
        ChatListenersWrapper.getInstance().removeGroupChangeListener(listener)
    }

    override fun addContactListener(listener: ChatContactListener) {
        ChatListenersWrapper.getInstance().addContactListener(listener)
    }

    override fun removeContactListener(listener: ChatContactListener) {
        ChatListenersWrapper.getInstance().removeContactListener(listener)
    }

    override fun addConversationListener(listener: ChatConversationListener) {
        ChatListenersWrapper.getInstance().addConversationListener(listener)
    }

    override fun removeConversationListener(listener: ChatConversationListener) {
        ChatListenersWrapper.getInstance().removeConversationListener(listener)
    }

    override fun addPresenceListener(listener: ChatPresenceListener) {
        ChatListenersWrapper.getInstance().addPresenceListener(listener)
    }

    override fun removePresenceListener(listener: ChatPresenceListener) {
        ChatListenersWrapper.getInstance().removePresenceListener(listener)
    }

    override fun addChatRoomChangeListener(listener: ChatRoomChangeListener) {
        ChatListenersWrapper.getInstance().addChatRoomChangeListener(listener)
    }

    override fun removeChatRoomChangeListener(listener: ChatRoomChangeListener) {
        ChatListenersWrapper.getInstance().removeChatRoomChangeListener(listener)
    }

    override fun addMultiDeviceListener(listener: ChatMultiDeviceListener) {
        ChatListenersWrapper.getInstance().addMultiDeviceListener(listener)
    }

    override fun removeMultiDeviceListener(listener: ChatMultiDeviceListener) {
        ChatListenersWrapper.getInstance().removeMultiDeviceListener(listener)
    }

    override fun addEventResultListener(listener: OnEventResultListener) {
        ChatListenersWrapper.getInstance().addEventResultListener(listener)
    }

    override fun removeEventResultListener(listener: OnEventResultListener) {
        ChatListenersWrapper.getInstance().removeEventResultListener(listener)
    }

    override fun callbackEvent(function: String, errorCode: Int, errorMessage: String?) {
        ChatListenersWrapper.getInstance().callbackEvent(function, errorCode, errorMessage)
    }

    override fun addThreadChangeListener(listener: ChatThreadChangeListener) {
        ChatListenersWrapper.getInstance().addThreadChangeListener(listener)
    }

    override fun removeThreadChangeListener(listener: ChatThreadChangeListener) {
        ChatListenersWrapper.getInstance().removeThreadChangeListener(listener)
    }

    override fun releaseGlobalListener() {
        removeChatListener()
    }

}