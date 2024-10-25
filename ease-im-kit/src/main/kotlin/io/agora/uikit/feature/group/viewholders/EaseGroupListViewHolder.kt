package io.agora.uikit.feature.group.viewholders

import android.graphics.drawable.Drawable
import androidx.viewbinding.ViewBinding
import coil.load
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.databinding.EaseLayoutGroupListItemBinding
import io.agora.uikit.feature.group.config.EaseGroupListConfig
import io.agora.uikit.feature.group.config.bindView
import io.agora.uikit.provider.getSyncProfile

class EaseGroupListViewHolder(
    private val viewBinding:EaseLayoutGroupListItemBinding,
    val config: EaseGroupListConfig = EaseGroupListConfig()
) : EaseBaseRecyclerViewAdapter.ViewHolder<ChatGroup>(binding = viewBinding) {

    private var bgDrawable: Drawable? = null

    init {
        config.bindView(viewBinding)
    }

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        viewBinding?.let {
            if (it is EaseLayoutGroupListItemBinding) {
                EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(it.groupAvatar)
                bgDrawable = it.root.background
            }
        }
    }

    override fun setData(item: ChatGroup?, position: Int) {
        item?.let {
            with(viewBinding) {
                groupName.text = it.groupName
                groupAvatar.setImageResource(R.drawable.ease_default_group_avatar)

                // Set custom data provided by group avatar
                EaseIM.getGroupProfileProvider()?.getSyncProfile(item.groupId)?.let { profile ->
                    if (profile.name.isNullOrEmpty().not()) {
                        groupName.text = profile.name
                    }
                    groupAvatar.load(profile.avatar) {
                        placeholder(R.drawable.ease_default_group_avatar)
                        error(R.drawable.ease_default_group_avatar)
                    }
                }

            }
        }
    }



}