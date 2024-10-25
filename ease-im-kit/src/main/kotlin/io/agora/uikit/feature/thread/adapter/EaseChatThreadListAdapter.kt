package io.agora.uikit.feature.thread.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatThread
import io.agora.uikit.databinding.EaseItemChatThreadListItemBinding
import io.agora.uikit.feature.thread.viewholder.EaseChatThreadListViewHolder

class EaseChatThreadListAdapter(

): EaseBaseRecyclerViewAdapter<ChatThread>(){
    private var messageMap:MutableMap<String,ChatMessage> = mutableMapOf()
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatThread> {
        return EaseChatThreadListViewHolder(EaseItemChatThreadListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatThread>, position: Int) {
        if (holder is EaseChatThreadListViewHolder){
            holder.setLatestMessages(messageMap)
        }
        super.onBindViewHolder(holder, position)
    }

    fun setLatestMessages(latestMsgMap:MutableMap<String,ChatMessage>?){
        latestMsgMap?.let {
            messageMap.putAll(it)
            notifyDataSetChanged()
        }
    }

    fun getLatestMessages():MutableMap<String,ChatMessage>{
        return messageMap
    }

    fun clearLatestMessages(){
        messageMap.clear()
    }
}