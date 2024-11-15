package com.hyphenate.easeui.feature.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.extensions.getEmojiText
import com.hyphenate.easeui.common.extensions.getMessageDigest
import com.hyphenate.easeui.common.extensions.getTextHeight
import com.hyphenate.easeui.common.extensions.highlightTargetText
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.common.extensions.loadNickname
import com.hyphenate.easeui.databinding.UikitLayoutGroupSelectContactBinding

class ChatUIKitSearchMessageAdapter: ChatUIKitBaseRecyclerViewAdapter<ChatMessage>() {
    private var query : String = ""

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> =
        ChatUIKitSearchMessageViewHolder(
            UikitLayoutGroupSelectContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    fun searchText(query: String){
        this.query = query
    }

    inner class ChatUIKitSearchMessageViewHolder(private val binding: UikitLayoutGroupSelectContactBinding)
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