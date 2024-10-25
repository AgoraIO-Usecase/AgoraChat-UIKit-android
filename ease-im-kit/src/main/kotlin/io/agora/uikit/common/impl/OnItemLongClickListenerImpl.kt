package io.agora.uikit.common.impl

import android.view.View
import io.agora.uikit.interfaces.OnItemLongClickListener

class OnItemLongClickListenerImpl(
    private val onItemLongClick: (view: View?, position: Int) -> Boolean
): OnItemLongClickListener {
    override fun onItemLongClick(view: View?, position: Int): Boolean {
        return onItemLongClick.invoke(view, position)
    }
}