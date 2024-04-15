package com.hyphenate.easeui.feature.contact.interfaces

import com.hyphenate.easeui.model.EaseUser

interface OnLoadContactListener {
    fun loadContactListSuccess(userList: MutableList<EaseUser>)

    fun loadContactListFail(code: Int, error: String)
}