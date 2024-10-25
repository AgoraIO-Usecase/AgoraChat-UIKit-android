package io.agora.uikit.feature.chat

import android.os.Bundle
import io.agora.uikit.R
import io.agora.uikit.common.dialog.EaseBottomSheetChildHelper
import io.agora.uikit.feature.contact.EaseContactsListFragment

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