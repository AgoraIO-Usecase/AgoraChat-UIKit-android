package com.hyphenate.easeui.repository

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatContactManager
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatSearchDirection
import com.hyphenate.easeui.common.ChatSearchScope
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.common.suspends.searchBlockContact
import com.hyphenate.easeui.common.suspends.searchContact
import com.hyphenate.easeui.common.suspends.searchMessage
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.provider.getSyncProfile
import com.hyphenate.easeui.provider.getSyncUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseSearchRepository(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
    private val chatContactManager: ChatContactManager = ChatClient.getInstance().contactManager(),
) {

    companion object {
        private const val TAG = "SearchRep"
    }

    /**
     * Search user from local .
     */
    suspend fun searchUser(query:String):MutableList<EaseUser> =
        withContext(Dispatchers.IO){
           chatContactManager.searchContact(query)
        }

    /**
     * Search block user from local .
     */
    suspend fun searchBlockUser(query:String):MutableList<EaseUser> =
        withContext(Dispatchers.IO){
            chatContactManager.searchBlockContact(query)
        }

    /**
     * Search conversation from local .
     */
    suspend fun searchConversation(query:String): List<EaseConversation> =
        withContext(Dispatchers.IO){
           chatManager.allConversationsBySort.filter { it ->
                   EaseIM.getGroupProfileProvider()?.getGroup(it.conversationId())?.let {
                         it.name?.contains(query)
                   } ?: kotlin.run{
                       when(it.type) {
                           ChatConversationType.GroupChat -> {
                               var name = EaseIM.getGroupProfileProvider()?.getSyncProfile(it.conversationId())?.name
                               if (name.isNullOrEmpty()) {
                                   name = ChatClient.getInstance().groupManager().getGroup(it.conversationId())?.groupName
                                   if (name.isNullOrEmpty()) {
                                       name = it.conversationId()
                                   }
                               }
                               name?.contains(query) ?: it.conversationId().contains(query)
                           }
                           ChatConversationType.ChatRoom -> {
                               ChatClient.getInstance().chatroomManager().getChatRoom(it.conversationId())?.name?.contains(query)
                                   ?: it.conversationId().contains(query)
                               it.conversationId().contains(query)
                           }
                           else -> {
                               val name = EaseIM.getUserProvider()?.getSyncUser(it.conversationId())?.getRemarkOrName() ?: it.conversationId()
                               name.contains(query)
                           }
                       }
                   }
               }
               .map { it.parse() }
        }

    /**
     * Search msg from local .
     */
    suspend fun searchMessage(
        keywords:String,
        timeStamp:Long,
        maxCount:Int,
        from:String?,
        direction:ChatSearchDirection,
        chatScope:ChatSearchScope
    ):List<ChatMessage> =
        withContext(Dispatchers.IO){
            chatManager.searchMessage(keywords, timeStamp, maxCount, from, direction,chatScope)
        }

    /**
     * Search conversation msg from local .
     */
    suspend fun searchMessageByConversation(
        conversationId:String,
        keywords:String,
        timeStamp:Long,
        maxCount:Int,
        from:String?,
        direction:ChatSearchDirection,
        chatScope:ChatSearchScope
    ):List<ChatMessage> =
        withContext(Dispatchers.IO){
            chatManager.getConversation(
                conversationId,
                ChatConversationType.Chat,
                true
            ).searchMessage(
                keywords,timeStamp,maxCount,from,direction,chatScope
            )
        }

}