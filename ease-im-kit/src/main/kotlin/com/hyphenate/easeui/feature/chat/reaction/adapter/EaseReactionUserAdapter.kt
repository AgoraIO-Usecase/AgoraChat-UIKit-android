package com.hyphenate.easeui.feature.chat.reaction.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.common.extensions.setAvatarConfig
import com.hyphenate.easeui.common.extensions.toProfile
import com.hyphenate.easeui.databinding.EaseItemReactionUserBinding
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.model.isCurrentUser

class EaseReactionUserAdapter: EaseBaseRecyclerViewAdapter<EaseUser>() {
    private var onDeleteClickListener: OnClickListener? = null
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseUser> {
        return EaseReactionUserViewHolder(EaseItemReactionUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private inner class EaseReactionUserViewHolder(val binding: EaseItemReactionUserBinding)
        : ViewHolder<EaseUser>(binding = binding) {

        override fun initView(viewBinding: ViewBinding?) {
            super.initView(viewBinding)
            viewBinding?.let {
                (it as EaseItemReactionUserBinding).ivAvatar.setAvatarConfig()
            }
        }

        override fun setData(item: EaseUser?, position: Int) {
            item?.let {
                binding.ivAvatar.loadAvatar(it.toProfile())
                binding.tvName.text = it.getRemarkOrName()

                if (it.isCurrentUser()) {
                    binding.ivDelete.visibility = View.VISIBLE
                } else {
                    binding.ivDelete.visibility = View.GONE
                }
                binding.ivDelete.setOnClickListener {
                    onDeleteClickListener?.onClick(it)
                }
            }
        }
    }

    /**
     * Set the listener for the delete button.
     */
    fun setOnDeleteClickListener(listener: OnClickListener) {
        onDeleteClickListener = listener
    }
}