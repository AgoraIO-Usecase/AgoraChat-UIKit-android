package io.agora.chat.uikit.conversation.interfaces;

import android.view.View;

/**
 * Item click listener for conversation
 */
public interface OnConItemClickListener<T> {
    /**
     * Item click
     * @param view
     * @param conversation
     * @param position
     */
    void onItemClick(View view, T conversation, int position);
}
