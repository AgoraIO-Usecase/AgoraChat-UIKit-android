package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.model.EaseEmojicon

/**
 * big emoji icons
 *
 */
open class EaseChatRowBigExpression @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRowText(context, attrs, defStyleAttr, isSender) {
    private val imageView: ImageView? by lazy { findViewById(R.id.image) }


    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_bigexpression else R.layout.ease_row_sent_bigexpression,
            this
        )
    }

    override fun onSetUpView() {
        message?.run {
            val emojiconId: String = getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null)
            var emojicon: EaseEmojicon? = null
            if (EaseIM.getEmojiconInfoProvider() != null) {
                emojicon = EaseIM.getEmojiconInfoProvider()?.getEmojiconInfo(emojiconId)
            }
            emojicon?.let {
                if (it.bigIcon != 0) {
                    imageView?.load(it.bigIcon) {
                        placeholder(R.drawable.ease_default_expression)
                        error(R.drawable.ease_default_expression)
                    }
                } else if (!it.bigIconPath.isNullOrEmpty()) {
                    imageView?.load(it.bigIconPath) {
                        placeholder(R.drawable.ease_default_expression)
                        error(R.drawable.ease_default_expression)
                    }
                } else {
                    imageView?.load(R.drawable.ease_default_expression)
                }
            }
            //setImageIncludeThread(imageView)
        }
    }
}