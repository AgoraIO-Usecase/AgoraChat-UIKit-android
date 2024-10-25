package io.agora.uikit.common.dialog

import android.view.View
import androidx.fragment.app.Fragment
import io.agora.uikit.R
import io.agora.uikit.base.EaseContainChildBottomSheetFragment
import io.agora.uikit.feature.chat.EaseAddUserCardFragment
import io.agora.uikit.feature.contact.EaseContactsListFragment
import io.agora.uikit.feature.search.EaseSearchType
import io.agora.uikit.interfaces.OnUserListItemClickListener
import io.agora.uikit.model.EaseUser

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
                .setDefaultMenuVisible(false)
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