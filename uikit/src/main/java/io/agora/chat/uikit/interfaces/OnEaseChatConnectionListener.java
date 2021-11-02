package io.agora.chat.uikit.interfaces;

public interface OnEaseChatConnectionListener {
    /**
     * Call back when connected to chat server successfully
     */
    void onConnected();

    /**
     * Call back when disconnected from chat server.
     * It contains {@link #onAccountLogout(int)}
     * @param error
     */
    void onDisconnect(int error);

    /**
     * It is the sub errors of {@link #onDisconnect(int)}
     * @param error
     */
    void onAccountLogout(int error);

    /**
     * Call back when the access token has expired
     */
    void onTokenExpired();

    /**
     * Call back when the access token is about to expire
     */
    void onTokenWillExpire();
}
