package io.agora.uikit.interfaces

import android.content.Context
import io.agora.uikit.EaseIMConfig
import io.agora.uikit.common.ChatConnectionListener
import io.agora.uikit.common.ChatContactListener
import io.agora.uikit.common.ChatConversationListener
import io.agora.uikit.common.ChatGroupChangeListener
import io.agora.uikit.common.ChatMessageListener
import io.agora.uikit.common.ChatMultiDeviceListener
import io.agora.uikit.common.ChatOptions
import io.agora.uikit.common.ChatPresenceListener
import io.agora.uikit.common.ChatRoomChangeListener
import io.agora.uikit.common.ChatThreadChangeListener
import io.agora.uikit.common.EaseIMCache
import io.agora.uikit.common.enums.EaseCacheType
import io.agora.uikit.common.impl.OnError
import io.agora.uikit.common.impl.OnSuccess
import io.agora.uikit.common.helper.EaseNotifier
import io.agora.uikit.model.EaseGroupProfile
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.EaseCustomActivityRoute
import io.agora.uikit.provider.EaseGroupProfileProvider
import io.agora.uikit.provider.EaseEmojiconInfoProvider
import io.agora.uikit.provider.EaseSettingsProvider
import io.agora.uikit.provider.EaseUserProfileProvider

interface EaseIMClient {

    /**
     * Initialize the Chat UIKit.
     * @param context The application context.
     * @param options The options of the Chat SDK.
     */
    fun init(context: Context, options: ChatOptions?)

    /**
     * Temp for test.
     */
    fun login(userId: String,
              password: String,
              onSuccess: OnSuccess,
              onError: OnError)

    /**
     * Login with user object by token.
     * @param user The user object, see [EaseUser].
     * @param token The token.
     * @param onSuccess The callback of success.
     * @param onError The callback of error.
     */
    fun login(user: EaseProfile,
              token: String,
              onSuccess: OnSuccess,
              onError: OnError)


    /**
     * Log out from the Chat SDK.
     * @param unbindDeviceToken Whether unbind the device token.
     * @param onSuccess The callback of success.
     * @param onError The callback of error.
     */
    fun logout(unbindDeviceToken: Boolean,
               onSuccess: OnSuccess,
               onError: OnError)

    /**
     * Whether the uikit is be initialized.
     */
    fun isInited(): Boolean

    /**
     * Whether the user is logged in.
     */
    fun isLoggedIn(): Boolean

    /**
     * Update the current user info.
     * @param user
     */
    fun updateCurrentUser(user: EaseProfile)

    /**
     * Get the current user.
     */
    fun getCurrentUser(): EaseProfile?

    /**
     * Set the provider of the emoji icon.
     */
    fun setEmojiconInfoProvider(provider: EaseEmojiconInfoProvider)

    /**
     * Set the conversation information provider.
     * @param provider The provider of the conversation information.
     */
    fun setGroupProfileProvider(provider: EaseGroupProfileProvider)

    /**
     * Set the userinfo provider.
     * @param provider The provider of the userinfo.
     */
    fun setUserProfileProvider(provider: EaseUserProfileProvider)

    /**
     * Update the UIKit profiles in cache.
     */
    fun updateGroupProfiles(profiles: List<EaseGroupProfile>)

    /**
     * Update the UIKit userinfo in cache.
     */
    fun updateUsersInfo(users: List<EaseProfile>)

    /**
     * Set the provider of the settings.
     */
    fun setSettingsProvider(provider: EaseSettingsProvider)

    /**
     * Set custom activity route.
     */
    fun setCustomActivityRoute(route: EaseCustomActivityRoute)

    /**
     * Set the configurations.
     */
    fun setConfig(config: EaseIMConfig?)

    /**
     * Get the application context.
     */
    fun getContext(): Context?

    /**
     * Get the emojicon provider.
     */
    fun getEmojiconInfoProvider(): EaseEmojiconInfoProvider?

    /**
     * Get the conversation information provider.
     */
    fun getGroupProfileProvider(): EaseGroupProfileProvider?

    /**
     * Get the userinfo provider.
     */
    fun getUserProvider(): EaseUserProfileProvider?

    /**
     * Get the settings provider.
     */
    fun getSettingsProvider(): EaseSettingsProvider?

    /**
     * Get the custom activity route.
     */
    fun getCustomActivityRoute(): EaseCustomActivityRoute?

    /**
     * Get the UIKit SDK configurations.
     */
    fun getConfig(): EaseIMConfig?

    /**
     * Clear the cache.
     */
    fun clearKitCache(type: EaseCacheType?)

    /**
     * Get the cache.
     */
    fun getKitCache(): EaseIMCache

    /**
     * Get the notification helper in EaseUIKit.
     */
    fun getNotifier(): EaseNotifier?

    /**
     * Add Connection Listener
     */
    fun addConnectionListener(listener:ChatConnectionListener){}

    /**
     * Remove Connection Listener
     */
    fun removeConnectionListener(listener:ChatConnectionListener){}

    /**
     * Add ChatMessage Listener
     */
    fun addChatMessageListener(listener:ChatMessageListener){}

    /**
     * Remove ChatMessage Listener
     */
    fun removeChatMessageListener(listener:ChatMessageListener){}

    /**
     * Add GroupChange Listener
     */
    fun addGroupChangeListener(listener:ChatGroupChangeListener){}

    /**
     * Remove GroupChange Listener
     */
    fun removeGroupChangeListener(listener:ChatGroupChangeListener){}

    /**
     * Add Contact Listener
     */
    fun addContactListener(listener:ChatContactListener){}

    /**
     * Remove Contact Listener
     */
    fun removeContactListener(listener:ChatContactListener){}

    /**
     * Add Conversation Listener
     */
    fun addConversationListener(listener:ChatConversationListener){}

    /**
     * Remove Conversation Listener
     */
    fun removeConversationListener(listener:ChatConversationListener){}

    /**
     * Add Presence Listener
     */
    fun addPresenceListener(listener:ChatPresenceListener){}

    /**
     * Remove Presence Listener
     */
    fun removePresenceListener(listener:ChatPresenceListener){}

    /**
     * Add ChatRoomChange Listener
     */
    fun addChatRoomChangeListener(listener:ChatRoomChangeListener){}

    /**
     * Remove ChatRoomChange Listener
     */
    fun removeChatRoomChangeListener(listener:ChatRoomChangeListener){}

    /**
     * Add MultiDevice Listener
     */
    fun addMultiDeviceListener(listener:ChatMultiDeviceListener){}

    /**
     * Remove MultiDevice Listener
     */
    fun removeMultiDeviceListener(listener:ChatMultiDeviceListener){}

    /**
     * Add Event Result Listener
     */
    fun addEventResultListener(listener:OnEventResultListener){}

    /**
     * Remove Event Result Listener
     */
    fun removeEventResultListener(listener:OnEventResultListener){}

    /**
     * Callback Event
     */
    fun callbackEvent(function: String, errorCode: Int, errorMessage: String?){}

    /**
     * Add Thread Change Listener
     */
    fun addThreadChangeListener(listener:ChatThreadChangeListener){}

    /**
     * Remove Thread Change Listener
     */
    fun removeThreadChangeListener(listener:ChatThreadChangeListener){}

    /**
     * release global listener
     */
    fun releaseGlobalListener(){}

}