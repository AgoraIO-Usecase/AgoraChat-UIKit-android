package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.widget.EaseImageView

open class EaseChatRowUserCard @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean
) : EaseChatRow(context, attrs, defStyleAttr, isSender) {
    protected val nicknameView: TextView? by lazy { findViewById(R.id.user_nick_name) }
    protected val userIdView: TextView? by lazy { findViewById(R.id.user_id) }
    protected val headImageView: EaseImageView? by lazy { findViewById(R.id.head_Image_view) }

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_user_card else R.layout.ease_row_sent_user_card,
            this
        )
    }

    override fun onSetUpView() {
        EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(headImageView)
        message?.run {
            (body as? ChatCustomMessageBody)?.let {
                val params: Map<String, String> = it.params
                val uId = params[EaseConstant.USER_CARD_ID]
                userIdView?.text = uId
                val nickName = params[EaseConstant.USER_CARD_NICK]
                nicknameView?.text = nickName
                val headUrl = params[EaseConstant.USER_CARD_AVATAR]
                headImageView?.load(headUrl) {
                    placeholder(R.drawable.ease_default_avatar)
                    error(R.drawable.ease_default_avatar)
                }
            }
        }
    }

}