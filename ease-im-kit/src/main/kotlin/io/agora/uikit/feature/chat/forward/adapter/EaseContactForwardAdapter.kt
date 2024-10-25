package io.agora.uikit.feature.chat.forward.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.agora.uikit.R
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.extensions.highlightTargetText
import io.agora.uikit.databinding.EaseItemForwardLayoutBinding
import io.agora.uikit.feature.chat.forward.viewholder.EaseContactForwardViewHolder
import io.agora.uikit.feature.contact.adapter.EaseContactListAdapter
import io.agora.uikit.interfaces.OnForwardClickListener
import io.agora.uikit.model.EaseUser

class EaseContactForwardAdapter: EaseContactListAdapter() {

    private var searchKey: String? = null
    private var forwardClickListener: OnForwardClickListener? = null
    private val sentUserList = mutableListOf<String>()

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseUser> {
        return EaseContactForwardViewHolder(EaseItemForwardLayoutBinding.inflate(LayoutInflater.from(parent.context)
            , parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder<EaseUser>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        if (holder is EaseContactForwardViewHolder){
            holder.btnForward.text = if (sentUserList.contains(item?.userId)) holder.itemView.context.getString(R.string.ease_chat_reply_forwarded)
            else holder.itemView.context.getString(R.string.ease_action_forward)
            holder.btnForward.isEnabled = !sentUserList.contains(item?.userId)
            holder.btnForward.setOnClickListener { view ->
                item?.userId?.let { sentUserList.add(it) }
                holder.btnForward.text = holder.itemView.context.getString(R.string.ease_chat_reply_forwarded)
                holder.btnForward.isEnabled = false
                item?.let {
                    forwardClickListener?.onForwardClick(view, it.userId, ChatType.Chat)
                }
            }
            searchKey?.let {
                val spannable = holder.tvName.text.toString().trim().highlightTargetText(it
                    , ContextCompat.getColor(holder.itemView.context, R.color.ease_color_primary))
                holder.tvName.text = spannable
            }
        }
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }

    fun setSearchKey(key: String){
        this.searchKey = key
        notifyDataSetChanged()
    }

    /**
     * Set the user id has been sent.
     */
    fun setSentUserList(userList: List<String>?){
        userList?.let {
            for (userId: String in it){
                if (!sentUserList.contains(userId)) {
                    sentUserList.add(userId)
                }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * Get the user list has been sent.
     */
    fun getSentUserList(): List<String>{
        return sentUserList
    }

}