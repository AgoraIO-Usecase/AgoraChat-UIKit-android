package com.hyphenate.easeui.feature.chat.forward.viewholder

import androidx.viewbinding.ViewBinding
import coil.load
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.extensions.setAvatarConfig
import com.hyphenate.easeui.databinding.UikitItemForwardLayoutBinding
import com.hyphenate.easeui.provider.getSyncProfile

class ChatUIKitGroupForwardViewHolder(
    private val binding: UikitItemForwardLayoutBinding
): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatGroup>(binding = binding) {

    val btnForward = binding.btnForward

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        (viewBinding as? UikitItemForwardLayoutBinding)?.let {
            it.ivAvatar.setAvatarConfig()
        }
    }
    override fun setData(item: ChatGroup?, position: Int) {
        item?.let {
            with(binding) {
                tvName.text = it.groupName
                ivAvatar.setImageResource(com.hyphenate.easeui.R.drawable.uikit_default_group_avatar)

                // Set custom data provided by group avatar
                ChatUIKitClient.getGroupProfileProvider()?.getSyncProfile(item.groupId)?.let { profile ->
                    if (profile.name.isNullOrEmpty()) {
                        tvName.text = profile.name
                    }
                    ivAvatar.load(profile.avatar) {
                        placeholder(R.drawable.uikit_default_group_avatar)
                        error(R.drawable.uikit_default_group_avatar)
                    }
                }

            }
        }
    }
}