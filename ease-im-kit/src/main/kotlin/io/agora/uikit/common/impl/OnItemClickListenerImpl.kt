package io.agora.uikit.common.impl

import android.view.View
import io.agora.uikit.interfaces.OnItemClickListener

class OnItemClickListenerImpl(
    private val onItemClick: (view: View?, position: Int) -> Unit
): OnItemClickListener {
    override fun onItemClick(view: View?, position: Int) {
        onItemClick.invoke(view, position)
    }
}