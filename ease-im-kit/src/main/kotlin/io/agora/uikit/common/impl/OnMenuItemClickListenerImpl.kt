package io.agora.uikit.common.impl

import io.agora.uikit.interfaces.OnMenuItemClickListener
import io.agora.uikit.model.EaseMenuItem

class OnMenuItemClickListenerImpl(
    private val onMenuItemClick: (item: EaseMenuItem?, position: Int) -> Boolean
): OnMenuItemClickListener {
    override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
        return onMenuItemClick.invoke(item, position)
    }
}