package io.agora.uikit.feature.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.extensions.getEmojiText
import io.agora.uikit.common.extensions.getMessageDigest
import io.agora.uikit.common.extensions.getTextHeight
import io.agora.uikit.common.extensions.highlightTargetText
import io.agora.uikit.common.extensions.loadAvatar
import io.agora.uikit.common.extensions.loadNickname
import io.agora.uikit.databinding.EaseLayoutGroupSelectContactBinding

class EaseSearchMessageAdapter: EaseBaseRecyclerViewAdapter<ChatMessage>() {
    private var query : String = ""

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> =
        EaseSearchMessageViewHolder(
            EaseLayoutGroupSelectContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    fun searchText(query: String){
        this.query = query
    }

    inner class EaseSearchMessageViewHolder(private val binding: EaseLayoutGroupSelectContactBinding)
        : ViewHolder<ChatMessage>(binding = binding) {
        override fun setData(item: ChatMessage?, position: Int) {
            item?.run {
                with(binding) {
                    cbSelect.visibility = View.GONE
                    tvSubtitle.visibility = View.VISIBLE
                    tvSubtitle.text = ""

                    tvSubtitle.text = item.getMessageDigest(itemView.context)
                        .getEmojiText(itemView.context, tvSubtitle.getTextHeight())

                    item.run {
                        emPresence.getUserAvatar().loadAvatar(this)
                        tvName.loadNickname(this)
                    }

                    if (item.type == ChatMessageType.TXT) {
                        tvSubtitle.text = tvSubtitle.text.toString().trim()
                            .highlightTargetText(query, ContextCompat.getColor(binding.root.context, R.color.ease_color_primary))
                    }
                }
            }
        }

    }
}