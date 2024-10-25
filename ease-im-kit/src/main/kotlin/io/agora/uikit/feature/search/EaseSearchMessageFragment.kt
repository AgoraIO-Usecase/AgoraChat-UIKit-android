package io.agora.uikit.feature.search

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.base.EaseBaseSearchFragment
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatSearchScope
import io.agora.uikit.feature.search.EaseSearchMessageFragment.Constant.KEY_SHOW_RIGHT_CANCEL
import io.agora.uikit.feature.search.adapter.EaseSearchMessageAdapter
import io.agora.uikit.feature.search.interfaces.IEaseSearchResultView
import io.agora.uikit.feature.search.interfaces.OnSearchMsgItemClickListener
import io.agora.uikit.viewmodel.search.EaseSearchViewModel
import io.agora.uikit.viewmodel.search.IEaseSearchRequest

class EaseSearchMessageFragment: EaseBaseSearchFragment<ChatMessage>(), IEaseSearchResultView {

    private var searchViewModel: IEaseSearchRequest? = null
    private var onCancelListener: OnCancelClickListener?=null
    private var itemClickListener: OnSearchMsgItemClickListener? = null
    private var data:MutableList<ChatMessage> = mutableListOf()
    private var keyWords:String? = null
    private var conversationId:String? = null

    override fun initViewModel() {
        super.initViewModel()
        searchViewModel = ViewModelProvider(context as AppCompatActivity)[EaseSearchViewModel::class.java]
        searchViewModel?.attachView(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.run {
            binding?.run {
                tvRight.visibility = if (getBoolean(KEY_SHOW_RIGHT_CANCEL, false)) View.VISIBLE else View.GONE
            }
        }
        binding?.etSearch?.hint = context?.resources?.getString(R.string.ease_chat_search_message)
        binding?.etSearch?.requestFocus()
    }

    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<ChatMessage> {
        return EaseSearchMessageAdapter()
    }

    override fun searchText(query: String) {
        this.keyWords = query
        if (!mListAdapter.data.isNullOrEmpty()){
            mListAdapter.clearData()
        }

        if (!TextUtils.isEmpty(query)) {
            if (mListAdapter is EaseSearchMessageAdapter){
                (mListAdapter as EaseSearchMessageAdapter).searchText(query)
            }
            searchData()
        }
    }

    override fun refreshData() {
        searchData()
    }

    private fun searchData(){
        if (!keyWords.isNullOrEmpty()){
            keyWords?.let {
                if (conversationId.isNullOrEmpty()){
                    searchViewModel?.searchMessage(
                        keywords = it,
                        chatScope = ChatSearchScope.CONTENT
                    )
                }else{
                    conversationId?.let { id->
                        searchViewModel?.searchMessage(
                            keywords = it,
                            conversationId= id,
                            chatScope = ChatSearchScope.CONTENT
                        )
                    }
                }
            }
        }
    }

    override fun onTvRightClick(view: View) {
        onCancelListener?.onCancelClick(view)
    }

    override fun searchSuccess(result: Any) {
        finishRefresh()
        if (result is MutableList<*>) {
            this.data = result as MutableList<ChatMessage>
            mListAdapter.setData(result)
        }
    }

    private fun setItemClickListener(itemClickListener: OnSearchMsgItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    private fun setOnCancelListener(onCancelListener: OnCancelClickListener?) {
        this.onCancelListener = onCancelListener
    }

    private fun setConversationId(conversationId: String?){
        this.conversationId = conversationId
    }

    override fun onItemClick(view: View?, position: Int) {
        super.onItemClick(view, position)
        mListAdapter.getItem(position)?.let {
            itemClickListener?.onSearchItemClick(view,position,it)
        }
    }

    interface OnCancelClickListener{
        fun onCancelClick(view:View)
    }

    class Builder {
        private val bundle: Bundle = Bundle()
        private var customFragment: EaseSearchMessageFragment? = null
        private var itemClickListener: OnSearchMsgItemClickListener? = null
        private var onCancelListener: OnCancelClickListener? = null
        private var conversationId: String? = null


        fun <T : EaseSearchMessageFragment?> setCustomFragment(fragment: T): Builder {
            customFragment = fragment
            return this
        }

        fun showRightCancel(showCancel: Boolean): Builder {
            bundle.putBoolean(KEY_SHOW_RIGHT_CANCEL, showCancel)
            return this
        }

        fun setItemClickListener(itemClickListener: OnSearchMsgItemClickListener): Builder {
            this.itemClickListener = itemClickListener
            return this
        }

        fun setOnCancelListener(onCancelListener: OnCancelClickListener?): Builder {
            this.onCancelListener = onCancelListener
            return this
        }

        fun setConversationId(conversationId:String?):Builder{
            this.conversationId = conversationId
            return this
        }

        fun build(): EaseSearchMessageFragment {
            val fragment =
                if (customFragment != null) customFragment else EaseSearchMessageFragment()
            fragment!!.arguments = bundle
            fragment.setConversationId(conversationId)
            fragment.setOnCancelListener(onCancelListener)
            fragment.setItemClickListener(itemClickListener)
            return fragment
        }
    }

    private object Constant {
        const val KEY_SHOW_RIGHT_CANCEL = "key_show_right_cancel"
    }
}