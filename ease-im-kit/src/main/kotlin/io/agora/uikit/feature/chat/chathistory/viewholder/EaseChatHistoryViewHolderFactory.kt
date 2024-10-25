package io.agora.uikit.feature.chat.chathistory.viewholder

import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryCombine
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryFile
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryImage
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryText
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryUserCard
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryVideo
import io.agora.uikit.feature.chat.chathistory.widget.EaseChatRowHistoryVoice
import io.agora.uikit.feature.chat.viewholders.EaseChatViewHolderFactory
import io.agora.uikit.feature.chat.viewholders.EaseMessageViewType
import io.agora.uikit.feature.chat.viewholders.EaseThreadUnKnownViewHolder
import io.agora.uikit.feature.chat.viewholders.EaseUnknownViewHolder
import io.agora.uikit.widget.chatrow.EaseChatRowThreadUnknown
import io.agora.uikit.widget.chatrow.EaseChatRowUnknown

object EaseChatHistoryViewHolderFactory {

    fun createViewHolder(
        parent: ViewGroup,
        viewType: EaseMessageViewType
    ): EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage> {
        return when (viewType) {
            EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_OTHER -> EaseHistoryTextViewHolder(
                EaseChatRowHistoryText(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_TXT_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_OTHER -> EaseHistoryImageViewHolder(
                EaseChatRowHistoryImage(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_IMAGE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_OTHER -> EaseHistoryVideoViewHolder(
                EaseChatRowHistoryVideo(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VIDEO_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_OTHER -> EaseHistoryVoiceViewHolder(
                EaseChatRowHistoryVoice(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_VOICE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_OTHER -> EaseHistoryFileViewHolder(
                EaseChatRowHistoryFile(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_FILE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_OTHER -> EaseHistoryCombineViewHolder(
                EaseChatRowHistoryCombine(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_COMBINE_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_OTHER -> EaseHistoryUserCardViewHolder(
                EaseChatRowHistoryUserCard(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_USER_CARD_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_OTHER -> EaseUnknownViewHolder(
                EaseChatRowUnknown(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_UNKNOWN_ME
                )
            )

            EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME, EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_OTHER -> EaseThreadUnKnownViewHolder(
                EaseChatRowThreadUnknown(
                    parent.context,
                    isSender = viewType == EaseMessageViewType.VIEW_TYPE_MESSAGE_UNSENT_ME
                )
            )

            else -> EaseUnknownViewHolder(EaseChatRowUnknown(parent.context, isSender = false))
        }
    }

    fun getViewType(message: ChatMessage?): Int {
        return EaseChatViewHolderFactory.getViewType(message)
    }
}