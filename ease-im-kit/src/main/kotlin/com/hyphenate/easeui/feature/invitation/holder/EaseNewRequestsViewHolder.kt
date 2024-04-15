package com.hyphenate.easeui.feature.invitation.holder

import android.view.View
import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.getSyncUserFromProvider
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.databinding.EaseLayoutInvitationItemBinding
import com.hyphenate.easeui.feature.invitation.enums.InviteMessageStatus
import com.hyphenate.easeui.feature.invitation.helper.RequestMsgHelper
import com.hyphenate.easeui.provider.getSyncUser

class EaseNewRequestsViewHolder(
    private val viewBinding: EaseLayoutInvitationItemBinding,
    private var listener:EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener?
): EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage>(binding = viewBinding) {

    override fun initView(viewBinding: ViewBinding?) {
        if(viewBinding is EaseLayoutInvitationItemBinding){
            EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(viewBinding.itemAvatar)
        }
    }

    override fun setData(item: ChatMessage?, position: Int) {
        item?.run {
            with(viewBinding) {
                val from = item.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM)
                val user = getSyncUserFromProvider()?.toUser()
                val reason = RequestMsgHelper.getSystemMessage(viewBinding.root.context, item)
                if (user == null){
                    itemTitle.text = from
                }else{
                    itemTitle.text = user.userId
                }
                itemReason.text = reason

                EaseIM.getUserProvider()?.getSyncUser(from)?.let {
                    itemTitle.text = it.getRemarkOrName()
                    itemAvatar.loadAvatar(it)
                }

                val statusParams: String = item.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS)
                when(InviteMessageStatus.valueOf(statusParams)){
                    InviteMessageStatus.BEINVITEED -> {
                        itemAction.setText(R.string.ease_invitation_action_add)
                        itemAction.isSelected = true
                        itemReason.visibility = View.VISIBLE
                    }
                    InviteMessageStatus.AGREED -> {
                        itemAction.setText(R.string.ease_invitation_action_added)
                        itemAction.isSelected = false
                        itemReason.visibility = View.GONE
                    }
                    else -> {}
                }

                itemAction.setOnClickListener{
                    listener?.onItemSubViewClick(it,position)
                }
            }
        }
    }

}