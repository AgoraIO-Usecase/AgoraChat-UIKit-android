package io.agora.chat.uikit.widget.chatrow;

import android.text.style.URLSpan;
import android.view.View;

import io.agora.chat.uikit.R;


public class AutolinkSpan extends URLSpan {

    public AutolinkSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        if (widget.getTag(R.id.action_chat_long_click) != null) {
            widget.setTag(R.id.action_chat_long_click, null);
            return;
        }
        super.onClick(widget);
    }
}
