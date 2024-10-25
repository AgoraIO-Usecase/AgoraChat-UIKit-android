package io.agora.uikit.feature.chat.controllers

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.feature.chat.activities.EaseShowBigImageActivity
import io.agora.uikit.feature.chat.activities.EaseShowNormalFileActivity
import io.agora.uikit.feature.chat.activities.EaseShowVideoActivity
import io.agora.uikit.common.ChatImageMessageBody
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.ChatNormalFileMessageBody
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.utils.EaseCompat
import io.agora.uikit.common.utils.EaseFileUtils
import io.agora.uikit.feature.chat.widgets.EaseChatLayout
import io.agora.uikit.feature.chat.interfaces.IChatTopExtendMenu
import io.agora.uikit.feature.chat.widgets.EaseChatInputMenu
import io.agora.uikit.feature.chat.widgets.EaseChatMessageListLayout
import io.agora.uikit.feature.chat.reply.EaseChatExtendMessageReplyView
import io.agora.uikit.viewmodel.messages.IChatViewRequest
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