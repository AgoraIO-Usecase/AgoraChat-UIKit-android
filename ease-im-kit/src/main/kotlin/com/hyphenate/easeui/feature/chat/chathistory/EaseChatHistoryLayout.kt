package com.hyphenate.easeui.feature.chat.chathistory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.databinding.EaseLayoutChatHistoryBinding
import com.hyphenate.easeui.feature.chat.adapter.EaseMessagesAdapter
import com.hyphenate.easeui.feature.chat.config.EaseChatMessageItemConfig
import com.hyphenate.easeui.feature.chat.interfaces.IChatHistoryLayout
import com.hyphenate.easeui.feature.chat.interfaces.IChatHistoryResultView
import com.hyphenate.easeui.feature.chat.interfaces.OnCombineMessageDownloadCallback
import com.hyphenate.easeui.feature.chat.widgets.EaseChatMessageListLayout
import com.hyphenate.easeui.viewmodel.chathistory.EaseChatHistoryViewModel
import com.hyphenate.easeui.viewmodel.chathistory.IChatHistoryRequest
import kotlinx.coroutines.launch

open class EaseChatHistoryLayout @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): RelativeLayout(context, attrs, defStyleAttr), IChatHistoryLayout, IChatHistoryResultView {

    /**
     * The view model of the chat history view.
     */
    private var viewModel: IChatHistoryRequest? = null

    /**
     * The adapter of the chat history view.
     */
    private var messagesAdapter: EaseMessagesAdapter? = null

    /**
     * The callback of the combine message download or parse.
     */
    private var historyCallback: OnCombineMessageDownloadCallback? = null

    protected val binding: EaseLayoutChatHistoryBinding by lazy { EaseLayoutChatHistoryBinding.inflate(
        LayoutInflater.from(context), this, true) }

    init {
        initView()
    }

    private fun initView() {
        viewModel = if (context is AppCompatActivity) {
            ViewModelProvider(context)[EaseChatHistoryViewModel::class.java]
        } else {
            EaseChatHistoryViewModel()
        }
        viewModel?.attachView(this)

        // Set the history adapter for the EaseChatMessageListLayout
        messagesAdapter = EaseChatHistoryAdapter()
        messagesAdapter?.setItemConfig(EaseChatMessageItemConfig(context, null))
        binding.root.setMessagesAdapter(messagesAdapter)

        binding.root.refreshLayout?.let {
            it.setEnableLoadMore(false)
            it.setEnableRefresh(false)
        }
    }

    fun loadData(message: ChatMessage?) {
        if (message == null) {
            return
        }
        if (message.type != ChatMessageType.COMBINE) {
            return
        }
        viewModel?.downloadCombineMessage(message)
    }

    override fun setViewModel(viewModel: IChatHistoryRequest?) {
        this.viewModel = viewModel
        this.viewModel?.attachView(this)
    }

    override fun setMessagesAdapter(adapter: EaseMessagesAdapter?) {
        adapter?.let {
            this.messagesAdapter = adapter
            binding.root.setMessagesAdapter(messagesAdapter)
        }
    }

    override fun setOnCombineMessageDownloadCallback(callback: OnCombineMessageDownloadCallback?) {
        this.historyCallback = callback
    }

    override fun getChatMessageListLayout(): EaseChatMessageListLayout {
        return binding.root
    }

    override fun downloadCombinedMessagesSuccess(messageList: List<ChatMessage>) {
        context.mainScope().launch {
            messagesAdapter?.setData(messageList.toMutableList())
            historyCallback?.onDownloadSuccess(messageList)
        }

    }

    override fun downloadCombinedMessagesFail(error: Int, errorMsg: String?) {
        context.mainScope().launch {
            historyCallback?.onDownloadFail(error, errorMsg)
        }
    }


}