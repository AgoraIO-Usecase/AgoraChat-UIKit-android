package com.hyphenate.easeui.feature.chat.reaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.UikitItemMessageMenuReactionBinding
import com.hyphenate.easeui.databinding.UikitItemMessageReactionBinding
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitReactionType
import com.hyphenate.easeui.feature.chat.reaction.viewholder.ChatUIKitReactionAddViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.ChatUIKitReactionDefaultViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.ChatUIKitReactionMoreViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.ChatUIKitReactionNormalViewHolder
import com.hyphenate.easeui.model.ChatUIKitReaction

object ChatUIKitMessageReactionViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitReaction> {
        return when (viewType) {
            ChatUIKitReactionType.DEFAULT.viewType -> ChatUIKitReactionDefaultViewHolder(
                UikitItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            ChatUIKitReactionType.ADD.viewType -> ChatUIKitReactionAddViewHolder(
                UikitItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            ChatUIKitReactionType.MORE.viewType -> ChatUIKitReactionMoreViewHolder()

            else -> ChatUIKitReactionNormalViewHolder(
                UikitItemMessageReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
        }
    }

    fun getViewType(reaction: ChatUIKitReaction?): Int {
        return reaction?.let { getReactionType(it) } ?: (ChatUIKitReactionType.NORMAL.viewType)
    }

    private fun getReactionType(reaction: ChatUIKitReaction): Int {
        val type = reaction.type
        return type.viewType
    }
}