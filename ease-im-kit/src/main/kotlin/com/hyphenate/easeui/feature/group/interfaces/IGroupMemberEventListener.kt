package com.hyphenate.easeui.feature.group.interfaces

import android.view.View
import com.hyphenate.easeui.model.ChatUIKitUser

interface IGroupMemberEventListener {

    fun onGroupMemberListItemClick(view: View?, user: ChatUIKitUser)

    fun onGroupMemberLoadSuccess(memberList:MutableList<ChatUIKitUser>){}
}