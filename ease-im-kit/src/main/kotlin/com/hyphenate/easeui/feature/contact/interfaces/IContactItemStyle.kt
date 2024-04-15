package com.hyphenate.easeui.feature.contact.interfaces

import com.hyphenate.easeui.feature.conversation.interfaces.IConvItemTextStyle
import com.hyphenate.easeui.common.interfaces.IAvatarStyle

interface IContactItemStyle : IAvatarStyle, IConvItemTextStyle {
    fun setItemHeight(height: Int)
}