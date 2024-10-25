package io.agora.uikit.widget.chatrow

import android.content.Context
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.feature.thread.EaseChatThreadActivity
import io.agora.uikit.model.EaseProfile

class EaseChatRowThreadNotify @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
): EaseChatRow(context, attrs, defStyleAttr, isSender)  {
    private val contentView: TextView? by lazy { findViewById(R.id.text_content) }

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_thread_notify, this)
    }

    override fun onSetUpView() {
        message?.run {
            if (ext().containsKey(EaseConstant.THREAD_TOPIC_MESSAGE_ID)){
                var creator:String? = from
                var content = ""
                EaseProfile.getGroupMember(conversationId(),from)?.let { profile ->
                    creator = profile.getRemarkOrName()
                }
                val coloredText = context.getString(R.string.ease_thread_notify_detail)
                val topicMsgId = getStringAttribute(EaseConstant.THREAD_TOPIC_MESSAGE_ID)
                val topicMsg = ChatClient.getInstance().chatManager().getMessage(topicMsgId)
                topicMsg?.let {
                    val threadName = it.chatThread?.chatThreadName?:""
                    content = "${context.getString(R.string.ease_thread_notify_content,creator,threadName)} $coloredText"
                    val spannableString = SpannableString(content)
                    val startIndex = content.indexOf(coloredText)
                    val endIndex = startIndex + coloredText.length
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            EaseIM.getConfig()?.chatConfig?.enableChatThreadMessage?.let {
                                if (!it) {
                                    return
                                }
                            }
                            it.chatThread?.let { thread->
                                EaseChatThreadActivity.actionStart(
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