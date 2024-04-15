package com.hyphenate.easeui.interfaces

import android.view.View
import com.hyphenate.easeui.model.EaseUser

interface OnUserListItemClickListener {
    fun onAvatarClick(v: View,position:Int){}
    fun onUserListItemClick(v: View?, position:Int, user: EaseUser?){}
}