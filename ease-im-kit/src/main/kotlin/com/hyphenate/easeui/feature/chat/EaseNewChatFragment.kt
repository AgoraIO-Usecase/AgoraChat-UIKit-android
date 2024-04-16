package com.hyphenate.easeui.feature.chat

import android.os.Bundle
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.EaseBottomSheetChildHelper
import com.hyphenate.easeui.feature.contact.EaseContactsListFragment

class EaseNewChatFragment: EaseContactsListFragment(), EaseBottomSheetChildHelper {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.searchBar?.setText(getString(R.string.ease_search_conversation_hint))
    }
    override val titleBarTitle: Int
        get() = R.string.ease_chat_new_conversation

    override val isShowTitleBarLeftLayout: Boolean
        get() = true
}