package io.agora.uikit.repository

import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatContactManager
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.common.suspends.acceptContactInvitation
import io.agora.uikit.common.suspends.addNewContact
import io.agora.uikit.common.suspends.addToBlackList
import io.agora.uikit.common.suspends.declineContactInvitation
import io.agora.uikit.common.suspends.deleteUserFromBlackList
import io.agora.uikit.common.suspends.fetchBlackListFromServer
import io.agora.uikit.common.suspends.fetchContactsFromServer
import io.agora.uikit.common.suspends.removeContact
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.fetchUsersBySuspend
import io.agora.uikit.provider.getSyncUser
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
    suspend fun addContact(userName:String,reason:String?=""):String =
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
            if (contactList.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "contactList is null or empty.")
            }
            EaseIM.getUserProvider()?.fetchUsersBySuspend(userList)
        }
}