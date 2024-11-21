package com.hyphenate.easeui.feature.chat.forward

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseSheetFragmentDialog
import com.hyphenate.easeui.databinding.UikitDialogMessageForwardBinding
import com.hyphenate.easeui.feature.chat.forward.adapter.ChatUIKitMessageForwardPagerAdapter
import com.hyphenate.easeui.interfaces.OnForwardClickListener
import com.hyphenate.easeui.model.ChatUIKitPager

class ChatUIKitMessageForwardDialogFragment: ChatUIKitBaseSheetFragmentDialog<UikitDialogMessageForwardBinding>() {
    private var forwardClickListener: OnForwardClickListener? = null

    private val pagerAdapter by lazy {
        ChatUIKitMessageForwardPagerAdapter(childFragmentManager, lifecycle)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): UikitDialogMessageForwardBinding? {
        return UikitDialogMessageForwardBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding?.run {
            titleBar.setTitle(R.string.uikit_message_forward)
            vpForward.adapter = pagerAdapter
            TabLayoutMediator(tlForward, vpForward) { tab, position ->
                tab.text = pagerAdapter.getData()?.get(position)?.title
            }.attach()
        }
    }

    override fun initListener() {
        super.initListener()
        binding?.titleBar?.setNavigationOnClickListener{
            dismiss()
        }
    }

    override fun initData() {
        super.initData()
        val contact = ChatUIKitContactForwardFragmentEvent()
        val group = ChatUIKitGroupListForwardFragment()
        contact.setOnForwardClickListener(forwardClickListener)
        group.setOnForwardClickListener(forwardClickListener)
        val list = mutableListOf(ChatUIKitPager(getString(R.string.uikit_message_forward_to_contact), contact)
            , ChatUIKitPager(getString(R.string.uikit_message_forward_to_group), group))
        pagerAdapter.setData(list)
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener){
        this.forwardClickListener = listener
    }

    override fun showExpandedState(): Boolean {
        return true
    }
}