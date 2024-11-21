package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.ChatUIKitShowBigImageActivity
import com.hyphenate.easeui.feature.chat.activities.ChatUIKitShowNormalFileActivity
import com.hyphenate.easeui.feature.chat.activities.ChatUIKitShowVideoActivity
import com.hyphenate.easeui.common.ChatImageMessageBody
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatNormalFileMessageBody
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.utils.ChatUIKitCompat
import com.hyphenate.easeui.common.utils.ChatUIKitFileUtils
import com.hyphenate.easeui.feature.chat.widgets.ChatUIKitLayout
import com.hyphenate.easeui.feature.chat.interfaces.IChatTopExtendMenu
import com.hyphenate.easeui.feature.chat.widgets.ChatUIKitInputMenu
import com.hyphenate.easeui.feature.chat.widgets.ChatUIKitMessageListLayout
import com.hyphenate.easeui.feature.chat.reply.ChatUIKitExtendMessageReplyView
import com.hyphenate.easeui.viewmodel.messages.IChatViewRequest
import org.json.JSONObject

class ChatUIKitMessageReplyController(
    private val context: Context,
    private val chatLayout: ChatUIKitLayout,
    private val viewModel: IChatViewRequest?,
) {
    /**
     * The inner label is used to mark whether a reference operation is in progress.
     */
    private var isReplying = false
    private var quoteObject: JSONObject? = null
    private val inputMenu: ChatUIKitInputMenu by lazy { chatLayout.findViewById(R.id.layout_menu) }
    private val chatMessageList: ChatUIKitMessageListLayout by lazy { chatLayout.findViewById(R.id.layout_chat_message) }

    /**
     * Show extend message reply view.
     */
    fun showExtendMessageReplyView(message: ChatMessage?) {
        showChatExtendQuoteView()
        if (inputMenu.chatTopExtendMenu is ChatUIKitExtendMessageReplyView) {
            (inputMenu.chatTopExtendMenu as? ChatUIKitExtendMessageReplyView)?.startQuote(message)
            inputMenu.chatPrimaryMenu?.showTextStatus()
            viewModel?.createReplyMessageExt(message)
        }
    }

    /**
     * Update reply message ext.
     */
    fun updateReplyMessageExt(isReplying: Boolean, messageExtObject: JSONObject?) {
        this.isReplying = isReplying
        this.quoteObject = messageExtObject
    }

    /**
     * Clear reply message ext after sending message finish.
     */
    fun clearReplyMessageExt(message: ChatMessage?) {
        if (isReplying && message?.type == ChatMessageType.TXT) {
            isReplying = false
            if (inputMenu.chatTopExtendMenu is ChatUIKitExtendMessageReplyView) {
                inputMenu.chatTopExtendMenu?.showTopExtendMenu(false)
            }
        }
    }

    /**
     * Add reply message ext to message.
     */
    fun addReplyExtToMessage(message: ChatMessage?) {
        if (isReplying && message?.type == ChatMessageType.TXT) {
            message?.setAttribute(ChatUIKitConstant.QUOTE_MSG_QUOTE, quoteObject)
        }
    }

    fun dealWithReplyViewClick(message: ChatMessage?) {
        if (message == null) {
            ChatLog.e("ChatMessageReply", "onReplyViewClick： message is null")
            return
        }
        if (showOriginalMessage(message)) {
            return
        }
        chatMessageList.moveToTarget(message)
    }

    private fun showOriginalMessage(message: ChatMessage): Boolean {
        when(message.type) {
            ChatMessageType.IMAGE -> {
                (message.body as? ChatImageMessageBody)?.let { body ->
                    val imgUri = body.localUri
                    ChatUIKitFileUtils.takePersistableUriPermission(context, imgUri)
                    if (ChatUIKitFileUtils.isFileExistByUri(context, imgUri)) {
                        ChatUIKitShowBigImageActivity.actionStart(context, imgUri)
                    } else {
                        ChatUIKitShowBigImageActivity.actionStart(context, message.msgId, body.remoteUrl)
                    }
                }
                return true
            }
            ChatMessageType.VIDEO -> {
                ChatUIKitShowVideoActivity.actionStart(context, message)
                return true
            }
            ChatMessageType.FILE -> {
                val fileMessageBody = message.body as? ChatNormalFileMessageBody
                val filePath: Uri? = fileMessageBody?.localUri
                ChatUIKitFileUtils.takePersistableUriPermission(context, filePath)
                if (ChatUIKitFileUtils.isFileExistByUri(context, filePath)) {
                    ChatUIKitCompat.openFile(context, filePath!!)
                } else {
                    ChatUIKitShowNormalFileActivity.actionStart(context, message)
                }
                return true
            }
            ChatMessageType.COMBINE -> {
                return false
            }
            else -> {
                return false
            }
        }

    }

    private fun showChatExtendQuoteView() {
        if (ChatUIKitClient.getConfig()?.chatConfig?.enableReplyMessage == false) {
            return
        }
        val chatTopExtendMenu: IChatTopExtendMenu? = inputMenu.chatTopExtendMenu
        if (chatTopExtendMenu is ChatUIKitExtendMessageReplyView) {
            inputMenu.chatPrimaryMenu?.setVisible(View.VISIBLE)
            return
        }
        val quoteView = ChatUIKitExtendMessageReplyView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        quoteView.layoutParams = params
        quoteView.showTopExtendMenu(false)
        inputMenu.setCustomTopExtendMenu(quoteView)
        inputMenu.showTopExtendMenu(true)
        inputMenu.chatPrimaryMenu?.setVisible(View.VISIBLE)
    }
}