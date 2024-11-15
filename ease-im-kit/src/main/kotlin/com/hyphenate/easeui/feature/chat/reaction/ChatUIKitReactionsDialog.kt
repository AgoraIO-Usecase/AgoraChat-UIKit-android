package com.hyphenate.easeui.feature.chat.reaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hyphenate.easeui.base.ChatUIKitBaseSheetFragmentDialog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.databinding.UikitDialogChatReactionsBinding
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitReactionType
import com.hyphenate.easeui.feature.chat.reaction.adapter.ChatUIKitMessageReactionAdapter
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IChatReactionResultView
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReaction
import com.hyphenate.easeui.feature.chat.reaction.interfaces.OnChatUIKitReactionErrorListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.model.ChatUIKitReaction
import com.hyphenate.easeui.viewmodel.reaction.ChatUIKitReactionViewModel
import com.hyphenate.easeui.viewmodel.reaction.IChatReactionRequest
import com.hyphenate.easeui.common.ChatLog

class ChatUIKitReactionsDialog: ChatUIKitBaseSheetFragmentDialog<UikitDialogChatReactionsBinding>(), IMessageReaction,
    IChatReactionResultView {
    private var spanCount: Int = 7
    private lateinit var message: ChatMessage
    private var reactionAdapter: ChatUIKitMessageReactionAdapter = ChatUIKitMessageReactionAdapter()
    private var viewModel: IChatReactionRequest? = null
    private var reactionErrorListener: OnChatUIKitReactionErrorListener? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): UikitDialogChatReactionsBinding? {
        return UikitDialogChatReactionsBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding?.rvReactionList?.layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        binding?.rvReactionList?.adapter = reactionAdapter
    }

    override fun initListener() {
        super.initListener()
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
                        } ?: ChatLog.e("ChatUIKitReactionsDialog", "identityCode is null")
                    }
                }
            }
        })
    }

    override fun initData() {
        super.initData()
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context as AppCompatActivity)[ChatUIKitReactionViewModel::class.java]
            } else {
                ChatUIKitReactionViewModel()
            }
        }
        viewModel?.attachView(message,this, true)

        showReaction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel?.detachView(message, true)
    }

    override fun setupWithMessage(message: ChatMessage) {
        this.message = message
    }

    override fun setViewModel(viewModel: IChatReactionRequest?) {
        this.viewModel = viewModel
    }

    override fun showReaction() {
        viewModel?.getAllChatReactions(message)
    }

    override fun addReaction(reaction: String) {
        viewModel?.addReaction(message, reaction)
    }

    override fun removeReaction(reaction: String) {
        viewModel?.removeReaction(message, reaction)
    }

    override fun setReactionErrorListener(listener: OnChatUIKitReactionErrorListener?) {
        reactionErrorListener = listener
    }

    override fun getAllChatReactionsSuccess(reactions: List<ChatUIKitReaction>) {
        super.getAllChatReactionsSuccess(reactions)
        reactionAdapter.setData(reactions.toMutableList())
    }

    override fun addReactionSuccess(messageId: String) {
        super.addReactionSuccess(messageId)
        dismiss()
        showReaction()
    }

    override fun addReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.addReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("ChatUIKitReactionsDialog", "addReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    override fun removeReactionSuccess(messageId: String) {
        super.removeReactionSuccess(messageId)
        dismiss()
        showReaction()
    }

    override fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.removeReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("ChatUIKitReactionsDialog", "removeReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }
}