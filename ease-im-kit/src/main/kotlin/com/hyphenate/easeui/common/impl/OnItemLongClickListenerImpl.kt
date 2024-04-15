package com.hyphenate.easeui.common.impl

import android.view.View
import com.hyphenate.easeui.interfaces.OnItemLongClickListener

class OnItemLongClickListenerImpl(
    private val onItemLongClick: (view: View?, position: Int) -> Boolean
): OnItemLongClickListener {
    override fun onItemLongClick(view: View?, position: Int): Boolean {
        return onItemLongClick.invoke(view, position)
    }
}