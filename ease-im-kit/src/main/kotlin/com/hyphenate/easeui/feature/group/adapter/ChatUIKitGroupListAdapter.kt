package com.hyphenate.easeui.feature.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.databinding.UikitLayoutGroupListItemBinding
import com.hyphenate.easeui.feature.group.config.ChatUIKitGroupListConfig
import com.hyphenate.easeui.feature.group.viewholders.ChatUIKitGroupListViewHolder

open class ChatUIKitGroupListAdapter(
    var config:ChatUIKitGroupListConfig = ChatUIKitGroupListConfig()
): ChatUIKitBaseRecyclerViewAdapter<ChatGroup>(){

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatGroup> {
        return ChatUIKitGroupListViewHolder(UikitLayoutGroupListItemBinding.inflate(LayoutInflater.from(parent.context)),config)
    }

    /**
     * Set group list item config.
     */
    fun setGroupListItemConfig(config: ChatUIKitGroupListConfig) {
        this.config = config
        notifyDataSetChanged()
    }

}