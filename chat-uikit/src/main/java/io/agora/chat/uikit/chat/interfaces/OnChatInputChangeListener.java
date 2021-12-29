package io.agora.chat.uikit.chat.interfaces;

public interface OnChatInputChangeListener {
    /**
     * EditText text change monitoring
     * @param s
     * @param start
     * @param before
     * @param count
     */
    void onTextChanged(CharSequence s, int start, int before, int count);
}
