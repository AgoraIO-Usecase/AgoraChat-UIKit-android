package com.hyphenate.easeui.feature.group.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.base.EaseBaseListFragment
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.helper.SidebarHelper
import com.hyphenate.easeui.feature.contact.interfaces.IEaseContactResultView
import com.hyphenate.easeui.feature.group.adapter.EaseGroupSelectListAdapter
import com.hyphenate.easeui.feature.group.interfaces.IEaseGroupResultView
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.viewmodel.contacts.EaseContactListViewModel
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest
import com.hyphenate.easeui.widget.EaseSidebar

open class EaseGroupAddMemberFragment: EaseBaseListFragment<EaseUser>()
    ,IEaseContactResultView,IEaseGroupResultView, OnContactSelectedListener {
    private val memberSelectAdapter: EaseGroupSelectListAdapter by lazy { EaseGroupSelectListAdapter(groupId) }
    private var groupId:String?=null
    private var currentGroup:ChatGroup?=null
    private var contactViewModel: IContactListRequest? = null
    private var listener:OnContactSelectedListener?=null
    private var sideBarContact:EaseSidebar?=null

    companion object {
        private const val TAG = "EaseGroupAddMemberFragment"
    }

    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseUser> {
        return memberSelectAdapter
    }

    override fun initView(savedInstanceState: Bundle?) {
        groupId = arguments?.getString(EaseConstant.EXTRA_CONVERSATION_ID) ?: ""
        super.initView(savedInstanceState)
        groupId?.let {
            currentGroup = ChatClient.getInstance().groupManager().getGroup(it)
        }
        initSideBar()
        contactViewModel = ViewModelProvider(context as AppCompatActivity)[EaseContactListViewModel::class.java]
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

    override fun initListener() {
        super.initListener()
        memberSelectAdapter.setCheckBoxSelectListener(this)

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
                    if (visibleList.isNullOrEmpty()) {
                        return
                    }
                    contactViewModel?.fetchContactInfo(visibleList)
                }
            }
        })
    }

    override fun initData() {
        loadData()
    }

    fun setMemberList(members: MutableList<String>){
        memberSelectAdapter.setGroupMemberList(members)
    }

    fun setSideBar(sidebar:EaseSidebar){
        this.sideBarContact = sidebar
    }

    override fun initViewModel() {
        super.initViewModel()
        contactViewModel?.attachView(this)
    }

    override fun refreshData() {
        loadLocalData()
    }

    open fun loadData(){
        contactViewModel?.loadData(true)
    }

    open fun loadLocalData(){
        contactViewModel?.loadData(false)
    }

    fun resetSelect(){
        memberSelectAdapter.resetSelect()
    }

    override fun loadContactListSuccess(list: MutableList<EaseUser>) {
        finishRefresh()
        memberSelectAdapter.setData(list)
    }

    override fun loadContactListFail(code: Int, error: String) {
        finishRefresh()
    }

    override fun fetchGroupMemberFail(code: Int, error: String) {
        finishRefresh()
    }

    override fun onContactSelectedChanged(v: View, selectedMembers: MutableList<String>) {
        listener?.onContactSelectedChanged(v, selectedMembers)
    }

    fun setAddSelectListener(listener: OnContactSelectedListener){
        this.listener = listener
    }

    override fun fetchUserInfoByUserSuccess(users: List<EaseUser>?) {
        if (!users.isNullOrEmpty()) {
            memberSelectAdapter.notifyDataSetChanged()
        }
    }

}