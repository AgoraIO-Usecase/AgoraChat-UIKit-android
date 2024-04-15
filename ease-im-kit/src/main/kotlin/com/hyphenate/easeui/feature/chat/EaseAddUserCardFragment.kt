package com.hyphenate.easeui.feature.chat

import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.dialog.EaseBottomSheetChildHelper
import com.hyphenate.easeui.feature.contact.EaseContactsListFragment

class EaseAddUserCardFragment: EaseContactsListFragment(), EaseBottomSheetChildHelper {

    override val titleBarTitle: Int
        get() = R.string.ease_chat_message_user_card_select_title

    override val isShowTitleBarLeftLayout: Boolean
        get() = true
}