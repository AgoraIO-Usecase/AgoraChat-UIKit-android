package com.hyphenate.easeui.feature.group.viewholders

import android.graphics.drawable.Drawable
import androidx.viewbinding.ViewBinding
import coil.load
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.databinding.UikitLayoutGroupListItemBinding
import com.hyphenate.easeui.feature.group.config.ChatUIKitGroupListConfig
import com.hyphenate.easeui.feature.group.config.bindView
import com.hyphenate.easeui.provider.getSyncProfile

class ChatUIKitGroupListViewHolder(
    private val viewBinding:UikitLayoutGroupListItemBinding,
    val config: ChatUIKitGroupListConfig = ChatUIKitGroupListConfig()
) : ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatGroup>(binding = viewBinding) {

    private var bgDrawable: Drawable? = null

    init {
        config.bindView(viewBinding)
    }

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        viewBinding?.let {
            if (it is UikitLayoutGroupListItemBinding) {
                ChatUIKitClient.getConfig()?.avatarConfig?.setAvatarStyle(it.groupAvatar)
                bgDrawable = it.root.background
            }
        }
    }

    override fun setData(item: ChatGroup?, position: Int) {
        item?.let {
            with(viewBinding) {
                groupName.text = it.groupName
                groupAvatar.setImageResource(R.drawable.uikit_default_group_avatar)

                // Set custom data provided by group avatar
                ChatUIKitClient.getGroupProfileProvider()?.getSyncProfile(item.groupId)?.let { profile ->
                    if (profile.name.isNullOrEmpty().not()) {
                        groupName.text = profile.name
                    }
                    groupAvatar.load(profile.avatar) {
                        placeholder(R.drawable.uikit_default_group_avatar)
                        error(R.drawable.uikit_default_group_avatar)
                    }
                }

            }
        }
    }



}