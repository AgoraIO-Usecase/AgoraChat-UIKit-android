package com.hyphenate.easeui.interfaces

import android.content.Context
import com.hyphenate.easeui.ChatUIKitConfig
import com.hyphenate.easeui.common.ChatConnectionListener
import com.hyphenate.easeui.common.ChatContactListener
import com.hyphenate.easeui.common.ChatConversationListener
import com.hyphenate.easeui.common.ChatGroupChangeListener
import com.hyphenate.easeui.common.ChatMessageListener
import com.hyphenate.easeui.common.ChatMultiDeviceListener
import com.hyphenate.easeui.common.ChatOptions
import com.hyphenate.easeui.common.ChatPresenceListener
import com.hyphenate.easeui.common.ChatRoomChangeListener
import com.hyphenate.easeui.common.ChatThreadChangeListener
import com.hyphenate.easeui.common.ChatUIKitCache
import com.hyphenate.easeui.common.enums.ChatUIKitCacheType
import com.hyphenate.easeui.common.impl.OnError
import com.hyphenate.easeui.common.impl.OnSuccess
import com.hyphenate.easeui.common.helper.ChatUIKitNotifier
import com.hyphenate.easeui.model.ChatUIKitGroupProfile
import com.hyphenate.easeui.model.ChatUIKitProfile
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.provider.ChatUIKitCustomActivityRoute
import com.hyphenate.easeui.provider.ChatUIKitGroupProfileProvider
import com.hyphenate.easeui.provider.ChatUIKitEmojiconInfoProvider
import com.hyphenate.easeui.provider.ChatUIKitSettingsProvider
import com.hyphenate.easeui.provider.ChatUIKitUserProfileProvider

interface IChatUIKitClient {

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
     * @param user The user object, see [ChatUIKitUser].
     * @param token The token.
     * @param onSuccess The callback of success.
     * @param onError The callback of error.
     */
    fun login(user: ChatUIKitProfile,
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
    fun updateCurrentUser(user: ChatUIKitProfile)

    /**
     * Get the current user.
     */
    fun getCurrentUser(): ChatUIKitProfile?

    /**
     * Set the provider of the emoji icon.
     */
    fun setEmojiconInfoProvider(provider: ChatUIKitEmojiconInfoProvider)

    /**
     * Set the conversation information provider.
     * @param provider The provider of the conversation information.
     */
    fun setGroupProfileProvider(provider: ChatUIKitGroupProfileProvider)

    /**
     * Set the userinfo provider.
     * @param provider The provider of the userinfo.
     */
    fun setUserProfileProvider(provider: ChatUIKitUserProfileProvider)

    /**
     * Update the UIKit profiles in cache.
     */
    fun updateGroupProfiles(profiles: List<ChatUIKitGroupProfile>)

    /**
     * Update the UIKit userinfo in cache.
     */
    fun updateUsersInfo(users: List<ChatUIKitProfile>)

    /**
     * Set the provider of the settings.
     */
    fun setSettingsProvider(provider: ChatUIKitSettingsProvider)

    /**
     * Set custom activity route.
     */
    fun setCustomActivityRoute(route: ChatUIKitCustomActivityRoute)

    /**
     * Set the configurations.
     */
    fun setConfig(config: ChatUIKitConfig?)

    /**
     * Get the application context.
     */
    fun getContext(): Context?

    /**
     * Get the emojicon provider.
     */
    fun getEmojiconInfoProvider(): ChatUIKitEmojiconInfoProvider?

    /**
     * Get the conversation information provider.
     */
    fun getGroupProfileProvider(): ChatUIKitGroupProfileProvider?

    /**
     * Get the userinfo provider.
     */
    fun getUserProvider(): ChatUIKitUserProfileProvider?

    /**
     * Get the settings provider.
     */
    fun getSettingsProvider(): ChatUIKitSettingsProvider?

    /**
     * Get the custom activity route.
     */
    fun getCustomActivityRoute(): ChatUIKitCustomActivityRoute?

    /**
     * Get the UIKit SDK configurations.
     */
    fun getConfig(): ChatUIKitConfig?

    /**
     * Clear the cache.
     */
    fun clearKitCache(type: ChatUIKitCacheType?)

    /**
     * Get the cache.
     */
    fun getKitCache(): ChatUIKitCache

    /**
     * Get the notification helper in EaseUIKit.
     */
    fun getNotifier(): ChatUIKitNotifier?

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