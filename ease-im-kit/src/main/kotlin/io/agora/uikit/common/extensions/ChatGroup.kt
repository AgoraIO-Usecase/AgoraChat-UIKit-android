package io.agora.uikit.common.extensions

import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatCustomMessageBody
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageStatus
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser

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