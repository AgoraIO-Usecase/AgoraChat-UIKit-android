package io.agora.uikit.feature.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.databinding.EasePinlistDefaultLayoutBinding
import io.agora.uikit.databinding.EasePinlistImageLayoutBinding
import io.agora.uikit.databinding.EasePinlistTextLayoutBinding
import io.agora.uikit.feature.chat.pin.holder.EaseChatPinDefaultViewHolder
import io.agora.uikit.feature.chat.pin.holder.EaseChatPinImageMessageViewHolder
import io.agora.uikit.feature.chat.pin.holder.EaseChatPinTextMessageViewHolder

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