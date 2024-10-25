package io.agora.uikit.feature.chat.reaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import io.agora.uikit.base.EaseBaseSheetFragmentDialog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.databinding.EaseDialogChatReactionsBinding
import io.agora.uikit.feature.chat.enums.EaseReactionType
import io.agora.uikit.feature.chat.reaction.adapter.EaseMessageReactionAdapter
import io.agora.uikit.feature.chat.reaction.interfaces.IChatReactionResultView
import io.agora.uikit.feature.chat.reaction.interfaces.IMessageReaction
import io.agora.uikit.feature.chat.reaction.interfaces.OnEaseChatReactionErrorListener
import io.agora.uikit.interfaces.OnItemClickListener
import io.agora.uikit.model.EaseReaction
import io.agora.uikit.viewmodel.reaction.EaseChatReactionViewModel
import io.agora.uikit.viewmodel.reaction.IChatReactionRequest
import io.agora.uikit.common.ChatLog

class EaseChatReactionsDialog: EaseBaseSheetFragmentDialog<EaseDialogChatReactionsBinding>(), IMessageReaction,
    IChatReactionResultView {
    private var spanCount: Int = 7
    private lateinit var message: ChatMessage
    private var reactionAdapter: EaseMessageReactionAdapter = EaseMessageReactionAdapter()
    private var viewModel: IChatReactionRequest? = null
    private var reactionErrorListener: OnEaseChatReactionErrorListener? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseDialogChatReactionsBinding? {
        return EaseDialogChatReactionsBinding.inflate(inflater, container, false)
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
                    if (it.type == EaseReactionType.DEFAULT) {
                        it.identityCode?.let { identityCode ->
                            if (it.isAddedBySelf) {
                                removeReaction(identityCode)
                            } else {
                                addReaction(identityCode)
                            }
                        } ?: ChatLog.e("EaseChatReactionsDialog", "identityCode is null")
                    }
                }
            }
        })
    }

    override fun initData() {
        super.initData()
        if (viewModel == null) {
            viewModel = if (context is AppCompatActivity) {
                ViewModelProvider(context as AppCompatActivity)[EaseChatReactionViewModel::class.java]
            } else {
                EaseChatReactionViewModel()
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

    override fun setReactionErrorListener(listener: OnEaseChatReactionErrorListener?) {
        reactionErrorListener = listener
    }

    override fun getAllChatReactionsSuccess(reactions: List<EaseReaction>) {
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
        ChatLog.e("EaseChatReactionsDialog", "addReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }

    override fun removeReactionSuccess(messageId: String) {
        super.removeReactionSuccess(messageId)
        dismiss()
        showReaction()
    }

    override fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {
        super.removeReactionFail(messageId, errorCode, errorMsg)
        ChatLog.e("EaseChatReactionsDialog", "removeReactionFail: $errorCode $errorMsg")
        reactionErrorListener?.onError(messageId, errorCode, errorMsg)
    }
}