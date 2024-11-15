package com.hyphenate.easeui.feature.chat.forward.viewholder

import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.common.extensions.setAvatarConfig
import com.hyphenate.easeui.common.extensions.toProfile
import com.hyphenate.easeui.databinding.UikitItemForwardLayoutBinding
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.provider.getSyncUser

class ChatUIKitContactForwardViewHolder(
    private val binding: UikitItemForwardLayoutBinding
): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitUser>(binding = binding) {

    val btnForward = binding.btnForward
    val tvName = binding.tvName

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        (viewBinding as? UikitItemForwardLayoutBinding)?.let {
            it.ivAvatar.setAvatarConfig()
        }
    }
    override fun setData(item: ChatUIKitUser?, position: Int) {
        item?.let {
            ChatUIKitClient.getUserProvider()?.getSyncUser(it.userId)?.let { profile ->
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