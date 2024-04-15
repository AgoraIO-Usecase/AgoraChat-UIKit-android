package com.hyphenate.easeui.feature.group.adapter

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.extensions.toProfile
import com.hyphenate.easeui.databinding.EaseLayoutContactItemBinding
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser

class EaseGroupMemberListAdapter(
    private val groupId: String?
): EaseBaseRecyclerViewAdapter<EaseUser>() {
    private var isShowInitLetter:Boolean = true
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseUser> =
        GroupMemberListViewHolder(
            EaseLayoutContactItemBinding.inflate(LayoutInflater.from(parent.context))
        )

    override fun onBindViewHolder(holder: ViewHolder<EaseUser>, position: Int) {
        if (holder is GroupMemberListViewHolder){
            holder.setShowInitialLetter(isShowInitLetter)
        }
        super.onBindViewHolder(holder, position)
    }


    fun setShowInitialLetter(isShow:Boolean){
        this.isShowInitLetter = isShow
    }

    inner class GroupMemberListViewHolder(
        private val mViewBinding: EaseLayoutContactItemBinding
    ) : ViewHolder<EaseUser>(binding = mViewBinding) {
        private var bgDrawable: Drawable? = null
        private var isShowInitLetter:Boolean = true
        private var user:EaseUser?=null

        override fun initView(viewBinding: ViewBinding?) {
            super.initView(viewBinding)
            viewBinding?.let {
                if (it is EaseLayoutContactItemBinding) {
                    bgDrawable = it.root.background
                }
            }
        }

        fun setShowInitialLetter(isShow:Boolean){
            this.isShowInitLetter = isShow
        }

        override fun setData(item: EaseUser?, position: Int) {
            this.user = item
            mViewBinding.let {
                val header = item?.initialLetter
                it.header.visibility = View.GONE
                it.emPresence.setPresenceData(user?.toProfile())
                it.tvName.text = user?.nickname ?: user?.userId

                groupId?.let { id ->
                    EaseProfile.getGroupMember(groupId, user?.userId)?.let { profile ->
                        it.emPresence.setPresenceData(profile)
                        it.tvName.text = profile.getRemarkOrName()
                    }
                }

                if (position == 0 || header != null && adapter is EaseGroupMemberListAdapter
                    && header != (adapter as EaseGroupMemberListAdapter).getItem(position - 1)?.initialLetter) {
                    if (!TextUtils.isEmpty(header) && isShowInitLetter) {
                        it.header.visibility = View.VISIBLE
                        it.header.text = header
                    }
                }
            }
        }

    }
}