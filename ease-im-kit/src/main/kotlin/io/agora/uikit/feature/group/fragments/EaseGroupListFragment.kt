package io.agora.uikit.feature.group.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseFragment
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.RefreshHeader
import io.agora.uikit.common.bus.EaseFlowBus
import io.agora.uikit.databinding.EaseFragmentGroupListBinding
import io.agora.uikit.feature.group.EaseGroupDetailActivity
import io.agora.uikit.feature.group.adapter.EaseGroupListAdapter
import io.agora.uikit.feature.group.interfaces.IEaseGroupResultView
import io.agora.uikit.interfaces.EaseGroupListener
import io.agora.uikit.interfaces.OnItemClickListener
import io.agora.uikit.model.EaseEvent
import io.agora.uikit.viewmodel.group.EaseGroupViewModel
import io.agora.uikit.viewmodel.group.IGroupRequest

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

        override fun onSpecificationChanged(group: ChatGroup?) {
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