package io.agora.uikit.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.databinding.EaseLayoutItemHeaderBinding
import io.agora.uikit.feature.contact.config.EaseContactHeaderConfig
import io.agora.uikit.feature.contact.viewholders.ContactHeaderViewHolder
import io.agora.uikit.feature.contact.interfaces.OnHeaderItemClickListener
import io.agora.uikit.model.EaseCustomHeaderItem

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