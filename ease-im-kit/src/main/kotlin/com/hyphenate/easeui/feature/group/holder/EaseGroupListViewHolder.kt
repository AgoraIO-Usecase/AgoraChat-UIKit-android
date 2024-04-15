package com.hyphenate.easeui.feature.group.holder

import android.graphics.drawable.Drawable
import androidx.viewbinding.ViewBinding
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.databinding.EaseLayoutGroupListItemBinding
import com.hyphenate.easeui.feature.group.config.EaseGroupListConfig
import com.hyphenate.easeui.feature.group.config.bindView
import com.hyphenate.easeui.provider.getSyncProfile

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