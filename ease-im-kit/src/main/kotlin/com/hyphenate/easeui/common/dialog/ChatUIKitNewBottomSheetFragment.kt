package com.hyphenate.easeui.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.UIKitChatActivity
import com.hyphenate.easeui.base.ChatUIKitContainChildBottomSheetFragment
import com.hyphenate.easeui.feature.contact.ChatUIKitContactsListFragment
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.chat.UIKitNewChatFragment
import com.hyphenate.easeui.feature.search.ChatUIKitSearchType
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.ChatUIKitUser

class ChatUIKitNewBottomSheetFragment: ChatUIKitContainChildBottomSheetFragment() {
    override val childFragment: Fragment
        get() {
            return ChatUIKitContactsListFragment.Builder()
                .useTitleBar(true)
                .setTitleBarTitle(getString(R.string.uikit_chat_new_conversation))
                .useSearchBar(true)
                .setDefaultMenuVisible(false)
                .enableTitleBarPressBack(true)
                .setTitleBarBackPressListener {
                    hide()
                }
                .setHeaderItemVisible(false)
                .setOnUserListItemClickListener(object : OnUserListItemClickListener {
                    override fun onUserListItemClick(v: View?, position: Int, user: ChatUIKitUser?) {
                        user?.let {
                            UIKitChatActivity.actionStart(context!!, it.userId, ChatUIKitType.SINGLE_CHAT)
                        }
                        this@ChatUIKitNewBottomSheetFragment.hide()
                    }
                })
                .setSearchType(ChatUIKitSearchType.USER)
                .setCustomFragment(UIKitNewChatFragment())
                .build()
        }


    override fun showExpandedState(): Boolean {
        return true
    }

    override fun setTitleBar() {
        super.setTitleBar()
        binding?.titleBar?.visibility = View.GONE
    }

}