package com.hyphenate.easeui.feature.thread.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseFragment
import com.hyphenate.easeui.common.ChatCursorResult
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.isAdmin
import com.hyphenate.easeui.common.extensions.isOwner
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.databinding.EaseFragmentThreadListBinding
import com.hyphenate.easeui.feature.thread.adapter.EaseChatThreadListAdapter
import com.hyphenate.easeui.feature.thread.interfaces.IChatThreadResultView
import com.hyphenate.easeui.feature.thread.interfaces.OnChatThreadListItemClickListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.viewmodel.thread.EaseChatThreadViewModel
import com.hyphenate.easeui.viewmodel.thread.IChatThreadRequest
import kotlinx.coroutines.launch

open class EaseChatThreadListFragment: EaseBaseFragment<EaseFragmentThreadListBinding>(),IChatThreadResultView, OnItemClickListener {
    private var adapter: EaseChatThreadListAdapter? = null
    private val layoutManager by lazy { LinearLayoutManager(mContext) }
    private var viewModel: IChatThreadRequest? = null
    private var backPressListener: View.OnClickListener? = null
    private var itemClickListener:OnChatThreadListItemClickListener? = null
    private var parentId: String? = ""
    private var cursor: String = ""
    private var isGroupAdmin = false
    private var mGroup: ChatGroup? = null
    private var data:MutableList<ChatThread> = mutableListOf()

    companion object{
        const val limit:Int = 10
        const val TAG = "EaseChatThreadListFragment"
        const val KEY_ENABLE_BACK = "key_enable_back"
        const val KEY_USE_TITLE_REPLACE = "key_use_replace_action_bar"
        const val KEY_USE_TITLE = "key_use_title"
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseFragmentThreadListBinding {
        return EaseFragmentThreadListBinding.inflate(inflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.let {
            parentId = it.getString(EaseConstant.EXTRA_CONVERSATION_ID)
            binding?.titleContact?.visibility = View.GONE
            val useHeader: Boolean = it.getBoolean(KEY_USE_TITLE, false)
            binding?.titleContact?.visibility = if (useHeader) View.VISIBLE else View.GONE
            if (useHeader){
                val canBack: Boolean = it.getBoolean(KEY_ENABLE_BACK, false)
                binding?.titleContact?.setDisplayHomeAsUpEnabled(canBack
                    , it.getBoolean(KEY_USE_TITLE_REPLACE, false))
                binding?.titleContact?.setNavigationOnClickListener {
                    if (backPressListener != null) {
                        backPressListener?.onClick(it)
                        return@setNavigationOnClickListener
                    }
                    mContext.onBackPressed()
                }
            }
        }

        binding?.run {
            rvList.layoutManager = layoutManager
            if (adapter == null){
                adapter = EaseChatThreadListAdapter()
            }
            rvList.adapter = adapter

            refreshLayout.setEnableLoadMore(true)
            val refreshHeader = refreshLayout.refreshHeader
            if (refreshHeader == null) {
                refreshLayout.setRefreshHeader(RefreshHeader(mContext))
            }
            updateTitle()
        }
    }

    override fun initViewModel() {
        super.initViewModel()

        viewModel = ViewModelProvider(this)[EaseChatThreadViewModel::class.java]
        viewModel?.attachView(this)
    }

    override fun initListener() {
        super.initListener()
        binding?.run {
            adapter?.setOnItemClickListener(this@EaseChatThreadListFragment)
            titleContact.setNavigationOnClickListener {
                if (backPressListener != null) {
                    backPressListener?.onClick(it)
                    return@setNavigationOnClickListener
                }
                mContext.finish()
            }
            refreshLayout.setOnRefreshListener {
                refreshData()
            }
            refreshLayout.setOnLoadMoreListener {
                loadMoreData()
            }
            rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    // When scroll to bottom, load more data
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val visibleList = adapter?.mData?.filterIndexed { index, _ ->
                            index in firstVisibleItemPosition..lastVisibleItemPosition
                        }
                        visibleList?.let {
                            parseResult(it.toMutableList())
                        }
                    }
                }
            })
        }
    }

    open fun initEventBus() {
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name).register(viewLifecycleOwner) {
            if (it.isThreadChange) {
                refreshData()
            }
        }
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name).register(viewLifecycleOwner) {
            if (it.isThreadChange) {
                refreshData()
            }
        }
    }

    private fun setHeaderBackPressListener(listener: View.OnClickListener?) {
        this.backPressListener = listener
    }

    override fun initData() {
        super.initData()
        parentId?.let {
            viewModel?.setGroupInfo(it)
        }
        initEventBus()
    }

    private fun loadMoreData(){
        getThreadInfo()
    }

    private fun refreshData(){
        cursor = ""
        data.clear()
        adapter?.clearData()
        adapter?.clearLatestMessages()
        getThreadInfo()
    }

    private fun getThreadInfo(){
        if (isGroupAdmin){
            mGroup?.groupId?.let {
                viewModel?.fetchChatThreadsFromServer(it,limit,cursor)
            }
        }else{
            mGroup?.groupId?.let {
                viewModel?.getJoinedChatThreadsFromServer(it,limit,cursor)
            }
        }
    }

    private fun updateTitle(){
        if (adapter?.mData == null){
            binding?.titleContact?.setTitle(resources.getString(R.string.ease_thread_list_title))
        }else{
            data.let {
                val count = "(${it.size})"
                binding?.titleContact?.setTitle(resources.getString(R.string.ease_thread_list_title,count))
            }
        }
    }

    private fun setOnChatThreadListItemClickListener(listener:OnChatThreadListItemClickListener?){
        this.itemClickListener = listener
    }

    private fun setCustomAdapter(adapter: EaseChatThreadListAdapter?) {
        this.adapter = adapter
    }

    override fun onItemClick(view: View?, position: Int) {
        if (data.isNotEmpty() && position < data.size){
            itemClickListener?.onChatThreadItemClick(view,data[position])
        }
    }

    override fun settGroupInfoSuccess(parent: ChatGroup?) {
        mGroup = parent
        mGroup?.let {
            isGroupAdmin = it.isOwner() || it.isAdmin()
            getThreadInfo()
        }
    }

    override fun setGroupInfoFail(code: Int, message: String?) {
        ChatLog.e(TAG,"getThreadParentInfoFail $code $message")
    }

    override fun getJoinedChatThreadsFromServerSuccess(result: ChatCursorResult<ChatThread>) {
        cursor = result.cursor
        finishRefresh()
        result.data.let {
            parseResult(it)
            data.addAll(it)
            adapter?.setData(data)
            updateTitle()
        }
    }

    override fun getJoinedChatThreadsFromServerFail(code: Int, message: String?) {
        ChatLog.e(TAG,"getJoinedChatThreadsFromServerFail $code $message")
        finishRefresh()
    }

    override fun fetchChatThreadsFromServerSuccess(result: ChatCursorResult<ChatThread>) {
        cursor = result.cursor
        finishRefresh()
        result.data.let {
            parseResult(it)
            data.addAll(it)
            adapter?.setData(data)
            updateTitle()
        }
    }

    private fun parseResult(result:MutableList<ChatThread>){
        val messageMap:MutableMap<String,ChatMessage> = mutableMapOf()
        val first = mutableListOf<String>()
        result.forEach{
            val lastMsgMap = adapter?.getLatestMessages()
            lastMsgMap?.let { map->
                if (map.containsKey(it.chatThreadId)){
                    return@forEach
                }
            }
            if (it.lastMessage == null){
                first.add(it.chatThreadId)
            }else{
                messageMap[it.chatThreadId] = it.lastMessage
            }
        }
        messageMap.let {
            if (it.isNotEmpty()){
                adapter?.setLatestMessages(it)
            }
        }
        first.let {
            if (it.isNotEmpty()){
                viewModel?.getChatThreadLatestMessage(it)
            }
        }
    }

    fun finishRefresh(){
        mContext.mainScope().launch {
            binding?.refreshLayout?.let {
                if (it.isRefreshing){
                    it.finishRefresh()
                }
                if (it.isLoading){
                    it.finishLoadMore()
                }
            }
        }
    }

    override fun fetchChatThreadsFromServerFail(code: Int, message: String?) {
        ChatLog.e(TAG,"fetchChatThreadsFromServerFail $code $message")
        finishRefresh()
        updateTitle()
    }

    override fun getChatThreadLatestMessageSuccess(result: MutableMap<String, ChatMessage>) {
        ChatLog.e(TAG,"getChatThreadLatestMessageSuccess ${result.size}")
        adapter?.setLatestMessages(result)
        finishRefresh()
    }

    override fun getChatThreadLatestMessageFail(code: Int, message: String?) {
        ChatLog.e(TAG,"getChatThreadLatestMessageFail $code $message")
        finishRefresh()
    }

    class Builder(
        private val conversationId: String?,
    ){
        protected val bundle: Bundle = Bundle()
        private var adapter: EaseChatThreadListAdapter? = null
        private var backPressListener: View.OnClickListener? = null
        protected var customFragment: EaseChatThreadListFragment? = null
        private var itemClickListener:OnChatThreadListItemClickListener? = null

        init {
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
        }

        /**
         * Set custom fragment which should extends EaseMessageFragment
         *
         * @param fragment
         * @param <T>
         * @return
        </T> */
        fun <T : EaseChatThreadListFragment?> setCustomFragment(fragment: T): Builder {
            customFragment = fragment
            return this
        }

        /**
         * Set custom adapter which should extends EaseMessageAdapter
         *
         * @param adapter
         * @return
         */
        fun setCustomAdapter(adapter: EaseChatThreadListAdapter?): Builder {
            this.adapter = adapter
            return this
        }

        /**
         * Whether show back icon in titleBar
         *
         * @param canBack
         * @return
         */
        fun enableTitleBarPressBack(canBack: Boolean): Builder {
            bundle.putBoolean(KEY_ENABLE_BACK, canBack)
            return this
        }

        /**
         * If you have set [Builder.enableTitleBarPressBack], you can set the listener
         *
         * @param listener
         * @return
         */
        fun setTitleBarBackPressListener(listener: View.OnClickListener?):Builder {
            backPressListener = listener
            return this
        }

        /**
         * Whether to use default titleBar to replace actionBar when activity is a AppCompatActivity.
         * If set true, will call [androidx.appcompat.app.AppCompatActivity.setSupportActionBar].
         * @param replace
         * @return
         */
        fun useTitleBarToReplaceActionBar(replace: Boolean): Builder {
            bundle.putBoolean(KEY_USE_TITLE_REPLACE, replace)
            return this
        }

        /**
         * Whether to use default titleBar which is [EaseTitleBar]
         *
         * @param useTitle
         * @return
         */
        fun useTitleBar(useTitle: Boolean): Builder {
            bundle.putBoolean(KEY_USE_TITLE, useTitle)
            return this
        }

        fun setOnChatThreadListItemClickListener(listener:OnChatThreadListItemClickListener?):Builder{
            this.itemClickListener = listener
            return this
        }

        fun build(): EaseChatThreadListFragment? {
            val fragment = if (customFragment != null) customFragment else EaseChatThreadListFragment()
            fragment?.let {
                it.arguments = bundle
                it.setOnChatThreadListItemClickListener(itemClickListener)
                it.setHeaderBackPressListener(backPressListener)
                it.setCustomAdapter(adapter)
            }
            return fragment
        }
    }

}