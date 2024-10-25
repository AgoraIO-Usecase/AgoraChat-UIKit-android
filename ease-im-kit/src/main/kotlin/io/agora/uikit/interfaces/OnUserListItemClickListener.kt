package io.agora.uikit.interfaces

import android.view.View
import io.agora.uikit.model.EaseUser

interface OnUserListItemClickListener {
    fun onAvatarClick(v: View,position:Int){}
    fun onUserListItemClick(v: View?, position:Int, user: EaseUser?){}
}