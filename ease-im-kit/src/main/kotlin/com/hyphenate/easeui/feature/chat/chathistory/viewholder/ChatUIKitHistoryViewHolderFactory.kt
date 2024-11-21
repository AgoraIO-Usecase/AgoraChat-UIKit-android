package com.hyphenate.easeui.feature.chat.chathistory.viewholder

import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryCombine
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryFile
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryImage
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryText
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryUserCard
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryVideo
import com.hyphenate.easeui.feature.chat.chathistory.widget.ChatUIKitRowHistoryVoice
import com.hyphenate.easeui.feature.chat.viewholders.ChatUIKitViewHolderFactory
import com.hyphenate.easeui.feature.chat.viewholders.ChatUIKitMessageViewType
import com.hyphenate.easeui.feature.chat.viewholders.ChatUIKitThreadUnKnownViewHolder
import com.hyphenate.easeui.feature.chat.viewholders.ChatUIKitUnknownViewHolder
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowThreadUnknown
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowUnknown

object ChatUIKitHistoryViewHolderFactory {

    fun createViewHolder(
        parent: ViewGroup,
        viewType: ChatUIKitMessageViewType
    ): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatMessage> {
        return when (viewType) {
            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER -> ChatUIKitHistoryTextViewHolder(
                ChatUIKitRowHistoryText(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER -> ChatUIKitHistoryImageViewHolder(
                ChatUIKitRowHistoryImage(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER -> ChatUIKitHistoryVideoViewHolder(
                ChatUIKitRowHistoryVideo(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER -> ChatUIKitHistoryVoiceViewHolder(
                ChatUIKitRowHistoryVoice(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER -> ChatUIKitHistoryFileViewHolder(
                ChatUIKitRowHistoryFile(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER -> ChatUIKitHistoryCombineViewHolder(
                ChatUIKitRowHistoryCombine(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER -> ChatUIKitHistoryUserCardViewHolder(
                ChatUIKitRowHistoryUserCard(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER -> ChatUIKitUnknownViewHolder(
                ChatUIKitRowUnknown(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
                )
            )

            ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME, ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER -> ChatUIKitThreadUnKnownViewHolder(
                ChatUIKitRowThreadUnknown(
                    parent.context,
                    isSender = viewType == ChatUIKitMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                )
            )

            else -> ChatUIKitUnknownViewHolder(ChatUIKitRowUnknown(parent.context, isSender = false))
        }
    }

    fun getViewType(message: ChatMessage?): Int {
        return ChatUIKitViewHolderFactory.getViewType(message)
    }
}