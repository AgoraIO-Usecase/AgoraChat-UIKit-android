package com.hyphenate.easeui.feature.chat

import android.os.Bundle
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.ChatUIKitBottomSheetChildHelper
import com.hyphenate.easeui.feature.contact.ChatUIKitContactsListFragment

class UIKitNewChatFragment: ChatUIKitContactsListFragment(), ChatUIKitBottomSheetChildHelper {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.searchBar?.setText(getString(R.string.uikit_search_conversation_hint))
    }
    override val titleBarTitle: Int
        get() = R.string.uikit_chat_new_conversation

    override val isShowTitleBarLeftLayout: Boolean
        get() = true
}