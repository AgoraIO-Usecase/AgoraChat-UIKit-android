package io.agora.uikit.viewmodel.reply

import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.viewmodel.EaseBaseViewModel
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatFileMessageBody
import io.agora.uikit.common.ChatImageMessageBody
import io.agora.uikit.common.ChatLocationMessageBody
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.ChatTextMessageBody
import io.agora.uikit.common.ChatVideoMessageBody
import io.agora.uikit.common.ChatVoiceMessageBody
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.getEmojiText
import io.agora.uikit.common.extensions.getUserCardInfo
import io.agora.uikit.common.extensions.getUserInfo
import io.agora.uikit.common.extensions.isUserCardMessage
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.common.utils.EaseFileUtils
import io.agora.uikit.feature.chat.internal.setTargetSpan
import io.agora.uikit.feature.chat.reply.interfaces.IChatMessageReplyResultView
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.getNickname

open class EaseChatMessageReplyViewModel: EaseBaseViewModel<IChatMessageReplyResultView>(), IChatMessageReplyRequest {

    override fun showQuoteMessageInfo(message: ChatMessage?) {
        if (message == null || message.body == null) {
            view?.onShowError(ChatError.GENERAL_ERROR, "Message or body cannot be null.")
            return
        }
        val user: EaseUser? = message.getUserInfo()?.toUser()
        var from:String? = null
        from = if (user == null) {
            message.from
        } else {
            user.getRemarkOrName()
        }
        view?.showQuoteMessageNickname(from)
        val builder = SpannableStringBuilder()
        var localPath: String? = null
        var remoteUrl: String? = null
        when (message.type) {
            ChatMessageType.TXT -> if (message.getBooleanAttribute(
                    EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION,
                    false
                )
            ) {
                builder.append(EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_emoji_type))
            } else {
                val textBody = message.body as ChatTextMessageBody
                builder.append(
                    "${textBody.message.getEmojiText(EaseIM.getContext()!!)}"
                    ).toString()
            }

            ChatMessageType.VOICE -> {
                val voiceBody = message.body as ChatVoiceMessageBody
                builder.append(EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_voice_type))
                    .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                        0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(" ")
                    .append((if (voiceBody.length > 0) voiceBody.length else 0).toString() + "\"")
                view?.showQuoteMessageAttachment(
                    ChatMessageType.VOICE,
                    null,
                    null,
                    R.drawable.ease_chatfrom_voice_playing
                )
            }

            ChatMessageType.VIDEO -> {
                builder.append(EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_video_type))
                    .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                        0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                val videoBody = message.body as ChatVideoMessageBody
                videoBody?.let {
                    if (!TextUtils.isEmpty(it.localThumb) && EaseFileUtils.isFileExistByUri(
                            EaseIM.getContext(),
                            it.localThumbUri
                        )
                    ) {
                        localPath = it.localThumb
                    }
                    remoteUrl = it.thumbnailUrl
                    view?.showQuoteMessageAttachment(
                        ChatMessageType.VIDEO,
                        localPath,
                        remoteUrl,
                        R.drawable.ease_chat_quote_icon_video
                    )
                }

            }

            ChatMessageType.FILE -> {
                val fileBody = message.body as ChatFileMessageBody
                builder.append(EaseIM.getContext()?.getResources()?.getString(R.string.ease_message_reply_file_type))
                    .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                        0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(" ")
                    .append(fileBody.fileName)
                view?.showQuoteMessageAttachment(
                    ChatMessageType.FILE,
                    null,
                    null,
                    R.drawable.ease_chat_quote_message_attachment
                )
            }

            ChatMessageType.IMAGE -> {
                builder.append(EaseIM.getContext()?.getResources()?.getString(R.string.ease_message_reply_image_type))
                    .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                        0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                val imageBody = message.body as ChatImageMessageBody
                imageBody?.let {
                    if (!imageBody.thumbnailUrl.isNullOrEmpty() && EaseFileUtils.isFileExistByUri(
                            EaseIM.getContext(), imageBody.thumbnailLocalUri())) {
                        localPath = imageBody.thumbnailLocalPath()
                    } else if (!imageBody.localUrl.isNullOrEmpty() && EaseFileUtils.isFileExistByUri(
                        EaseIM.getContext(), imageBody.localUri)) {
                        localPath = imageBody.localUrl
                    }
                    remoteUrl = imageBody.remoteUrl
                    view?.showQuoteMessageAttachment(
                        ChatMessageType.IMAGE,
                        localPath,
                        remoteUrl,
                        R.drawable.ease_chat_quote_icon_image
                    )
                }
            }

            ChatMessageType.LOCATION -> {
                val locationBody = message.body as ChatLocationMessageBody
                builder.append(EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_location_type))
                    .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                        0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (locationBody != null && !TextUtils.isEmpty(locationBody.address)) {
                    builder.append(": ").append(locationBody.address)
                }
            }

            ChatMessageType.CUSTOM -> {
                if (message.isUserCardMessage()) {
                    view?.showQuoteMessageAttachment(
                        ChatMessageType.CUSTOM,
                        null,
                        null,
                        R.drawable.ease_chat_quote_icon_user_card
                    )
                    builder.append(message.getUserCardInfo()?.name)
                        .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                            0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    builder.append(EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_custom_type))
                        .setTargetSpan(TextAppearanceSpan(EaseIM.getContext(), R.style.ease_chat_message_reply_type_style),
                            0, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            ChatMessageType.COMBINE -> {
                view?.showQuoteMessageAttachment(
                    ChatMessageType.COMBINE,
                    null,
                    null,
                    R.drawable.ease_chat_quote_icon_combine
                )
                builder.append(
                    EaseIM.getContext()?.resources?.getString(R.string.ease_message_reply_combine_type)
                )
            }

            else -> {}
        }
        view?.showQuoteMessageContent(builder)
    }

}