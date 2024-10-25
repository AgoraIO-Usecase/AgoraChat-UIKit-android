package io.agora.uikit.feature.invitation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.databinding.EaseLayoutInvitationItemBinding
import io.agora.uikit.feature.invitation.holder.EaseNewRequestsViewHolder

class EaseRequestAdapter: EaseBaseRecyclerViewAdapter<ChatMessage>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        return EaseNewRequestsViewHolder(
            EaseLayoutInvitationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ),
            mItemSubViewListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatMessage>, position: Int) {
        super.onBindViewHolder(holder, position)
    }

}