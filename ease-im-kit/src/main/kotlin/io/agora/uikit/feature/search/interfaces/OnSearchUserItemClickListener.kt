package io.agora.uikit.feature.search.interfaces

import android.view.View
import io.agora.uikit.model.EaseUser

interface OnSearchUserItemClickListener {
    /**
     * item click
     * @param view
     * @param position
     * @param user
     */
    fun onSearchItemClick(view: View?, position: Int,user: EaseUser)
}