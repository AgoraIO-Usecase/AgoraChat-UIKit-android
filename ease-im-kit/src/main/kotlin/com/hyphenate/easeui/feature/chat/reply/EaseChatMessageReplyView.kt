package com.hyphenate.easeui.feature.chat.reply

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatDownloadStatus
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatFileMessageBody
import com.hyphenate.easeui.common.ChatImageMessageBody
import com.hyphenate.easeui.common.ChatLocationMessageBody
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatVideoMessageBody
import com.hyphenate.easeui.common.ChatVoiceMessageBody
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.getEmojiText
import com.hyphenate.easeui.common.extensions.getTextHeight
import com.hyphenate.easeui.common.extensions.getUserCardInfo
import com.hyphenate.easeui.common.extensions.isUserCardMessage
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.common.suspends.downloadThumbnailBySuspend
import com.hyphenate.easeui.common.utils.EaseFileUtils
import com.hyphenate.easeui.feature.chat.reply.interfaces.OnMessageReplyViewClickListener
import com.hyphenate.easeui.common.enums.EaseReplyMap
import com.hyphenate.easeui.common.extensions.isReplyMessage
import com.hyphenate.easeui.common.extensions.isUnsentMessage
import com.hyphenate.easeui.feature.chat.internal.setTargetSpan
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.provider.getSyncUser
import com.hyphenate.easeui.widget.EaseImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class EaseChatMessageReplyView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private var isHistory: Boolean = false,
    private var isSender: Boolean = false
) : LinearLayout(
    context, attrs, defStyleAttr
) {

    private var listener: OnMessageReplyViewClickListener? = null
    private var quoteMessage: ChatMessage? = null
    private val message: ChatMessage? = null
    private var quoteSender: String? = null
    private val quoteName: TextView by lazy { findViewById(R.id.quote_name) }
    private val quoteContent: TextView by lazy { findViewById(R.id.quote_content) }
    private val quoteIcon: ImageView by lazy { findViewById(R.id.quote_icon) }
    private val quoteImage: EaseImageView by lazy { findViewById(R.id.quote_image) }
    private val quoteVideoIcon: ImageView by lazy { findViewById(R.id.quote_video_icon) }

    init {
        initAttrs(context, attrs)
        initListener()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageReplyView).let { a ->
            isSender = isSender || a.getBoolean(R.styleable.EaseChatMessageReplyView_ease_chat_message_reply_is_sender, false)
            isHistory = isHistory || a.getBoolean(R.styleable.EaseChatMessageReplyView_ease_chat_message_reply_is_history, false)
            a.recycle()
        }
        if (!isHistory) {
            if (isSender) {
                inflate(context, R.layout.ease_row_sent_reply_layout, this)
            } else {
                inflate(context, R.layout.ease_row_received_reply_layout, this)
            }
        }
        setReplyViewParams(isSender)
    }

    private fun initListener() {
        setOnClickListener {
            if (listener != null && quoteMessage != null) {
                listener?.onReplyViewClick(quoteMessage)
            }
        }
        setOnLongClickListener {
            return@setOnLongClickListener listener?.onReplyViewLongClick(this, quoteMessage) == true
        }
    }

    fun updateMessageInfo(message: ChatMessage?): Boolean {
        if (message == null) {
            ChatLog.e(
                TAG,
                getContext().getString(R.string.ease_error_message_not_exist)
            )
            return false
        }
        return message.isReplyMessage {
            quoteName.text = ""
            quoteContent.text = ""
            quoteIcon.visibility = GONE
            quoteImage.visibility = GONE
            quoteVideoIcon.visibility = GONE
            parseJsonObject(it)
        }
    }

    private fun setReplyViewParams(isSender: Boolean) {
        if (isSender) {
            gravity = Gravity.END
        } else {
            gravity = Gravity.START
        }
    }

    private fun parseJsonObject(jsonObject: JSONObject) {
        try {
            val quoteMsgID = jsonObject.getString(EaseConstant.QUOTE_MSG_ID)
            val quoteSender = jsonObject.getString(EaseConstant.QUOTE_MSG_SENDER)
            val quoteType = jsonObject.getString(EaseConstant.QUOTE_MSG_TYPE)
            val quoteContent = jsonObject.getString(EaseConstant.QUOTE_MSG_PREVIEW)
            var quoteSenderNick: String? = ""
            if (!TextUtils.isEmpty(quoteSender)) {
                val user: EaseUser? = EaseIM.getCache().getMessageUserInfo(quoteSender)?.toUser()
                    ?: EaseIM.getUserProvider()?.getSyncUser(quoteSender)?.toUser()
                quoteSenderNick = if (user == null) {
                    quoteSender
                } else {
                    user.getNickname()
                }
            }
            this.quoteSender = quoteSenderNick
            quoteMessage = ChatClient.getInstance().chatManager().getMessage(quoteMsgID)
            quoteMessage?.let {
                if (it.isUnsentMessage()) {
                    quoteMessage = null
                }
            }
            setMessageReplyInfo(getQuoteMessageType(quoteType), quoteContent)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setMessageReplyInfo(
        quoteMessageType: ChatMessageType,
        quoteContent: String
    ) {
        this.quoteName.text = quoteSender
        this.quoteName.visibility = VISIBLE
        if (quoteMessage == null) {
            this.quoteContent.text = resources.getString(R.string.ease_quote_message_not_exist)
            this.quoteContent.visibility = VISIBLE
            this.quoteName.visibility = GONE
            return
        }
        this.quoteIcon.visibility = VISIBLE
        this.quoteContent.visibility = VISIBLE
        this.quoteVideoIcon.visibility = GONE
        when(quoteMessageType) {
            ChatMessageType.TXT -> {
                this.quoteIcon.visibility = GONE
                textTypeDisplay(quoteContent)
            }
            ChatMessageType.IMAGE -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_image)
                imageTypeDisplay(quoteContent)
            }
            ChatMessageType.VIDEO -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_video)
                videoTypeDisplay(quoteContent)
            }
            ChatMessageType.LOCATION -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_item_menu_location)
                locationTypeDisplay(quoteContent)
            }
            ChatMessageType.VOICE -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_voice)
                voiceTypeDisplay(quoteContent)
            }
            ChatMessageType.FILE -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_file)
                fileTypeDisplay(quoteContent)
            }
            ChatMessageType.COMBINE -> {
                this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_combine)
                combineTypeDisplay(quoteContent)
            }
            ChatMessageType.CUSTOM -> {
                this.quoteIcon.visibility = GONE
                specifyCustomDisplay(quoteContent)
            }
            else -> {
                disableDisplay()
            }
        }

    }

    private fun disableDisplay() {
        this.quoteIcon.visibility = GONE
        this.quoteContent.text = resources.getString(R.string.ease_message_reply_unknown_type)
        this.quoteContent.setTextColor(ContextCompat.getColor(context, R.color.ease_color_text_not_enable))
    }

    private fun specifyCustomDisplay(quoteContent: String) {
        if (this.quoteMessage?.isUserCardMessage() == true) {
            this.quoteIcon.setImageResource(R.drawable.ease_chat_quote_icon_user_card)
            this.quoteMessage?.getUserCardInfo()?.let {
                this.quoteContent.text = it.name
            }
            this.quoteIcon.visibility = VISIBLE
        } else {
            disableDisplay()
        }
    }

    private fun textTypeDisplay(quoteContent: String) {
        if (this.quoteMessage?.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false) == true) {
            // show big expression
            bigExpressionDisplay()
        } else {
            val content = (this.quoteMessage?.body as? ChatTextMessageBody)?.message ?: quoteContent
            this.quoteContent.text = content.getEmojiText(context, this.quoteContent.getTextHeight())
            this.quoteContent.ellipsize = TextUtils.TruncateAt.END
            this.quoteContent.maxLines = 2
            this.quoteContent.visibility = VISIBLE
        }
    }

    private fun imageTypeDisplay(quoteContent: String) {
        this.quoteContent.text = resources.getString(R.string.ease_message_reply_image_type)
        if (this.quoteMessage == null) {
            this.quoteImage.setImageResource(R.drawable.ease_chat_quote_default_image)
            this.quoteImage.visibility = VISIBLE
        } else {
            imageDisplay()
        }
    }

    private fun videoTypeDisplay(quoteContent: String) {
        this.quoteContent.text = resources.getString(R.string.ease_message_reply_video_type)
        if (this.quoteMessage == null) {
            this.quoteImage.setImageResource(R.drawable.ease_chat_quote_default_video)
            this.quoteImage.visibility = VISIBLE
        } else {
            videoDisplay()
        }
    }

    private fun locationTypeDisplay(quoteContent: String) {
        if (this.quoteMessage == null) {
            this.quoteContent.text = resources.getString(R.string.ease_message_reply_location_type)
        } else {
            (this.quoteMessage?.body as? ChatLocationMessageBody)?.let {
                SpannableStringBuilder(resources.getString(R.string.ease_message_reply_location_type))
                    .append(": ").let { builder->
                        builder.setTargetSpan(TextAppearanceSpan(context, R.style.ease_chat_row_message_type_text_style), 0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(it.address)
                        this.quoteContent.text = builder
                    }
            }
        }
    }

    private fun voiceTypeDisplay(quoteContent: String) {
        if (this.quoteMessage == null) {
            this.quoteContent.text = resources.getString(R.string.ease_message_reply_voice_type)
        } else {
            (this.quoteMessage?.body as? ChatVoiceMessageBody)?.let {
                SpannableStringBuilder(resources.getString(R.string.ease_message_reply_voice_type))
                    .append(": ").let { builder->
                        builder.setTargetSpan(TextAppearanceSpan(context, R.style.ease_chat_row_message_type_text_style), 0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(it.length.toString())
                        this.quoteContent.text = builder
                    }
            }
        }
    }

    private fun fileTypeDisplay(quoteContent: String) {
        if (this.quoteMessage == null) {
            this.quoteContent.text = resources.getString(R.string.ease_message_reply_file_type)
        } else {
            (this.quoteMessage?.body as? ChatFileMessageBody)?.let {
                SpannableStringBuilder(resources.getString(R.string.ease_message_reply_file_type))
                    .append(": ").let { builder->
                        builder.setTargetSpan(TextAppearanceSpan(context, R.style.ease_chat_row_message_type_text_style), 0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(it.fileName)
                        this.quoteContent.text = builder
                    }
            }
        }
    }

    private fun combineTypeDisplay(quoteContent: String) {
        this.quoteContent.text = resources.getString(R.string.ease_combine_default)
    }

    private fun bigExpressionDisplay() {
        this.quoteMessage?.run {
            val emojiconId =
                getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null)
            EaseIM.getEmojiconInfoProvider()?.getEmojiconInfo(emojiconId)?.let {
                if (it.bigIcon != -1) {
                    quoteImage.load(it.bigIcon) {
                        placeholder(R.drawable.ease_default_expression)
                        error(R.drawable.ease_default_expression)
                    }
                } else if (!it.bigIconPath.isNullOrEmpty()) {
                    quoteImage.load(it.bigIconPath) {
                        placeholder(R.drawable.ease_default_expression)
                        error(R.drawable.ease_default_expression)
                    }
                } else {
                    quoteImage.load(R.drawable.ease_default_expression)
                }
                quoteIcon.visibility = VISIBLE
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun imageDisplay() {
        this.quoteMessage?.run {
            val imgBody = body as ChatImageMessageBody
            if (EaseFileUtils.isFileExistByUri(context, imgBody.thumbnailLocalUri())
                && imgBody.thumbnailDownloadStatus() != ChatDownloadStatus.FAILED) {

                if (imgBody.thumbnailDownloadStatus() == ChatDownloadStatus.SUCCESSED) {
                    loadImage(imgBody.thumbnailLocalUri(), R.drawable.ease_chat_quote_default_image)
                } else {
                    context.mainScope().launch {
                        for (i in 0..3) {
                            delay(500)
                            if (imgBody.thumbnailDownloadStatus() == ChatDownloadStatus.SUCCESSED) {
                                loadImage(imgBody.thumbnailLocalUri(), R.drawable.ease_chat_quote_default_image)
                                break
                            }
                        }
                        if (imgBody.thumbnailDownloadStatus() != ChatDownloadStatus.SUCCESSED) {
                            quoteImage.load(R.drawable.ease_chat_quote_default_image)
                            quoteImage.visibility = VISIBLE
                        }
                    }
                }
            } else if (EaseFileUtils.isFileExistByUri(context, imgBody.localUri)) {
                loadImage(imgBody.localUri, R.drawable.ease_chat_quote_default_image)
            } else {
                context.mainScope().launch {
                    val result = async {
                        try {
                            ChatClient.getInstance().chatManager().downloadThumbnailBySuspend(this@run)
                        } catch (e: Exception) {
                            ChatLog.e(TAG, "download thumbnail failed: ${e.message}")
                        }
                    }
                    result.await()
                    withContext(Dispatchers.Main) {
                        if ((result.getCompleted() as Pair<*, *>).first == ChatError.EM_NO_ERROR) {
                            loadImage(imgBody.thumbnailLocalUri(), R.drawable.ease_chat_quote_default_image)
                        } else {
                            quoteImage.load(R.drawable.ease_chat_quote_default_image)
                            quoteImage.visibility = VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun videoDisplay() {
        (this.quoteMessage?.body as? ChatVideoMessageBody)?.let {
            val localThumbUri = it.localThumbUri
            if (EaseFileUtils.isFileExistByUri(context, localThumbUri)
                && it.thumbnailDownloadStatus() == ChatDownloadStatus.SUCCESSED) {
                loadImage(localThumbUri, R.drawable.ease_chat_quote_default_video, true)
            } else {
                loadImage(it.thumbnailUrl, R.drawable.ease_chat_quote_default_video, true)
            }
        }
    }

    private fun loadImage(source: Any, placeholder: Int, isVideo: Boolean = false) {
        this.quoteImage.load(source) {
            placeholder(placeholder)
            error(placeholder)
            listener(onSuccess = { _, _ ->
                if (isVideo) {
                    quoteVideoIcon.visibility = VISIBLE
                }
            })
        }
        this.quoteImage.visibility = VISIBLE
    }

    private fun getQuoteMessageType(quoteType: String): ChatMessageType {
        if (receiveMsgTypes.containsKey(quoteType)) {
            return ChatMessageType.valueOf(receiveMsgTypes[quoteType]!!)
        }
        val type: ChatMessageType
        try {
            type = ChatMessageType.valueOf(quoteType.uppercase(Locale.getDefault()))
            return type
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return ChatMessageType.TXT
    }

    fun setOnMessageReplyViewClickListener(listener: OnMessageReplyViewClickListener?) {
        this.listener = listener
    }

    companion object {
        private const val TAG = "EaseChatMessageReplyView"
        private val receiveMsgTypes: Map<String, String> = mapOf(
            EaseReplyMap.txt.name to ChatMessageType.TXT.name,
            EaseReplyMap.img.name to ChatMessageType.IMAGE.name,
            EaseReplyMap.video.name to ChatMessageType.VIDEO.name,
            EaseReplyMap.location.name to ChatMessageType.LOCATION.name,
            EaseReplyMap.audio.name to ChatMessageType.VOICE.name,
            EaseReplyMap.file.name to ChatMessageType.FILE.name,
            EaseReplyMap.cmd.name to ChatMessageType.CMD.name,
            EaseReplyMap.custom.name to ChatMessageType.CUSTOM.name,
            EaseReplyMap.combine.name to ChatMessageType.COMBINE.name
        )
    }

}