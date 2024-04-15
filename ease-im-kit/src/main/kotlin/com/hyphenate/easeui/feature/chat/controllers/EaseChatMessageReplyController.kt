package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseShowBigImageActivity
import com.hyphenate.easeui.feature.chat.activities.EaseShowNormalFileActivity
import com.hyphenate.easeui.feature.chat.activities.EaseShowVideoActivity
import com.hyphenate.easeui.common.ChatImageMessageBody
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatNormalFileMessageBody
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.utils.EaseCompat
import com.hyphenate.easeui.common.utils.EaseFileUtils
import com.hyphenate.easeui.feature.chat.widgets.EaseChatLayout
import com.hyphenate.easeui.feature.chat.interfaces.IChatTopExtendMenu
import com.hyphenate.easeui.feature.chat.widgets.EaseChatInputMenu
import com.hyphenate.easeui.feature.chat.widgets.EaseChatMessageListLayout
import com.hyphenate.easeui.feature.chat.reply.EaseChatExtendMessageReplyView
import com.hyphenate.easeui.viewmodel.messages.IChatViewRequest
import org.json.JSONObject

class EaseChatMessageReplyController(
    private val context: Context,
    private val chatLayout: EaseChatLayout,
    private val viewModel: IChatViewRequest?,
) {
    /**
     * The inner label is used to mark whether a reference operation is in progress.
     */
    private var isReplying = false
    private var quoteObject: JSONObject? = null
    private val inputMenu: EaseChatInputMenu by lazy { chatLayout.findViewById(R.id.layout_menu) }
    private val chatMessageList: EaseChatMessageListLayout by lazy { chatLayout.findViewById(R.id.layout_chat_message) }

    /**
     * Show extend message reply view.
     */
    fun showExtendMessageReplyView(message: ChatMessage?) {
        showChatExtendQuoteView()
        if (inputMenu.chatTopExtendMenu is EaseChatExtendMessageReplyView) {
            (inputMenu.chatTopExtendMenu as? EaseChatExtendMessageReplyView)?.startQuote(message)
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
            if (inputMenu.chatTopExtendMenu is EaseChatExtendMessageReplyView) {
                inputMenu.chatTopExtendMenu?.showTopExtendMenu(false)
            }
        }
    }

    /**
     * Add reply message ext to message.
     */
    fun addReplyExtToMessage(message: ChatMessage?) {
        if (isReplying && message?.type == ChatMessageType.TXT) {
            message?.setAttribute(EaseConstant.QUOTE_MSG_QUOTE, quoteObject)
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
                    EaseFileUtils.takePersistableUriPermission(context, imgUri)
                    if (EaseFileUtils.isFileExistByUri(context, imgUri)) {
                        EaseShowBigImageActivity.actionStart(context, imgUri)
                    } else {
                        EaseShowBigImageActivity.actionStart(context, message.msgId, body.remoteUrl)
                    }
                }
                return true
            }
            ChatMessageType.VIDEO -> {
                EaseShowVideoActivity.actionStart(context, message)
                return true
            }
            ChatMessageType.FILE -> {
                val fileMessageBody = message.body as? ChatNormalFileMessageBody
                val filePath: Uri? = fileMessageBody?.localUri
                EaseFileUtils.takePersistableUriPermission(context, filePath)
                if (EaseFileUtils.isFileExistByUri(context, filePath)) {
                    EaseCompat.openFile(context, filePath!!)
                } else {
                    EaseShowNormalFileActivity.actionStart(context, message)
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
        if (EaseIM.getConfig()?.chatConfig?.enableReplyMessage == false) {
            return
        }
        val chatTopExtendMenu: IChatTopExtendMenu? = inputMenu.chatTopExtendMenu
        if (chatTopExtendMenu is EaseChatExtendMessageReplyView) {
            inputMenu.chatPrimaryMenu?.setVisible(View.VISIBLE)
            return
        }
        val quoteView = EaseChatExtendMessageReplyView(context)
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