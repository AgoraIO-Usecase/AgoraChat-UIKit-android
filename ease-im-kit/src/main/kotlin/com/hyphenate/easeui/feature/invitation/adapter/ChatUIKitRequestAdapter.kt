package com.hyphenate.easeui.feature.invitation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.databinding.UikitLayoutInvitationItemBinding
import com.hyphenate.easeui.feature.invitation.holder.ChatUIKitNewRequestsViewHolder

class ChatUIKitRequestAdapter: ChatUIKitBaseRecyclerViewAdapter<ChatMessage>() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        return ChatUIKitNewRequestsViewHolder(
            UikitLayoutInvitationItemBinding.inflate(
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