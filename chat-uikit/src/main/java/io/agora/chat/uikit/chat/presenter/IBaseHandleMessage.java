package io.agora.chat.uikit.chat.presenter;

import android.net.Uri;

import io.agora.chat.ChatMessage;

public interface IBaseHandleMessage {

    /**
     * Send text message
     * @param content
     * @param isNeedGroupAck Whether need a group receipt
     */
    void sendTextMessage(String content, boolean isNeedGroupAck);

    /**
     * Send voice message
     * @param filePath
     * @param length
     */
    void sendVoiceMessage(Uri filePath, int length);

    /**
     * Send image message
     * @param imageUri
     * @param sendOriginalImage
     */
    void sendImageMessage(Uri imageUri, boolean sendOriginalImage);

    /**
     * Send location message
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    void sendLocationMessage(double latitude, double longitude, String locationAddress);

    /**
     * Send video message
     * @param videoUri
     * @param videoLength
     */
    void sendVideoMessage(Uri videoUri, int videoLength);

    /**
     * Send file message
     * @param fileUri
     */
    void sendFileMessage(Uri fileUri);

    /**
     * Send message
     * @param message
     */
    void sendMessage(ChatMessage message);
}
