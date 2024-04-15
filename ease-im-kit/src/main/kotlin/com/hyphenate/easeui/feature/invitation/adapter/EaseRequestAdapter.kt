package com.hyphenate.easeui.feature.invitation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.databinding.EaseLayoutInvitationItemBinding
import com.hyphenate.easeui.feature.invitation.holder.EaseNewRequestsViewHolder

class EaseRequestAdapter: EaseBaseRecyclerViewAdapter<ChatMessage>() {
    protected var mItemSubViewListener: OnItemSubViewClickListener? = null

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

    /**
     * set item sub view click
     * @param mItemSubViewListener
     */
    fun setOnItemSubViewClickListener(mItemSubViewListener: OnItemSubViewClickListener?) {
        this.mItemSubViewListener = mItemSubViewListener
    }
}