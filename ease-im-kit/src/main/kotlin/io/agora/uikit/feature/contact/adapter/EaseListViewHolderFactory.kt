package io.agora.uikit.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.enums.EaseListViewType
import io.agora.uikit.databinding.EaseLayoutGroupSelectContactBinding
import io.agora.uikit.feature.contact.viewholders.ContactViewHolder
import io.agora.uikit.feature.contact.item.EaseUserContactItem
import io.agora.uikit.feature.contact.viewholders.EaseGroupMemberViewHolder
import io.agora.uikit.feature.group.viewholders.EaseSelectContactViewHolder
import io.agora.uikit.model.EaseUser

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