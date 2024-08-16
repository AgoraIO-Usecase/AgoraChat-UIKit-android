package com.hyphenate.easeui.viewmodel.messages

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatCallback
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatCmdMessageBody
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageBody
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.enums.EaseReplyMap
import com.hyphenate.easeui.common.extensions.addUserInfo
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.createUnsentMessage
import com.hyphenate.easeui.common.extensions.isChatroom
import com.hyphenate.easeui.common.extensions.isGroupChat
import com.hyphenate.easeui.common.extensions.send
import com.hyphenate.easeui.common.helper.EaseAtMessageHelper
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.suspends.deleteMessage
import com.hyphenate.easeui.common.utils.EaseFileUtils
import com.hyphenate.easeui.common.utils.createExpressionMessage
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.chat.enums.EaseLoadDataType
import com.hyphenate.easeui.feature.chat.enums.getConversationType
import com.hyphenate.easeui.feature.chat.forward.helper.EaseChatMessageMultiSelectHelper
import com.hyphenate.easeui.feature.chat.interfaces.IHandleChatResultView
import com.hyphenate.easeui.repository.EaseChatManagerRepository
import com.hyphenate.easeui.common.utils.EaseImageUtils
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

open class EaseChatViewModel: EaseBaseViewModel<IHandleChatResultView>(), IChatViewRequest {
    
    private var _conversation: ChatConversation? = null
    private var _loadDataType: EaseLoadDataType? = null
    private var _parentId: String? = null
    private val chatRepository by lazy { EaseChatManagerRepository() }

    override fun setupWithToUser(
        toChatUsername: String?,
        chatType: EaseChatType?,
        loadDataType: EaseLoadDataType
    ) {
        _loadDataType = loadDataType
        _conversation = ChatClient.getInstance().chatManager().getConversation(toChatUsername
            , chatType?.getConversationType(), true, loadDataType == EaseLoadDataType.THREAD)
    }

    override fun bindParentId(parentId: String?) {
        _parentId = parentId
    }

    override fun sendChannelAck() {
        safeInConvScope {
            viewModelScope.launch {
                flow {
                    emit(chatRepository.ackConversationRead(it.conversationId()))
                }
                .catchChatException { e ->
                    view?.ackConversationReadFail(e.errorCode, e.description)
                }
                .collect {
                    view?.ackConversationReadSuccess()
                }
            }

        }
    }

    override fun sendGroupMessageReadAck(messageId: String?, ext: String?) {
        safeInConvScope {
            viewModelScope.launch {
                flow {
                    emit(chatRepository.ackGroupMessageRead(it.conversationId(), messageId, ext))
                }
                .catchChatException { e ->
                    view?.ackGroupMessageReadFail(e.errorCode, e.description)
                }
                .collect {
                    view?.ackGroupMessageReadSuccess()
                }
            }
        }
    }

    override fun sendMessageReadAck(messageId: String?) {
        safeInConvScope {
            viewModelScope.launch {
                flow {
                    emit(chatRepository.ackMessageRead(it.conversationId(), messageId))
                }
                .catchChatException { e ->
                    view?.ackMessageReadFail(e.errorCode, e.description)
                }
                .collect {
                    view?.ackMessageReadSuccess()
                }
            }
        }
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        safeInConvScope {
            if (it.isGroupChat) {
                if (EaseAtMessageHelper.get().containsAtUsername(content)) {
                    sendAtMessage(content)
                    return@safeInConvScope
                }
            }
            val message:ChatMessage? = ChatMessage.createTextSendMessage(content, it.conversationId())
            if (it.isGroupChat) {
                message?.setIsNeedGroupAck(isNeedGroupAck)
            }
            sendMessage(message)
        }
    }

    override fun sendAtMessage(content: String?) {
        safeInConvScope {
            if (!it.isGroupChat) {
                inMainScope {
                    view?.onErrorBeforeSending(ChatError.INVALID_PARAM, "Not group chat.")
                }
                return@safeInConvScope
            }
            val group: ChatGroup? = ChatClient.getInstance().groupManager().getGroup(it.conversationId())
            if (group == null) {
                inMainScope {
                    view?.onErrorBeforeSending(ChatError.INVALID_PARAM, "Group: ${it.conversationId()} is null.")
                }
                return@safeInConvScope
            }
            val message = ChatMessage.createTextSendMessage(content, it.conversationId())
            if (ChatClient.getInstance().currentUser == group.owner
                && EaseAtMessageHelper.get().containsAtAll(content)) {
                message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL)
            } else {
                message.setAttribute(
                    EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(
                        EaseAtMessageHelper.get().getAtMessageUsernames(content!!)
                    )
                )
            }
            sendMessage(message)
        }
    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        if (_conversation == null) {
            inMainScope {
                view?.onErrorBeforeSending(ChatError.INVALID_PARAM, "Conversation is null.")
            }
            return
        }
        _conversation?.let {
            val message: ChatMessage? = createExpressionMessage(it.conversationId(), name, identityCode)
            sendMessage(message)
        }
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        safeInConvScope {
            val message = ChatMessage.createVoiceSendMessage(filePath, length, it.conversationId())
            sendMessage(message)
        }
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {

        safeInConvScope {
            //Compatible with web and does not support heif image terminal
            //convert heif format to jpeg general image format
            val uri = EaseImageUtils.handleImageHeifToJpeg(
                EaseIM.getContext(),
                imageUri,
                it.messageAttachmentPath
            )
            val message =
                ChatMessage.createImageSendMessage(uri, sendOriginalImage, it.conversationId())
            sendMessage(message)
        }
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?
    ) {
        safeInConvScope {
            val message = ChatMessage.createLocationSendMessage(
                latitude,
                longitude,
                locationAddress,
                it.conversationId()
            )
            ChatLog.i(
                TAG,
                "current = " + ChatClient.getInstance().currentUser + " to = " + it.conversationId()
            )
            sendMessage(message)
        }
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        safeInConvScope {
            val thumbPath: String = EaseFileUtils.getThumbPath(EaseIM.getContext(), videoUri)
            val message = ChatMessage.createVideoSendMessage(
                videoUri,
                thumbPath,
                videoLength,
                it.conversationId()
            )
            sendMessage(message)
        }
    }

    override fun sendFileMessage(fileUri: Uri?) {
        safeInConvScope {
            val message = ChatMessage.createFileSendMessage(fileUri, it.conversationId())
            sendMessage(message)
        }
    }

    override fun sendCombineMessage(
        title: String?,
        summary: String?,
        compatibleText: String?,
        msgIds: List<String>?
    ) {
        safeInConvScope {
            val innerTitle = title ?: EaseIM.getContext()?.getString(R.string.ease_combine_default)
            val innerSummary = summary ?: EaseChatMessageMultiSelectHelper.getCombineMessageSummary(msgIds)
            val innerCompatible = compatibleText ?: EaseIM.getContext()?.getString(R.string.ease_combine_compatible_default)
            val message = ChatMessage.createCombinedSendMessage(
                innerTitle,
                innerSummary,
                innerCompatible,
                msgIds,
                it.conversationId()
            )
            sendMessage(message)
        }
    }

    override fun sendCombineMessage(
        to: String?,
        chatType: ChatType?,
        msgIds: List<String>?
    ) {
        val innerTitle = EaseIM.getContext()?.getString(R.string.ease_combine_default)
        val innerSummary = EaseChatMessageMultiSelectHelper.getCombineMessageSummary(msgIds)
        val innerCompatible = EaseIM.getContext()?.getString(R.string.ease_combine_compatible_default)
        val message = ChatMessage.createCombinedSendMessage(
            innerTitle,
            innerSummary,
            innerCompatible,
            msgIds,
            to
        )
        message.chatType = chatType
        sendMessage(message, false, callback = CallbackImpl(
            onSuccess = {
                inMainScope {
                    view?.onSendCombineMessageSuccess(message)
                }
            },
            onError = { code, error ->
                inMainScope {
                    view?.onSendCombineMessageFail(message, code, error)
                }
            }
        ))
    }

    override fun sendCombineMessage(message: ChatMessage?) {
        safeInConvScope {
            sendMessage(message, false)
        }
    }

    override fun sendCmdMessage(action: String?) {
        safeInConvScope {
            val beginMsg = ChatMessage.createSendMessage(ChatMessageType.CMD)
            val body = ChatCmdMessageBody(action)
            // Only deliver this cmd msg to online users
            body.deliverOnlineOnly(true)
            beginMsg.addBody(body)
            beginMsg.to = it.conversationId()
            ChatClient.getInstance().chatManager().sendMessage(beginMsg)
        }
    }

    override fun addMessageAttributes(message: ChatMessage?) {
        view?.addMsgAttrBeforeSend(message)
    }

    override fun sendMessage(message: ChatMessage?) {
        safeInConvScope {
            sendMessage(message, true)
        }
    }

    override fun sendMessage(message: ChatMessage?, isCheck: Boolean, callback: ChatCallback?) {
        safeInConvScope {
            if (message == null) {
                inMainScope {
                    view?.onErrorBeforeSending(ChatError.MESSAGE_INVALID, "Message is null.")
                    return@inMainScope
                }
            }
            message?.run {
                if (isCheck) {
                    if (it.isGroupChat) {
                        chatType = ChatType.GroupChat
                    } else if (it.isChatroom) {
                        chatType = ChatType.ChatRoom
                    }
                    setIsChatThreadMessage(it.isChatThread)
                }
                EaseIM.getCurrentUser()?.let { profile ->
                    addUserInfo(profile.name, profile.avatar)
                }
                addMessageAttributes(message)
                message.send(onSuccess = {
                    inMainScope {
                        callback?.onSuccess() ?:
                        view?.onSendMessageSuccess(message)
                    }
                }, onError = { code, error ->
                    inMainScope {
                        callback?.onError(code, error) ?:
                        view?.onSendMessageError(message, code, error)
                    }
                }, onProgress = {
                    inMainScope {
                        callback?.onProgress(it, "") ?:
                        view?.onSendMessageInProgress(message, it)
                    }
                })
                inMainScope {
                    view?.sendMessageFinish(message)
                }
            }
        }
    }

    override fun resendMessage(message: ChatMessage?) {
        safeInConvScope {
            message?.let {
                it.setStatus(ChatMessageStatus.CREATE)
                val currentTimeMillis = System.currentTimeMillis()
                it.setLocalTime(currentTimeMillis)
                it.msgTime = currentTimeMillis
                ChatClient.getInstance().chatManager().updateMessage(it)
                sendMessage(it)
            }
        }
    }

    override fun reportMessage(tag: String, reason: String?, msgId: String) {
        safeInConvScope {
            viewModelScope.launch {
                flow {
                    emit(chatRepository.reportMessage(tag, reason,msgId ))
                }
                .catchChatException { e ->
                    view?.onReportMessageFail(msgId,e.errorCode, e.description)
                }
                .collect {
                    view?.onReportMessageSuccess(msgId)
                }
            }
        }
    }

    override fun deleteMessage(message: ChatMessage?) {
        safeInConvScope { conv ->
            message?.let { msg ->
                if ((_loadDataType == EaseLoadDataType.ROAM || _loadDataType == EaseLoadDataType.THREAD)
                    && msg.status() == ChatMessageStatus.SUCCESS) {
                    viewModelScope.launch {
                        flow {
                            emit(conv.deleteMessage(mutableListOf(msg.msgId)))
                        }
                        .catchChatException { e ->
                            view?.deleteMessageFail(message, e.errorCode, e.description)
                        }
                        .collect {
                            conv.removeMessage(msg.msgId)
                            view?.deleteMessageSuccess(message)
                        }
                    }
                } else {
                    conv.removeMessage(msg?.msgId)
                    inMainScope {
                        view?.deleteMessageSuccess(message)
                    }
                }
            } ?: run {
                inMainScope {
                    view?.deleteMessageFail(message, ChatError.MESSAGE_INVALID, "Message is null.")
                }
            }

        }
    }

    override fun deleteMessages(messages: List<String>?) {
        safeInConvScope { conv ->
            messages?.let { list ->
                if (_loadDataType == EaseLoadDataType.ROAM || _loadDataType == EaseLoadDataType.THREAD) {
                    viewModelScope.launch {
                        flow {
                            emit(conv.deleteMessage(list))
                        }
                        .catchChatException { e ->
                            view?.deleteMessageListFail(e.errorCode, e.description)
                        }
                        .collect {
                            view?.deleteMessageListSuccess()
                        }
                    }
                } else {
                    list.forEach { item ->
                        conv.removeMessage(item)
                    }
                    inMainScope {
                        view?.deleteMessageListSuccess()
                    }
                }
            } ?: run {
                inMainScope {
                    view?.deleteMessageListFail(ChatError.INVALID_PARAM, "Message list is null.")
                }
            }
        }
    }

    override fun recallMessage(message: ChatMessage?) {
        safeInConvScope { conv->
            message?.let { msg ->
                viewModelScope.launch {
                    val msgNotification = msg.createUnsentMessage()

                    flow {
                        emit(chatRepository.recallMessage(msg))
                    }
                    .catchChatException { e ->
                        view?.recallMessageFail(e.errorCode, e.description)
                    }
                    .collect {
                        conv.insertMessage(msgNotification)
                        view?.recallMessageFinish(message, msgNotification)
                    }
                }

            }
        }
    }

    override fun modifyMessage(messageId: String?, messageBodyModified: ChatMessageBody?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.modifyMessage(messageId, messageBodyModified))
            }
            .catchChatException { e ->
                view?.onModifyMessageFailure(messageId, e.errorCode, e.description)
            }
            .collect {
                view?.onModifyMessageSuccess(it)
            }
        }
    }

    override fun forwardMessage(message: ChatMessage?, toId: String, chatType: ChatType) {
        safeInConvScope {
            message?.let { msg ->
                ChatMessage.createSendMessage(msg.type).apply {
                    val body = msg.body
                    if (body != null) {
                        setBody(body)
                    }
                    to = toId
                    this.chatType = chatType
                    sendMessage(this, false, callback = CallbackImpl(
                        onSuccess = {
                            inMainScope {
                                view?.onForwardMessageSuccess(msg)
                            }
                        },
                        onError = { code, error ->
                            inMainScope {
                                view?.onForwardMessageFail(msg, code, error)
                            }
                        }
                    ))
                }
            }
        }
    }

    override fun addReaction(message: ChatMessage?, reaction: String?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.addReaction(message, reaction))
            }
            .catchChatException { e->
                view?.addReactionMessageFail(message, e.errorCode, e.description)
            }
            .collect {
                view?.addReactionMessageSuccess(message)
            }
        }
    }

    override fun removeReaction(message: ChatMessage?, reaction: String?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.removeReaction(message, reaction))
            }
            .catchChatException { e->
                view?.addReactionMessageFail(message, e.errorCode, e.description)
            }
            .collect {
                view?.addReactionMessageSuccess(message)
            }
        }
    }

    override fun createReplyMessageExt(message: ChatMessage?) {
        message?.run {
            val quoteObject = JSONObject()
            try {
                if (body != null) {
                    quoteObject.put(EaseConstant.QUOTE_MSG_ID, msgId)
                    if (type === ChatMessageType.TXT && !TextUtils.isEmpty((body as ChatTextMessageBody).message)) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            (body as ChatTextMessageBody).message
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.txt.name)
                    } else if (type === ChatMessageType.IMAGE) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_picture)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.img.name)
                    } else if (type === ChatMessageType.VIDEO) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_video)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.video.name)
                    } else if (type === ChatMessageType.LOCATION) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_location)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.location.name)
                    } else if (type === ChatMessageType.VOICE) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_voice)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.audio.name)
                    } else if (type === ChatMessageType.FILE) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_file)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.file.name)
                    } else if (type === ChatMessageType.CUSTOM) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_custom)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.custom.name)
                    } else if (type === ChatMessageType.COMBINE) {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            EaseIM.getContext()?.getResources()?.getString(R.string.ease_combine)
                        )
                        quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, EaseReplyMap.combine.name)
                    } else {
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_PREVIEW,
                            "[" + type.name.lowercase(Locale.getDefault()) + "]"
                        )
                        quoteObject.put(
                            EaseConstant.QUOTE_MSG_TYPE,
                            type.name.lowercase(Locale.getDefault())
                        )
                    }
                    quoteObject.put(EaseConstant.QUOTE_MSG_SENDER, getFrom())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                view?.createReplyMessageExtFail(ChatError.GENERAL_ERROR, e.message)
                return
            }
            view?.createReplyMessageExtSuccess(quoteObject)
        }
    }

    override fun translationMessage(message: ChatMessage?,languages:MutableList<String>) {
        viewModelScope.launch {
            flow {
                message?.let {
                    emit(chatRepository.translationMessage(it, languages))
                }
            }
                .catchChatException { e ->
                    view?.onTranslationMessageFail(e.errorCode, e.description)
                }
                .collect {
                    it.setAttribute(EaseConstant.TRANSLATION_STATUS,true)
                    ChatClient.getInstance().chatManager().updateMessage(it).apply {
                        if (this) view?.onTranslationMessageSuccess(it)
                    }
                }
        }
    }

    override fun hideTranslationMessage(message: ChatMessage?) {
        message?.setAttribute(EaseConstant.TRANSLATION_STATUS,false)
        ChatClient.getInstance().chatManager().updateMessage(message).apply {
            if (this) view?.onHideTranslationMessage(message)
        }
    }

    override fun getInProgressMessages() {
        viewModelScope.launch {
            safeInConvScope {
                it.allMessages.filter { msg -> msg.status() == ChatMessageStatus.INPROGRESS }.forEach { msg ->
                    // Set message status callback again
                    msg.setMessageStatusCallback(CallbackImpl(onSuccess = {
                        inMainScope {
                            view?.onSendMessageSuccess(msg)
                        }
                    }, onError = { code, error ->
                        inMainScope {
                            view?.onSendMessageError(msg, code, error)
                        }
                    }, onProgress = { progress ->
                        inMainScope {
                            view?.onSendMessageInProgress(msg, progress)
                        }
                    }))
                }
            }
        }
    }

    override fun pinMessage(message: ChatMessage?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.pinMessage(message))
            }
                .catchChatException { e ->
                    view?.onPinMessageFail(e.errorCode, e.description)
                }
                .collect {
                    view?.onPinMessageSuccess(message)
                }
        }
    }

    override fun unPinMessage(message: ChatMessage?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.unPinMessage(message))
            }
                .catchChatException { e ->
                    view?.onUnPinMessageFail(e.errorCode, e.description)
                }
                .collect {
                    view?.onUnPinMessageSuccess(message)
                }
        }
    }

    override fun fetchPinMessageFromServer(conversationId: String?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.fetchPinMessageFromService(conversationId))
            }
                .catchChatException { e ->
                    view?.onFetchPinMessageFromServerFail(e.errorCode, e.description)
                }
                .collect {
                    view?.onFetchPinMessageFromServerSuccess(it)
                }
        }
    }

    private inline fun safeInConvScope(scope: (ChatConversation)->Unit) {
        if (_conversation == null) {
            inMainScope {
                view?.onErrorBeforeSending(ChatError.INVALID_PARAM, "Conversation is null.")
            }
            return
        }
        _conversation?.let {
            scope(it)
        }
    }

    private fun inMainScope(scope: ()->Unit) {
        viewModelScope.launch(context = Dispatchers.Main) {
            scope()
        }
    }

    companion object {
        private val TAG = EaseChatViewModel::class.java.simpleName
    }

}