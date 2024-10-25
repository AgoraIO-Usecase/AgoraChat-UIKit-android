package io.agora.uikit

import android.content.Context
import io.agora.uikit.common.ChatOptions
import io.agora.uikit.common.ChatPresence
import io.agora.uikit.common.ChatPresenceListener
import io.agora.uikit.common.ChatThreadChangeListener
import io.agora.uikit.common.enums.EaseCacheType
import io.agora.uikit.common.impl.EaseIMClientImpl
import io.agora.uikit.common.impl.OnError
import io.agora.uikit.common.impl.OnSuccess
import io.agora.uikit.common.helper.EaseNotifier
import io.agora.uikit.common.helper.EasePreferenceManager
import io.agora.uikit.interfaces.EaseChatRoomListener
import io.agora.uikit.interfaces.EaseConnectionListener
import io.agora.uikit.interfaces.EaseContactListener
import io.agora.uikit.interfaces.EaseConversationListener
import io.agora.uikit.interfaces.EaseGroupListener
import io.agora.uikit.interfaces.EaseIMClient
import io.agora.uikit.interfaces.EaseMessageListener
import io.agora.uikit.interfaces.EaseMultiDeviceListener
import io.agora.uikit.interfaces.OnEventResultListener
import io.agora.uikit.model.EaseGroupProfile
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.EaseCustomActivityRoute
import io.agora.uikit.provider.EaseGroupProfileProvider
import io.agora.uikit.provider.EaseEmojiconInfoProvider
import io.agora.uikit.provider.EaseSettingsProvider
import io.agora.uikit.provider.EaseUserProfileProvider


/**
 * It is the main class of the Chat UIKit.
 */
object EaseIM {
    // Whether the debug mode is open in EaseIM.
    const val DEBUG: Boolean = true
    const val version = "1.3.0"

    /**
     * Open Application first load block list from server.
     */
    var isLoadBlockListFromServer: Boolean? = false

    private val client: EaseIMClient by lazy {
        EaseIMClientImpl()
    }

    /**
     * Initialize the Chat UIKit.
     * @param context The application context.
     * @param options The options of the Chat SDK.
     */
    @Synchronized
    fun init(context: Context, options: ChatOptions, config: EaseIMConfig? = null): EaseIM {
        client.init(context, options)
        client.setConfig(config)
        return this
    }

    /**
     * Judge whether the uikit is be initialized.
     */
    fun isInited(): Boolean {
        return client.isInited()
    }

    fun releaseGlobalListener(){
        client.releaseGlobalListener()
    }

    /**
     * Temp for test.
     */
    fun login(userId: String,
              password: String,
              onSuccess: OnSuccess = {},
              onError: OnError = {_,_->}) {
        client.login(userId, password, onSuccess, onError)
    }

    /**
     * Login with user object by token.
     * @param user The user object, see [EaseUser].
     * @param token The token.
     * @param onSuccess The callback of success.
     * @param onError The callback of error.
     */
    fun login(user: EaseProfile,
              token: String,
              onSuccess: OnSuccess = {},
              onError: OnError = {_,_ ->}) {
        client.login(user, token, onSuccess, onError)
    }



    /**
     * Log out from the Chat SDK.
     * @param unbindDeviceToken Whether unbind the device token.
     * @param onSuccess The callback of success.
     * @param onError The callback of error.
     */
    fun logout(unbindDeviceToken: Boolean,
               onSuccess: OnSuccess = {},
               onError: OnError = {_,_ ->}) {
        client.logout(unbindDeviceToken, onSuccess, onError)
    }

    /**
     * Whether the user is logged in.
     */
    fun isLoggedIn(): Boolean {
        return client.isLoggedIn()
    }

    /**
     * Update the current user.
     */
    fun updateCurrentUser(user: EaseProfile) {
        client.updateCurrentUser(user)
    }

    /**
     * Get the current user.
     */
    fun getCurrentUser(): EaseProfile? {
        return client.getCurrentUser()
    }

    /**
     * Set the provider of the emoji icon.
     * @param provider The provider of the emoji icon.
     */
    fun setEmojiconInfoProvider(provider: EaseEmojiconInfoProvider): EaseIM {
        client.setEmojiconInfoProvider(provider)
        return this
    }

    /**
     * Set the conversation information provider.
     * @param provider The provider of the conversation information.
     */
    fun setGroupProfileProvider(provider: EaseGroupProfileProvider): EaseIM {
        client.setGroupProfileProvider(provider)
        return this
    }

    /**
     * Set the userinfo provider.
     * @param provider The provider of the userinfo.
     */
    fun setUserProfileProvider(provider: EaseUserProfileProvider): EaseIM {
        client.setUserProfileProvider(provider)
        return this
    }

    /**
     * Update the UIKit group information in cache.
     * @param profiles The profiles to update.
     */
    fun updateGroupInfo(profiles: List<EaseGroupProfile>) {
        client.updateGroupProfiles(profiles)
    }

    /**
     * Update the UIKit user information in cache.
     * @param users The profiles to update.
     */
    fun updateUsersInfo(users: List<EaseProfile>) {
        client.updateUsersInfo(users)
    }

    /**
     * Set the provider of the settings.
     * @param provider The provider of the settings.
     */
    fun setSettingsProvider(provider: EaseSettingsProvider): EaseIM {
        client.setSettingsProvider(provider)
        return this
    }

    /**
     * Set the activity route in UIKit.
     * @param route The provider of the activity route.
     */
    fun setCustomActivityRoute(route: EaseCustomActivityRoute): EaseIM {
        client.setCustomActivityRoute(route)
        return this
    }

    /**
     * Get the application context.
     */
    fun getContext(): Context? {
        return client.getContext()
    }

    /**
     * Get the UIKit SDK configurations.
     */
    fun getConfig(): EaseIMConfig? {
        return client.getConfig()
    }

    /**
     * Clear the cache.
     */
    fun clearCache(type: EaseCacheType? = EaseCacheType.ALL) {
        client.clearKitCache(type)
    }

    /**
     * Get the cache.
     */
    internal fun getCache() = client.getKitCache()

    /**
     * Get the notification helper in EaseUIKit.
     */
    fun getNotifier(): EaseNotifier? {
        return client.getNotifier()
    }

    /**
     * Get the emojicon provider.
     */
    fun getEmojiconInfoProvider(): EaseEmojiconInfoProvider? {
        return client.getEmojiconInfoProvider()
    }

    /**
     * Get the conversation information provider.
     */
    fun getGroupProfileProvider(): EaseGroupProfileProvider? {
        return client.getGroupProfileProvider()
    }

    /**
     * Get the userinfo provider.
     */
    fun getUserProvider(): EaseUserProfileProvider? {
        return client.getUserProvider()
    }

    /**
     * Get the settings provider.
     */
    fun getSettingsProvider(): EaseSettingsProvider? {
        return client.getSettingsProvider()
    }

    /**
     * Get the activity route provider.
     */
    fun getCustomActivityRoute(): EaseCustomActivityRoute? {
        return client.getCustomActivityRoute()
    }

    fun checkMutedConversationList(userId:String):Boolean{
        return getCache().getMutedConversationList().containsKey(userId)
    }

    /**
     * Add Connection Listener
     */
    fun addConnectionListener(connectListener:EaseConnectionListener){
        client.addConnectionListener(connectListener)
    }

    /**
     * Remove Connection Listener
     */
    fun removeConnectionListener(connectListener:EaseConnectionListener){
        client.removeConnectionListener(connectListener)
    }

    /**
     * Add ChatMessage Listener
     */
    fun addChatMessageListener(listener:EaseMessageListener){
        client.addChatMessageListener(listener)
    }

    /**
     * Remove ChatMessage Listener
     */
    fun removeChatMessageListener(listener:EaseMessageListener){
        client.removeChatMessageListener(listener)
    }

    /**
     * Add GroupChange Listener
     */
    fun addGroupChangeListener(listener:EaseGroupListener){
        client.addGroupChangeListener(listener)
    }

    /**
     * Remove GroupChange Listener
     */
    fun removeGroupChangeListener(listener:EaseGroupListener){
        client.removeGroupChangeListener(listener)
    }

    /**
     * Add Contact Listener
     */
    fun addContactListener(listener:EaseContactListener){
        client.addContactListener(listener)
    }

    /**
     * Remove Contact Listener
     */
    fun removeContactListener(listener:EaseContactListener){
        client.removeContactListener(listener)
    }

    /**
     * Add Conversation Listener
     */
    fun addConversationListener(listener:EaseConversationListener){
        client.addConversationListener(listener)
    }

    /**
     * Remove Conversation Listener
     */
    fun removeConversationListener(listener:EaseConversationListener){
        client.removeConversationListener(listener)
    }

    /**
     * Add Presence Listener
     */
    fun addPresenceListener(listener:ChatPresenceListener){
        client.addPresenceListener(listener)
    }

    /**
     * Remove Presence Listener
     */
    fun removePresenceListener(listener:ChatPresenceListener){
        client.removePresenceListener(listener)
    }

    /**
     * Add ChatRoomChange Listener
     */
    fun addChatRoomChangeListener(listener:EaseChatRoomListener){
        client.addChatRoomChangeListener(listener)
    }

    /**
     * Remove ChatRoomChange Listener
     */
    fun removeChatRoomChangeListener(listener:EaseChatRoomListener){
        client.removeChatRoomChangeListener(listener)
    }

    /**
     * Add MultiDevice Listener
     */
    fun addMultiDeviceListener(listener:EaseMultiDeviceListener){
        client.addMultiDeviceListener(listener)
    }

    /**
     * Remove MultiDevice Listener
     */
    fun removeMultiDeviceListener(listener:EaseMultiDeviceListener){
        client.removeMultiDeviceListener(listener)
    }

    /**
     * Add Event Result Listener
     */
    fun addEventResultListener(listener: OnEventResultListener){
        client.addEventResultListener(listener)
    }

    /**
     * Remove Event Result Listener
     */
    fun removeEventResultListener(listener:OnEventResultListener){
        client.removeEventResultListener(listener)
    }

    /**
     * Set Event Result Callback
     */
    fun setEventResultCallback(function: String, errorCode: Int, errorMessage: String?){
        client.callbackEvent(function, errorCode, errorMessage)
    }

    /**
     * Add Thread Change Listener
     */
    fun addThreadChangeListener(listener: ChatThreadChangeListener){
        client.addThreadChangeListener(listener)
    }

    /**
     * Remove Thread Change Listener
     */
    fun removeThreadChangeListener(listener: ChatThreadChangeListener){
        client.removeThreadChangeListener(listener)
    }

}