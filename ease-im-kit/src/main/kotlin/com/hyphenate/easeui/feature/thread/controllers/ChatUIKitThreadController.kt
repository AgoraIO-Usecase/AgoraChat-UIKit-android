package com.hyphenate.easeui.feature.thread.controllers

import android.content.Context
import android.net.Uri
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.extensions.getEmojiText
import com.hyphenate.easeui.common.extensions.getMessageDigest
import com.hyphenate.easeui.common.extensions.isGroupChat
import com.hyphenate.easeui.common.helper.ChatUIKitAtMessageHelper
import com.hyphenate.easeui.common.utils.ChatUIKitFileUtils
import com.hyphenate.easeui.common.utils.createExpressionMessage
import com.hyphenate.easeui.viewmodel.thread.IChatThreadRequest

class ChatUIKitThreadController(
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
                if (ChatUIKitAtMessageHelper.get().containsAtUsername(content)) {
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
                && ChatUIKitAtMessageHelper.get().containsAtAll(content)) {
                message.setAttribute(ChatUIKitConstant.MESSAGE_ATTR_AT_MSG, ChatUIKitConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL)
            } else {
                message.setAttribute(
                    ChatUIKitConstant.MESSAGE_ATTR_AT_MSG,
                    ChatUIKitAtMessageHelper.get().atListToJsonArray(
                        ChatUIKitAtMessageHelper.get().getAtMessageUsernames(content!!)
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
            val thumbPath: String = ChatUIKitFileUtils.getThumbPath(ChatUIKitClient.getContext(), videoUri)
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
        private val TAG = ChatUIKitThreadController::class.java.simpleName
    }

}