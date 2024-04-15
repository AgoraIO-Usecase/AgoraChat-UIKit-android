package com.hyphenate.easeui.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseContainChildBottomSheetFragment
import com.hyphenate.easeui.feature.chat.EaseAddUserCardFragment
import com.hyphenate.easeui.feature.contact.EaseContactsListFragment
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.EaseUser

class EaseContactBottomSheetFragment: EaseContainChildBottomSheetFragment() {
    private var userListItemClickListener: OnUserListItemClickListener? = null
    override val childFragment: Fragment
        get() {
            return EaseContactsListFragment.Builder()
                .useTitleBar(true)
                .setTitleBarTitle(getString(R.string.ease_chat_message_user_card_select_title))
                .useSearchBar(true)
                .enableTitleBarPressBack(true)
                .setTitleBarBackPressListener {
                    hide()
                }
                .setHeaderItemVisible(false)
                .setSideBarVisible(true)
                .setSearchType(EaseSearchType.USER)
                .setOnUserListItemClickListener(object : OnUserListItemClickListener {
                    override fun onUserListItemClick(v: View?, position: Int, user: EaseUser?) {
                        userListItemClickListener?.onUserListItemClick(v, position, user)
                    }

                    override fun onAvatarClick(v: View, position: Int) {

                    }
                })
                .setCustomFragment(EaseAddUserCardFragment())
                .build()
        }


    override fun showExpandedState(): Boolean {
        return true
    }

    override fun setTitleBar() {
        super.setTitleBar()
        binding?.titleBar?.visibility = View.GONE
    }

    fun setOnUserListItemClickListener(listener:OnUserListItemClickListener?) {
        this.userListItemClickListener = listener
    }

}