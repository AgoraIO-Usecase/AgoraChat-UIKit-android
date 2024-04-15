package com.hyphenate.easeui.feature.chat.reaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemMessageMenuReactionBinding
import com.hyphenate.easeui.databinding.EaseItemMessageReactionBinding
import com.hyphenate.easeui.feature.chat.enums.EaseReactionType
import com.hyphenate.easeui.feature.chat.reaction.viewholder.EaseReactionAddViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.EaseReactionDefaultViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.EaseReactionMoreViewHolder
import com.hyphenate.easeui.feature.chat.reaction.viewholder.EaseReactionNormalViewHolder
import com.hyphenate.easeui.model.EaseReaction

object EaseMessageReactionViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EaseBaseRecyclerViewAdapter.ViewHolder<EaseReaction> {
        return when (viewType) {
            EaseReactionType.DEFAULT.viewType -> EaseReactionDefaultViewHolder(
                EaseItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            EaseReactionType.ADD.viewType -> EaseReactionAddViewHolder(
                EaseItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            EaseReactionType.MORE.viewType -> EaseReactionMoreViewHolder()

            else -> EaseReactionNormalViewHolder(
                EaseItemMessageReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
        }
    }

    fun getViewType(reaction: EaseReaction?): Int {
        return reaction?.let { getReactionType(it) } ?: (EaseReactionType.NORMAL.viewType)
    }

    private fun getReactionType(reaction: EaseReaction): Int {
        val type = reaction.type
        return type.viewType
    }
}