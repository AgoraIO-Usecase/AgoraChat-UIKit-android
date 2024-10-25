package io.agora.uikit.feature.search

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.base.EaseBaseSearchFragment
import io.agora.uikit.feature.search.adapter.EaseSearchUserAdapter
import io.agora.uikit.feature.search.interfaces.IEaseSearchResultView
import io.agora.uikit.feature.search.interfaces.OnSearchUserItemClickListener
import io.agora.uikit.model.EaseUser
import io.agora.uikit.viewmodel.search.EaseSearchViewModel
import io.agora.uikit.viewmodel.search.IEaseSearchRequest

open class EaseSearchUserFragment: EaseBaseSearchFragment<EaseUser>(),IEaseSearchResultView {

    private var searchViewModel: IEaseSearchRequest? = null
    private var keyWords:String? = null
    private var data:MutableList<EaseUser> = mutableListOf()
    private var onCancelListener:OnCancelClickListener?=null
    private var itemClickListener: OnSearchUserItemClickListener? = null
    private var isSearchBlockUser:Boolean = false

    override fun initViewModel() {
        super.initViewModel()
        searchViewModel = ViewModelProvider(context as AppCompatActivity)[EaseSearchViewModel::class.java]
        searchViewModel?.attachView(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        arguments?.run {
            isSearchBlockUser = this.getBoolean(Constant.KEY_IS_SEARCH_BLOCK,false)
            binding?.run {
                tvRight.visibility = if (getBoolean(Constant.KEY_SHOW_RIGHT_CANCEL, false)) View.VISIBLE else View.GONE
            }
        }
        binding?.etSearch?.requestFocus()
    }

    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseUser> {
        return EaseSearchUserAdapter()
    }

    override fun searchText(query: String) {
        this.keyWords = query
        if (!mListAdapter.data.isNullOrEmpty()){
            mListAdapter.clearData()
        }

        if (!TextUtils.isEmpty(query)) {
            if (mListAdapter is EaseSearchUserAdapter){
                (mListAdapter as EaseSearchUserAdapter).searchText(query)
            }
            query.let { content->
                if (isSearchBlockUser){
                    searchViewModel?.searchBlockUser(content)
                }else{
                    searchViewModel?.searchUser(content)
                }
            }
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        super.onItemClick(view, position)
        mListAdapter.getItem(position)?.let {
            itemClickListener?.onSearchItemClick(view,position,it)
        }
    }

    override fun refreshData() {
        if (!keyWords.isNullOrEmpty()){
            keyWords?.let {
                searchViewModel?.searchUser(it)
            }
        }
    }

    override fun onTvRightClick(view: View) {
        onCancelListener?.onCancelClick(view)
    }

    override fun searchSuccess(result: Any) {
        finishRefresh()
        if (result is MutableList<*>) {
            this.data = result as MutableList<EaseUser>
            mListAdapter.setData(result)
        }
    }

    override fun searchBlockUserSuccess(result: Any) {
        finishRefresh()
        if (result is MutableList<*>) {
            this.data = result as MutableList<EaseUser>
            mListAdapter.setData(result)
        }
    }

    interface OnCancelClickListener{
        fun onCancelClick(view:View)
    }

    private fun setItemClickListener(itemClickListener: OnSearchUserItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    private fun setOnCancelListener(onCancelListener: OnCancelClickListener?) {
        this.onCancelListener = onCancelListener
    }


    class Builder {
        private val bundle: Bundle = Bundle()
        private var customFragment: EaseSearchUserFragment? = null
        private var itemClickListener: OnSearchUserItemClickListener? = null
        private var onCancelListener:OnCancelClickListener?=null

        fun <T : EaseSearchUserFragment?> setCustomFragment(fragment: T): Builder {
            customFragment = fragment
            return this
        }

        fun showRightCancel(showCancel: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_SHOW_RIGHT_CANCEL, showCancel)
            return this
        }

        fun setItemClickListener(itemClickListener: OnSearchUserItemClickListener): Builder {
            this.itemClickListener = itemClickListener
            return this
        }

        fun setOnCancelListener(onCancelListener: OnCancelClickListener?): Builder {
            this.onCancelListener = onCancelListener
            return this
        }

        fun setSearchBlockUser(isSearchBlockUser: Boolean): Builder{
            bundle.putBoolean(Constant.KEY_IS_SEARCH_BLOCK, isSearchBlockUser)
            return this
        }

        fun build(): EaseSearchUserFragment {
            val fragment =
                if (customFragment != null) customFragment else EaseSearchUserFragment()
            fragment!!.arguments = bundle
            fragment.setItemClickListener(itemClickListener)
            fragment.setOnCancelListener(onCancelListener)
            return fragment
        }
    }

    private object Constant {
        const val KEY_SHOW_RIGHT_CANCEL = "key_show_right_cancel"
        const val KEY_IS_SEARCH_BLOCK = "key_is_search_block"
    }

}