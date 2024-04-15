package com.hyphenate.easeui.feature.group.interfaces

import android.view.View
import com.hyphenate.easeui.model.EaseUser

interface IGroupMemberEventListener {

    fun onGroupMemberListItemClick(view: View?, user: EaseUser)

    fun onGroupMemberLoadSuccess(memberList:MutableList<EaseUser>){}
}