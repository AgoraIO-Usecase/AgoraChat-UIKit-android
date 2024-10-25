package io.agora.uikit.feature.chat.forward.viewholder

import androidx.viewbinding.ViewBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.extensions.loadAvatar
import io.agora.uikit.common.extensions.setAvatarConfig
import io.agora.uikit.common.extensions.toProfile
import io.agora.uikit.databinding.EaseItemForwardLayoutBinding
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.getNickname
import io.agora.uikit.provider.getSyncUser

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