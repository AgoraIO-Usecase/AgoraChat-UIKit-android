package com.hyphenate.easeui.feature.thread.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.databinding.UikitItemChatThreadListItemBinding
import com.hyphenate.easeui.feature.thread.viewholder.ChatUIKitThreadListViewHolder

class ChatUIKitThreadListAdapter(

): ChatUIKitBaseRecyclerViewAdapter<ChatThread>(){
    private var messageMap:MutableMap<String,ChatMessage> = mutableMapOf()
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatThread> {
        return ChatUIKitThreadListViewHolder(UikitItemChatThreadListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder<ChatThread>, position: Int) {
        if (holder is ChatUIKitThreadListViewHolder){
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