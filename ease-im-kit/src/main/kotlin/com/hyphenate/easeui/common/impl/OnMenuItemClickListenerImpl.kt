package com.hyphenate.easeui.common.impl

import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem

class OnMenuItemClickListenerImpl(
    private val onMenuItemClick: (item: EaseMenuItem?, position: Int) -> Boolean
): OnMenuItemClickListener {
    override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
        return onMenuItemClick.invoke(item, position)
    }
}