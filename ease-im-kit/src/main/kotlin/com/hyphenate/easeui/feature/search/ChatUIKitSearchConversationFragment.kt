package com.hyphenate.easeui.feature.search

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.UIKitChatActivity
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.base.ChatUIKitBaseSearchFragment
import com.hyphenate.easeui.feature.search.adapter.ChatUIKitSearchConversationAdapter
import com.hyphenate.easeui.feature.search.interfaces.IUIKitSearchResultView
import com.hyphenate.easeui.model.ChatUIKitConversation
import com.hyphenate.easeui.model.getChatType
import com.hyphenate.easeui.viewmodel.search.ChatUIKitSearchViewModel
import com.hyphenate.easeui.viewmodel.search.IUIKitSearchRequest

class ChatUIKitSearchConversationFragment: ChatUIKitBaseSearchFragment<ChatUIKitConversation>(),
    IUIKitSearchResultView {
    private var query:String? = null
    private var searchViewModel: IUIKitSearchRequest? = null

    override fun initViewModel() {
        super.initViewModel()
        searchViewModel = ViewModelProvider(context as AppCompatActivity)[ChatUIKitSearchViewModel::class.java]
        searchViewModel?.attachView(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.etSearch?.hint = getString(R.string.uikit_search_conversation_hint)
        binding?.etSearch?.requestFocus()
    }
    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): ChatUIKitBaseRecyclerViewAdapter<ChatUIKitConversation> {
        return ChatUIKitSearchConversationAdapter()
    }

    override fun searchText(query: String) {
        this.query = query
        if (!mListAdapter.data.isNullOrEmpty()){
            mListAdapter.clearData()
        }

        if (!TextUtils.isEmpty(query)) {
            if (mListAdapter is ChatUIKitSearchConversationAdapter){
                (mListAdapter as ChatUIKitSearchConversationAdapter).searchText(query)
            }
            query.let { content->
                searchViewModel?.searchConversation(content)
            }
        }
    }

    override fun refreshData() {
        if (!query.isNullOrEmpty()){
            query?.let {
                searchViewModel?.searchConversation(it)
            }
        }
    }

    override fun onTvRightClick(view: View) {
        mContext.finish()
    }

    override fun searchSuccess(result: Any) {
        finishRefresh()
        if (result is MutableList<*>) {
            mListAdapter.setData(result as MutableList<ChatUIKitConversation>)
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        super.onItemClick(view, position)
        mListAdapter.getItem(position)?.let {
            UIKitChatActivity.actionStart(mContext,it.conversationId, it.getChatType())
        }
    }
}