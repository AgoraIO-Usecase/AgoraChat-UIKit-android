package com.hyphenate.easeui.repository

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatGroupManager
import com.hyphenate.easeui.common.ChatGroupOptions
import com.hyphenate.easeui.common.extensions.getOwnerInfo
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.common.helper.ContactSortedHelper
import com.hyphenate.easeui.common.suspends.addGroupMember
import com.hyphenate.easeui.common.suspends.changeChatGroupDescription
import com.hyphenate.easeui.common.suspends.changeChatGroupName
import com.hyphenate.easeui.common.suspends.changeChatGroupOwner
import com.hyphenate.easeui.common.suspends.createChatGroup
import com.hyphenate.easeui.common.suspends.destroyChatGroup
import com.hyphenate.easeui.common.suspends.fetchChatGroupMembers
import com.hyphenate.easeui.common.suspends.fetchGroupDetails
import com.hyphenate.easeui.common.suspends.fetchGroupMemberAllAttributes
import com.hyphenate.easeui.common.suspends.fetchJoinedGroupsFromServer
import com.hyphenate.easeui.common.suspends.leaveChatGroup
import com.hyphenate.easeui.common.suspends.removeChatGroupMember
import com.hyphenate.easeui.common.suspends.setGroupMemberAttributes
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.setUserInitialLetter
import com.hyphenate.easeui.provider.fetchUsersBySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseGroupRepository(
    private val groupManager: ChatGroupManager = ChatClient.getInstance().groupManager()
) {
    private var Max:Int = 1000

    init {
        EaseIM.getContext()?.resources?.getInteger(R.integer.ease_group_member_max_count)?.let {
            Max = it
        }
    }

    companion object {
        private const val TAG = "GroupRep"
        private const val LIMIT = 200
    }

    /**
     * Load joined group list from local db or server.
     */
    suspend fun loadJoinedGroupData(
        page:Int,pageSize: Int,needMemberCount:Boolean,needRole:Boolean
    ):MutableList<ChatGroup> =
        withContext(Dispatchers.IO){
            groupManager.fetchJoinedGroupsFromServer(page, pageSize, needMemberCount, needRole)
        }.toMutableList()

    suspend fun loadLocalJoinedGroupData():MutableList<ChatGroup> =
        withContext(Dispatchers.IO){
            groupManager.allGroups
        }.toMutableList()

    /**
     * create group
     */
    suspend fun createGroup(
        groupName:String,
        desc:String,
        members:MutableList<String>,
        reason:String,
        options:ChatGroupOptions,
    ):ChatGroup =
        withContext(Dispatchers.IO){
            groupManager.createChatGroup(
                groupName, desc, members, reason, options
            )
        }

    suspend fun fetchGroupDetails(
        groupId:String
    ):ChatGroup = withContext(Dispatchers.IO){
            groupManager.fetchGroupDetails(groupId)
    }

    suspend fun loadLocalMember(groupId:String):MutableList<EaseUser> =
        withContext(Dispatchers.IO) {
            val data = mutableListOf<EaseUser>()
            val currentGroup = ChatClient.getInstance().groupManager().getGroup(groupId)
            currentGroup?.members?.let {
                for (userId in it) {
                    EaseProfile.getGroupMember(groupId, userId)?.let { profile->
                        data.add(profile.toUser())
                    } ?: run {
                        data.add(EaseUser(userId))
                    }
                }
            }
            val ownerInfo = currentGroup?.getOwnerInfo()
            ownerInfo?.let { owner->
                data.add(owner)
            }
            data.forEach {
                it.setUserInitialLetter()
            }
            val sortedList = ContactSortedHelper.sortedList(data)
            sortedList.toMutableList()
        }


    suspend fun fetGroupMemberFromService(
        groupId:String
    ):MutableList<EaseUser> = withContext(Dispatchers.IO){
        val groupMemberList = mutableListOf<EaseUser>()
        var cursor: String? = null
        do {
            val result = groupManager.fetchChatGroupMembers(groupId,cursor,LIMIT)
            val data = result.data.map {
                EaseProfile.getGroupMember(groupId, it)?.toUser() ?: EaseUser(it)
            }
            cursor = result.cursor
            groupMemberList.addAll(data)
        }while (!cursor.isNullOrEmpty() && groupMemberList.size <= Max )
        groupMemberList
    }


    suspend fun addGroupMember(
        groupId: String,
        members: MutableList<String>,
    ):Int = withContext(Dispatchers.IO){
        groupManager.addGroupMember(groupId, members)
    }


    suspend fun removeGroupMember(
        groupId: String,
        members: MutableList<String>,
    ):Int = withContext(Dispatchers.IO){
        groupManager.removeChatGroupMember(groupId, members)
    }


    suspend fun changeChatGroupName(
        groupId: String,
        newName: String,
    ):Int = withContext(Dispatchers.IO){
        groupManager.changeChatGroupName(groupId, newName)
    }

    suspend fun changeChatGroupDescription(
        groupId: String,
        description: String,
    ):Int = withContext(Dispatchers.IO){
        groupManager.changeChatGroupDescription(groupId, description)
    }

    suspend fun changeChatGroupOwner(
        groupId: String,
        newOwner: String,
    ) :ChatGroup = withContext(Dispatchers.IO){
        groupManager.changeChatGroupOwner(groupId, newOwner)
    }

    suspend fun leaveChatGroup(
        groupId: String
    ) : Int = withContext(Dispatchers.IO){
        groupManager.leaveChatGroup(groupId)
    }

    suspend fun destroyChatGroup(
        groupId: String
    ):Int = withContext(Dispatchers.IO){
        groupManager.destroyChatGroup(groupId)
    }

    suspend fun fetchMemberAllAttributes(
        groupId: String,
        userList: List<String>,
        keyList: List<String>,
    ):MutableMap<String,MutableMap<String,String>> = withContext(Dispatchers.IO){
        groupManager.fetchGroupMemberAllAttributes(groupId,userList,keyList)
    }

    suspend fun setGroupMemberAttributes(
        groupId: String,
        userId: String,
        attribute:MutableMap<String,String>
    ):Int = withContext(Dispatchers.IO){
        groupManager.setGroupMemberAttributes(groupId,userId,attribute)
    }

    suspend fun fetchMemberInfo(groupId: String?, members: List<String>?) =
        withContext(Dispatchers.IO) {
            if (groupId.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "groupId is null.")
            }
            if (members.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "members is null.")
            }
            val data = members.filter {
                    EaseProfile.getGroupMember(groupId,it) == null
            }
            EaseIM.getUserProvider()?.fetchUsersBySuspend(data)
        }
}