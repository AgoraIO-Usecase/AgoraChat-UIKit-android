package com.hyphenate.easeui.feature.chat

import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.ChatUIKitBottomSheetChildHelper
import com.hyphenate.easeui.feature.contact.ChatUIKitContactsListFragment

class ChatUIKitAddUserCardFragment: ChatUIKitContactsListFragment(), ChatUIKitBottomSheetChildHelper {

    override val titleBarTitle: Int
        get() = R.string.uikit_chat_message_user_card_select_title

    override val isShowTitleBarLeftLayout: Boolean
        get() = true
}