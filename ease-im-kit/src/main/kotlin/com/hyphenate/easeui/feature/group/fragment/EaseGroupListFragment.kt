package com.hyphenate.easeui.feature.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.chat.EMGroup
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseFragment
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.databinding.EaseFragmentGroupListBinding
import com.hyphenate.easeui.feature.group.EaseGroupDetailActivity
import com.hyphenate.easeui.feature.group.adapter.EaseGroupListAdapter
import com.hyphenate.easeui.feature.group.interfaces.IEaseGroupResultView
import com.hyphenate.easeui.interfaces.EaseGroupListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.viewmodel.group.EaseGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest

open class EaseGroupListFragment: EaseBaseFragment<EaseFragmentGroupListBinding>(),
    IEaseGroupResultView, OnItemClickListener {

    protected lateinit var adapter: EaseGroupListAdapter
    private var groupViewModel: IGroupRequest? = null
    private var data:MutableList<ChatGroup> = mutableListOf()
    private val layoutManager by lazy { LinearLayoutManager(mContext) }
    private var currentPage:Int = 0

    private val groupChangeListener = object : EaseGroupListener() {

        override fun onGroupDestroyed(groupId: String?, groupName: String?) {
            refreshData()
        }

        override fun onAutoAcceptInvitationFromGroup(
            groupId: String?,
            inviter: String?,
            inviteMessage: String?
        ) {
            refreshData()
        }

        override fun onSpecificationChanged(group: EMGroup?) {
            refreshData()
        }

    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseFragmentGroupListBinding? {
        return EaseFragmentGroupListBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.let { binding ->
            binding.rvList.layoutManager = layoutManager
            adapter = getCustomAdapter()
            binding.rvList.adapter = adapter

            groupViewModel = ViewModelProvider(this)[EaseGroupViewModel::class.java]
            groupViewModel?.attachView(this)

            // Set refresh layout
            // Can not load more
            binding.refreshLayout.setEnableLoadMore(false)
            val refreshHeader = binding.refreshLayout.refreshHeader
            if (refreshHeader == null) {
                binding.refreshLayout.setRefreshHeader(RefreshHeader(mContext))
            }
            updateView()
        }

    }

    open fun getCustomAdapter(): EaseGroupListAdapter {
        return EaseGroupListAdapter()
    }

    override fun initListener() {
        super.initListener()
        EaseIM.addGroupChangeListener(groupChangeListener)
        binding?.let { binding ->
            binding.titleContact.setNavigationOnClickListener {
                mContext.finish()
            }
            adapter.setOnItemClickListener(this)
            binding.refreshLayout.setOnRefreshListener {
                refreshData()
            }
            binding.refreshLayout.setOnLoadMoreListener {
                currentPage++
                loadMoreData()
            }
        }
    }

    override fun initData() {
        super.initData()
        currentPage = 0
        data.clear()
        adapter.clearData()
        groupViewModel?.loadJoinedGroupData(currentPage)
        initEventBus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EaseIM.removeGroupChangeListener(groupChangeListener)
    }

    private fun updateView(){
        binding?.titleContact?.setTitle(resources.getString(R.string.ease_group_count,data.size))
    }

    private fun initEventBus(){
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name).register(this) {
            if (it.isGroupChange) {
                refreshData()
            }
        }

        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name).register(this) {
            if (it.isGroupChange) {
                refreshData()
            }
        }

        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.ADD.name).register(this) {
            if (it.isGroupChange) {
                refreshData()
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE + EaseEvent.TYPE.GROUP).register(this) {
            if (it.isGroupChange && it.event == EaseConstant.EVENT_UPDATE_GROUP_NAME) {
                adapter.mData?.forEachIndexed { index, group ->
                    if (group.groupId == it.message) {
                        adapter.notifyItemChanged(index)
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    private fun refreshData(){
        groupViewModel?.loadLocalJoinedGroupData()
    }

    private fun loadMoreData(){
        groupViewModel?.loadJoinedGroupData(currentPage)
    }

    override fun loadGroupListSuccess(list: MutableList<ChatGroup>) {
        data.addAll(list)
        adapter.addData(list)
        binding?.refreshLayout?.finishRefresh()
        updateView()
    }

    override fun loadGroupListFail(code: Int, error: String) {
        binding?.refreshLayout?.finishRefresh()
        updateView()
    }

    override fun loadLocalGroupListSuccess(list: MutableList<ChatGroup>) {
        data.clear()
        adapter.clearData()
        currentPage = 0

        list.reverse().apply {
            data.addAll(list)
            adapter.addData(list)
            binding?.refreshLayout?.finishRefresh()
            updateView()
        }
    }

    override fun loadLocalGroupListFail(code: Int, error: String) {
        ChatLog.e(TAG,"loadLocalGroupListFail $code $error")
        binding?.refreshLayout?.finishRefresh()
        updateView()
    }

    override fun onItemClick(view: View?, position: Int) {
        startActivity(EaseGroupDetailActivity.createIntent(mContext, data[position].groupId))
    }

    companion object {
        const val TAG = "EaseGroupListFragment"
    }
}