package com.hyphenate.easeui.feature.contact.viewholders

import android.text.TextUtils
import android.view.View
import com.hyphenate.easeui.feature.contact.adapter.EaseContactListAdapter
import com.hyphenate.easeui.model.EaseUser

class EaseGroupMemberViewHolder(
    itemView: View
) : ContactViewHolder(itemView) {

    override fun setData(item: EaseUser?, position: Int) {
        easeUserListRow.setUpView(item, position, true)
        val header = item?.initialLetter

        easeUserListRow.mViewBinding.header.visibility = View.GONE
        if (position == 0 || header != null && adapter is EaseContactListAdapter
            && header != (adapter as EaseContactListAdapter).getItem(position - 1)?.initialLetter) {
            if (!TextUtils.isEmpty(header) && isShowInitLetter) {
                easeUserListRow.mViewBinding.header.visibility = View.VISIBLE
                easeUserListRow.mViewBinding.header.text = header
            }
        }
    }

}