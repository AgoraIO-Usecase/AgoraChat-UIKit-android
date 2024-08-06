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
import com.hyphenate.easeui.feature.chat.enums.EaseReactionType
import com.hyphenate.easeui.feature.chat.reaction.adapter.EaseMessageReactionAdapter
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IChatReactionResultView
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReaction
import com.hyphenate.easeui.feature.chat.reaction.interfaces.OnEaseChatReactionErrorListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.menu.chat.EaseChatMenuHelper
import com.hyphenate.easeui.model.EaseReaction
import com.hyphenate.easeui.viewmodel.reaction.EaseChatReactionViewModel
import com.hyphenate.easeui.viewmodel.reaction.IChatReactionRequest

class EaseMessageMenuReactionView @JvmOverloads constructor(
    private val context: Context,
    private val spanCount: Int = 7,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): RecyclerView(context, attrs, defStyleAttr), IMessageReaction, IChatReactionResultView {
    private lateinit var message: ChatMessage
    private var reactionAdapter: EaseMessageReactionAdapter = EaseMessageReactionAdapter()
    private var viewModel: IChatReactionRequest? = null
    private var moreReactionClickListener: OnClickListener? = null
    private var reactionErrorListener: OnEaseChatReactionErrorListener? = null
    private var menuHelper: EaseChatMenuHelper? = null

    init {
        this.layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        this.adapter = reactionAdapter

        reactionAdapter.setOnItemClickListener(object : OnItemClickListener {

            override fun onItemClick(view: View?, position: Int) {
                reactionAdapter.getItem(position)?.let {
                    if (it.type == EaseReactionType.DEFAULT) {
                        it.identityCode?.let { identityCode ->
                            if (it.isAddedBySelf) {
                                removeReaction(identityCode)
                            } else {
                                addReaction(identityCode)
                            }
                        } ?: ChatLog.e("EaseMessageMenuReactionView", "identityCode is null")
                    } else if (it.type == EaseReactionType.ADD) {
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

    fun bindWithMenuHelper(helper: EaseChatMenuHelper?) {
        menuHelper = helper
    }

    override fun setupWithMessage(message: ChatMessage) {
        this.message = message
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context)[EaseChatReactionViewModel::class.java]
            } else {
                EaseChatReactionViewModel()
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
        EaseChatReactionsDialog().apply {
            setupWithMessage(message)
            setReactionErrorListener(reactionErrorListener)
            show((this@EaseMessageMenuReactionView.context as AppCompatActivity).supportFragmentManager, "EaseChatReactionsDialog")
        }
    }

    override fun setMoreReactionClickListener(listener: OnClickListener?) {
        super.setMoreReactionClickListener(listener)
        moreReactionClickListener = listener
    }

    override fun setReactionErrorListener(listener: OnEaseChatReactionErrorListener?) {
        reactionErrorListener = listener
    }

    override fun getDefaultReactionsSuccess(reactions: List<EaseReaction>) {
        reactionAdapter.setData(reactions.toMutableList())
    }

    override fun addReactionSuccess(messageId: String) {
        super.addReactionSuccess(messageId)
        menuHelper?.dismiss()
        showReaction()
    }

    override fun addReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.addReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("EaseMessageMenuReactionView", "addReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    override fun removeReactionSuccess(messageId: String) {
        super.removeReactionSuccess(messageId)
        menuHelper?.dismiss()
        showReaction()
    }

    override fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.removeReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("EaseMessageMenuReactionView", "removeReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }
}