package com.hyphenate.easeui.feature.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseSheetFragmentDialog
import com.hyphenate.easeui.feature.contact.adapter.ChatUIKitContactListAdapter
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.enums.ChatUIKitListViewType
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.extensions.getOwnerInfo
import com.hyphenate.easeui.databinding.UikitLayoutGroupMentionBinding
import com.hyphenate.easeui.feature.contact.adapter.ChatUIKitCustomHeaderAdapter
import com.hyphenate.easeui.feature.contact.interfaces.OnHeaderItemClickListener
import com.hyphenate.easeui.feature.contact.interfaces.OnMentionResultListener
import com.hyphenate.easeui.feature.group.interfaces.IUIKitGroupResultView
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.ChatUIKitCustomHeaderItem
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.viewmodel.group.ChatUIKitGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest

class ChatUIKitGroupMentionBottomSheet(
    private val groupId:String,
    private val itemListener: OnMentionResultListener? = null,
    private val headerItemListener:OnHeaderItemClickListener? = null,
    private val headerList:List<ChatUIKitCustomHeaderItem>?
) : ChatUIKitBaseSheetFragmentDialog<UikitLayoutGroupMentionBinding>(),
    IUIKitGroupResultView, OnHeaderItemClickListener, View.OnClickListener,
    OnUserListItemClickListener {
    private var groupViewModel: IGroupRequest? = null
    private var listAdapter: ChatUIKitContactListAdapter? = null
    private var headerAdapter: ChatUIKitCustomHeaderAdapter?=null


    override fun onStart() {
        setTopOffset(300)
        super.onStart()
    }

    override fun initData() {
        groupViewModel = ViewModelProvider(context as AppCompatActivity)[ChatUIKitGroupViewModel::class.java]
        groupViewModel?.attachView(this)

        refreshData()
    }

    override fun showExpandedState(): Boolean {
        return true
    }

    override fun isDraggable(): Boolean {
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): UikitLayoutGroupMentionBinding {
        return UikitLayoutGroupMentionBinding.inflate(inflater)
    }

    override fun initView(){
        binding?.let {
            setOnApplyWindowInsets(it.root)
            it.groupSheetTitle.text = getString(R.string.uikit_group_mention_title)

            listAdapter = ChatUIKitContactListAdapter(ChatUIKitListViewType.LIST_GROUP_MEMBER)
            listAdapter?.setHasStableIds(true)
            listAdapter?.setShowInitialLetter(false)

            headerAdapter = ChatUIKitCustomHeaderAdapter()
            headerAdapter?.setHasStableIds(true)
            headerAdapter?.let {ha->
                headerList?.let { data->
                    ha.setItems(data.toMutableList())
                }
            }

            val concatAdapter1 = ConcatAdapter()

            concatAdapter1.addAdapter(0,headerAdapter!!)
            concatAdapter1.addAdapter(listAdapter!!)
            it.rvList.layoutManager = LinearLayoutManager(context)
            it.rvList.adapter = concatAdapter1

            // Set refresh layout
            // Can not load more
            it.refreshLayout.setEnableLoadMore(false)
            val refreshHeader = it.refreshLayout.refreshHeader
            if (refreshHeader == null) {
                it.refreshLayout.setRefreshHeader(RefreshHeader(context))
            }
        }
        refreshData()
    }

    override fun initListener(){
        headerAdapter?.setOnHeaderItemClickListener(this)
        listAdapter?.setOnUserListItemClickListener(this)
        binding?.iconBack?.setOnClickListener(this)
        binding?.refreshLayout?.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData(){
        groupViewModel?.fetchGroupMemberFromService(groupId)
    }

    override fun onHeaderItemClick(v: View, itemIndex: Int,itemId:Int?) {
        headerItemListener?.onHeaderItemClick(v,itemIndex,itemId)
        dismiss()
    }

    override fun onUserListItemClick(v: View?, position: Int, user: ChatUIKitUser?) {
        itemListener?.onMentionItemClick(view,position,user?.userId)
        dismiss()
    }

    override fun fetchGroupMemberSuccess(user: List<ChatUIKitUser>) {
        val data = user.toMutableList()
        val groupManager = ChatClient.getInstance().groupManager()
        val group = groupManager.getGroup(groupId)
        val ownerInfo = group?.getOwnerInfo()
        ownerInfo?.let {
            data.add(it)
        }
        listAdapter?.setData(data.filter {
            it.userId != ChatClient.getInstance().currentUser
        }.toMutableList())
        finishRefresh()
    }

    override fun fetchGroupMemberFail(code: Int, error: String) {
        super.fetchGroupMemberFail(code, error)
        finishRefresh()
    }

    private fun finishRefresh(){
        if (binding?.refreshLayout?.isRefreshing == true){
            binding?.refreshLayout?.finishRefresh()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.icon_back -> { dismiss() }
            else -> {}
        }
    }


}