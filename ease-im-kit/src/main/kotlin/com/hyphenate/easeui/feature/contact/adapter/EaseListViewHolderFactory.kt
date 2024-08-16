package com.hyphenate.easeui.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.enums.EaseListViewType
import com.hyphenate.easeui.databinding.EaseLayoutGroupSelectContactBinding
import com.hyphenate.easeui.feature.contact.viewholders.ContactViewHolder
import com.hyphenate.easeui.feature.contact.item.EaseUserContactItem
import com.hyphenate.easeui.feature.contact.viewholders.EaseGroupMemberViewHolder
import com.hyphenate.easeui.feature.group.viewholders.EaseSelectContactViewHolder
import com.hyphenate.easeui.model.EaseUser

object EaseListViewHolderFactory {

    fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: EaseListViewType?,
    ): EaseBaseRecyclerViewAdapter.ViewHolder<EaseUser> {
        return when (viewType) {
            EaseListViewType.LIST_CONTACT -> {
                ContactViewHolder(EaseUserContactItem(parent.context))
            }
            EaseListViewType.LIST_SELECT_CONTACT -> {
                EaseSelectContactViewHolder(
                    EaseLayoutGroupSelectContactBinding.inflate(inflater, parent, false)
                )
            }
            EaseListViewType.LIST_GROUP_MEMBER -> {
                EaseGroupMemberViewHolder(EaseUserContactItem(parent.context))
            }

            else ->  ContactViewHolder(EaseUserContactItem(parent.context))
        }
    }

}