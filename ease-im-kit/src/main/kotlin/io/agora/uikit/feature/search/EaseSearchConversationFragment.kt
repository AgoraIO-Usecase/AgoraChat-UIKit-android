package io.agora.uikit.feature.search

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.agora.uikit.R
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.base.EaseBaseSearchFragment
import io.agora.uikit.feature.search.adapter.EaseSearchConversationAdapter
import io.agora.uikit.feature.search.interfaces.IEaseSearchResultView
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.model.getChatType
import io.agora.uikit.viewmodel.search.EaseSearchViewModel
import io.agora.uikit.viewmodel.search.IEaseSearchRequest

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