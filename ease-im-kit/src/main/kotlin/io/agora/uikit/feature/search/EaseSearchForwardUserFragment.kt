package io.agora.uikit.feature.search

import android.os.Bundle
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.feature.chat.forward.adapter.EaseContactForwardAdapter
import io.agora.uikit.interfaces.OnForwardClickListener
import io.agora.uikit.model.EaseUser

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