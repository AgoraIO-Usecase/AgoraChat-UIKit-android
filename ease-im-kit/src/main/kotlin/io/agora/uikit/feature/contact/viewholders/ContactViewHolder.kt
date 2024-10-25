package io.agora.uikit.feature.contact.viewholders

import android.text.TextUtils
import android.view.View
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.feature.contact.adapter.EaseContactListAdapter
import io.agora.uikit.feature.contact.item.EaseUserContactItem
import io.agora.uikit.model.EaseUser

open class ContactViewHolder(
    itemView: View
) : EaseBaseRecyclerViewAdapter.ViewHolder<EaseUser>(itemView) {
    protected lateinit var easeUserListRow: EaseUserContactItem
    protected var isShowInitLetter:Boolean = true

    override fun initView(itemView: View?) {
        if (itemView is EaseUserContactItem){
            easeUserListRow = itemView
        }
    }

    override fun setData(item: EaseUser?, position: Int) {
        easeUserListRow.setUpView(item,position)
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

    fun setShowInitialLetter(isShow:Boolean){
        this.isShowInitLetter = isShow
    }


}