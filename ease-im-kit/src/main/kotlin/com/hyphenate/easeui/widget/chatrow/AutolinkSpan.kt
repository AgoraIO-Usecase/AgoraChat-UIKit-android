package com.hyphenate.easeui.widget.chatrow

import android.text.style.URLSpan
import android.view.View
import com.hyphenate.easeui.R

class AutolinkSpan(url: String?) : URLSpan(url) {
    override fun onClick(widget: View) {
        if (widget.getTag(R.id.action_chat_long_click) != null) {
            widget.setTag(R.id.action_chat_long_click, null)
            return
        }
        super.onClick(widget)
    }
}