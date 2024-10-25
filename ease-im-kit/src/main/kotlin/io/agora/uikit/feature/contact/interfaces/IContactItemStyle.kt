package io.agora.uikit.feature.contact.interfaces

import io.agora.uikit.feature.conversation.interfaces.IConvItemTextStyle
import io.agora.uikit.common.interfaces.IAvatarStyle

interface IContactItemStyle : IAvatarStyle, IConvItemTextStyle {
    fun setItemHeight(height: Int)
}