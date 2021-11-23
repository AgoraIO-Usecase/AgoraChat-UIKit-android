package io.agora.chat.uikit.chat.interfaces;

import android.view.MotionEvent;
import android.view.View;

public interface OnChatRecordTouchListener {
    /**
     * Record pressing events
     * @param v
     * @param event
     * @return
     */
    boolean onRecordTouch(View v, MotionEvent event);
}
