package com.hyphenate.easeui.common.impl

import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.ChatUIKitMenuItem

class OnMenuItemClickListenerImpl(
    private val onMenuItemClick: (item: ChatUIKitMenuItem?, position: Int) -> Boolean
): OnMenuItemClickListener {
    override fun onMenuItemClick(item: ChatUIKitMenuItem?, position: Int): Boolean {
        return onMenuItemClick.invoke(item, position)
    }
}