package io.agora.uikit.feature.group.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.uikit.EaseIM
import io.agora.uikit.base.EaseBaseListFragment
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.getOwnerInfo
import io.agora.uikit.common.helper.ContactSortedHelper
import io.agora.uikit.feature.group.adapter.EaseGroupMemberListAdapter
import io.agora.uikit.feature.group.interfaces.IEaseGroupResultView
import io.agora.uikit.feature.group.interfaces.IGroupMemberEventListener
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.setUserInitialLetter
import io.agora.uikit.viewmodel.group.EaseGroupViewModel
import io.agora.uikit.viewmodel.group.IGroupRequest

open class EaseGroupMemberFragment:EaseBaseListFragment<EaseUser>(),IEaseGroupResultView {
    private var groupViewModel: IGroupRequest? = null
    protected var groupId:String?=null
    protected var currentGroup:ChatGroup?=null
    private var sortedList:MutableList<EaseUser> = mutableListOf()
    private var listener:IGroupMemberEventListener?=null

    companion object {
        private const val TAG = "EaseGroupMemberFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            groupId = arguments?.getString(EaseConstant.EXTRA_CONVERSATION_ID) ?: ""
        }
        super.initView(savedInstanceState)
        setSearchViewVisible(false)
        groupId?.let {
            currentGroup = ChatClient.getInstance().groupManager().getGroup(it)
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        groupViewModel = ViewModelProvider(this)[EaseGroupViewModel::class.java]
        groupViewModel?.attachView(this)
    }

    override fun initListener() {
        super.initListener()
        binding?.rvList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // When scroll to bottom, load more data
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val visibleList = mListAdapter.mData?.filterIndexed { index, _ ->
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

    override fun initData() {
        super.initData()
        loadData()
    }

    open fun loadData(){
        groupId?.let {
            groupViewModel?.fetchGroupMemberFromService(it)
        }
    }

    fun loadLocalData(){
        finishRefresh()
        groupId?.let { groupId->
            sortedList.clear()
            groupViewModel?.loadLocalMember(groupId)
        }
    }

    override fun initRecyclerView(): RecyclerView? {
        return binding?.rvList
    }

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseUser> {
        return EaseGroupMemberListAdapter(groupId)
    }

    override fun refreshData() {
        loadLocalData()
    }

    override fun fetchGroupMemberSuccess(user: List<EaseUser>) {
        sortedList = user.toMutableList()
        finishRefresh()
        groupId?.let {
            val ownerInfo = currentGroup?.getOwnerInfo()
            ownerInfo?.let {
                it.setUserInitialLetter()
                it.let { it1 ->
                    if (!sortedList.contains(it1)){
                        sortedList.add(it1)
                    }
                }
            }
            sortedList = ContactSortedHelper.sortedList(sortedList).toMutableList()
            mListAdapter.setData(sortedList)
            listener?.onGroupMemberLoadSuccess(sortedList)
        }
    }

    override fun fetchGroupMemberFail(code: Int, error: String) {
        finishRefresh()
        ChatLog.e(TAG,"fetchGroupMemberFail $code $error")
    }

    override fun onItemClick(view: View?, position: Int) {
        sortedList.let {
            ChatLog.d(TAG,"onItemClick data size ${it.size} - position:$position")
            if (it.isNotEmpty() && it.size > position){
                if (EaseIM.getCurrentUser()?.id != it[position].userId){
                    listener?.onGroupMemberListItemClick(view,it[position])
                }
            }
        }
    }

    fun setOnGroupMemberItemClickListener(listener: IGroupMemberEventListener){
        this.listener = listener
    }

    override fun loadLocalMemberSuccess(members: List<EaseUser>) {
        sortedList = members.toMutableList()
        finishRefresh()
        groupId?.let {
            val ownerInfo = currentGroup?.getOwnerInfo()
            ownerInfo?.let {
                it.setUserInitialLetter()
                it.let { it1 ->
                    if (!sortedList.contains(it1)){
                        sortedList.add(it1)
                    }
                }
            }
            sortedList = ContactSortedHelper.sortedList(sortedList).toMutableList()
            mListAdapter.setData(sortedList)
            listener?.onGroupMemberLoadSuccess(sortedList)
        }
    }

    override fun fetchMemberInfoSuccess(members: Map<String, EaseProfile>?) {
        super.fetchMemberInfoSuccess(members)
        mListAdapter.notifyDataSetChanged()
    }

    override fun fetchMemberInfoFail(code: Int, error: String) {
        super.fetchMemberInfoFail(code, error)
    }
}