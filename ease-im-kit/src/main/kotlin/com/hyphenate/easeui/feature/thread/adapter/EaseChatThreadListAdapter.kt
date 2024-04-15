package com.hyphenate.easeui.feature.thread.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.databinding.EaseItemChatThreadListItemBinding
import com.hyphenate.easeui.feature.thread.viewholder.EaseChatThreadListViewHolder

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