package com.hyphenate.easeui.feature.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.databinding.EasePinlistDefaultLayoutBinding
import com.hyphenate.easeui.databinding.EasePinlistImageLayoutBinding
import com.hyphenate.easeui.databinding.EasePinlistTextLayoutBinding
import com.hyphenate.easeui.feature.chat.pin.holder.EaseChatPinDefaultViewHolder
import com.hyphenate.easeui.feature.chat.pin.holder.EaseChatPinImageMessageViewHolder
import com.hyphenate.easeui.feature.chat.pin.holder.EaseChatPinTextMessageViewHolder

class EaseChatPinMessageListAdapter: EaseBaseRecyclerViewAdapter<ChatMessage>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        mData?.let {
            return it[position].type.ordinal
        }
        return -1
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        val viewHolder: ViewHolder<ChatMessage>

        when (ChatMessageType.values()[viewType]) {
            ChatMessageType.TXT -> viewHolder = EaseChatPinTextMessageViewHolder(
                mItemSubViewListener,
                EasePinlistTextLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )

            ChatMessageType.IMAGE -> viewHolder = EaseChatPinImageMessageViewHolder(
                mItemSubViewListener,
                EasePinlistImageLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )

            else -> viewHolder = EaseChatPinDefaultViewHolder(
                mItemSubViewListener,
                EasePinlistDefaultLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatMessage>, position: Int) {
        super.onBindViewHolder(holder, position)
    }
}