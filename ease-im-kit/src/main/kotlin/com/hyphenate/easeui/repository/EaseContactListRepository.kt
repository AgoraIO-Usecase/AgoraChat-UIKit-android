package com.hyphenate.easeui.repository

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatContactManager
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.common.suspends.acceptContactInvitation
import com.hyphenate.easeui.common.suspends.addNewContact
import com.hyphenate.easeui.common.suspends.addToBlackList
import com.hyphenate.easeui.common.suspends.declineContactInvitation
import com.hyphenate.easeui.common.suspends.deleteUserFromBlackList
import com.hyphenate.easeui.common.suspends.fetchBlackListFromServer
import com.hyphenate.easeui.common.suspends.fetchContactsFromServer
import com.hyphenate.easeui.common.suspends.removeContact
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.provider.fetchUsersBySuspend
import com.hyphenate.easeui.provider.getSyncUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class EaseContactListRepository(
    val chatContactManager: ChatContactManager = ChatClient.getInstance().contactManager(),
) {

    companion object {
        private const val TAG = "ContactRep"
    }

    /**
     * Load server contacts.
     */
    open suspend fun loadData():List<EaseUser> =
        withContext(Dispatchers.IO){
            chatContactManager.fetchContactsFromServer()
        }

    /**
     * Load local contacts.
     */
    suspend fun loadLocalContact(): List<EaseUser> =
        withContext(Dispatchers.IO) {
            chatContactManager.contactsFromLocal.map {
                EaseIM.getUserProvider()?.getSyncUser(it)?.toUser() ?: EaseUser(it)
            }
        }

    /**
     * Add a Contact
     */
    suspend fun addContact(userName:String,reason:String?=""):Int =
        withContext(Dispatchers.IO){
            chatContactManager.addNewContact(userName = userName,reason = reason)
        }

    /**
     * Delete a Contact and keepConversation
     */
    suspend fun deleteContact(userName:String,keepConversation: Boolean?):Int =
        withContext(Dispatchers.IO){
            chatContactManager.removeContact(userName, keepConversation)
        }

    /**
     * Get blocklist list from server
     */
    suspend fun getBlockListFromServer():MutableList<EaseUser> =
        withContext(Dispatchers.IO){
            chatContactManager.fetchBlackListFromServer()
        }

    /**
     * Get blocklist list from local
     */
    suspend fun getBlockListFromLocal():MutableList<EaseUser> =
        withContext(Dispatchers.IO){
            chatContactManager.blackListUsernames.map {
                EaseIM.getUserProvider()?.getSyncUser(it)?.toUser() ?: EaseUser(it)
            }.toMutableList()
        }

    /**
     * Add User to blocklist
     */
    suspend fun addUserToBlockList(userList:MutableList<String>):Int =
        withContext(Dispatchers.IO){
            chatContactManager.addToBlackList(userList)
        }

    /**
     * Remove User to blocklist
     */
    suspend fun removeUserFromBlockList(userName:String):Int =
        withContext(Dispatchers.IO){
            chatContactManager.deleteUserFromBlackList(userName)
        }

    /**
     * Agree to contact invitation
     */
    suspend fun acceptInvitation(userName:String):Int =
        withContext(Dispatchers.IO){
            chatContactManager.acceptContactInvitation(userName)
        }

    /**
     * Refuse contact invitation
     */
    suspend fun declineInvitation(userName:String):Int =
        withContext(Dispatchers.IO){
            chatContactManager.declineContactInvitation(userName)
        }

    /**
     * Fetch user information from user.
     */
    suspend fun fetchContactInfo(contactList: List<EaseUser>?) =
        withContext(Dispatchers.IO) {
            val userList = contactList?.map { it.userId }
            EaseIM.getUserProvider()?.fetchUsersBySuspend(userList)
        }
}