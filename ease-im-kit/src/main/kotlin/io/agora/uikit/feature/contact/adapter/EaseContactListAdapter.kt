package io.agora.uikit.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.enums.EaseListViewType
import io.agora.uikit.feature.contact.viewholders.ContactViewHolder
import io.agora.uikit.feature.contact.item.EaseUserContactItem
import io.agora.uikit.feature.group.viewholders.EaseSelectContactViewHolder
import io.agora.uikit.feature.search.interfaces.OnContactSelectListener
import io.agora.uikit.interfaces.OnUserListItemClickListener
import io.agora.uikit.model.EaseUser
import java.util.concurrent.ConcurrentHashMap

open class EaseContactListAdapter(
    private var viewType: EaseListViewType = EaseListViewType.LIST_CONTACT
): EaseBaseRecyclerViewAdapter<EaseUser>() {
    private var itemClickListener: OnUserListItemClickListener? = null
    private var selectedListener: OnContactSelectListener? = null
    private var userAvatarInfo: ConcurrentHashMap<String, Int>? = null
    private var isShowInitLetter:Boolean = true
    private var selectedMember:MutableList<String> = mutableListOf()

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseUser> {
        return EaseListViewHolderFactory.createViewHolder(
            LayoutInflater.from(parent.context),parent, EaseListViewType.fromCode(viewType))
    }

    override fun getItemNotEmptyViewType(position: Int): Int {
        return viewType.code
    }

    override fun onBindViewHolder(holder: ViewHolder<EaseUser>, position: Int) {
        if ((holder.itemView) is EaseUserContactItem){
            userAvatarInfo?.let {
                ((holder.itemView) as EaseUserContactItem).setUserAvatarInfo(it)
            }
        }
        if (holder is ContactViewHolder){
            holder.setShowInitialLetter(isShowInitLetter)
        }

        if (holder is EaseSelectContactViewHolder){
            holder.setSelectedMembers(selectedMember)
        }

        super.onBindViewHolder(holder, position)
        if ((holder.itemView) is EaseUserContactItem){
            itemClickListener?.let {
                ((holder.itemView) as EaseUserContactItem).setOnUserListItemClickListener(it)
            }
        }
        if (holder is EaseSelectContactViewHolder){
            selectedListener?.let {
                holder.setCheckBoxSelectListener(it)
            }
        }
    }

    fun setListViewItemType(viewType: EaseListViewType){
        this.viewType = viewType
        notifyDataSetChanged()
    }

    fun setUserAvatarInfo(info: ConcurrentHashMap<String, Int>?) {
        this.userAvatarInfo = info
        notifyDataSetChanged()
    }

    fun setShowInitialLetter(isShow:Boolean){
        this.isShowInitLetter = isShow
    }

    fun setSelectedMembers(selectedMember:MutableList<String>){
        this.selectedMember = selectedMember
        notifyDataSetChanged()
    }

    /**
     * Set message item click listeners.
     * @param listener
     */
    fun setOnUserListItemClickListener(listener: OnUserListItemClickListener) {
        this.itemClickListener = listener
    }


    fun setCheckBoxSelectListener(listener: OnContactSelectListener){
        this.selectedListener = listener
    }



}