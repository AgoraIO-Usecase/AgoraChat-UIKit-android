package io.agora.uikit.feature.chat.viewholders

import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatCustomMessageBody
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageDirection
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.widget.chatrow.EaseChatRowCustom
import io.agora.uikit.widget.chatrow.EaseChatRowFile
import io.agora.uikit.widget.chatrow.EaseChatRowImage
import io.agora.uikit.widget.chatrow.EaseChatRowText
import io.agora.uikit.widget.chatrow.EaseChatRowUnknown
import io.agora.uikit.widget.chatrow.EaseChatRowUnsent
import io.agora.uikit.widget.chatrow.EaseChatRowUserCard
import io.agora.uikit.widget.chatrow.EaseChatRowVideo
import io.agora.uikit.widget.chatrow.EaseChatRowVoice
import io.agora.uikit.widget.chatrow.EaseChatRowAlert
import io.agora.uikit.widget.chatrow.EaseChatRowThreadNotify
import io.agora.uikit.widget.chatrow.EaseChatRowCombine

object EaseChatViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: EaseMessageViewType
    ): EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage> {
        return when (viewType) {
            EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER -> EaseTextViewHolder(
                EaseChatRowText(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER -> EaseImageViewHolder(
                EaseChatRowImage(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER -> EaseVideoViewHolder(
                EaseChatRowVideo(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER -> EaseVoiceViewHolder(
                EaseChatRowVoice(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER -> EaseFileViewHolder(
                EaseChatRowFile(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER -> EaseCustomViewHolder(
                EaseChatRowCustom(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER -> EaseCombineViewHolder(
                EaseChatRowCombine(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER -> EaseUnsentViewHolder(
                EaseChatRowUnsent(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER -> EaseUserCardViewHolder(
                EaseChatRowUserCard(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_ALERT -> EaseAlertViewHolder(
                EaseChatRowAlert(parent.context)
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER -> EaseUnknownViewHolder(
                EaseChatRowUnknown(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY -> EaseThreadNotifyViewHolder(
                EaseChatRowThreadNotify(
                    parent.context
                )
            )

            else -> EaseUnknownViewHolder(EaseChatRowUnknown(parent.context, isSender = false))
        }
    }

    fun getViewType(message: ChatMessage?): Int {
        return message?.let { getChatType(it).value } ?: EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER.value
    }

    fun getChatType(message: ChatMessage): EaseMessageViewType {
        val type: EaseMessageViewType
        val messageType = message.type
        val direct = message.direct()
        type = if (messageType == ChatMessageType.TXT) {
            val isThreadNotify: Boolean =
                message.getBooleanAttribute(EaseConstant.THREAD_NOTIFICATION_TYPE, false)
            val isRecallMessage: Boolean =
                message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)
            val isContactNotify: Boolean =
                message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_CONTACT_NOTIFY, false)
            if (isThreadNotify) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY
            } else if (isRecallMessage || isContactNotify) {
                if (direct == ChatMessageDirection.SEND) {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                } else {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER
                }
            } else {
                if (direct == ChatMessageDirection.SEND) {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                } else {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER
                }
            }
        } else if (messageType == ChatMessageType.IMAGE) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER
            }
        } else if (messageType == ChatMessageType.VIDEO) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER
            }
        } else if (messageType == ChatMessageType.LOCATION) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_OTHER
            }
        } else if (messageType == ChatMessageType.VOICE) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER
            }
        } else if (messageType == ChatMessageType.FILE) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER
            }
        } else if (messageType == ChatMessageType.CMD) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_CMD_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_CMD_OTHER
            }
        } else if (messageType == ChatMessageType.CUSTOM) {
            val event = (message.body as? ChatCustomMessageBody)?.event() ?: ""
            if (event == EaseConstant.USER_CARD_EVENT) {
                if (direct == ChatMessageDirection.SEND) {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                } else {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER
                }
            } else if (event == EaseConstant.MESSAGE_CUSTOM_ALERT) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_ALERT
            } else {
                if (direct == ChatMessageDirection.SEND) {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME
                } else {
                    EaseMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER
                }
            }
        } else if (messageType == ChatMessageType.COMBINE) {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER
            }
        } else {
            if (direct == ChatMessageDirection.SEND) {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
            } else {
                EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER
            }
        }
        return type
    }
}