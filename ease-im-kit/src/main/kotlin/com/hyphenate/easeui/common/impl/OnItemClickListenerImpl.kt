package com.hyphenate.easeui.common.impl

import android.view.View
import com.hyphenate.easeui.interfaces.OnItemClickListener

class OnItemClickListenerImpl(
    private val onItemClick: (view: View?, position: Int) -> Unit
): OnItemClickListener {
    override fun onItemClick(view: View?, position: Int) {
        onItemClick.invoke(view, position)
    }
}