package io.agora.uikit.feature.invitation.holder

import android.view.View
import androidx.viewbinding.ViewBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.getSyncUserFromProvider
import io.agora.uikit.common.extensions.loadAvatar
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.databinding.EaseLayoutInvitationItemBinding
import io.agora.uikit.feature.invitation.enums.InviteMessageStatus
import io.agora.uikit.feature.invitation.helper.RequestMsgHelper
import io.agora.uikit.provider.getSyncUser

class EaseNewRequestsViewHolder(
    private val viewBinding: EaseLayoutInvitationItemBinding,
    private var listener: EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener?
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