package com.hyphenate.easeui.feature.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.UIKitChatActivity
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.enums.ChatUIKitListViewType
import com.hyphenate.easeui.common.ChatGroupOptions
import com.hyphenate.easeui.common.ChatGroupStyle
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.extensions.createNewGroupMessage
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.databinding.UikitActivityCreateGroupLayoutBinding
import com.hyphenate.easeui.feature.contact.ChatUIKitContactsListFragment
import com.hyphenate.easeui.feature.group.interfaces.IUIKitGroupResultView
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.search.ChatUIKitSearchType
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.viewmodel.group.ChatUIKitGroupViewModel
import com.hyphenate.easeui.viewmodel.group.IGroupRequest

open class ChatUIKitCreateGroupActivity:ChatUIKitBaseActivity<UikitActivityCreateGroupLayoutBinding>(),
    IUIKitGroupResultView {

    protected var groupUserList:MutableList<String> = mutableListOf()
    private var groupViewModel: IGroupRequest? = null

    override fun getViewBinding(inflater: LayoutInflater): UikitActivityCreateGroupLayoutBinding {
        return UikitActivityCreateGroupLayoutBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    open fun initView(){
        groupViewModel = ViewModelProvider(this)[ChatUIKitGroupViewModel::class.java]
        groupViewModel?.attachView(this)
        binding.let {
            it.groupMemberCount.isEnabled = false
            val fragment1 = it.fragment.id
            it.groupMemberCount.text = getString(R.string.uikit_new_group_count,0)

            val contactsListFragment = ChatUIKitContactsListFragment.Builder()
                .setListViewType(ChatUIKitListViewType.LIST_SELECT_CONTACT)
                .useTitleBar(false)
                .setSideBarVisible(true)
                .useSearchBar(true)
                .setSearchType(ChatUIKitSearchType.SELECT_USER)
                .setOnContactSelectedListener(object : OnContactSelectedListener {
                    override fun onContactSelectedChanged(
                        v: View,
                        selectedMembers: MutableList<String>
                    ) {
                        groupUserList = selectedMembers

                        it.groupMemberCount.text = resources?.getString(R.string.uikit_new_group_count,groupUserList.size)
                        it.groupMemberCount.isSelected = groupUserList.size > 0
                        it.groupMemberCount.isEnabled = groupUserList.size > 0
                    }
                })
                .build()

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(fragment1, contactsListFragment,"createGroup")
            transaction.commit()
        }
    }

    open fun initListener(){
        binding.let {
            it.iconBack.setOnClickListener{
                finish()
            }
            it.groupMemberCount.setOnClickListener{
                if (binding.groupMemberCount.isEnabled){
                    binding.groupMemberCount.isEnabled = false
                }
                createGroup()
            }
        }
    }

    open fun createGroup(){
        val chatGroupOptions = ChatGroupOptions()
        chatGroupOptions.style = ChatGroupStyle.EMGroupStylePrivateMemberCanInvite
        groupViewModel?.createGroup(
            groupName = getGroupNameRule(),
            desc = "",
            groupUserList,
            reason = "",
            options = chatGroupOptions
        )
    }

    open fun getGroupNameRule():String{
        val data = groupUserList
        data.map {
            ChatUIKitClient.getUserProvider()?.getUser(it)?.toUser()?.getNickname()?:it
        }.let {
            return if (it.size >= 3){
                it.take(3).joinToString(",")
            }else{
                if (it.isEmpty()){
                    ChatUIKitClient.getCurrentUser()?.name ?: ChatUIKitClient.getCurrentUser()?.id.toString()
                }else{
                    it.take(it.size).joinToString(",")
                }
            }
        }
    }

    override fun createGroupSuccess(group: ChatGroup) {
        binding.groupMemberCount.isEnabled = true
        ChatClient.getInstance().chatManager().saveMessage(group.createNewGroupMessage(group.groupName))
        UIKitChatActivity.actionStart(mContext,group.groupId, ChatUIKitType.GROUP_CHAT)
        finish()
    }

    override fun createGroupFail(code: Int, error: String) {
        ChatLog.e(TAG,"createGroupFail $code $error")
        binding.groupMemberCount.isEnabled = true
        finish()
    }

    companion object {
        private const val TAG = "ChatUIKitCreateGroupActivity"
        fun actionStart(context: Context) {
            val intent = Intent(context, ChatUIKitCreateGroupActivity::class.java)
            ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return context.startActivity(it)
                }
            }
            context.startActivity(intent)
        }
    }
}