package com.hyphenate.easeui.feature.search.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.extensions.getChatroomName
import com.hyphenate.easeui.common.extensions.getGroupNameFromId
import com.hyphenate.easeui.databinding.EaseLayoutGroupSelectContactBinding
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.isChatRoom
import com.hyphenate.easeui.model.isGroupChat
import com.hyphenate.easeui.provider.getSyncProfile
import com.hyphenate.easeui.provider.getSyncUser

class EaseSearchConversationAdapter: EaseBaseRecyclerViewAdapter<EaseConversation>() {
    private var query : String = ""

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseConversation> =
        EaseSearchConversationViewHolder(
            EaseLayoutGroupSelectContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    fun searchText(query: String){
        this.query = query
    }

    inner class EaseSearchConversationViewHolder(private val binding: EaseLayoutGroupSelectContactBinding)
        : ViewHolder<EaseConversation>(binding = binding) {
        override fun setData(item: EaseConversation?, position: Int) {
            item?.run {
                with(binding) {
                    cbSelect.visibility = View.GONE

                    // Set conversation avatar and name.
                    if (item.isGroupChat()) {
                        emPresence.setPresenceData(avatar = R.drawable.ease_default_group_avatar
                            ,nickname = null)
                        tvName.text = item.conversationId.getGroupNameFromId()
                        EaseIM.getGroupProfileProvider()?.getSyncProfile(item.conversationId)?.let { profile ->
                            if (profile.name.isNullOrEmpty().not()) {
                                tvName.text = profile.name
                            }
                            emPresence.getUserAvatar().load(profile.avatar) {
                                placeholder(R.drawable.ease_default_group_avatar)
                                error(R.drawable.ease_default_group_avatar)
                            }
                        }
                    } else if (item.isChatRoom()) {
                        emPresence.setPresenceData(avatar = R.drawable.ease_default_chatroom_avatar
                            ,nickname = null)
                        tvName.text = item.conversationId.getChatroomName()
                    } else {
                        emPresence.setPresenceData(avatar = R.drawable.ease_default_avatar
                            ,nickname = item.conversationId)
                        tvName.text = item.conversationId
                        EaseIM.getUserProvider()?.getSyncUser(item.conversationId)?.let { profile ->
                            tvName.text = profile.getRemarkOrName()
                            emPresence.getUserAvatar().load(profile.avatar) {
                                placeholder(R.drawable.ease_default_avatar)
                                error(R.drawable.ease_default_avatar)
                            }
                        }
                    }


                    val title  = tvName.text.toString().trim()
                    val spannableString = SpannableString(title)
                    query.let {
                        val startIndex = title.indexOf(it, ignoreCase = true)
                        if (startIndex != -1) {
                            val endIndex = startIndex + it.length
                            spannableString.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(binding.root.context, R.color.ease_color_primary)),
                                startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            tvName.text = spannableString
                        }
                    }
                }
            }
        }
    }

}