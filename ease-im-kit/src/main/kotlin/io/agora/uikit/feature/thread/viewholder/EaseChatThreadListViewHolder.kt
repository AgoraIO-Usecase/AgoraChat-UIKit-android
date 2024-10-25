package io.agora.uikit.feature.thread.viewholder

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatConversationType
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatThread
import io.agora.uikit.common.extensions.getEmojiText
import io.agora.uikit.common.extensions.getMessageDigest
import io.agora.uikit.databinding.EaseItemChatThreadListItemBinding
import io.agora.uikit.model.EaseProfile

class EaseChatThreadListViewHolder(
    private val viewBinding: EaseItemChatThreadListItemBinding,
): EaseBaseRecyclerViewAdapter.ViewHolder<ChatThread>(binding = viewBinding) {
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
                    val msg =  EaseIM.getContext()?.let { context->
                        it.getMessageDigest(context).getEmojiText(context)
                    }
                    var content = it.from + ": " + msg
                    tvTopicContent.text = content

                    EaseProfile.getGroupMember(it.conversationId(),it.from)?.let { profile->
                        if (profile.name != null){
                            content = profile.name.toString() + ": " + msg
                        }
                    }

                    tvTopicContent.text = content
                }
                ivTopicIcon.visibility = View.VISIBLE
            }else{
                context?.let {
                    tvTopicContent.text = it.getString(R.string.ease_thread_region_no_msg)
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