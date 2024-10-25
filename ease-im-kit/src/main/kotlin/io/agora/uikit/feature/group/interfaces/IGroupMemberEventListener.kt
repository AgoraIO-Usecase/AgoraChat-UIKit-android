package io.agora.uikit.feature.group.interfaces

import android.view.View
import io.agora.uikit.model.EaseUser

interface IGroupMemberEventListener {

    fun onGroupMemberListItemClick(view: View?, user: EaseUser)

    fun onGroupMemberLoadSuccess(memberList:MutableList<EaseUser>){}
}