package io.agora.chat.uikit.chat.interfaces;

import android.net.Uri;

import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.MessageBody;
import io.agora.chat.uikit.chat.presenter.EaseHandleMessagePresenter;
import io.agora.chat.uikit.chat.widget.EaseChatInputMenu;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;


public interface IChatLayout {

    /**
     * set external Presenter
     * @param presenter
     */
    void setPresenter(EaseHandleMessagePresenter presenter);
    /**
     * Get chat list
     * @return
     */
    EaseChatMessageListLayout getChatMessageListLayout();

    /**
     * Get input menu
     * @return
     */
    EaseChatInputMenu getChatInputMenu();

    /**
     * Get input content
     * @return
     */
    String getInputContent();

    /**
     * Whether to open the input monitoring
     * @param turnOn
     */
    void turnOnTypingMonitor(boolean turnOn);

    /**
     * Send text message
     * @param content
     */
    void sendTextMessage(String content);

    /**
     * Send text message
     * @param content
     * @param isNeedGroupAck Need group receipt
     */
    void sendTextMessage(String content, boolean isNeedGroupAck);

    /**
     * send @ message
     * @param content
     */
    void sendAtMessage(String content);

    /**
     * Send GIF message
     * @param name
     * @param identityCode
     */
    void sendBigExpressionMessage(String name, String identityCode);

    /**
     * Send voice message
     * @param filePath
     * @param length
     */
    void sendVoiceMessage(String filePath, int length);

    /**
     * Send voice message
     * @param filePath
     * @param length
     */
    void sendVoiceMessage(Uri filePath, int length);

    /**
     * Send picture message
     * @param imageUri
     */
    void sendImageMessage(Uri imageUri);

    /**
     * Send picture message
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
     * Send a combine message.
     * @param message
     */
    void sendCombineMessage(ChatMessage message);

    /**
     * Send file message
     * @param fileUri
     */
    void sendFileMessage(Uri fileUri);

    /**
     * Add extension fields to the message
     * @param message
     */
    void addMessageAttributes(ChatMessage message);

    /**
     * Send message
     * @param message
     */
    void sendMessage(ChatMessage message);

    /**
     * Resend message
     * @param message
     */
    void resendMessage(ChatMessage message);

    /**
     * delete local message
     * @param message
     */
    void deleteMessage(ChatMessage message);

    /**
     * Delete local message list.
     * @param messages
     */
    void deleteMessages(List<String> messages);

    /**
     * Recall server message
     * @param message
     */
    void recallMessage(ChatMessage message);

    /**
     * modify message
     * @param messageId
     * @param messageBodyModified
     */
    void modifyMessage(String messageId, MessageBody messageBodyModified);

    /**
     * Set the edit message listening
     * @param listener
     */
    void setOnEditMessageListener(OnModifyMessageListener listener);

    void setOnChatLayoutListener(OnChatLayoutListener listener);

    /**
     * Used to monitor touch events for sending voice
     * @param voiceTouchListener
     */
    void setOnChatRecordTouchListener(OnChatRecordTouchListener voiceTouchListener);

    /**
     * Message withdrawal monitoring
     * @param listener
     */
    void setOnRecallMessageResultListener(OnRecallMessageResultListener listener);

    /**
     * Set the attribute event before sending message
     * @param sendMsgEvent
     */
    void setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent);

    /**
     * The listener of reaction
     *
     * @param reactionListener
     */
    void setOnReactionListener(OnReactionMessageListener reactionListener);

    /**
     * Set the listener of multi-select.
     * @param listener  The listener of multi-select.
     */
    void setOnSelectClickListener(OnMessageSelectResultListener listener);
}
