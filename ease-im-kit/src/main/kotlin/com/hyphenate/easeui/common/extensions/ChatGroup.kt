package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser

/**
 * Check if the current user is the owner of the group.
 */
fun ChatGroup.isOwner() = owner == ChatClient.getInstance().currentUser

/**
 * Check if the current user is the admin of the group.
 */
fun ChatGroup.isAdmin(): Boolean {
   return adminList?.firstOrNull { it == ChatClient.getInstance().currentUser } != null
}

fun ChatGroup.getOwnerInfo():EaseUser{
   return EaseProfile.getGroupMember(groupId, owner)?.toUser() ?: EaseUser(owner)
}

/**
 * Create local group message.
 */
internal fun ChatGroup.createNewGroupMessage(groupName:String): ChatMessage? {
   val name = if (groupName.length > 16) {
      groupName.substring(0, 16) + "..."
   } else {
      groupName
   }
   EaseIM.getContext()?.let { context ->
      return ChatMessage.createSendMessage(ChatMessageType.CUSTOM).let {
         it.from = ChatClient.getInstance().currentUser
         it.to = groupId
         it.chatType = ChatType.GroupChat
         val body = ChatCustomMessageBody(EaseConstant.MESSAGE_CUSTOM_ALERT)
         mutableMapOf(
            EaseConstant.MESSAGE_CUSTOM_ALERT_TYPE to EaseConstant.GROUP_WELCOME_MESSAGE,
            EaseConstant.MESSAGE_CUSTOM_ALERT_CONTENT to context.getString(R.string.ease_chat_new_group_welcome,name),
            EaseConstant.GROUP_WELCOME_MESSAGE_GROUP_NAME to groupName
         ).let { map ->
            body.params = map
         }
         it.body = body
         it.setStatus(ChatMessageStatus.SUCCESS)
         it
      }
   }
   return null
}