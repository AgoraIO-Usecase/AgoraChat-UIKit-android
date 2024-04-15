package com.hyphenate.easeui.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseLayoutItemHeaderBinding
import com.hyphenate.easeui.feature.contact.config.EaseContactHeaderConfig
import com.hyphenate.easeui.feature.contact.viewholders.ContactHeaderViewHolder
import com.hyphenate.easeui.feature.contact.interfaces.OnHeaderItemClickListener
import com.hyphenate.easeui.model.EaseCustomHeaderItem

class EaseCustomHeaderAdapter(
    val config: EaseContactHeaderConfig? = EaseContactHeaderConfig()
) : EaseBaseRecyclerViewAdapter<EaseCustomHeaderItem>(){
    private var listener: OnHeaderItemClickListener? = null
    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<EaseCustomHeaderItem> {
        return ContactHeaderViewHolder(EaseLayoutItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),config)
    }

    override fun onBindViewHolder(holder: ViewHolder<EaseCustomHeaderItem>, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.itemView.setOnClickListener{
            listener?.onHeaderItemClick(it,position,getItem(position)?.headerId)
        }
    }

    fun addItem(data:EaseCustomHeaderItem){
        this.addData(data)
    }

    fun setItems(data:MutableList<EaseCustomHeaderItem>){
        this.setData(data)
    }

    fun setOnHeaderItemClickListener(listener: OnHeaderItemClickListener?){
        this.listener = listener
    }
}