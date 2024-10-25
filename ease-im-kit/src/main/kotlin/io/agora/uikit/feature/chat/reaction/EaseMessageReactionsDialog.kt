package io.agora.uikit.feature.chat.reaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.agora.uikit.base.EaseBaseSheetFragmentDialog
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.bus.EaseFlowBus
import io.agora.uikit.common.extensions.dpToPx
import io.agora.uikit.databinding.EaseDialogMessageReactionsBinding
import io.agora.uikit.databinding.EaseItemMessageReactionTabBinding
import io.agora.uikit.feature.chat.reaction.adapter.EaseReactionUserPagerAdapter
import io.agora.uikit.feature.chat.reaction.interfaces.IMessageReactionList
import io.agora.uikit.feature.chat.reaction.interfaces.IMessageReactionListResultView
import io.agora.uikit.model.EaseEvent
import io.agora.uikit.model.EaseReaction
import io.agora.uikit.viewmodel.reaction.EaseMessageReactionListViewModel
import io.agora.uikit.viewmodel.reaction.IMessageReactionListRequest

class EaseMessageReactionsDialog(
    private val message: ChatMessage
): EaseBaseSheetFragmentDialog<EaseDialogMessageReactionsBinding>(), IMessageReactionList, IMessageReactionListResultView {

    private val pagerAdapter by lazy {
        EaseReactionUserPagerAdapter(message.msgId,
            childFragmentManager, lifecycle)
    }
    private var viewModel: IMessageReactionListRequest? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseDialogMessageReactionsBinding? {
        return EaseDialogMessageReactionsBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding?.run {
            vpReactions.adapter = pagerAdapter
            TabLayoutMediator(tlReactions, vpReactions) { tab, position ->
                // customize tab view
                val binding = EaseItemMessageReactionTabBinding.inflate(LayoutInflater.from(context), null, false)
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
            viewModel = ViewModelProvider(this)[EaseMessageReactionListViewModel::class.java]
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
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name).register(this) {
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
        reactions: List<EaseReaction>?
    ) {
        pagerAdapter.setData(reactions)
    }

    override fun fetchReactionListFail(messageId: String, errorCode: Int, errorMsg: String?) {
        ChatLog.e("EaseMessageReactionsDialog", "fetchReactionListFail errorCode: $errorCode, errorMsg: $errorMsg")
    }
}