package com.hyphenate.easeui.feature.contact.interfaces

import com.hyphenate.easeui.common.ChatSilentModeResult
import com.hyphenate.easeui.common.interfaces.IControlDataView
import com.hyphenate.easeui.model.EaseUser

interface IEaseContactResultView: IControlDataView {

    /**
     * Load contact list successfully.
     */
    fun loadContactListSuccess(list: MutableList<EaseUser>){}

    /**
     * Load contact list failed.
     */
    fun loadContactListFail(code: Int, error: String){}

    /**
     * Add contact successfully.
     */
    fun addContactSuccess(){}

    /**
     * Add contact failed.
     */
    fun addContactFail(code: Int, error: String){}

    /**
     * Delete contact successfully.
     */
    fun deleteContactSuccess(){}

    /**
     * Delete contact failed.
     */
    fun deleteContactFail(code: Int, error: String){}

    /**
     * Get black list from server successfully.
     */
    fun getBlackListFromServerSuccess(list: MutableList<String>){}

    /**
     * Get black list from server failed.
     */
    fun getBlackListFromServerFail(code: Int, error: String){}

    /**
     * Add user to blacklist successfully.
     */
    fun addUserToBlackListSuccess(){}

    /**
     * Add user to blacklist failed.
     */
    fun addUserToBlackListFail(code: Int, error: String){}

    /**
     * Remove user from blacklist successfully.
     */
    fun removeUserFromBlackListSuccess(){}

    /**
     * Remove user from blacklist failed.
     */
    fun removeUserFromBlackListFail(code: Int, error: String){}

    /**
     * Accept invitation successfully.
     */
    fun acceptInvitationSuccess(){}

    /**
     * Accept invitation failed.
     */
    fun acceptInvitationFail(code: Int, error: String){}

    /**
     * Decline invitation successfully.
     */
    fun declineInvitationSuccess(){}

    /**
     * Decline invitation failed.
     */
    fun declineInvitationFail(code: Int, error: String){}

    /**
     * Delete conversation successfully.
     */
    fun deleteConversationSuccess(conversationId: String?){}

    /**
     * Delete conversation failed.
     */
    fun deleteConversationFail(code: Int, error: String?){}


    /**
     * Fetch user info from user successfully.
     */
    fun fetchUserInfoByUserSuccess(users: List<EaseUser>?) {}

    /**
     * Make contact interruption-free successfully.
     */
    fun makeSilentForContactSuccess(silentResult: ChatSilentModeResult){}

    /**
     * Make contact interruption-free failed.
     */
    fun makeSilentForContactFail(code: Int, error: String?){}

    /**
     * Cancel conversation do not disturb
     */
    fun cancelSilentForContactSuccess(){}

    /**
     * Cancel conversation do not disturb
     */
    fun cancelSilentForContactFail(code: Int, error: String?){}

}