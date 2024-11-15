package com.hyphenate.easeui.feature.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.databinding.UikitPinlistDefaultLayoutBinding
import com.hyphenate.easeui.databinding.UikitPinlistImageLayoutBinding
import com.hyphenate.easeui.databinding.UikitPinlistTextLayoutBinding
import com.hyphenate.easeui.feature.chat.pin.holder.ChatUIKitPinDefaultViewHolder
import com.hyphenate.easeui.feature.chat.pin.holder.ChatUIKitPinImageMessageViewHolder
import com.hyphenate.easeui.feature.chat.pin.holder.ChatUIKitPinTextMessageViewHolder

class ChatUIKitPinMessageListAdapter: ChatUIKitBaseRecyclerViewAdapter<ChatMessage>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        mData?.let {
            return it[position].type.ordinal
        }
        return -1
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        val viewHolder: ViewHolder<ChatMessage>

        when (ChatMessageType.values()[viewType]) {
            ChatMessageType.TXT -> viewHolder = ChatUIKitPinTextMessageViewHolder(
                mItemSubViewListener,
                UikitPinlistTextLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )

            ChatMessageType.IMAGE -> viewHolder = ChatUIKitPinImageMessageViewHolder(
                mItemSubViewListener,
                UikitPinlistImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )

            else -> viewHolder = ChatUIKitPinDefaultViewHolder(
                mItemSubViewListener,
                UikitPinlistDefaultLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatMessage>, position: Int) {
        super.onBindViewHolder(holder, position)
    }
}