package io.agora.uikit.feature.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatGroup
import io.agora.uikit.common.enums.EaseListViewType
import io.agora.uikit.common.ChatGroupOptions
import io.agora.uikit.common.ChatGroupStyle
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.extensions.createNewGroupMessage
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.databinding.EaseActivityCreateGroupLayoutBinding
import io.agora.uikit.feature.contact.EaseContactsListFragment
import io.agora.uikit.feature.group.interfaces.IEaseGroupResultView
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.search.EaseSearchType
import io.agora.uikit.interfaces.OnContactSelectedListener
import io.agora.uikit.model.getNickname
import io.agora.uikit.viewmodel.group.EaseGroupViewModel
import io.agora.uikit.viewmodel.group.IGroupRequest

open class EaseCreateGroupActivity: EaseBaseActivity<EaseActivityCreateGroupLayoutBinding>(),
    IEaseGroupResultView {

    protected var groupUserList:MutableList<String> = mutableListOf()
    private var groupViewModel: IGroupRequest? = null

    override fun getViewBinding(inflater: LayoutInflater): EaseActivityCreateGroupLayoutBinding {
        return EaseActivityCreateGroupLayoutBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    open fun initView(){
        groupViewModel = ViewModelProvider(this)[EaseGroupViewModel::class.java]
        groupViewModel?.attachView(this)
        binding.let {
            it.groupMemberCount.isEnabled = false
            val fragment1 = it.fragment.id
            it.groupMemberCount.text = getString(R.string.ease_new_group_count,0)

            val contactsListFragment = EaseContactsListFragment.Builder()
                .setListViewType(EaseListViewType.LIST_SELECT_CONTACT)
                .useTitleBar(false)
                .setSideBarVisible(true)
                .useSearchBar(true)
                .setSearchType(EaseSearchType.SELECT_USER)
                .setOnContactSelectedListener(object : OnContactSelectedListener {
                    override fun onContactSelectedChanged(
                        v: View,
                        selectedMembers: MutableList<String>
                    ) {
                        groupUserList = selectedMembers

                        it.groupMemberCount.text = resources?.getString(R.string.ease_new_group_count,groupUserList.size)
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
        chatGroupOptions.style = ChatGroupStyle.GroupStylePrivateMemberCanInvite
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
            EaseIM.getUserProvider()?.getUser(it)?.toUser()?.getNickname()?:it
        }.let {
            return if (it.size >= 3){
                it.take(3).joinToString(",")
            }else{
                if (it.isEmpty()){
                    EaseIM.getCurrentUser()?.name ?: EaseIM.getCurrentUser()?.id.toString()
                }else{
                    it.take(it.size).joinToString(",")
                }
            }
        }
    }

    override fun createGroupSuccess(group: ChatGroup) {
        binding.groupMemberCount.isEnabled = true
        ChatClient.getInstance().chatManager().saveMessage(group.createNewGroupMessage(group.groupName))
        EaseChatActivity.actionStart(mContext,group.groupId, EaseChatType.GROUP_CHAT)
        finish()
    }

    override fun createGroupFail(code: Int, error: String) {
        ChatLog.e(TAG,"createGroupFail $code $error")
        binding.groupMemberCount.isEnabled = true
        finish()
    }

    companion object {
        private const val TAG = "EaseCreateGroupActivity"
        fun actionStart(context: Context) {
            val intent = Intent(context, EaseCreateGroupActivity::class.java)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return context.startActivity(it)
                }
            }
            context.startActivity(intent)
        }
    }
}