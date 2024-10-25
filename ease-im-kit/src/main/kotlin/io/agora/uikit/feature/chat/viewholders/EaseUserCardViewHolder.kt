package io.agora.uikit.feature.chat.viewholders

import android.view.View
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.getUserCardInfo
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.feature.contact.EaseContactCheckActivity

class EaseUserCardViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {

    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        message?.getUserCardInfo()?.let {
            mContext.startActivity(EaseContactCheckActivity.createIntent(mContext, it.toUser()))
        }
    }
}