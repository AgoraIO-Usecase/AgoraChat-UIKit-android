package com.hyphenate.easeui.viewmodel.presence

import com.hyphenate.easeui.viewmodel.IAttachView

interface IPresenceRequest: IAttachView {

    /**
     * Publish your own status
     */
    fun publishPresence(ext:String?){}

    /**
     * Gets the current presence state of users.
     * @param userIds The array of IDs of users whose current presence state you want to check.
     */
    fun fetchPresenceStatus(userIds:MutableList<String>?){}

    /**
     * Subscribes to a user's presence states. If the subscription succeeds, the subscriber will receive the callback when the user's presence state changes.
     * @param userIds Subscription ID List
     * @param expiry The expiration time of the presence subscription.
     */
    fun subscribePresences(userIds:MutableList<String>?,expiry:Long){}

    /**
     * Unsubscribes from a user's presence states.
     * @param userIds Subscription ID List
     */
    fun unsubscribePresences(userIds:MutableList<String>?){}

}