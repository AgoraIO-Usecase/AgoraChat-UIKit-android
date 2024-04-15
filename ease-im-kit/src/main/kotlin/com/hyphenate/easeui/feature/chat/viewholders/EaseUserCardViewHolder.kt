package com.hyphenate.easeui.feature.chat.viewholders

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.extensions.getUserCardInfo
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.feature.contact.EaseContactCheckActivity

class EaseUserCardViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {

    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        message?.getUserCardInfo()?.let {
            mContext.startActivity(EaseContactCheckActivity.createIntent(mContext, it.toUser()))
        }
    }
}