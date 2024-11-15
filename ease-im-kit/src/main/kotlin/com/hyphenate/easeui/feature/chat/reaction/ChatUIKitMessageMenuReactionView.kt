package com.hyphenate.easeui.feature.chat.reaction

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitReactionType
import com.hyphenate.easeui.feature.chat.reaction.adapter.ChatUIKitMessageReactionAdapter
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IChatReactionResultView
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReaction
import com.hyphenate.easeui.feature.chat.reaction.interfaces.OnChatUIKitReactionErrorListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.menu.chat.ChatUIKitChatMenuHelper
import com.hyphenate.easeui.model.ChatUIKitReaction
import com.hyphenate.easeui.viewmodel.reaction.ChatUIKitReactionViewModel
import com.hyphenate.easeui.viewmodel.reaction.IChatReactionRequest

class ChatUIKitMessageMenuReactionView @JvmOverloads constructor(
    private val context: Context,
    private val spanCount: Int = 7,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): RecyclerView(context, attrs, defStyleAttr), IMessageReaction, IChatReactionResultView {
    private lateinit var message: ChatMessage
    private var reactionAdapter: ChatUIKitMessageReactionAdapter = ChatUIKitMessageReactionAdapter()
    private var viewModel: IChatReactionRequest? = null
    private var moreReactionClickListener: OnClickListener? = null
    private var reactionErrorListener: OnChatUIKitReactionErrorListener? = null
    private var menuHelper: ChatUIKitChatMenuHelper? = null

    init {
        this.layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        this.adapter = reactionAdapter

        reactionAdapter.setOnItemClickListener(object : OnItemClickListener {

            override fun onItemClick(view: View?, position: Int) {
                reactionAdapter.getItem(position)?.let {
                    if (it.type == ChatUIKitReactionType.DEFAULT) {
                        it.identityCode?.let { identityCode ->
                            if (it.isAddedBySelf) {
                                removeReaction(identityCode)
                            } else {
                                addReaction(identityCode)
                            }
                        } ?: ChatLog.e("ChatUIKitMessageMenuReactionView", "identityCode is null")
                    } else if (it.type == ChatUIKitReactionType.ADD) {
                        showMoreReactions(view)
                    }
                }
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel?.detachView(message, true)
    }

    fun bindWithMenuHelper(helper: ChatUIKitChatMenuHelper?) {
        menuHelper = helper
    }

    override fun setupWithMessage(message: ChatMessage) {
        this.message = message
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context)[ChatUIKitReactionViewModel::class.java]
            } else {
                ChatUIKitReactionViewModel()
            }
        }
        viewModel?.attachView(message,this, true)
    }

    override fun setViewModel(viewModel: IChatReactionRequest?) {
        this.viewModel = viewModel
        viewModel?.attachView(message,this, true)
    }

    override fun showReaction() {
        viewModel?.getDefaultReactions(message)
    }

    override fun addReaction(reaction: String) {
        viewModel?.addReaction(message, reaction)
    }

    override fun removeReaction(reaction: String) {
        viewModel?.removeReaction(message, reaction)
    }

    override fun showMoreReactions(view: View?) {
        moreReactionClickListener?.onClick(view)
        ChatUIKitReactionsDialog().apply {
            setupWithMessage(message)
            setReactionErrorListener(reactionErrorListener)
            show((this@ChatUIKitMessageMenuReactionView.context as AppCompatActivity).supportFragmentManager, "ChatUIKitReactionsDialog")
        }
    }

    override fun setMoreReactionClickListener(listener: OnClickListener?) {
        super.setMoreReactionClickListener(listener)
        moreReactionClickListener = listener
    }

    override fun setReactionErrorListener(listener: OnChatUIKitReactionErrorListener?) {
        reactionErrorListener = listener
    }

    override fun getDefaultReactionsSuccess(reactions: List<ChatUIKitReaction>) {
        reactionAdapter.setData(reactions.toMutableList())
    }

    override fun addReactionSuccess(messageId: String) {
        super.addReactionSuccess(messageId)
        menuHelper?.dismiss()
        showReaction()
    }

    override fun addReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.addReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("ChatUIKitMessageMenuReactionView", "addReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    override fun removeReactionSuccess(messageId: String) {
        super.removeReactionSuccess(messageId)
        menuHelper?.dismiss()
        showReaction()
    }

    override fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.removeReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("ChatUIKitMessageMenuReactionView", "removeReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }
}