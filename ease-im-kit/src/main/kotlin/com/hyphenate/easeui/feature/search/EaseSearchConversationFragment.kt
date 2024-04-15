package com.hyphenate.easeui.feature.search

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.base.EaseBaseSearchFragment
import com.hyphenate.easeui.feature.search.adapter.EaseSearchConversationAdapter
import com.hyphenate.easeui.feature.search.interfaces.IEaseSearchResultView
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.getChatType
import com.hyphenate.easeui.viewmodel.search.EaseSearchViewModel
import com.hyphenate.easeui.viewmodel.search.IEaseSearchRequest

class EaseSearchConversationFragment: EaseBaseSearchFragment<EaseConversation>(),
    IEaseSearchResultView {
    private var query:String? = null
    private var searchViewModel: IEaseSearchRequest? = null

    override fun initViewModel() {
        super.initViewModel()
        searchViewModel = ViewModelProvider(context as AppCompatActivity)[EaseSearchViewModel::class.java]
        searchViewModel?.attachView(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.etSearch?.hint = getString(R.string.ease_search_conversation_hint)
        binding?.etSearch?.requestFocus()
    }
    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseConversation> {
        return EaseSearchConversationAdapter()
    }

    override fun searchText(query: String) {
        this.query = query
        if (!mListAdapter.data.isNullOrEmpty()){
            mListAdapter.clearData()
        }

        if (!TextUtils.isEmpty(query)) {
            if (mListAdapter is EaseSearchConversationAdapter){
                (mListAdapter as EaseSearchConversationAdapter).searchText(query)
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
            mListAdapter.setData(result as MutableList<EaseConversation>)
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        super.onItemClick(view, position)
        mListAdapter.getItem(position)?.let {
            EaseChatActivity.actionStart(mContext,it.conversationId, it.getChatType())
        }
    }
}