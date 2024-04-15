package com.hyphenate.easeui.base

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.databinding.EaseFragmentSearchLayoutBinding
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch


abstract class EaseBaseSearchFragment<T> : EaseBaseFragment<EaseFragmentSearchLayoutBinding>(),
    OnItemClickListener {

    lateinit var srlContactRefresh:SmartRefreshLayout
    var mRecyclerView: RecyclerView? = null
    lateinit var mListAdapter: EaseBaseRecyclerViewAdapter<T>
    protected lateinit var concatAdapter: ConcatAdapter

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.let {
            srlContactRefresh = it.srlContactRefresh
            val refreshHeader = it.srlContactRefresh.refreshHeader
            if (refreshHeader == null) {
                it.srlContactRefresh.setRefreshHeader(RefreshHeader(context))
            }
            mRecyclerView = if (initRecyclerView()?.isNotEmpty() == true){
                initRecyclerView()
            }else{
                it.rvList
            }
        }
        mRecyclerView?.layoutManager = getLayoutManager()
        concatAdapter = ConcatAdapter()
        addHeader(concatAdapter)
        mListAdapter = initAdapter()
        concatAdapter.addAdapter(mListAdapter)
        mRecyclerView?.adapter = concatAdapter
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseFragmentSearchLayoutBinding? {
       return EaseFragmentSearchLayoutBinding.inflate(inflater)
    }

    override fun initListener() {
        super.initListener()
        mListAdapter.setOnItemClickListener(this)
        srlContactRefresh.setOnRefreshListener {
            refreshData()
        }
        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val searchContent = s.toString().trim { it <= ' ' }
                searchText(searchContent)
                if (searchContent.isNotEmpty()){
                    binding?.searchClear?.visibility = View.VISIBLE
                }else{
                    binding?.searchClear?.visibility = View.GONE
                }
            }
        })
        binding?.searchClear?.setOnClickListener{
            binding?.etSearch?.setText("")
        }
        binding?.tvRight?.setOnClickListener{
            onTvRightClick(it)
        }
    }

    override fun initData() {
        super.initData()
        mRecyclerView?.adapter = concatAdapter
    }


    /**
     * Can add header adapters
     * @param adapter
     */
    open fun addHeader(adapter: ConcatAdapter) {
        // Add header adapter by adapter
    }

    /**
     * Can change the RecyclerView's orientation
     * @return
     */
    protected open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(mContext)
    }

    /**
     * Must initialize the RecyclerView
     * @return
     */
    protected abstract fun initRecyclerView(): RecyclerView?

    /**
     * Must provide the list adapter
     * @return
     */
    protected abstract fun initAdapter(): EaseBaseRecyclerViewAdapter<T>

    protected abstract fun searchText(query: String)

    protected abstract fun refreshData()

    protected abstract fun onTvRightClick(view: View)


    override fun onItemClick(view: View?, position: Int) {

    }

     fun finishRefresh() {
         lifecycleScope.launch {
            if (srlContactRefresh.isNotEmpty()) {
                srlContactRefresh.finishRefresh()
            }
         }
    }

}