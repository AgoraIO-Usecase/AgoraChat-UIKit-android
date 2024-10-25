package io.agora.uikit.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import coil.load
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.ChatCustomMessageBody
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.widget.EaseImageView

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
                if (nickName.isNullOrEmpty()){
                    nicknameView?.visibility = GONE
                    userIdView?.visibility = VISIBLE
                }else{
                    nicknameView?.visibility = VISIBLE
                    userIdView?.visibility = GONE
                }
                val headUrl = params[EaseConstant.USER_CARD_AVATAR]
                headImageView?.load(headUrl) {
                    placeholder(R.drawable.ease_default_avatar)
                    error(R.drawable.ease_default_avatar)
                }
            }
        }
    }

}