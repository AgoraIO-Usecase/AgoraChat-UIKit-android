package io.agora.uikit.feature.chat.forward.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.R
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.ChatType
import io.agora.uikit.databinding.EaseItemForwardLayoutBinding
import io.agora.uikit.feature.chat.forward.viewholder.EaseGroupForwardViewHolder
import io.agora.uikit.feature.group.adapter.EaseGroupListAdapter
import io.agora.uikit.interfaces.OnForwardClickListener

class EaseGroupListForwardAdapter: EaseGroupListAdapter() {
    private var forwardClickListener: OnForwardClickListener? = null
    private val sentGroupList = mutableListOf<Int>()
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatGroup> {
        return EaseGroupForwardViewHolder(EaseItemForwardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatGroup>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        if (holder is EaseGroupForwardViewHolder){
            holder.btnForward.isEnabled = !sentGroupList.contains(position)
            holder.btnForward.text = if (sentGroupList.contains(position)) holder.itemView.context.getString(R.string.ease_chat_reply_forwarded)
            else holder.itemView.context.getString(R.string.ease_action_forward)
            holder.btnForward.setOnClickListener { view ->
                sentGroupList.add(position)
                holder.btnForward.isEnabled = false
                holder.btnForward.text = holder.itemView.context.getString(R.string.ease_chat_reply_forwarded)
                item?.let {
                    forwardClickListener?.onForwardClick(view, it.groupId, ChatType.GroupChat)
                }
            }
        }
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }
}