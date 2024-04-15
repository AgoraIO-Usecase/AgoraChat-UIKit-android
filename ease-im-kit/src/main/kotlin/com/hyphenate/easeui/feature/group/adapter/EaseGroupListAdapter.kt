package com.hyphenate.easeui.feature.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.databinding.EaseLayoutGroupListItemBinding
import com.hyphenate.easeui.feature.group.config.EaseGroupListConfig
import com.hyphenate.easeui.feature.group.holder.EaseGroupListViewHolder

open class EaseGroupListAdapter(
    var config:EaseGroupListConfig = EaseGroupListConfig()
): EaseBaseRecyclerViewAdapter<ChatGroup>(){

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatGroup> {
        return EaseGroupListViewHolder(EaseLayoutGroupListItemBinding.inflate(LayoutInflater.from(parent.context)),config)
    }

    /**
     * Set group list item config.
     */
    fun setGroupListItemConfig(config: EaseGroupListConfig) {
        this.config = config
        notifyDataSetChanged()
    }

}