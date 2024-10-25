package io.agora.uikit.feature.chat.config

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.configs.EaseAvatarConfig
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.feature.chat.widgets.EaseChatMessageListLayout.ShowType
import io.agora.uikit.widget.EaseImageView

/**
 * The configuration to set the style of the message item.
 */
data class EaseChatMessageItemConfig(
    /**
     * The size of the message text, when message type is text.
     */
    var textSize: Int = -1,

    /**
     * The color of the message text, when message type is text.
     */
    var textColor: Int = -1,
    /**
     * The text size of time component.
     */
    var timeTextSize: Int = -1,
    /**
     * The text color of time component.
     */
    var timeTextColor: Int = -1,
    /**
     * The background of time component.
     */
    var timeBackground: Drawable? = null,
    /**
     * Set dedault avatar resource.
     */
    var avatarSrc: Drawable? = null,
    /**
     * Set the config of avatar.
     */
    var avatarConfig: EaseAvatarConfig = EaseIM.getConfig()?.avatarConfig?.copy() ?: EaseAvatarConfig(),
    /**
     * Set the minimum height of the item.
     */
    var itemMinHeight: Int = -1,
    /**
     * Set whether to show the nickname.
     */
    var showNickname: Boolean = true,
    /**
     * Set whether to show the avatar.
     */
    var showAvatar: Boolean = true,
    /**
     * Set whether to hide the avatar of receiver.
     */
    var hideReceiverAvatar: Boolean = false,
    /**
     * Set whether to hide the avatar of sender.
     */
    var hideSenderAvatar: Boolean = false,
    /**
     * Set the receiver bubble background.
     */
    var receiverBackground: Drawable? = null,
    /**
     * Set the sender bubble background.
     */
    var senderBackground: Drawable? = null,
    /**
     * Set the item show type.
     */
    var showType: ShowType = ShowType.NORMAL,
) {

    internal companion object {

        operator fun invoke(context: Context, attrs: AttributeSet?): EaseChatMessageItemConfig {
            val itemConfig = EaseChatMessageItemConfig()
            context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageListLayout).let { a ->
                a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_size, -1f).let {
                    if (it != -1f) itemConfig.textSize = it.toInt()
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color, -1).let {
                    if (it != -1) itemConfig.textColor = ContextCompat.getColor(context, it)
                }
                a.getColor(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color, -1).let {
                    if (it != -1) itemConfig.textColor = it
                }
                a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_min_height, -1f).let {
                    if (it != -1f) itemConfig.itemMinHeight = it.toInt()
                }
                a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_size, -1f).let {
                    if (it != -1f) itemConfig.timeTextSize = it.toInt()
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color, -1).let {
                    if (it != -1) itemConfig.timeTextColor = ContextCompat.getColor(context, it)
                }
                a.getColor(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color, -1).let {
                    if (it != -1) itemConfig.timeTextColor = it
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_background, -1).let {
                    if (it != -1) itemConfig.timeBackground = ContextCompat.getDrawable(context, it)
                }
                a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_background)?.let {
                    itemConfig.timeBackground = it
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_default_src, -1).let {
                    if (it != -1) itemConfig.avatarSrc = ContextCompat.getDrawable(context, it)
                }
                a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_default_src)?.let {
                    itemConfig.avatarSrc = it
                }
                a.getInteger(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_shape_type, -1).let {
                    if (it != -1) itemConfig.avatarConfig.avatarShape = EaseImageView.ShapeType.values()[it]
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_sender_background, -1).let {
                    if (it != -1) itemConfig.senderBackground = ContextCompat.getDrawable(context, it)
                }
                a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_sender_background)?.let {
                    itemConfig.senderBackground = it
                }
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_receiver_background, -1).let {
                    if (it != -1) itemConfig.receiverBackground = ContextCompat.getDrawable(context, it)
                }
                a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_receiver_background)?.let {
                    itemConfig.receiverBackground = it
                }
                a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_nickname, true).let {
                    itemConfig.showNickname = it
                }
                a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_avatar, true).let {
                    itemConfig.showAvatar = it
                }
                a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_hide_receiver_avatar, false).let {
                    itemConfig.hideReceiverAvatar = it
                }
                a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_hide_sender_avatar, false).let {
                    itemConfig.hideSenderAvatar = it
                }
                a.getInteger(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_type, -1).let {
                    if (it != -1) itemConfig.showType = ShowType.values()[it]
                }
                a.recycle()
            }
            return itemConfig
        }
    }
}

/**
 * Set chat item avatar configures.
 * @param userAvatarView
 * @param isSender
 */
internal fun EaseChatMessageItemConfig.setAvatarConfig(userAvatarView: ImageView?, isSender: Boolean) {
    userAvatarView?.let {
        it.visibility = View.GONE
        if (showAvatar) {
            it.visibility = View.VISIBLE
            if (it !is EaseImageView) {
                return
            }
            avatarSrc?.run { it.setImageDrawable(this) }
            avatarConfig.setAvatarStyle(it)
        }
        if (hideReceiverAvatar && !isSender) {
            it.visibility = View.GONE
        }
        if (hideSenderAvatar && isSender) {
            it.visibility = View.GONE
        }
    }
}

/**
 * Set chat item nickname configures.
 */
internal fun EaseChatMessageItemConfig.setNicknameConfig(userNicknameView: TextView?, isSender: Boolean) {
    userNicknameView?.let {
        it.visibility = View.GONE
        if (showType != ShowType.NORMAL) {
            it.visibility = View.VISIBLE
        } else {
            it.visibility = if (showNickname && !isSender) View.VISIBLE else View.GONE
        }
    }
}

/**
 * Set text message item's min height.
 */
internal fun EaseChatMessageItemConfig.setTextMessageMinHeight(bubble: ViewGroup?) {
    bubble?.let {
        if (itemMinHeight != -1) it.minimumHeight = itemMinHeight
    }
}

/**
 * Set text message text view's configures.
 */
internal fun EaseChatMessageItemConfig.setTextMessageTextConfigs(content: TextView?) {
    content?.let {
        if (textSize != -1) it.textSize = textSize.toFloat()
        if (textColor != -1) it.setTextColor(textColor)
    }
}

/**
 * Set bubble's background by configs.
 */
internal fun EaseChatMessageItemConfig.resetBubbleBackground(bubble: ViewGroup?, isSender: Boolean) {
    bubble?.let {
        if (isSender) {
            senderBackground?.run { it.background = this }
        } else {
            receiverBackground?.run { it.background = this }
        }
    }
}

/**
 * Set time textView's configures.
 */
internal fun EaseChatMessageItemConfig.setTimeTextConfig(timeView: TextView?) {
    timeView?.let {
        if (timeTextSize != -1) it.textSize = timeTextSize.toFloat()
        if (timeTextColor != -1) it.setTextColor(timeTextColor)
        timeBackground?.run { it.background = this }
    }
}
