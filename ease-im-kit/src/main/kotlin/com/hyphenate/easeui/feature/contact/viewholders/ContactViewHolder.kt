package com.hyphenate.easeui.feature.contact.viewholders

import android.text.TextUtils
import android.view.View
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.contact.adapter.ChatUIKitContactListAdapter
import com.hyphenate.easeui.feature.contact.item.ChatUIKitUserContactItem
import com.hyphenate.easeui.model.ChatUIKitUser

open class ContactViewHolder(
    itemView: View
) : ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitUser>(itemView) {
    protected lateinit var easeUserListRow: ChatUIKitUserContactItem
    protected var isShowInitLetter:Boolean = true

    override fun initView(itemView: View?) {
        if (itemView is ChatUIKitUserContactItem){
            easeUserListRow = itemView
        }
    }

    override fun setData(item: ChatUIKitUser?, position: Int) {
        easeUserListRow.setUpView(item,position)
        val header = item?.initialLetter

        easeUserListRow.mViewBinding.header.visibility = View.GONE
        if (position == 0 || header != null && adapter is ChatUIKitContactListAdapter
            && header != (adapter as ChatUIKitContactListAdapter).getItem(position - 1)?.initialLetter) {
            if (!TextUtils.isEmpty(header) && isShowInitLetter) {
                easeUserListRow.mViewBinding.header.visibility = View.VISIBLE
                easeUserListRow.mViewBinding.header.text = header
            }
        }
    }

    fun setShowInitialLetter(isShow:Boolean){
        this.isShowInitLetter = isShow
    }


}