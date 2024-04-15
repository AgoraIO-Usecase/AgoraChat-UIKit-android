package com.hyphenate.easeui.feature.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.extensions.getInviteMessageStatus
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.EaseLayoutNewRequestBinding
import com.hyphenate.easeui.feature.contact.interfaces.IEaseContactResultView
import com.hyphenate.easeui.feature.invitation.adapter.EaseRequestAdapter
import com.hyphenate.easeui.feature.invitation.enums.InviteMessageStatus
import com.hyphenate.easeui.feature.invitation.helper.EaseNotificationMsgManager
import com.hyphenate.easeui.feature.invitation.interfaces.IEaseNotificationResultView
import com.hyphenate.easeui.interfaces.EaseContactListener
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.viewmodel.contacts.EaseContactListViewModel
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest
import com.hyphenate.easeui.viewmodel.request.EaseNotificationViewModel
import com.hyphenate.easeui.viewmodel.request.INotificationRequest

open class EaseNewRequestsActivity : EaseBaseActivity<EaseLayoutNewRequestBinding>(),
    IEaseNotificationResultView,IEaseContactResultView,
    EaseBaseRecyclerViewAdapter.OnItemSubViewClickListener {
    private var listAdapter: EaseRequestAdapter? = null
    private var noticeViewModel: INotificationRequest? = null
    private var contactViewModel: IContactListRequest? = null
    private var mData: List<ChatMessage> = mutableListOf()
    private var isFirst:Boolean = true

    private val contactListener = object : EaseContactListener() {

        override fun onContactDeleted(username: String?) {
            refreshData()
        }

        override fun onContactInvited(username: String?, reason: String?) {
            updateNotifyCount()
        }

    }

    override fun getViewBinding(inflater: LayoutInflater): EaseLayoutNewRequestBinding {
        return EaseLayoutNewRequestBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initListener()
        initData()

    }

    override fun onDestroy() {
        super.onDestroy()
        EaseIM.removeContactListener(contactListener)
    }

    open fun initView(){

        noticeViewModel = ViewModelProvider(this)[EaseNotificationViewModel::class.java]
        noticeViewModel?.attachView(this)

        contactViewModel = ViewModelProvider(this)[EaseContactListViewModel::class.java]
        contactViewModel?.attachView(this)

        binding.let {
            it.rvList.layoutManager = LinearLayoutManager(this)
            listAdapter = EaseRequestAdapter()
            listAdapter?.setHasStableIds(true)
            listAdapter?.setEmptyView(R.layout.ease_layout_default_no_data)
            it.rvList.adapter = listAdapter

            // Set refresh layout
            // Can not load more
            it.refreshLayout.setEnableLoadMore(false)
            val refreshHeader = it.refreshLayout.refreshHeader
            if (refreshHeader == null) {
                it.refreshLayout.setRefreshHeader(RefreshHeader(this))
            }
        }
    }

    open fun initData(){
        refreshData()
        initEventBus()
    }

    private fun initEventBus(){
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.ADD.name).register(this) {
            if (it.isNotifyChange) {
                refreshData()
            }
        }

        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE.name).register(this) {
            if (it.isNotifyChange) {
                refreshData()
            }
        }
    }

    fun initListener(){
        EaseIM.addContactListener(contactListener)
        binding.titleBar.setNavigationOnClickListener {
            mContext.onBackPressed()
        }
        listAdapter?.setOnItemSubViewClickListener(this)
        binding.refreshLayout.setOnRefreshListener{
            refreshData()
        }
        binding.titleBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_add_contact -> {
                    showAddContactDialog()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener false
        }

        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isFirst && dy == 0){
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    listAdapter?.mData?.let {
                        if (it.isEmpty()) return
                    }
                    val visibleList = listAdapter?.mData?.filterIndexed { index, _ ->
                        if (firstVisibleItemPosition == 0 && lastVisibleItemPosition == 0 && index == 0){
                            true
                        }else{
                            index in firstVisibleItemPosition..lastVisibleItemPosition
                        }
                    }
                    val idList = mutableListOf<String>()
                    visibleList?.forEach { msg->
                        if (msg.ext().containsKey(EaseConstant.SYSTEM_MESSAGE_FROM)){
                            idList.add(msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM))
                        }
                    }
                    if (idList.isEmpty()){
                        return
                    }
                    noticeViewModel?.fetchProfileInfo(idList)
                    isFirst = false
                }

            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // When scroll to bottom, load more data
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val visibleList = listAdapter?.mData?.filterIndexed { index, _ ->
                        index in firstVisibleItemPosition..lastVisibleItemPosition
                    }
                    val idList = mutableListOf<String>()
                    visibleList?.forEach { msg->
                        if (msg.ext().containsKey(EaseConstant.SYSTEM_MESSAGE_FROM)){
                            idList.add(msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM))
                        }
                    }
                    if (idList.isEmpty()){
                        return
                    }
                    noticeViewModel?.fetchProfileInfo(idList)
                }
            }
        })
    }

    open fun refreshData(){
        noticeViewModel?.getAllMessage()
    }

    private fun showAddContactDialog(){
        val contactDialog = CustomDialog(
            this@EaseNewRequestsActivity,
            resources.getString(R.string.ease_contact_add_dialog_title),
            resources.getString(R.string.ease_contact_add_dialog_contact),
            true,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {

            },
            onInputModeConfirmListener = {
                contactViewModel?.addContact(it)
            }
        )
        contactDialog.show()
    }

    override fun getAllMessageSuccess(msgList: List<ChatMessage>) {
        mData = msgList
        finishRefresh()
        listAdapter?.setData(msgList.toMutableList())
    }

    override fun getAllMessageFail(code: Int, error: String) {
        finishRefresh()
    }

    override fun fetchProfileSuccess(members: Map<String, EaseProfile>?) {
        ChatLog.d(TAG,"fetchProfileSuccess $members")
        finishRefresh()
        refreshData()
    }

    override fun fetchChatPresenceFail(code: Int, error: String) {
        ChatLog.e(TAG,"fetchChatPresenceFail $code $error")
    }

    override fun addContactSuccess() {

    }

    override fun addContactFail(code: Int, error: String) {

    }

    override fun agreeInviteSuccess(userId: String, msg: ChatMessage) {
        ChatLog.d(TAG,"agreeInviteSuccess")
        refreshData()
        noticeViewModel?.removeInviteMsg(msg)
    }

    override fun agreeInviteFail(code: Int, error: String) {
        ChatLog.e(TAG,"agreeInviteFail $code $error")
    }

    override fun onItemSubViewClick(view: View?, position: Int) {
        when(view?.id){
            R.id.item_action -> {
                listAdapter?.mData?.let {
                    if (position < it.size){
                        it[position].getInviteMessageStatus()?.let { status ->
                            if (status == InviteMessageStatus.BEINVITEED){
                                noticeViewModel?.agreeInvite(this,it[position])
                            }
                        }
                    }
                }
            }
        }
    }

    fun finishRefresh(){
        if (binding.refreshLayout.isRefreshing){
            binding.refreshLayout.finishRefresh()
        }
    }

    open fun updateNotifyCount(){
        val useDefaultContactSystemMsg = EaseIM.getConfig()?.systemMsgConfig?.useDefaultContactSystemMsg ?: false
        if (useDefaultContactSystemMsg){
            EaseNotificationMsgManager.getInstance().markAllMessagesAsRead()
            refreshData()
        }
    }

    companion object {
        private const val TAG = "EaseNewRequestsActivity"
        fun createIntent(
            context: Context,
        ): Intent {
            val intent = Intent(context, EaseNewRequestsActivity::class.java)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }

}