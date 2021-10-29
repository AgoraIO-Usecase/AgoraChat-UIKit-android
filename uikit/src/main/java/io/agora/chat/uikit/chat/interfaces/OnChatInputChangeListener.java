package io.agora.chat.uikit.chat.interfaces;

public interface OnChatInputChangeListener {
    /**
     * EditText文本变化监听
     * @param s
     * @param start
     * @param before
     * @param count
     */
    void onTextChanged(CharSequence s, int start, int before, int count);
}
