package com.hyphenate.easeui.common.extensions

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatDownloadStatus
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatImageMessageBody
import com.hyphenate.easeui.common.ChatImageUtils
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageDirection
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatNormalFileMessageBody
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.ChatVideoMessageBody
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_EXT_USER_INFO_AVATAR_KEY
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_EXT_USER_INFO_NICKNAME_KEY
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_EXT_USER_INFO_REMARK_KEY
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_URL_PREVIEW_DESCRIPTION
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_URL_PREVIEW_IMAGE_URL
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_URL_PREVIEW_TITLE
import com.hyphenate.easeui.common.EaseConstant.MESSAGE_URL_PREVIEW_URL
import com.hyphenate.easeui.common.helper.DateFormatHelper
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.impl.OnError
import com.hyphenate.easeui.common.impl.OnProgress
import com.hyphenate.easeui.common.impl.OnSuccess
import com.hyphenate.easeui.configs.EaseDateFormatConfig
import com.hyphenate.easeui.feature.invitation.enums.InviteMessageStatus
import com.hyphenate.easeui.model.EasePreview
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseSize
import com.hyphenate.easeui.provider.getSyncUser
import org.json.JSONObject


/**
 * Send message to server.
 * @param onSuccess The callback when send message success.
 * @param onError The callback when send message error.
 * @param onProgress The callback when send message progress.
 */
fun ChatMessage.send(onSuccess: OnSuccess = {}
                     , onError: OnError = {_,_ ->}
                     , onProgress: OnProgress = {}) {
    // Set the message status callback by ChatMessage.
    // Should set callback before send message.
    setMessageStatusCallback(CallbackImpl(onSuccess, onError, onProgress))
    ChatClient.getInstance().chatManager().sendMessage(this)
}

/**
 * Set parent message id attribute for chat thread message.
 * @param parentId The parent id, usually is the group that the chat thread belongs to.
 * @param parentMsgId The parent message id, usually is the group message id which created the chat thread.
 */
fun ChatMessage.setParentInfo(parentId: String?, parentMsgId: String?): Boolean {
    if (isChatThreadMessage && (parentId.isNullOrEmpty().not() || parentMsgId.isNullOrEmpty().not())) {
        if (parentId.isNullOrEmpty().not()) {
            setAttribute(EaseConstant.MESSAGE_ATTR_THREAD_FLAG_PARENT_ID, parentId)
        }
        if (parentMsgId.isNullOrEmpty().not()) {
            setAttribute(EaseConstant.MESSAGE_ATTR_THREAD_FLAG_PARENT_MSG_ID, parentMsgId)
        }
        return true
    }
    return false
}

/**
 * Get parent id attribute for chat thread message.
 * @return The parent id.
 */
fun ChatMessage.getParentId(): String? {
    if (isChatThreadMessage.not()) {
        return null
    }
    val parentId = getStringAttribute(EaseConstant.MESSAGE_ATTR_THREAD_FLAG_PARENT_ID, "")
    return if (parentId.isNullOrEmpty()) {
        ChatClient.getInstance().chatManager().getMessage(getParentMessageId())?.conversationId()
    } else {
        parentId
    }
}

/**
 * Get parent message id attribute for chat thread message.
 * @return The parent message id.
 */
fun ChatMessage.getParentMessageId(): String? {
    if (isChatThreadMessage.not()) {
        return null
    }
    return getStringAttribute(EaseConstant.MESSAGE_ATTR_THREAD_FLAG_PARENT_MSG_ID, "")
}

internal fun ChatMessage.getMessageDigest(context: Context): String {
    return when(type) {
        ChatMessageType.LOCATION -> {
            if (direct() == ChatMessageDirection.RECEIVE) {
                var name = from
                getSyncUserFromProvider()?.let { profile ->
                    name = profile.getRemarkOrName()
                }
                context.getString(R.string.ease_location_recv, name)
            } else {
                context.getString(R.string.ease_location_prefix)
            }
        }
        ChatMessageType.IMAGE -> {
            context.getString(R.string.ease_picture)
        }
        ChatMessageType.VIDEO -> {
            context.getString(R.string.ease_video)
        }
        ChatMessageType.VOICE -> {
            context.getString(R.string.ease_voice)
        }
        ChatMessageType.FILE -> {
            val body = body as ChatNormalFileMessageBody
            val filename = body.fileName
            if (filename.isNullOrEmpty()) {
                context.getString(R.string.ease_file)
            } else {
                "${context.getString(R.string.ease_file)} $filename"
            }
        }
        ChatMessageType.CUSTOM -> {
            if (isUserCardMessage()) {
                context.getString(R.string.ease_user_card, getUserCardInfo()?.getRemarkOrName() ?: "")
            } else if (isAlertMessage()) {
                (body as ChatCustomMessageBody).params[EaseConstant.MESSAGE_CUSTOM_ALERT_CONTENT]
                    ?: context.getString(R.string.ease_custom)
            } else {
                context.getString(R.string.ease_custom)
            }
        }
        ChatMessageType.TXT -> {
            (body as ChatTextMessageBody).let {
                getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false).let { isBigExp ->
                    if(isBigExp) {
                        if (it.message.isNullOrEmpty()) {
                            context.getString(R.string.ease_dynamic_expression)
                        } else {
                            it.message
                        }
                    } else {
                        it.message
                    }
                } ?: it.message
            }
        }
        ChatMessageType.COMBINE -> {
            context.getString(R.string.ease_combine)
        }
        else -> {
            ""
        }
    }
}

internal fun ChatMessage.getSyncUserFromProvider(): EaseProfile? {
    return if (chatType == ChatType.Chat) {
        if (direct() == ChatMessageDirection.RECEIVE) {
            // Get user info from user profile provider.
            EaseIM.getUserProvider()?.getSyncUser(from)
        } else {
            EaseIM.getCurrentUser()
        }
    } else if (chatType == ChatType.GroupChat) {
        if (direct() == ChatMessageDirection.RECEIVE) {
            // Get user info from cache first.
            // Then get user info from user provider.
            EaseProfile.getGroupMember(conversationId(), from)
        } else {
            EaseIM.getCurrentUser()
        }
    } else {
        null
    }
}

/**
 * Create a local message when unsent a message or receive a unsent message.
 */
internal fun ChatMessage.createUnsentMessage(isReceive: Boolean = false): ChatMessage {
    val msgNotification = if (isReceive) {
        ChatMessage.createReceiveMessage(ChatMessageType.TXT)
    } else {
        ChatMessage.createSendMessage(ChatMessageType.TXT)
    }

    val text: String = if (isSend()) {
        EaseIM.getContext()?.resources?.getString(R.string.ease_msg_recall_by_self)
            ?: ""
    } else {
        EaseIM.getContext()?.resources?.getString(R.string.ease_msg_recall_by_user, getUserInfo()?.getRemarkOrName() ?: from)
            ?: ""
    }
    val txtBody = ChatTextMessageBody(
        text
    )
    msgNotification.msgId = msgId
    msgNotification.addBody(txtBody)
    msgNotification.to = to
    msgNotification.from = from
    msgNotification.msgTime = msgTime
    msgNotification.chatType = chatType
    msgNotification.setLocalTime(localTime())
    msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true)
    msgNotification.setStatus(ChatMessageStatus.SUCCESS)
    msgNotification.setIsChatThreadMessage(isChatThreadMessage)
    return msgNotification
}

/**
 * Create a local message when pin message.
 */
internal fun ChatMessage.createNotifyPinMessage(operationUser: String?): ChatMessage {
    val userInfo = EaseIM.getUserProvider()?.getSyncUser(operationUser)
    var msgNotification:ChatMessage?
    if (TextUtils.equals(to, ChatClient.getInstance().currentUser)) {
        msgNotification= ChatMessage.createReceiveMessage(ChatMessageType.TXT)
    }else{
        msgNotification= ChatMessage.createSendMessage(ChatMessageType.TXT)
    }
    var content:String
    content = if (pinnedInfo() == null || pinnedInfo().operatorId().isNullOrEmpty()) {
        "${userInfo?.getRemarkOrName()?:operationUser} removed a pin message"
    } else {
        "${userInfo?.getRemarkOrName()?:operationUser} pinned a message"
    }
    operationUser?.let {
        if (TextUtils.equals(it, ChatClient.getInstance().currentUser)) {
            content = content.replace(it, "You")
        }
    }
    val txtBody = ChatTextMessageBody(content)
    msgNotification.addBody(txtBody)
    msgNotification.from = from
    msgNotification.to = to
    msgNotification.msgTime = msgTime
    msgNotification.chatType = chatType
    msgNotification.isUnread = false
    msgNotification.msgTime = System.currentTimeMillis()
    msgNotification.setLocalTime(System.currentTimeMillis())
    msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true)
    msgNotification.setStatus(ChatMessageStatus.SUCCESS)
    msgNotification.setIsChatThreadMessage(isChatThreadMessage)
    return msgNotification
}

/**
 * Get the timestamp of the message based on the chat options.
 * @return The timestamp of the message.
 */
fun ChatMessage.getTimestamp(): Long {
    return if (ChatClient.getInstance().options.isSortMessageByServerTime) msgTime else localTime()
}

/**
 * Get the String timestamp from [ChatMessage].
 */
fun ChatMessage.getDateFormat(isChat: Boolean = false): String? {
    val timestamp = getTimestamp()
    if (isChat) {
        return if (DateFormatHelper.isSameDay(timestamp)) {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.chatTodayFormat
                ?: EaseDateFormatConfig.DEFAULT_CHAT_TODAY_FORMAT)
        } else if (DateFormatHelper.isSameYear(timestamp)) {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.chatOtherDayFormat
                    ?: EaseDateFormatConfig.DEFAULT_CHAT_OTHER_DAY_FORMAT)
        } else {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.chatOtherYearFormat
                    ?: EaseDateFormatConfig.DEFAULT_CHAT_OTHER_YEAR_FORMAT)
        }
    } else {
        return if (DateFormatHelper.isSameDay(timestamp)) {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.convTodayFormat
                    ?: EaseDateFormatConfig.DEFAULT_CONV_TODAY_FORMAT)
        } else if (DateFormatHelper.isSameYear(timestamp)) {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.convOtherDayFormat
                    ?: EaseDateFormatConfig.DEFAULT_CONV_OTHER_DAY_FORMAT)
        } else {
            DateFormatHelper.timestampToDateString(timestamp
                , EaseIM.getConfig()?.dateFormatConfig?.convOtherYearFormat
                    ?: EaseDateFormatConfig.DEFAULT_CONV_OTHER_YEAR_FORMAT)
        }
    }
}

/**
 * Check whether the message is a user card message.
 */
fun ChatMessage.isUserCardMessage(): Boolean {
    val event = (body as? ChatCustomMessageBody)?.event() ?: ""
    return event == EaseConstant.USER_CARD_EVENT
}

/**
 * Check whether the message is a alert message.
 */
fun ChatMessage.isAlertMessage(): Boolean {
    val event = (body as? ChatCustomMessageBody)?.event() ?: ""
    return event == EaseConstant.MESSAGE_CUSTOM_ALERT
}

/**
 * Get user card info from message.
 */
fun ChatMessage.getUserCardInfo(): EaseProfile? {
    if (isUserCardMessage()) {
        (body as? ChatCustomMessageBody)?.let {
            val params: Map<String, String> = it.params
            val uId = params[EaseConstant.USER_CARD_ID]
            val nickname = params[EaseConstant.USER_CARD_NICK]
            val headUrl = params[EaseConstant.USER_CARD_AVATAR]
            if (uId.isNullOrEmpty()) return null
            return EaseProfile(uId, nickname, headUrl)
        }
    }
    return null
}

internal fun ChatMessage.isGroupChat(): Boolean {
    return chatType == ChatType.GroupChat
}

internal fun ChatMessage.isSingleChat(): Boolean {
    return chatType == ChatType.Chat
}

/**
 * Check whether the message is sent by current user.
 */
internal fun ChatMessage.isSend(): Boolean {
    return direct() == ChatMessageDirection.SEND
}

/**
 * Add userinfo to message when sending message.
 */
internal fun ChatMessage.addUserInfo(nickname: String?, avatarUrl: String?, remark: String? = null) {
    if (nickname.isNullOrEmpty() && avatarUrl.isNullOrEmpty() && remark.isNullOrEmpty()) {
        return
    }
    val info = JSONObject()
    if (!nickname.isNullOrEmpty()) info.put(MESSAGE_EXT_USER_INFO_NICKNAME_KEY, nickname)
    if (!avatarUrl.isNullOrEmpty()) info.put(MESSAGE_EXT_USER_INFO_AVATAR_KEY, avatarUrl)
    if (!remark.isNullOrEmpty()) info.put(MESSAGE_EXT_USER_INFO_REMARK_KEY, remark)
    setAttribute(EaseConstant.MESSAGE_EXT_USER_INFO_KEY, info)
}

/**
 * Parse userinfo from message when receiving a message.
 */
internal fun ChatMessage.getUserInfo(updateCache: Boolean = false): EaseProfile? {
    EaseIM.getUserProvider()?.getSyncUser(from)?.let {
        return it
    }
    var profile: EaseProfile? = EaseProfile(from)
    try {
        getJSONObjectAttribute(EaseConstant.MESSAGE_EXT_USER_INFO_KEY)?.let { info ->
            profile = EaseProfile(
                id = from,
                name = info.optString(MESSAGE_EXT_USER_INFO_NICKNAME_KEY),
                avatar = info.optString(MESSAGE_EXT_USER_INFO_AVATAR_KEY),
                remark = info.optString(MESSAGE_EXT_USER_INFO_REMARK_KEY)
            )
            profile?.setTimestamp(msgTime)
            EaseIM.getCache().insertMessageUser(from, profile!!)
            profile
        } ?: kotlin.run {
            EaseIM.getCache().getMessageUserInfo(from)
        }
    } catch (e: ChatException) {
        profile = EaseIM.getCache().getMessageUserInfo(from)
    }
    if (profile == null) {
        profile = EaseProfile(from)
    }
    return profile
}

internal fun ChatMessage.getThumbnailLocalUri(context: Context): Uri? {
    var imageUri: Uri? = null
    if (type == ChatMessageType.IMAGE) {
        (body as ChatImageMessageBody).let {
            imageUri = it.localUri
            imageUri?.takePersistablePermission(context)
            if (imageUri?.isFileExist(context) == false) {
                imageUri = it.thumbnailLocalUri()
                imageUri?.takePersistablePermission(context)
                if (imageUri?.isFileExist(context) == false) {
                    imageUri = null
                    ChatLog.e("getImageShowSize", "image file not exist")
                }
            }
        }
    } else if (type == ChatMessageType.VIDEO) {
        (body as ChatVideoMessageBody).let {
            imageUri = it.localThumbUri
            imageUri?.takePersistablePermission(context)
            if (imageUri?.isFileExist(context) == false) {
                imageUri = null
                ChatLog.e("getImageShowSize", "video file not exist")
            }
        }
    }
    return imageUri
}

/**
 * Get the size of image or video.
 */
fun ChatMessage.getImageSize(context: Context): EaseSize? {
    if (type != ChatMessageType.IMAGE && type != ChatMessageType.VIDEO) {
        return null
    }
    val originalSize = EaseSize(0,0)
    if (type == ChatMessageType.IMAGE) {
        (body as ChatImageMessageBody).let {
            originalSize.width = it.width
            originalSize.height = it.height
        }
    } else if (type == ChatMessageType.VIDEO) {
        (body as ChatVideoMessageBody).let {
            originalSize.width = it.thumbnailWidth
            originalSize.height = it.thumbnailHeight
        }
    }
    val imageUri: Uri? = getThumbnailLocalUri(context)
    // If not has original size, get size from uri.
    if (originalSize.isEmpty() && imageUri != null) {
        try {
            ChatImageUtils.getBitmapOptions(context, imageUri)?.let {
                originalSize.width = it.outWidth
                originalSize.height = it.outHeight
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return originalSize
}

/**
 * Get the show size of image or video according to the screen info.
 */
fun ChatMessage.getImageShowSize(context: Context?): EaseSize? {
    if (context == null) return null
    val originalSize = getImageSize(context) ?: return null
    val maxSize = context.getImageMaxSize()
    // if not get the screen size, the image show the wrap_content size.
    if (maxSize.isEmpty()) {
        return null
    }
    var radio = originalSize.width * 1.0f / if (originalSize.height == 0) 1 else originalSize.height
    if (radio == 0f) {
        radio = 1f
    }
    val showSize = EaseSize(0, 0)
    when(radio) {
        // If radio is less than 1/10, the middle part will be cut off to display the 1/10 part.
        in 0f..1/10f -> {
            showSize.width = (maxSize.height / 10f).toInt()
            showSize.height = maxSize.height
        }
        // If radio is more than 0.1f and less than 3/4f
        in 1/10f..3/4f -> {
             // the maximum show height is used
            showSize.width = (maxSize.height * radio).toInt()
            showSize.height = maxSize.height
        }
        in 3/4f..10f -> {
            // the maximum show width is used
            showSize.width = maxSize.width
            showSize.height = (maxSize.width / radio).toInt()
        }
        else -> {
            showSize.width = maxSize.width
            showSize.height = (maxSize.width / 10f).toInt()
        }
    }
    return showSize
}

/**
 * If message is a sent message:
 * (1) If local file exists, set the placeholder size with message show size.
 * (2) If local file not exists, set the placeholder size with default placeholder resource size.
 * If message is a received message:
 * (1) If thumbnail local file exists or local file exists, set the placeholder size with thumbnail show size.
 * (2) If thumbnail local file not exists and local file not exists,
 *      (1) If thumbnailDownloadStatus is pending or downloading, set the placeholder size with thumbnail show size.
 *      (2) If thumbnailDownloadStatus is success or failed, set the placeholder size with thumbnail show size.
 */
fun ChatMessage.getPlaceholderShowSize(context: Context?): EaseSize? {
    if (context == null) return null
    if (isSend()) {
        val imageUri = getThumbnailLocalUri(context)
        if (imageUri != null && imageUri.isFileExist(context)) {
            return getImageShowSize(context)
        }
    } else {
        val imageUri = getThumbnailLocalUri(context)
        if (imageUri != null && imageUri.isFileExist(context)) {
            return getImageShowSize(context)
        }
        if (type == ChatMessageType.IMAGE) {
            val imgBody = body as ChatImageMessageBody
            if (imgBody.thumbnailDownloadStatus() == ChatDownloadStatus.DOWNLOADING
                || imgBody.thumbnailDownloadStatus() == ChatDownloadStatus.PENDING) {
                return getImageShowSize(context)
            }
        }
    }
    return null
}

/**
 * Check whether the message is a silent message.
 */
internal fun ChatMessage.isSilentMessage(): Boolean {
    return getBooleanAttribute("em_ignore_notification", false)
}

/**
 * Check whether the message can be edited.
 */
internal fun ChatMessage.canEdit(): Boolean {
    return type == ChatMessageType.TXT
            && isSend()
            && isSuccess()
            && EaseIM.getConfig()?.chatConfig?.enableModifyMessageAfterSent == true
}

internal fun ChatMessage.isSuccess(): Boolean {
    return status() == ChatMessageStatus.SUCCESS
}

internal fun ChatMessage.isFail(): Boolean {
    return status() == ChatMessageStatus.FAIL
}

internal fun ChatMessage.inProgress(): Boolean {
    return status() == ChatMessageStatus.INPROGRESS
}

internal fun ChatMessage.isUnsentMessage(): Boolean {
    return if (type == ChatMessageType.TXT) {
        getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)
    } else {
        false
    }
}

internal fun ChatMessage.getInviteMessageStatus(): InviteMessageStatus? {
    if (conversationId() == EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID) {
        return InviteMessageStatus.valueOf(getStringAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS))
    }
    return null
}

/**
 * Judge whether the message is a reply message.
 */
internal fun ChatMessage.isReplyMessage(jsonResult: (JSONObject) -> Unit = {}): Boolean {
    if (ext() != null && !ext().containsKey(EaseConstant.QUOTE_MSG_QUOTE)) {
        return false
    }
    val jsonObject: JSONObject? = try {
        val msgQuote = getStringAttribute(EaseConstant.QUOTE_MSG_QUOTE, null)
        if (msgQuote.isNullOrEmpty()) {
            getJSONObjectAttribute(EaseConstant.QUOTE_MSG_QUOTE)
        } else {
            JSONObject(msgQuote)
        }
    } catch (e: ChatException) {
        ChatLog.e(
            "isReplyMessage",
            "error message: " + e.description
        )
        null
    }
    if (jsonObject == null) {
        ChatLog.e(
            "isReplyMessage",
            "error message: jsonObject is null"
        )
        return false
    }
    jsonResult(jsonObject)
    return true
}

internal fun ChatMessage.hasThreadChat(): Boolean {
    return chatThread != null
}

fun ChatMessage.isUrlPreviewMessage():Boolean{
   return attributes.containsKey(EaseConstant.MESSAGE_URL_PREVIEW)
}

fun ChatMessage.parseUrlPreview():EasePreview?{
    EaseIM.getConfig()?.chatConfig?.enableUrlPreview?.let {
        if (!it) {
            return null
        }
    }
    var preview:EasePreview? = null
    if (isUrlPreviewMessage()){
        try {
            getJSONObjectAttribute(EaseConstant.MESSAGE_URL_PREVIEW)?.let { info ->
                preview = EasePreview(
                    url = info.optString(MESSAGE_URL_PREVIEW_URL),
                    title = info.optString(MESSAGE_URL_PREVIEW_TITLE),
                    description = info.optString(MESSAGE_URL_PREVIEW_DESCRIPTION),
                    imageURL = info.optString(MESSAGE_URL_PREVIEW_IMAGE_URL)
                )
                preview?.let {
                    EaseIM.getCache().saveUrlPreviewInfo(msgId,it)
                }
            }
        } catch (e: ChatException) {
           ChatLog.e("ChatMessage","parse message error ${e.message}")
        }
    }
    return preview
}