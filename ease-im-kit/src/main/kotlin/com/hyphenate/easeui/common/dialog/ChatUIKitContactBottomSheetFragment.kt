package com.hyphenate.easeui.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitContainChildBottomSheetFragment
import com.hyphenate.easeui.feature.chat.ChatUIKitAddUserCardFragment
import com.hyphenate.easeui.feature.contact.ChatUIKitContactsListFragment
import com.hyphenate.easeui.feature.search.ChatUIKitSearchType
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.ChatUIKitUser

class ChatUIKitContactBottomSheetFragment: ChatUIKitContainChildBottomSheetFragment() {
    private var userListItemClickListener: OnUserListItemClickListener? = null
    override val childFragment: Fragment
        get() {
            return ChatUIKitContactsListFragment.Builder()
                .useTitleBar(true)
                .setTitleBarTitle(getString(R.string.uikit_chat_message_user_card_select_title))
                .useSearchBar(true)
                .enableTitleBarPressBack(true)
                .setTitleBarBackPressListener {
                    hide()
                }
                .setDefaultMenuVisible(false)
                .setHeaderItemVisible(false)
                .setSideBarVisible(true)
                .setSearchType(ChatUIKitSearchType.USER)
                .setOnUserListItemClickListener(object : OnUserListItemClickListener {
                    override fun onUserListItemClick(v: View?, position: Int, user: ChatUIKitUser?) {
                        userListItemClickListener?.onUserListItemClick(v, position, user)
                    }

                    override fun onAvatarClick(v: View, position: Int) {

                    }
                })
                .setCustomFragment(ChatUIKitAddUserCardFragment())
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