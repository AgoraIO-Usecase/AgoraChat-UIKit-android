package com.hyphenate.easeui.feature.group.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.base.ChatUIKitBaseListFragment
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.helper.SidebarHelper
import com.hyphenate.easeui.feature.group.adapter.ChatUIKitGroupSelectListAdapter
import com.hyphenate.easeui.feature.group.interfaces.IUIKitGroupResultView
import com.hyphenate.easeui.feature.group.interfaces.ISearchResultListener
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.model.ChatUIKitProfile
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.viewmodel.group.ChatUIKitGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest
import com.hyphenate.easeui.widget.ChatUIKitSidebar

open class ChatUIKitGroupRemoveMemberFragment : ChatUIKitBaseListFragment<ChatUIKitUser>(), IUIKitGroupResultView,
    OnContactSelectedListener, ISearchResultListener {
    private val memberSelectAdapter: ChatUIKitGroupSelectListAdapter by lazy { ChatUIKitGroupSelectListAdapter(groupId) }
    private var listener:OnContactSelectedListener?=null
    private var groupId:String?=null
    private var groupViewModel: IGroupRequest? = null
    private var data:MutableList<ChatUIKitUser> = mutableListOf()
    private var sideBarContact: ChatUIKitSidebar?=null

    companion object {
        private const val TAG = "ChatUIKitGroupRemoveMemberFragment"
    }

    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): ChatUIKitBaseRecyclerViewAdapter<ChatUIKitUser> {
        return memberSelectAdapter
    }

    override fun refreshData() {
        loadData()
    }

    override fun initData() {
        super.initData()
        loadData()
    }

    fun resetSelect(){
        memberSelectAdapter.resetSelect()
    }

    fun setMemberList(members: MutableList<String>){
        memberSelectAdapter.setGroupMemberList(members)
    }

    fun addSelectList(members: MutableList<String>){
        memberSelectAdapter.addSelectList(members)
    }

    open fun loadData(){
        finishRefresh()
        data.clear()
        groupId?.let { groupId->
            groupViewModel?.loadLocalMember(groupId)
        }
    }

    open fun initSideBar(){
        sideBarContact?.visibility = View.VISIBLE
        val sidebarHelper = SidebarHelper()
        sidebarHelper.setupWithRecyclerView(
            binding?.rvList,
            memberSelectAdapter
        )
        sideBarContact?.setOnTouchEventListener(sidebarHelper)
    }

    fun setSideBar(sidebar:ChatUIKitSidebar){
        this.sideBarContact = sidebar
    }

    override fun initViewModel() {
        super.initViewModel()
        groupViewModel?.attachView(this)
    }

    override fun initView(savedInstanceState: Bundle?) {
        groupId = arguments?.getString(ChatUIKitConstant.EXTRA_CONVERSATION_ID) ?: ""
        super.initView(savedInstanceState)
        groupViewModel = ViewModelProvider(this)[ChatUIKitGroupViewModel::class.java]
        initSideBar()
    }

    override fun initListener() {
        super.initListener()
        memberSelectAdapter.setCheckBoxSelectListener(this)
        setSearchResultListener(this)
        binding?.rvList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // When scroll to bottom, load more data
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val visibleList = memberSelectAdapter.mData?.filterIndexed { index, _ ->
                        index in firstVisibleItemPosition..lastVisibleItemPosition
                    }
                    groupId?.let { id ->
                        val idList = visibleList?.map { user->
                            user.userId
                        }
                        if (idList.isNullOrEmpty()){
                            return
                        }
                        groupViewModel?.fetchMemberInfo(id, idList)
                    }
                }
            }
        })
    }

    override fun onSearchResultListener(selectMembers: MutableList<String>) {
        ChatLog.d(TAG,"onSearchResultListener $selectMembers")
        if (selectMembers.isNotEmpty()){
            memberSelectAdapter.addSelectList(selectMembers)
            listener?.onSearchSelectedResult(selectMembers)
        }
    }

    override fun onContactSelectedChanged(v: View, selectedMembers: MutableList<String>) {
        listener?.onContactSelectedChanged(v, selectedMembers)
    }

    fun setRemoveSelectListener(listener: OnContactSelectedListener){
        this.listener = listener
    }

    override fun loadLocalMemberSuccess(members: List<ChatUIKitUser>) {
        data = members.toMutableList().filter {
            it.userId != ChatUIKitClient.getCurrentUser()?.id
        }.toMutableList()
        memberSelectAdapter.setData(data)
    }

    override fun fetchMemberInfoSuccess(members: Map<String, ChatUIKitProfile>?) {
        finishRefresh()
        mListAdapter.notifyDataSetChanged()
    }

    override fun fetchMemberInfoFail(code: Int, error: String) {
        finishRefresh()
    }

}