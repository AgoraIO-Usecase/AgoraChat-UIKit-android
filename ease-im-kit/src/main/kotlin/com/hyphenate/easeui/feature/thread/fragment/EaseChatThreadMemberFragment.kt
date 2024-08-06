package com.hyphenate.easeui.feature.thread.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.common.ChatCursorResult
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.isOwner
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.feature.group.fragments.EaseGroupMemberFragment
import com.hyphenate.easeui.feature.thread.interfaces.IChatThreadResultView
import com.hyphenate.easeui.feature.thread.interfaces.OnThreadMemberItemClickListener
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.viewmodel.thread.EaseChatThreadViewModel
import com.hyphenate.easeui.viewmodel.thread.IChatThreadRequest

class EaseChatThreadMemberFragment:EaseGroupMemberFragment(), IChatThreadResultView {

    private var listener:OnThreadMemberItemClickListener?=null
    private var viewModel: IChatThreadRequest? = null
    private var data:MutableList<EaseUser> = mutableListOf()
    private var chatThreadId:String? = null
    private var cursor:String = ""

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.let {
            chatThreadId = it.getString(EaseConstant.THREAD_CHAT_THREAD_ID)
        }
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this)[EaseChatThreadViewModel::class.java]
        viewModel?.attachView(this)
    }
    override fun initData() {
        loadData()
    }

    override fun loadData(){
        chatThreadId?.let { viewModel?.getChatThreadMembers(it,LIMIT,cursor) }
    }

    override fun refreshData() {
        cursor = ""
        loadData()
    }

    fun removeMember(userId:String){
        chatThreadId?.let {
            viewModel?.removeMemberFromChatThread(it,userId)
        }
    }

    fun isGroupOwner():Boolean{
        currentGroup?.let {
            return it.isOwner()
        }
        return false
    }

    override fun onItemClick(view: View?, position: Int) {
        ChatLog.e(TAG,"onThreadMemberItemClick $position ${data.size} ${data[position]}")
        if (data.isNotEmpty() && position < data.size){
            listener?.onThreadMemberItemClick(view,data[position].userId)
        }
    }

    fun setOnThreadMemberItemClickListener(listener: OnThreadMemberItemClickListener){
        this.listener = listener
    }

    override fun getChatThreadMembersSuccess(result: ChatCursorResult<String>) {
        ChatLog.e(TAG,"getChatThreadMembersSuccess ${data.size}")
        finishRefresh()
        cursor = result.cursor
        val resultList = result.data.map {
            groupId?.let { id -> EaseProfile.getGroupMember(id,it)?.toUser() } ?: EaseUser(it)
        }
        data = resultList.toMutableList()
        mListAdapter.setData(data)
    }

    override fun getChatThreadMembersFail(code: Int, message: String?) {
        finishRefresh()
        ChatLog.e(TAG,"getChatThreadMembersFail $code $message")
    }

    override fun removeMemberFromChatThreadSuccess(member:String) {
        ChatLog.e(TAG,"removeMemberFromChatThreadSuccess $member")
        var position = -1
        mListAdapter.mData?.forEachIndexed { index, easeUser ->
            if (easeUser.userId == member){
                position = index
            }
        }
        if (position != -1){
            mListAdapter.mData?.removeAt(position)
            mListAdapter.notifyDataSetChanged()
        }
    }

    override fun removeMemberFromChatThreadFail(code: Int, message: String?) {
        ChatLog.e(TAG,"removeMemberFromChatThreadFail $code $message")
    }

    companion object {
        private const val TAG = "EaseChatThreadMemberFragment"
        private const val LIMIT:Int = 10
    }

}