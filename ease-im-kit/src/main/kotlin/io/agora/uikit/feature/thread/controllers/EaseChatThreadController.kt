package io.agora.uikit.feature.thread.controllers

import android.content.Context
import android.net.Uri
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatConversation
import io.agora.uikit.common.ChatConversationType
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.getEmojiText
import io.agora.uikit.common.extensions.getMessageDigest
import io.agora.uikit.common.extensions.isGroupChat
import io.agora.uikit.common.helper.EaseAtMessageHelper
import io.agora.uikit.common.utils.EaseFileUtils
import io.agora.uikit.common.utils.createExpressionMessage
import io.agora.uikit.viewmodel.thread.IChatThreadRequest

class EaseChatThreadController(
    private val context: Context,
    private val viewModel: IChatThreadRequest?
) {
    private var parentId = ""
    private var topicMsgId = ""
    private var _conversation: ChatConversation? = null
    private var topicMsg: ChatMessage? = null

    fun setupWithToConversation(
        parentId: String,
        topicMsgId: String,
    ){
        this.parentId = parentId
        this.topicMsgId = topicMsgId

        viewModel?.setupWithToConversation(parentId,topicMsgId)

        _conversation = ChatClient.getInstance().chatManager().getConversation(parentId
            , ChatConversationType.GroupChat, true, true)

        topicMsg = ChatClient.getInstance().chatManager().getMessage(this.topicMsgId)

    }

    fun sendTextMessage(content: String?, isNeedGroupAck: Boolean = false){
        viewModel?.checkoutConvScope()
        _conversation?.let {
            if (it.isGroupChat) {
                if (EaseAtMessageHelper.get().containsAtUsername(content)) {
                    sendAtMessage(content)
                    return@let
                }
            }
            val message = ChatMessage.createTextSendMessage(content, it.conversationId())
            if (it.isGroupChat) {
                message.setIsNeedGroupAck(isNeedGroupAck)
            }
            setMessage(message)
        }
    }

    fun sendAtMessage(content: String?) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val group: ChatGroup? = ChatClient.getInstance().groupManager().getGroup(it.conversationId())
            viewModel?.checkoutGroupScope(group)
            val message = ChatMessage.createTextSendMessage(content, it.conversationId())
            if (ChatClient.getInstance().currentUser == group?.owner
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
            setMessage(message)
        }
    }

    fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val message: ChatMessage? = createExpressionMessage(it.conversationId(), name, identityCode)
            message?.let { msg -> setMessage(msg) }
        }
    }

    fun sendVoiceMessage(filePath: Uri?, length: Int) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val message = ChatMessage.createVoiceSendMessage(filePath, length, it.conversationId())
            setMessage(message)
        }
    }

    fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val message = ChatMessage.createImageSendMessage(imageUri, sendOriginalImage, it.conversationId())
            setMessage(message)
        }
    }

    fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?
    ) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val message = ChatMessage.createLocationSendMessage(
                latitude,
                longitude,
                locationAddress,
                it.conversationId()
            )
            setMessage(message)
        }
    }

    fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val thumbPath: String = EaseFileUtils.getThumbPath(EaseIM.getContext(), videoUri)
            val message = ChatMessage.createVideoSendMessage(
                videoUri,
                thumbPath,
                videoLength,
                it.conversationId()
            )
            setMessage(message)
        }
    }

    fun sendFileMessage(fileUri: Uri?) {
        viewModel?.checkoutConvScope()
        _conversation?.let {
            val message = ChatMessage.createFileSendMessage(fileUri, it.conversationId())
            setMessage(message)
        }
    }

    fun setMessage(message:ChatMessage){
        message.chatType = ChatType.GroupChat
        val threadName = topicMsg?.getMessageDigest(context)?.getEmojiText(context)
        threadName?.let {
            viewModel?.createChatThread(it.toString(), message)
        }
    }

    companion object{
        private val TAG = EaseChatThreadController::class.java.simpleName
    }

}