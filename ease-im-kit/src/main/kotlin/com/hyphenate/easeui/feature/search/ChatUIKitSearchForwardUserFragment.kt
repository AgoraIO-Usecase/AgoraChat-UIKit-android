package com.hyphenate.easeui.feature.search

import android.os.Bundle
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.chat.forward.adapter.ChatUIKitContactForwardAdapter
import com.hyphenate.easeui.interfaces.OnForwardClickListener
import com.hyphenate.easeui.model.ChatUIKitUser

class ChatUIKitSearchForwardUserFragment: ChatUIKitSearchUserFragment() {
    private var forwardClickListener: OnForwardClickListener? = null
    private var sentUserList: List<String>? = null

    override fun initAdapter(): ChatUIKitBaseRecyclerViewAdapter<ChatUIKitUser> {
        return ChatUIKitContactForwardAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (mListAdapter is ChatUIKitContactForwardAdapter){
            (mListAdapter as ChatUIKitContactForwardAdapter).setSentUserList(sentUserList)
        }
    }

    override fun searchText(query: String) {
        super.searchText(query)
        if (mListAdapter is ChatUIKitContactForwardAdapter){
            (mListAdapter as ChatUIKitContactForwardAdapter).setSearchKey(query)
        }
    }

    override fun initListener() {
        super.initListener()
        if (mListAdapter is ChatUIKitContactForwardAdapter){
            (mListAdapter as ChatUIKitContactForwardAdapter).setOnForwardClickListener(forwardClickListener)
        }
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }

    fun setSentUserList(userList: List<String>?) {
        this.sentUserList = userList
    }

}