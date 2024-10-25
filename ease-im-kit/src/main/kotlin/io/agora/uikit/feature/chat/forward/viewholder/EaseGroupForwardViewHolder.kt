package io.agora.uikit.feature.chat.forward.viewholder

import androidx.viewbinding.ViewBinding
import coil.load
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.extensions.setAvatarConfig
import io.agora.uikit.databinding.EaseItemForwardLayoutBinding
import io.agora.uikit.provider.getSyncProfile

class EaseGroupForwardViewHolder(
    private val binding: EaseItemForwardLayoutBinding
): EaseBaseRecyclerViewAdapter.ViewHolder<ChatGroup>(binding = binding) {

    val btnForward = binding.btnForward

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        (viewBinding as? EaseItemForwardLayoutBinding)?.let {
            it.ivAvatar.setAvatarConfig()
        }
    }
    override fun setData(item: ChatGroup?, position: Int) {
        item?.let {
            with(binding) {
                tvName.text = it.groupName
                ivAvatar.setImageResource(io.agora.uikit.R.drawable.ease_default_group_avatar)

                // Set custom data provided by group avatar
                EaseIM.getGroupProfileProvider()?.getSyncProfile(item.groupId)?.let { profile ->
                    if (profile.name.isNullOrEmpty()) {
                        tvName.text = profile.name
                    }
                    ivAvatar.load(profile.avatar) {
                        placeholder(R.drawable.ease_default_group_avatar)
                        error(R.drawable.ease_default_group_avatar)
                    }
                }

            }
        }
    }
}