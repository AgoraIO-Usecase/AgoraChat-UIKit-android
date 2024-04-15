package com.hyphenate.easeui.feature.search.interfaces

import android.view.View
import com.hyphenate.easeui.model.EaseUser

interface OnSearchUserItemClickListener {
    /**
     * item click
     * @param view
     * @param position
     * @param user
     */
    fun onSearchItemClick(view: View?, position: Int,user: EaseUser)
}