package com.hyphenate.easeui.feature.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseSheetFragmentDialog
import com.hyphenate.easeui.feature.contact.adapter.EaseContactListAdapter
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.enums.EaseListViewType
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.extensions.getOwnerInfo
import com.hyphenate.easeui.databinding.EasetLayoutGroupMentionBinding
import com.hyphenate.easeui.feature.contact.adapter.EaseCustomHeaderAdapter
import com.hyphenate.easeui.feature.contact.interfaces.OnHeaderItemClickListener
import com.hyphenate.easeui.feature.contact.interfaces.OnMentionResultListener
import com.hyphenate.easeui.feature.group.interfaces.IEaseGroupResultView
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.EaseCustomHeaderItem
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.viewmodel.group.EaseGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest

class EaseGroupMentionBottomSheet(
    private val groupId:String,
    private val itemListener: OnMentionResultListener? = null,
    private val headerItemListener:OnHeaderItemClickListener? = null,
    private val headerList:List<EaseCustomHeaderItem>?
) : EaseBaseSheetFragmentDialog<EasetLayoutGroupMentionBinding>(),
    IEaseGroupResultView, OnHeaderItemClickListener, View.OnClickListener,
    OnUserListItemClickListener {
    private var groupViewModel: IGroupRequest? = null
    private var listAdapter: EaseContactListAdapter? = null
    private var headerAdapter: EaseCustomHeaderAdapter?=null


    override fun onStart() {
        setTopOffset(300)
        super.onStart()
    }

    override fun initData() {
        groupViewModel = ViewModelProvider(context as AppCompatActivity)[EaseGroupViewModel::class.java]
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
    ): EasetLayoutGroupMentionBinding {
        return EasetLayoutGroupMentionBinding.inflate(inflater)
    }

    override fun initView(){
        binding?.let {
            setOnApplyWindowInsets(it.root)
            it.groupSheetTitle.text = getString(R.string.ease_group_mention_title)

            listAdapter = EaseContactListAdapter(EaseListViewType.LIST_GROUP_MEMBER)
            listAdapter?.setHasStableIds(true)
            listAdapter?.setShowInitialLetter(false)

            headerAdapter = EaseCustomHeaderAdapter()
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

    override fun onUserListItemClick(v: View?, position: Int, user: EaseUser?) {
        itemListener?.onMentionItemClick(view,position,user?.userId)
        dismiss()
    }

    override fun fetchGroupMemberSuccess(user: List<EaseUser>) {
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