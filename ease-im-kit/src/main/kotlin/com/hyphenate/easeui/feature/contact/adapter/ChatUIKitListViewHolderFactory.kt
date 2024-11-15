package com.hyphenate.easeui.feature.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.enums.ChatUIKitListViewType
import com.hyphenate.easeui.databinding.UikitLayoutGroupSelectContactBinding
import com.hyphenate.easeui.feature.contact.viewholders.ContactViewHolder
import com.hyphenate.easeui.feature.contact.item.ChatUIKitUserContactItem
import com.hyphenate.easeui.feature.contact.viewholders.ChatUIKitGroupMemberViewHolder
import com.hyphenate.easeui.feature.group.viewholders.ChatUIKitSelectContactViewHolder
import com.hyphenate.easeui.model.ChatUIKitUser

object ChatUIKitListViewHolderFactory {

    fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: ChatUIKitListViewType?,
    ): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitUser> {
        return when (viewType) {
            ChatUIKitListViewType.LIST_CONTACT -> {
                ContactViewHolder(ChatUIKitUserContactItem(parent.context))
            }
            ChatUIKitListViewType.LIST_SELECT_CONTACT -> {
                ChatUIKitSelectContactViewHolder(
                    UikitLayoutGroupSelectContactBinding.inflate(inflater, parent, false)
                )
            }
            ChatUIKitListViewType.LIST_GROUP_MEMBER -> {
                ChatUIKitGroupMemberViewHolder(ChatUIKitUserContactItem(parent.context))
            }

            else ->  ContactViewHolder(ChatUIKitUserContactItem(parent.context))
        }
    }

}