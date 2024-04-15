package com.hyphenate.easeui.common

import com.hyphenate.EMChatThreadChangeListener
import com.hyphenate.easeui.common.ChatMultiDeviceListener.CONTACT_ACCEPT
import com.hyphenate.easeui.common.ChatMultiDeviceListener.CONTACT_REMOVE
import com.hyphenate.easeui.common.ChatMultiDeviceListener.GROUP_CREATE
import com.hyphenate.easeui.common.ChatMultiDeviceListener.GROUP_DESTROY
import com.hyphenate.easeui.common.ChatMultiDeviceListener.GROUP_JOIN
import com.hyphenate.easeui.common.ChatMultiDeviceListener.GROUP_LEAVE
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.createUnsentMessage
import com.hyphenate.easeui.common.extensions.getUserInfo
import com.hyphenate.easeui.common.extensions.isGroupChat
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.common.helper.EaseAtMessageHelper
import com.hyphenate.easeui.feature.invitation.helper.EaseNotificationMsgManager
import com.hyphenate.easeui.feature.invitation.enums.InviteMessageStatus
import com.hyphenate.easeui.feature.invitation.helper.RequestMsgHelper
import com.hyphenate.easeui.interfaces.OnEventResultListener
import com.hyphenate.easeui.model.EaseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections


internal class ChatListenersWrapper : ChatConnectionListener, ChatMessageListener, ChatGroupChangeListener,
    ChatContactListener, ChatConversationListener, ChatPresenceListener, ChatRoomChangeListener,
    ChatMultiDeviceListener, EMChatThreadChangeListener {

    private val chatConnectionListener = Collections.synchronizedList(mutableListOf<ChatConnectionListener>())
    private val chatMessageListener = Collections.synchronizedList(mutableListOf<ChatMessageListener>())
    private val chatGroupChangeListener = Collections.synchronizedList(mutableListOf<ChatGroupChangeListener>())
    private val chatContactListener = Collections.synchronizedList(mutableListOf<ChatContactListener>())
    private val chatConversationListener = Collections.synchronizedList(mutableListOf<ChatConversationListener>())
    private val chatPresenceListener = Collections.synchronizedList(mutableListOf<ChatPresenceListener>())
    private val chatRoomChangeListener = Collections.synchronizedList(mutableListOf<ChatRoomChangeListener>())
    private val chatMultiDeviceListener = Collections.synchronizedList(mutableListOf<ChatMultiDeviceListener>())
    private val eventResultListener = Collections.synchronizedList(mutableListOf<OnEventResultListener>())
    private val chatThreadChangeListener = Collections.synchronizedList(mutableListOf<ChatThreadChangeListener>())

    fun addListeners(){
        ChatClient.getInstance().addConnectionListener(this)
        ChatClient.getInstance().chatManager().addMessageListener(this)
        ChatClient.getInstance().chatManager().addConversationListener(this)
        ChatClient.getInstance().groupManager().addGroupChangeListener(this)
        ChatClient.getInstance().contactManager().setContactListener(this)
        ChatClient.getInstance().presenceManager().addListener(this)
        ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(this)
        ChatClient.getInstance().addMultiDeviceListener(this)
        ChatClient.getInstance().chatThreadManager().addChatThreadChangeListener(this)
    }

    fun removeListeners(){
        ChatClient.getInstance().removeConnectionListener(this)
        ChatClient.getInstance().chatManager().removeMessageListener(this)
        ChatClient.getInstance().chatManager().removeConversationListener(this)
        ChatClient.getInstance().groupManager().removeGroupChangeListener(this)
        ChatClient.getInstance().contactManager().removeContactListener(this)
        ChatClient.getInstance().presenceManager().removeListener(this)
        ChatClient.getInstance().chatroomManager().removeChatRoomListener(this)
        ChatClient.getInstance().removeMultiDeviceListener(this)
        ChatClient.getInstance().chatThreadManager().removeChatThreadChangeListener(this)

        chatConnectionListener.clear()
        chatMessageListener.clear()
        chatGroupChangeListener.clear()
        chatContactListener.clear()
        chatConversationListener.clear()
        chatPresenceListener.clear()
        chatRoomChangeListener.clear()
        chatMultiDeviceListener.clear()
        chatThreadChangeListener.clear()
    }

    fun addConnectionListener(listener: ChatConnectionListener){
        if (chatConnectionListener.contains(listener)) return
        chatConnectionListener.add(listener)
    }

    fun removeConnectionListener(listener: ChatConnectionListener){
        chatConnectionListener.remove(listener)
    }

    fun addChatMessageListener(listener:ChatMessageListener){
        if (chatMessageListener.contains(listener)) return
        chatMessageListener.add(listener)
    }

    fun removeChatMessageListener(listener:ChatMessageListener){
        chatMessageListener.remove(listener)
    }

    fun addGroupChangeListener(listener:ChatGroupChangeListener){
        if (chatGroupChangeListener.contains(listener)) return
        chatGroupChangeListener.add(listener)
    }

    fun removeGroupChangeListener(listener:ChatGroupChangeListener){
        chatGroupChangeListener.remove(listener)
    }

    fun addContactListener(listener:ChatContactListener){
        if (chatContactListener.contains(listener)) return
        chatContactListener.add(listener)
    }

    fun removeContactListener(listener:ChatContactListener){
        chatContactListener.remove(listener)
    }

    fun addConversationListener(listener:ChatConversationListener){
        if (chatConversationListener.contains(listener)) return
        chatConversationListener.add(listener)
    }

    fun removeConversationListener(listener:ChatConversationListener){
        chatConversationListener.remove(listener)
    }

    fun addPresenceListener(listener:ChatPresenceListener){
        if (chatPresenceListener.contains(listener)) return
        chatPresenceListener.add(listener)
    }

    fun removePresenceListener(listener:ChatPresenceListener){
        chatPresenceListener.remove(listener)
    }

    fun addChatRoomChangeListener(listener:ChatRoomChangeListener){
        if (chatRoomChangeListener.contains(listener)) return
        chatRoomChangeListener.add(listener)
    }

    fun removeChatRoomChangeListener(listener:ChatRoomChangeListener){
        chatRoomChangeListener.remove(listener)
    }

    fun addMultiDeviceListener(listener:ChatMultiDeviceListener){
        if (chatMultiDeviceListener.contains(listener)) return
        chatMultiDeviceListener.add(listener)
    }

    fun removeMultiDeviceListener(listener:ChatMultiDeviceListener){
        chatMultiDeviceListener.remove(listener)
    }

    fun addEventResultListener(listener:OnEventResultListener){
        if (eventResultListener.contains(listener)) return
        eventResultListener.add(listener)
    }

    fun removeEventResultListener(listener:OnEventResultListener){
        eventResultListener.remove(listener)
    }

    fun addThreadChangeListener(listener:ChatThreadChangeListener){
        chatThreadChangeListener.add(listener)
    }

    fun removeThreadChangeListener(listener:ChatThreadChangeListener){
        chatThreadChangeListener.remove(listener)
    }

    companion object {

        private var instance: ChatListenersWrapper? = null

        fun getInstance(): ChatListenersWrapper {
            if (instance == null) {
                synchronized(ChatListenersWrapper::class.java) {
                    if (instance == null) {
                        instance = ChatListenersWrapper()
                    }
                }
            }
            return instance!!
        }
    }


    /**  ChatConnectionListener  */
    override fun onConnected() {
        chatConnectionListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onConnected()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDisconnected(errorCode: Int) {
        chatConnectionListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onDisconnected(errorCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTokenWillExpire() {
        chatConnectionListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onTokenWillExpire()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTokenExpired() {
        chatConnectionListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onTokenExpired()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onLogout(errorCode: Int, info: String?) {
        chatConnectionListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onLogout(errorCode, info)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  ChatConnectionListener  */
    override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
        // Update message userinfo when receive messages.
        CoroutineScope(Dispatchers.IO).launch {
            messages?.forEach { msg ->
                msg.getUserInfo(true)
            }
            try {
                EaseAtMessageHelper.get().parseMessages(messages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMessageReceived(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCmdMessageReceived(messages: MutableList<ChatMessage>?) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onCmdMessageReceived(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onGroupMessageRead(groupReadAcks: MutableList<ChatGroupReadAck>?) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onGroupMessageRead(groupReadAcks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageChanged(message: ChatMessage?, change: Any?) {
        super.onMessageChanged(message, change)
        chatMessageListener.let {
            try {
                for (messageListener in it) {
                    messageListener.onMessageChanged(message, change)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageContentChanged(
        messageModified: ChatMessage?,
        operatorId: String?,
        operationTime: Long
    ) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMessageContentChanged(messageModified,operatorId,operationTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageDelivered(messages: MutableList<ChatMessage>?) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMessageDelivered(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageRead(messages: MutableList<ChatMessage>?) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMessageRead(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageRecalled(messages: MutableList<ChatMessage>?) {
        if (messages != null && messages.size > 0) {
            for (message in messages) {
                if (message.isGroupChat() && EaseAtMessageHelper.get().isAtMeMsg(message)) {
                    EaseAtMessageHelper.get().removeAtMeGroup(message.conversationId())
                }
                val recallMsg = message.createUnsentMessage(true)
                ChatClient.getInstance().chatManager().saveMessage(recallMsg)
            }
        }
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMessageRecalled(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReactionChanged(messageReactionChangeList: MutableList<ChatMessageReactionChange>?) {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onReactionChanged(messageReactionChangeList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReadAckForGroupMessageUpdated() {
        chatMessageListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onReadAckForGroupMessageUpdated()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  GroupChangeListener  */
    override fun onInvitationReceived(
        groupId: String?,
        groupName: String?,
        inviter: String?,
        reason: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onInvitationReceived(groupId,groupName,inviter,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestToJoinReceived(
        groupId: String?,
        groupName: String?,
        applicant: String?,
        reason: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onRequestToJoinReceived(groupId,groupName,applicant,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestToJoinAccepted(groupId: String?, groupName: String?, accepter: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onRequestToJoinAccepted(groupId,groupName,accepter)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestToJoinDeclined(
        groupId: String?,
        groupName: String?,
        decliner: String?,
        reason: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onRequestToJoinDeclined(groupId,groupName,decliner,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onInvitationAccepted(groupId: String?, invitee: String?, reason: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onInvitationAccepted(groupId,invitee,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onInvitationDeclined(groupId: String?, invitee: String?, reason: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onInvitationDeclined(groupId,invitee,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onUserRemoved(groupId: String?, groupName: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onUserRemoved(groupId,groupName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onGroupDestroyed(groupId: String?, groupName: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onGroupDestroyed(groupId,groupName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAutoAcceptInvitationFromGroup(
        groupId: String?,
        inviter: String?,
        inviteMessage: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onAutoAcceptInvitationFromGroup(groupId,inviter,inviteMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMuteListAdded(groupId: String?, mutes: MutableList<String>?, muteExpire: Long) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMuteListAdded(groupId,mutes,muteExpire)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMuteListRemoved(groupId: String?, mutes: MutableList<String>?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMuteListRemoved(groupId,mutes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onWhiteListAdded(groupId: String?, whitelist: MutableList<String>?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onWhiteListAdded(groupId,whitelist)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onWhiteListRemoved(groupId: String?, whitelist: MutableList<String>?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onWhiteListRemoved(groupId,whitelist)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAllMemberMuteStateChanged(groupId: String?, isMuted: Boolean) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onAllMemberMuteStateChanged(groupId,isMuted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAdminAdded(groupId: String?, administrator: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onAdminAdded(groupId,administrator)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAdminRemoved(groupId: String?, administrator: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onAdminRemoved(groupId,administrator)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOwnerChanged(groupId: String?, newOwner: String?, oldOwner: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onOwnerChanged(groupId,newOwner,oldOwner)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMemberJoined(groupId: String?, member: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMemberJoined(groupId,member)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMemberExited(groupId: String?, member: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMemberExited(groupId,member)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAnnouncementChanged(groupId: String?, announcement: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onAnnouncementChanged(groupId,announcement)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSharedFileAdded(groupId: String?, sharedFile: ChatShareFile?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onSharedFileAdded(groupId,sharedFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSharedFileDeleted(groupId: String?, fileId: String?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onSharedFileDeleted(groupId,fileId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSpecificationChanged(group: ChatGroup?) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onSpecificationChanged(group)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onGroupMemberAttributeChanged(
        groupId: String?,
        userId: String?,
        attribute: MutableMap<String, String>?,
        from: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onGroupMemberAttributeChanged(groupId, userId, attribute, from)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestToJoinDeclined(
        groupId: String?,
        groupName: String?,
        decliner: String?,
        reason: String?,
        applicant: String?
    ) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onRequestToJoinDeclined(groupId, groupName, decliner, reason, applicant)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStateChanged(group: ChatGroup?, isDisabled: Boolean) {
        chatGroupChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onStateChanged(group, isDisabled)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  ContactListener  */
    override fun onContactAdded(username: String?) {
        chatContactListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onContactAdded(username)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onContactDeleted(username: String?) {
        deleteDefaultContactAgreedMsg(username)
        chatContactListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onContactDeleted(username)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onContactInvited(username: String?, reason: String?) {
        saveDefaultContactInvitedMsg(username, reason)
        chatContactListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onContactInvited(username,reason)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onFriendRequestAccepted(username: String?) {
        chatContactListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onFriendRequestAccepted(username)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onFriendRequestDeclined(username: String?) {
        chatContactListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onFriendRequestDeclined(username)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  ConversationListener  */
    override fun onConversationUpdate() {
        chatConversationListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onConversationUpdate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onConversationRead(from: String?, to: String?) {
        chatConversationListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onConversationRead(from, to)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  PresenceListener  */
    override fun onPresenceUpdated(presences: MutableList<ChatPresence>?) {
        defaultPresencesEvent(presences)
        chatPresenceListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onPresenceUpdated(presences)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  ChatRoomChangeListener  */
    override fun onChatRoomDestroyed(roomId: String?, roomName: String?) {
        chatRoomChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onChatRoomDestroyed(roomId,roomName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMemberExited(roomId: String?, roomName: String?, participant: String?) {
        chatRoomChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onMemberExited(roomId,roomName,participant)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRemovedFromChatRoom(
        reason: Int,
        roomId: String?,
        roomName: String?,
        participant: String?
    ) {
        chatRoomChangeListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onRemovedFromChatRoom(reason,roomId,roomName,participant)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**  MultiDeviceListener  */
    override fun onContactEvent(event: Int, target: String?, ext: String?) {
        defaultMultiDeviceContactEvent(event,target,ext)
        chatMultiDeviceListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onContactEvent(event,target,ext)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onGroupEvent(event: Int, target: String?, usernames: MutableList<String>?) {
        defaultMultiDeviceGroupEvent(event,target,usernames)
        chatMultiDeviceListener.let {
            try {
                for (connectionListener in it) {
                    connectionListener.onGroupEvent(event,target,usernames)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onConversationEvent(
        event: Int,
        conversationId: String?,
        type: ChatConversationType?
    ) {
        super.onConversationEvent(event, conversationId, type)
        chatMultiDeviceListener.let {
            try {
                for (emMultiDeviceListener in it) {
                    emMultiDeviceListener.onConversationEvent(event, conversationId, type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageRemoved(conversationId: String?, deviceId: String?) {
        super.onMessageRemoved(conversationId, deviceId)
        chatMultiDeviceListener.let {
            try {
                for (emMultiDeviceListener in it) {
                    emMultiDeviceListener.onMessageRemoved(conversationId, deviceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onChatThreadEvent(event: Int, target: String?, usernames: MutableList<String>?) {
        super.onChatThreadEvent(event, target, usernames)
        chatMultiDeviceListener.let {
            try {
                for (emMultiDeviceListener in it) {
                    emMultiDeviceListener.onChatThreadEvent(event, target, usernames)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onChatThreadCreated(event: ChatThreadEvent?) {
        chatThreadChangeListener.let {
            try {
                for (emChatThreadChangeListener in it) {
                    emChatThreadChangeListener.onChatThreadCreated(event)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onChatThreadUpdated(event: ChatThreadEvent?) {
        chatThreadChangeListener.let {
            try {
                for (emChatThreadChangeListener in it) {
                    emChatThreadChangeListener.onChatThreadUpdated(event)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onChatThreadDestroyed(event: ChatThreadEvent?) {
        chatThreadChangeListener.let {
            try {
                for (emChatThreadChangeListener in it) {
                    emChatThreadChangeListener.onChatThreadDestroyed(event)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onChatThreadUserRemoved(event: ChatThreadEvent?) {
        chatThreadChangeListener.let {
            try {
                for (emChatThreadChangeListener in it) {
                    emChatThreadChangeListener.onChatThreadUserRemoved(event)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveDefaultContactInvitedMsg(username: String?, reason: String?){
        val useDefaultContactSystemMsg = EaseIM.getConfig()?.systemMsgConfig?.useDefaultContactSystemMsg ?: false
        if (useDefaultContactSystemMsg){
            var isExist = false
            val allRequestMessage = EaseNotificationMsgManager.getInstance().getAllMessage()
            allRequestMessage.map {msg->
                if (msg.ext()[EaseConstant.SYSTEM_MESSAGE_FROM] == username){
                    isExist = true
                }
            }
            if (!isExist){
                val ext: MutableMap<String, Any> = EaseNotificationMsgManager.getInstance().createMsgExt()
                username?.let {
                    ext[EaseConstant.SYSTEM_MESSAGE_FROM] = it
                }
                reason?.let {
                    ext[EaseConstant.SYSTEM_MESSAGE_REASON] = it
                }
                ext[EaseConstant.SYSTEM_MESSAGE_STATUS] = InviteMessageStatus.BEINVITEED.name
                EaseIM.getContext()?.let {
                    EaseNotificationMsgManager.getInstance()
                        .createMessage( RequestMsgHelper.getSystemMessage(it,ext), ext)

                    EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.ADD.name)
                        .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.NOTIFY))

                }
            }
        }
    }

    private fun deleteDefaultContactAgreedMsg(username: String?){
        val useDefaultContactSystemMsg = EaseIM.getConfig()?.systemMsgConfig?.useDefaultContactSystemMsg ?: false
        if (useDefaultContactSystemMsg){
            val allRequestMessage = EaseNotificationMsgManager.getInstance().getAllMessage()
            allRequestMessage.map {msg->
                if (msg.ext()[EaseConstant.SYSTEM_MESSAGE_FROM] == username &&
                    msg.ext()[EaseConstant.SYSTEM_MESSAGE_STATUS] == InviteMessageStatus.AGREED.name
                ){
                    EaseNotificationMsgManager.getInstance().removeMessage(msg)
                    EaseIM.getContext()?.let {
                        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.NOTIFY))

                    }
                }
            }
        }
    }

    private fun defaultMultiDeviceContactEvent(event: Int, target: String?, ext: String?){
        val useDefaultMultiDeviceContactEvent = EaseIM.getConfig()?.multiDeviceConfig?.useDefaultMultiDeviceContactEvent ?: false
        if (useDefaultMultiDeviceContactEvent){
            when(event){
                CONTACT_REMOVE -> {
                    val allRequestMessage = EaseNotificationMsgManager.getInstance().getAllMessage()
                    allRequestMessage.map {msg->
                        if (msg.ext()[EaseConstant.SYSTEM_MESSAGE_FROM] == target &&
                            msg.ext()[EaseConstant.SYSTEM_MESSAGE_STATUS] == InviteMessageStatus.AGREED.name
                        ){
                            EaseNotificationMsgManager.getInstance().removeMessage(msg)
                            EaseIM.getContext()?.let {
                                EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
                                    .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.CONTACT))

                                EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.REMOVE.name)
                                    .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.REMOVE.name, EaseEvent.TYPE.NOTIFY))
                            }
                        }
                    }
                }
                CONTACT_ACCEPT -> {
                    EaseIM.getContext()?.let {
                        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.ADD.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.CONTACT))
                    }
                }
                else -> {}
            }

        }
    }

    private fun defaultMultiDeviceGroupEvent(event: Int, target: String?, usernames: MutableList<String>?){
        val useDefaultMultiDeviceGroupEvent = EaseIM.getConfig()?.multiDeviceConfig?.useDefaultMultiDeviceGroupEvent ?: false
        if (useDefaultMultiDeviceGroupEvent){
            when(event){
                GROUP_CREATE -> {
                    EaseIM.getContext()?.let {
                        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.ADD.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.GROUP, target))
                    }
                }
                GROUP_DESTROY -> {
                    EaseIM.getContext()?.let {
                        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.DESTROY.name, EaseEvent.TYPE.GROUP, target))
                    }
                }
                GROUP_JOIN -> {
                    EaseIM.getContext()?.let {
                        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.ADD.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.GROUP, target))
                    }
                }
                GROUP_LEAVE -> {
                    EaseIM.getContext()?.let {
                        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name)
                            .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.LEAVE.name, EaseEvent.TYPE.GROUP, target))
                    }
                }
                else -> {}
            }
        }
    }

    private fun defaultPresencesEvent(presences: MutableList<ChatPresence>?){
        val enablePresences = EaseIM.getConfig()?.presencesConfig?.enablePresences ?: false
        if (enablePresences){
            presences?.forEach { presence->
                EaseIM.getCache().insertPresences(presence.publisher,presence)
                EaseIM.getContext()?.let {
                    EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE.name)
                        .post(it.mainScope(), EaseEvent(EaseEvent.EVENT.UPDATE.name, EaseEvent.TYPE.PRESENCE))
                }
            }
        }
    }

    @Synchronized
    internal fun callbackEvent(function: String, errorCode: Int, errorMessage: String?) {
        if (eventResultListener.isEmpty()) {
            return
        }
        eventResultListener.iterator().forEach { listener ->
            listener.onEventResult(function, errorCode, errorMessage)
        }
    }

}