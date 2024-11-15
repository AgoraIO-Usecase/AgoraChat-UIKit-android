package com.hyphenate.easeui.feature.chat.reaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hyphenate.easeui.base.ChatUIKitBaseSheetFragmentDialog
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.bus.ChatUIKitFlowBus
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.databinding.UikitDialogMessageReactionsBinding
import com.hyphenate.easeui.databinding.UikitItemMessageReactionTabBinding
import com.hyphenate.easeui.feature.chat.reaction.adapter.ChatUIKitReactionUserPagerAdapter
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReactionList
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReactionListResultView
import com.hyphenate.easeui.model.ChatUIKitEvent
import com.hyphenate.easeui.model.ChatUIKitReaction
import com.hyphenate.easeui.viewmodel.reaction.ChatUIKitReactionListViewModel
import com.hyphenate.easeui.viewmodel.reaction.IMessageReactionListRequest

class ChatUIKitMessageReactionsDialog(
    private val message: ChatMessage
): ChatUIKitBaseSheetFragmentDialog<UikitDialogMessageReactionsBinding>(), IMessageReactionList, IMessageReactionListResultView {

    private val pagerAdapter by lazy {
        ChatUIKitReactionUserPagerAdapter(message.msgId,
            childFragmentManager, lifecycle)
    }
    private var viewModel: IMessageReactionListRequest? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): UikitDialogMessageReactionsBinding? {
        return UikitDialogMessageReactionsBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding?.run {
            vpReactions.adapter = pagerAdapter
            TabLayoutMediator(tlReactions, vpReactions) { tab, position ->
                // customize tab view
                val binding = UikitItemMessageReactionTabBinding.inflate(LayoutInflater.from(context), null, false)
                val item = binding.root
                item.layoutParams = MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                pagerAdapter.getData()?.get(position)?.let {
                    if (it.icon == 0) {
                        binding.tvEmoji.visibility = View.VISIBLE
                        binding.ivEmoji.visibility = View.GONE
                        binding.tvEmoji.text = it.emojiText
                    } else {
                        binding.tvEmoji.visibility = View.GONE
                        binding.ivEmoji.visibility = View.VISIBLE
                        binding.ivEmoji.setImageResource(it.icon)
                    }
                    binding.tvEmojiCount.text = it.count.toString()
                }
                // Set tab view padding according to position
                setTabPadding(tab.view, position)
                tab.setCustomView(item)
            }.attach()

            tlReactions.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    setTabWidth(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    setTabWidth(tab)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            tlReactions.tabMode = TabLayout.MODE_SCROLLABLE
        }
        // set behavior
        updateState(BottomSheetBehavior.STATE_COLLAPSED)
        setPeekHeight(getHeight() / 2)


        if (viewModel == null) {
            viewModel = ViewModelProvider(this)[ChatUIKitReactionListViewModel::class.java]
        }
        viewModel?.attachView(this)
    }

    private fun setTabWidth(tab: TabLayout.Tab?) {
        tab?.let {
            (it.customView?.layoutParams as? MarginLayoutParams)?.let { params ->
                val layoutParams = it.view.layoutParams as MarginLayoutParams
                layoutParams.width = params.width
                setTabPadding(it.view, it.position)
            }
        }
    }

    private fun setTabPadding(tabView: View, position: Int) {
        if (position == 0) {
            tabView.setPadding(16.dpToPx(requireContext()), 0, 4.dpToPx(requireContext()), 0)
        } else {
            tabView.setPadding(4.dpToPx(requireContext()), 0, 4.dpToPx(requireContext()), 0)
        }
    }

    override fun initData() {
        super.initData()
        fetchReactionList(message)
        initEvent()
    }

    override fun onStart() {
        super.onStart()
        val layoutParams = requireView().layoutParams
        layoutParams.height = getHeight()
    }

    private fun initEvent() {
        ChatUIKitFlowBus.with<ChatUIKitEvent>(ChatUIKitEvent.EVENT.REMOVE.name).register(this) {
            if (it.isReactionChange) {
                dismiss()
            }
        }
    }

    override fun setViewModel(viewModel: IMessageReactionListRequest?) {
        this.viewModel = viewModel
        viewModel?.attachView(this)
    }

    override fun fetchReactionList(message: ChatMessage) {
        this.viewModel?.fetchReactionList(message)
    }

    override fun fetchReactionListSuccess(
        messageId: String,
        reactions: List<ChatUIKitReaction>?
    ) {
        pagerAdapter.setData(reactions)
    }

    override fun fetchReactionListFail(messageId: String, errorCode: Int, errorMsg: String?) {
        ChatLog.e("ChatUIKitMessageReactionsDialog", "fetchReactionListFail errorCode: $errorCode, errorMsg: $errorMsg")
    }
}