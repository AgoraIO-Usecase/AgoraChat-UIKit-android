package io.agora.uikit.feature.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.databinding.EaseLayoutGroupListItemBinding
import io.agora.uikit.feature.group.config.EaseGroupListConfig
import io.agora.uikit.feature.group.viewholders.EaseGroupListViewHolder

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