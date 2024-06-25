package com.hyphenate.easeui.configs

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.extensions.getBooleanResource
import com.hyphenate.easeui.common.extensions.getIntegerResource

class EaseChatConfig {

    /**
     * The config that whether to enable the function of replying message.
     */
    var enableReplyMessage: Boolean = true

    /**
     * The config that whether to enable the function of combining message.
     */
    var enableSendCombineMessage: Boolean = true

    /**
     * The config that whether to enable the function of reaction message.
     */
    var enableMessageReaction: Boolean = false

    /**
     * The config that whether to enable the function of modifying message after sent.
     */
    var enableModifyMessageAfterSent: Boolean = true

    /**
     * The config that whether to enable the function of translation message after sent.
     */
    var enableTranslationMessage: Boolean = false

    /**
     * Default translation target language.
     */
    var targetTranslationLanguage: String = "zh"

    /**
     * Open Application first load block list from server.
     */
    var isLoadBlockListFromServer: Boolean = false

    /**
     * The config that whether to enable the function of url preview.
     */
    var enableUrlPreview: Boolean = true

    /**
     * Open Graph 协议的正则表达式模式
     */
    var titleOGPattern: String = "<meta property=\"og:title\" content=\"(.*?)\"\\s*/?>"
    var descriptionOGPattern: String = "<meta property=\"og:description\" content=\"(.*?)\"\\s*/?>"
    var imageOGPattern: String = "<meta property=\"og:image\" content=\"(.*?)\"\\s*/?>"

    /**
     *  非 Open Graph 协议的正则表达式模式
     */
    var titlePattern: String = "<title>(.*?)</title>"
    var descriptionPattern: String = "<meta\\s+name=\"description\"\\s+content=\"(.*?)\"\\s*/?>"
    var imagePattern: String = "<img[^>]*src=[^>]*>"

    var imageSrcPattern: String = "<link\\s+rel=\"image_src\"\\s+href=\"(http[^\"]*)\"\\s*/?>"

    /**
     * The config that whether to show unread notification in chat activity.
     * If true:
     * 1、When the message item is not visible, the message read ack will not be sent.
     * 2、If click the unread notification view, the channel ack will be sent.
     * 3、if it is not on the bottom of the chat list, sending new message will send the channel ack.
     * If false:
     * 1、When you receive the new message, the message read ack will be sent.
     * 2、Only when you enter the chat activity, the channel ack will be sent.
     */
    var showUnreadNotificationInChat: Boolean = true

    /**
     * The config that whether to enable the function of replying message.
     */
    var enableChatThreadMessage: Boolean = false

    var enableChatTyping: Boolean = true

    /**
     * Set the time period within which messages can be recalled, in milliseconds
     */
    var timePeriodCanRecallMessage: Long = -1L
        get() {
            if (field != -1L) return field
            if (isUIKitInitialized()) {
                return EaseIM.getContext()?.getIntegerResource(R.integer.ease_chat_message_recall_period)?.toLong() ?: -1L
            }
            return -1L
        }

    /**
     * Check if the UIKit is initialized.
     */
    private fun isUIKitInitialized(): Boolean {
        return EaseIM.isInited()
    }

    /**
     * The max width of the image that will be shown in the chat page.
     */
    internal var maxShowWidthRadio: Float = 3 / 5f

    /**
     * The max height of the image that will be shown in the chat page.
     */
    internal var maxShowHeightRadio: Float = 3 / 8f

    /**
     * The config that whether to enable the function of sending channel ack.
     */
    internal var enableSendChannelAck: Boolean = true

    internal var intervalDismissInChatList: Long = -1L
        get() {
            if (field != -1L) return field
            if (isUIKitInitialized()) {
                return EaseIM.getContext()?.getIntegerResource(R.integer.ease_chat_item_timestamp_dismiss_interval)?.toLong() ?: -1L
            }
            return -1L
        }
}