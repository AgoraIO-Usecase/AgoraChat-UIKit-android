package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.feature.thread.ChatUIKitThreadActivity
import com.hyphenate.easeui.model.ChatUIKitProfile

class ChatUIKitRowThreadNotify @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
): ChatUIKitRow(context, attrs, defStyleAttr, isSender)  {
    private val contentView: TextView? by lazy { findViewById(R.id.text_content) }

    override fun onInflateView() {
        inflater.inflate(R.layout.uikit_row_thread_notify, this)
    }

    override fun onSetUpView() {
        message?.run {
            if (ext().containsKey(ChatUIKitConstant.THREAD_TOPIC_MESSAGE_ID)){
                var creator:String? = from
                var content = ""
                ChatUIKitProfile.getGroupMember(conversationId(),from)?.let { profile ->
                    creator = profile.getRemarkOrName()
                }
                val coloredText = context.getString(R.string.uikit_thread_notify_detail)
                val topicMsgId = getStringAttribute(ChatUIKitConstant.THREAD_TOPIC_MESSAGE_ID)
                val topicMsg = ChatClient.getInstance().chatManager().getMessage(topicMsgId)
                topicMsg?.let {
                    val threadName = it.chatThread?.chatThreadName?:""
                    content = "${context.getString(R.string.uikit_thread_notify_content,creator,threadName)} $coloredText"
                    val spannableString = SpannableString(content)
                    val startIndex = content.indexOf(coloredText)
                    val endIndex = startIndex + coloredText.length
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            ChatUIKitClient.getConfig()?.chatConfig?.enableChatThreadMessage?.let {
                                if (!it) {
                                    return
                                }
                            }
                            it.chatThread?.let { thread->
                                ChatUIKitThreadActivity.actionStart(
                                    context = context,
                                    conversationId = thread.parentId,
                                    threadId = thread.chatThreadId,
                                    topicMsgId = topicMsgId,
                                )
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = resources.getColor(R.color.ease_color_primary)
                            ds.isUnderlineText = false
                        }
                    }
                    spannableString.setSpan(clickableSpan, startIndex, endIndex, 0)
                    contentView?.movementMethod = LinkMovementMethod.getInstance()
                    contentView?.text = spannableString
                }
            }
        }
    }
}