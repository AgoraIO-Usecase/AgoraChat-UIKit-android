package com.hyphenate.easeui.feature.thread.viewholder

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.extensions.getEmojiText
import com.hyphenate.easeui.common.extensions.getMessageDigest
import com.hyphenate.easeui.databinding.UikitItemChatThreadListItemBinding
import com.hyphenate.easeui.model.ChatUIKitProfile

class ChatUIKitThreadListViewHolder(
    private val viewBinding: UikitItemChatThreadListItemBinding,
): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatThread>(binding = viewBinding) {
    private var messageMap:MutableMap<String,ChatMessage> = mutableMapOf()
    private var context: Context? = null

    fun setLatestMessages(latestMsgMap:MutableMap<String,ChatMessage>?){
        latestMsgMap?.let {
            messageMap.putAll(it)
        }
    }

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
        viewBinding?.let {
            context = it.root.context
        }
    }

    override fun setData(item: ChatThread?, position: Int) {
        val threadId = item?.chatThreadId
        viewBinding.run {
            tvTopicTitle.text = item?.chatThreadName
            val latestMsg = getLatestMessage(threadId)
            if (latestMsg != null && latestMsg.isChatThreadMessage){
                latestMsg.let {
                    val msg =  ChatUIKitClient.getContext()?.let { context->
                        it.getMessageDigest(context).getEmojiText(context)
                    }
                    var content = it.from + ": " + msg
                    tvTopicContent.text = content

                    ChatUIKitProfile.getGroupMember(it.conversationId(),it.from)?.let { profile->
                        if (profile.name != null){
                            content = profile.name.toString() + ": " + msg
                        }
                    }

                    tvTopicContent.text = content
                }
                ivTopicIcon.visibility = View.VISIBLE
            }else{
                context?.let {
                    tvTopicContent.text = it.getString(R.string.uikit_thread_region_no_msg)
                }
                ivTopicIcon.visibility = View.GONE
            }
        }
    }

    private fun getLatestMessage(threadId:String?):ChatMessage?{
        return if (messageMap.isNotEmpty() && messageMap.containsKey(threadId)){
            messageMap[threadId]?.let {
                return it
            }
        }else{
            val conversation = ChatClient.getInstance().chatManager().getConversation(threadId
                ,ChatConversationType.GroupChat , true, true)
            return conversation.lastMessage
        }
    }


}