package com.hyphenate.easeui.feature.chat.forward.viewholder

import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.common.extensions.setAvatarConfig
import com.hyphenate.easeui.common.extensions.toProfile
import com.hyphenate.easeui.databinding.EaseItemForwardLayoutBinding
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.provider.getSyncUser

class EaseContactForwardViewHolder(
    private val binding: EaseItemForwardLayoutBinding
): EaseBaseRecyclerViewAdapter.ViewHolder<EaseUser>(binding = binding) {

    val btnForward = binding.btnForward
    val tvName = binding.tvName

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        (viewBinding as? EaseItemForwardLayoutBinding)?.let {
            it.ivAvatar.setAvatarConfig()
        }
    }
    override fun setData(item: EaseUser?, position: Int) {
        item?.let {
            EaseIM.getUserProvider()?.getSyncUser(it.userId)?.let { profile ->
                binding.ivAvatar.loadAvatar(profile)
                if (profile.name.isNullOrEmpty().not()) {
                    binding.tvName.text = profile.name
                } else {
                    binding.tvName.text = it.getNickname()
                }
            } ?: run {
                binding.ivAvatar.loadAvatar(it.toProfile())
                binding.tvName.text = it.getNickname()
            }
        }
    }
}