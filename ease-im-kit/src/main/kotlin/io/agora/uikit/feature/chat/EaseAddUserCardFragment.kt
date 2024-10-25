package io.agora.uikit.feature.chat

import io.agora.uikit.R
import io.agora.uikit.common.dialog.EaseBottomSheetChildHelper
import io.agora.uikit.feature.contact.EaseContactsListFragment

class EaseAddUserCardFragment: EaseContactsListFragment(), EaseBottomSheetChildHelper {

    override val titleBarTitle: Int
        get() = R.string.ease_chat_message_user_card_select_title

    override val isShowTitleBarLeftLayout: Boolean
        get() = true
}