package com.hyphenate.easeui.feature.chat.reaction

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.extensions.isSend
import com.hyphenate.easeui.databinding.EaseItemMessageReactionBinding
import com.hyphenate.easeui.databinding.EaseWidgetChatMessageReactionBinding
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IChatReactionResultView
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReaction
import com.hyphenate.easeui.feature.chat.reaction.interfaces.OnEaseChatReactionErrorListener
import com.hyphenate.easeui.model.EaseReaction
import com.hyphenate.easeui.viewmodel.reaction.EaseChatReactionViewModel
import com.hyphenate.easeui.viewmodel.reaction.IChatReactionRequest
import com.hyphenate.easeui.widget.EaseFlowLayout

class EaseChatMessageReactionView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), IMessageReaction, IChatReactionResultView {

    private var reactionErrorListener: OnEaseChatReactionErrorListener? = null
    private val binding = EaseWidgetChatMessageReactionBinding.inflate(
                                LayoutInflater.from(context), this, true
                            )

    private lateinit var message: ChatMessage

    private var viewModel: IChatReactionRequest? = null
    private val adapter = EaseReactionAdapter()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        binding.flowReactionList.setAdapter(adapter)
        adapter.setOnItemClickListener { view, i ->
            val reaction = adapter.getData()?.get(i)
            reaction?.let {
                it.identityCode?.run {
                    if (it.isAddedBySelf) {
                        removeReaction(this)
                    } else {
                        addReaction(this)
                    }
                }
            }
        }
        binding.ivIcon.setOnClickListener {
            showMoreReactions(it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel?.detachView(message)
    }

    override fun setupWithMessage(message: ChatMessage) {
        this.message = message
        setLayout(message.isSend())
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context)[EaseChatReactionViewModel::class.java]
            } else {
                EaseChatReactionViewModel()
            }
        }
        viewModel?.attachView(message,this)
    }

    private fun setLayout(isSender: Boolean) {
        val marginToParent = context.resources.getDimensionPixelSize(R.dimen.ease_chat_message_reaction_margin_parent)
        val marginToBubble = context.resources.getDimensionPixelSize(R.dimen.ease_chat_padding_bubble)
        if (isSender) {
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginToParent, 0, marginToBubble, 0)
            }
        } else {
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginToBubble, 0, marginToParent, 0)
            }
        }
    }

    override fun setViewModel(viewModel: IChatReactionRequest?) {
        this.viewModel = viewModel
        viewModel?.attachView(message, this)
    }

    override fun showReaction() {
        viewModel?.getMessageReactions(message)
    }

    override fun addReaction(reaction: String) {
        viewModel?.addReaction(message, reaction)
    }

    override fun removeReaction(reaction: String) {
        viewModel?.removeReaction(message, reaction)
    }

    override fun showMoreReactions(view: View?) {
        EaseMessageReactionsDialog(message).apply {
            show((this@EaseChatMessageReactionView.context as AppCompatActivity).supportFragmentManager, "EaseMessageReactionsDialog")
        }
    }

    override fun setReactionErrorListener(listener: OnEaseChatReactionErrorListener?) {
        reactionErrorListener = listener
    }

    override fun getMessageReactionSuccess(reactions: List<EaseReaction>) {
        adapter.setData(reactions.toMutableList())
    }

    override fun addReactionSuccess(messageId: String) {
        showReaction()
    }

    override fun addReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        ChatLog.e("EaseChatMessageReactionView", "addReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    override fun removeReactionSuccess(messageId: String) {
        showReaction()
    }

    override fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.removeReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("EaseChatMessageReactionView", "removeReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    private class EaseReactionAdapter: EaseFlowLayout.EaseFlowAdapter<EaseReaction, EaseReactionAdapter.ViewHolder>() {
        class ViewHolder(val binding: EaseItemMessageReactionBinding): EaseFlowLayout.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(EaseItemMessageReactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val reaction = getData()?.get(position)
            reaction?.let {
                with(holder.binding) {
                    ivEmoji.setImageResource(reaction.icon)
                    tvEmojiCount.text = reaction.count.toString()
                    root.isSelected = reaction.isAddedBySelf
                }
            }
        }

    }
}