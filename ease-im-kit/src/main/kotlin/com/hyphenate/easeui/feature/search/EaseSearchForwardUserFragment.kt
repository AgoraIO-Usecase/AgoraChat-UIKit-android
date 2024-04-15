package com.hyphenate.easeui.feature.search

import android.os.Bundle
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.chat.forward.adapter.EaseContactForwardAdapter
import com.hyphenate.easeui.interfaces.OnForwardClickListener
import com.hyphenate.easeui.model.EaseUser

class EaseSearchForwardUserFragment: EaseSearchUserFragment() {
    private var forwardClickListener: OnForwardClickListener? = null
    private var sentUserList: List<String>? = null

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseUser> {
        return EaseContactForwardAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (mListAdapter is EaseContactForwardAdapter){
            (mListAdapter as EaseContactForwardAdapter).setSentUserList(sentUserList)
        }
    }

    override fun searchText(query: String) {
        super.searchText(query)
        if (mListAdapter is EaseContactForwardAdapter){
            (mListAdapter as EaseContactForwardAdapter).setSearchKey(query)
        }
    }

    override fun initListener() {
        super.initListener()
        if (mListAdapter is EaseContactForwardAdapter){
            (mListAdapter as EaseContactForwardAdapter).setOnForwardClickListener(forwardClickListener)
        }
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }

    fun setSentUserList(userList: List<String>?) {
        this.sentUserList = userList
    }

}