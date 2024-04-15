package com.hyphenate.easeui.feature.invitation.enums

import androidx.annotation.StringRes
import com.hyphenate.easeui.R

enum class InviteMessageStatus(@param:StringRes val msgContent: Int) {
    //==contact
    /**being invited */
    BEINVITEED(R.string.ease_invitation_reason),

    /**being refused */
    BEREFUSED(R.string.contact_listener_onFriendRequestDeclined),

    /**You have agreed to the operation */
    AGREED(R.string.ease_agreed_to),

}