package io.agora.uikit.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import io.agora.uikit.R
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.base.EaseContainChildBottomSheetFragment
import io.agora.uikit.feature.contact.EaseContactsListFragment
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.chat.EaseNewChatFragment
import io.agora.uikit.feature.search.EaseSearchType
import io.agora.uikit.interfaces.OnUserListItemClickListener
import io.agora.uikit.model.EaseUser

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