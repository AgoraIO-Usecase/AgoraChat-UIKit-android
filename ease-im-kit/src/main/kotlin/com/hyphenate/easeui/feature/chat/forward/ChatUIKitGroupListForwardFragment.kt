package com.hyphenate.easeui.feature.chat.forward

import android.os.Bundle
import android.view.View
import com.hyphenate.easeui.feature.chat.forward.adapter.ChatUIKitGroupListForwardAdapter
import com.hyphenate.easeui.feature.group.adapter.ChatUIKitGroupListAdapter
import com.hyphenate.easeui.feature.group.fragments.ChatUIKitGroupListFragment
import com.hyphenate.easeui.interfaces.OnForwardClickListener

class ChatUIKitGroupListForwardFragment: ChatUIKitGroupListFragment() {
    private var forwardClickListener: OnForwardClickListener? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.titleContact?.visibility = View.GONE
    }

    override fun getCustomAdapter(): ChatUIKitGroupListAdapter {
        return ChatUIKitGroupListForwardAdapter()
    }

    override fun initListener() {
        super.initListener()
        (this.adapter as? ChatUIKitGroupListForwardAdapter)
            ?.setOnForwardClickListener(forwardClickListener)
    }

    override fun onItemClick(view: View?, position: Int) {
        // do nothing
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }
}