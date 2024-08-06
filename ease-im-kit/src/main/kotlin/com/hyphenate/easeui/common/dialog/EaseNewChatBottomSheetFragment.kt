package com.hyphenate.easeui.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.base.EaseContainChildBottomSheetFragment
import com.hyphenate.easeui.feature.contact.EaseContactsListFragment
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.chat.EaseNewChatFragment
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.EaseUser

class EaseNewChatBottomSheetFragment: EaseContainChildBottomSheetFragment() {
    override val childFragment: Fragment
        get() {
            return EaseContactsListFragment.Builder()
                .useTitleBar(true)
                .setTitleBarTitle(getString(R.string.ease_chat_new_conversation))
                .useSearchBar(true)
                .setDefaultMenuVisible(false)
                .enableTitleBarPressBack(true)
                .setTitleBarBackPressListener {
                    hide()
                }
                .setHeaderItemVisible(false)
                .setOnUserListItemClickListener(object : OnUserListItemClickListener {
                    override fun onUserListItemClick(v: View?, position: Int, user: EaseUser?) {
                        user?.let {
                            EaseChatActivity.actionStart(context!!, it.userId, EaseChatType.SINGLE_CHAT)
                        }
                        this@EaseNewChatBottomSheetFragment.hide()
                    }
                })
                .setSearchType(EaseSearchType.USER)
                .setCustomFragment(EaseNewChatFragment())
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