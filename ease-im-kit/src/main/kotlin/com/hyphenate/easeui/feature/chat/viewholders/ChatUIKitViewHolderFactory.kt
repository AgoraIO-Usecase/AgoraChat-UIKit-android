package com.hyphenate.easeui.feature.chat.viewholders

import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageDirection
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowCustom
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowFile
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowImage
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowText
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowUnknown
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowUnsent
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowUserCard
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowVideo
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowVoice
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowAlert
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowThreadNotify
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowCombine

object ChatUIKitViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: ChatUIKitMessageViewType
    ): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatMessage> {
        return when (viewType) {
            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER -> ChatUIKitTextViewHolder(
                ChatUIKitRowText(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER -> ChatUIKitImageViewHolder(
                ChatUIKitRowImage(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER -> ChatUIKitVideoViewHolder(
                ChatUIKitRowVideo(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER -> ChatUIKitVoiceViewHolder(
                ChatUIKitRowVoice(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER -> ChatUIKitFileViewHolder(
                ChatUIKitRowFile(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER -> ChatUIKitCustomViewHolder(
                ChatUIKitRowCustom(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER -> ChatUIKitCombineViewHolder(
                ChatUIKitRowCombine(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER -> ChatUIKitUnsentViewHolder(
                ChatUIKitRowUnsent(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER -> ChatUIKitUserCardViewHolder(
                ChatUIKitRowUserCard(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_ALERT -> ChatUIKitAlertViewHolder(
                ChatUIKitRowAlert(parent.context)
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER -> ChatUIKitUnknownViewHolder(
                ChatUIKitRowUnknown(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY -> ChatUIKitThreadNotifyViewHolder(
                ChatUIKitRowThreadNotify(
                    parent.context
                )
            )

            else -> ChatUIKitUnknownViewHolder(ChatUIKitRowUnknown(parent.context, isSender = false))
        }
    }

    fun getViewType(message: ChatMessage?): Int {
        return message?.let { getChatType(it).value } ?: ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER.value
    }

    fun getChatType(message: ChatMessage): ChatUIKitMessageViewType {
        val type: ChatUIKitMessageViewType
        val messageType = message.type
        val direct = message.direct()
        type = if (messageType == ChatMessageType.TXT) {
            val isThreadNotify: Boolean =
                message.getBooleanAttribute(ChatUIKitConstant.THREAD_NOTIFICATION_TYPE, false)
            val isRecallMessage: Boolean =
                message.getBooleanAttribute(ChatUIKitConstant.MESSAGE_TYPE_RECALL, false)
            val isContactNotify: Boolean =
                message.getBooleanAttribute(ChatUIKitConstant.MESSAGE_TYPE_CONTACT_NOTIFY, false)
            if (isThreadNotify) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CHAT_THREAD_NOTIFY
            } else if (isRecallMessage || isContactNotify) {
                if (direct == ChatMessageDirection.SEND) {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                } else {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER
                }
            } else {
                if (direct == ChatMessageDirection.SEND) {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                } else {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER
                }
            }
        } else if (messageType == ChatMessageType.IMAGE) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER
            }
        } else if (messageType == ChatMessageType.VIDEO) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER
            }
        } else if (messageType == ChatMessageType.LOCATION) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_LOCATION_OTHER
            }
        } else if (messageType == ChatMessageType.VOICE) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER
            }
        } else if (messageType == ChatMessageType.FILE) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER
            }
        } else if (messageType == ChatMessageType.CMD) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CMD_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CMD_OTHER
            }
        } else if (messageType == ChatMessageType.CUSTOM) {
            val event = (message.body as? ChatCustomMessageBody)?.event() ?: ""
            if (event == ChatUIKitConstant.USER_CARD_EVENT) {
                if (direct == ChatMessageDirection.SEND) {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                } else {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER
                }
            } else if (event == ChatUIKitConstant.MESSAGE_CUSTOM_ALERT) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_ALERT
            } else {
                if (direct == ChatMessageDirection.SEND) {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_ME
                } else {
                    ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_CUSTOM_OTHER
                }
            }
        } else if (messageType == ChatMessageType.COMBINE) {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER
            }
        } else {
            if (direct == ChatMessageDirection.SEND) {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
            } else {
                ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER
            }
        }
        return type
    }
}