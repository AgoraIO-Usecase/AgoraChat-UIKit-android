package com.hyphenate.easeui.feature.thread.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.extensions.getEmojiText
import com.hyphenate.easeui.common.extensions.getMessageDigest
import com.hyphenate.easeui.common.extensions.isSend
import com.hyphenate.easeui.common.extensions.maxUnreadCount
import com.hyphenate.easeui.common.interfaces.IControlDataView
import com.hyphenate.easeui.databinding.EaseLayoutChatThreadRegionBinding
import com.hyphenate.easeui.feature.thread.interfaces.IMessageThread
import com.hyphenate.easeui.feature.thread.interfaces.OnMessageChatThreadClickListener
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.viewmodel.thread.EaseChatThreadViewModel
import com.hyphenate.easeui.viewmodel.thread.IChatThreadRequest

class EaseChatMessageThreadView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr),IMessageThread, IControlDataView {
    private lateinit var message: ChatMessage
    private lateinit var thread:ChatThread
    private var viewModel:IChatThreadRequest? = null
    private var listener:OnMessageChatThreadClickListener? = null

    private val binding = EaseLayoutChatThreadRegionBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        binding.threadRegionItem.setOnClickListener {
            listener?.onThreadViewItemClick(it, thread, message)
        }
    }

    override fun setupWithMessage(message: ChatMessage) {
        this.message = message
        thread = message.chatThread
        setLayout(message.isSend())
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context)[EaseChatThreadViewModel::class.java]
            } else {
                EaseChatThreadViewModel()
            }
        }
        viewModel?.attachView(this)
    }

    private fun setLayout(isSender: Boolean) {
        val marginTopBubble = context.resources.getDimensionPixelSize(R.dimen.ease_chat_thread_padding_bubble)
        val marginStartBubble = context.resources.getDimensionPixelSize(R.dimen.ease_chat_padding_bubble)
        val marginEndBubble = context.resources.getDimensionPixelSize(R.dimen.ease_chat_padding_bubble)
        if (isSender) {
            gravity = Gravity.END
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, marginTopBubble, marginEndBubble, 0)
            }
        } else {
            gravity = Gravity.START
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginStartBubble, marginTopBubble, 0, 0)
            }
        }
    }

    override fun showThread() {
       binding.run {
           tvThreadName.text = thread.chatThreadName?:""
           tvThreadMsgCount.text = context.getString(R.string.ease_thread_region_count,thread.messageCount.maxUnreadCount(context))
           val lastMsg = thread.lastMessage
           if (lastMsg == null){
               tvNoMsg.text = context.getString(R.string.ease_thread_region_no_msg)
               tvNoMsg.visibility = VISIBLE
               tvMsgContent.visibility = GONE
               return
           }
           tvNoMsg.visibility = GONE
           tvMsgContent.visibility = VISIBLE
           val conversationId = thread.parentId

           tvMsgUsername.text = lastMsg.from
           tvMsgContent.text = lastMsg.getMessageDigest(context).getEmojiText(context)

           EaseProfile.getGroupMember(conversationId,lastMsg.from)?.let {
//               ivUserIcon.loadAvatar()
               tvMsgUsername.text = it.getRemarkOrName()
           }
       }
    }

    override fun setThreadEventListener(listener: OnMessageChatThreadClickListener?) {
        this.listener = listener
    }

}