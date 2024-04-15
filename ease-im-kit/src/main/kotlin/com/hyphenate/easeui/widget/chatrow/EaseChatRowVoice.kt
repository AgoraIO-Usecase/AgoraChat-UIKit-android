package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatDownloadStatus
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatVoiceMessageBody
import com.hyphenate.easeui.common.extensions.isSend
import com.hyphenate.easeui.common.helper.EaseChatRowVoicePlayer
import java.io.File

open class EaseChatRowVoice @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRowFile(context, attrs, defStyleAttr, isSender) {
    protected var voiceImageView: ImageView? = null
    protected var voiceLengthView: TextView? = null
    private var readStatusView: ImageView? = null
    protected var voiceAnimation: AnimationDrawable? = null

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_voice else R.layout.ease_row_sent_voice,
            this
        )
    }

    override fun onFindViewById() {
        voiceImageView = findViewById<View>(R.id.iv_voice) as? ImageView
        voiceLengthView = findViewById<View>(R.id.tv_length) as? TextView
        readStatusView = findViewById<View>(R.id.iv_unread_voice) as? ImageView
    }

    override fun onSetUpView() {
        message?.run {
            val voiceBody = body as ChatVoiceMessageBody
            val len: Int = voiceBody.length
            var padding = 0
            if (len > 0) {
                padding = getVoicePadding(len)
                voiceLengthView?.text = "${voiceBody.length}\""
                voiceLengthView?.visibility = VISIBLE
            } else {
                voiceLengthView?.visibility = INVISIBLE
            }
            if (!isSender) {
                voiceImageView?.setImageResource(R.drawable.ease_chatfrom_voice_playing)
                voiceLengthView?.setPadding(padding, 0, 0, 0)
            } else {
                voiceImageView?.setImageResource(R.drawable.ease_chatto_voice_playing)
                voiceLengthView?.setPadding(0, 0, padding, 0)
            }
            if (!isSend()) {
                val downloadStatus = voiceBody.downloadStatus()
                if (downloadStatus === ChatDownloadStatus.PENDING &&
                    ChatClient.getInstance().options.autodownloadThumbnail
                ) {
                    if (isVoiceFileExit(voiceBody)) {
                        ChatClient.getInstance().chatManager().downloadAttachment(this)
                    }
                }
                if (readStatusView != null) {
                    if (isListened) {
                        // hide the unread icon
                        readStatusView?.visibility = INVISIBLE
                    } else {
                        readStatusView?.visibility = VISIBLE
                    }
                }
                ChatLog.d(TAG, "it is receive msg")
                if (voiceBody.downloadStatus() === ChatDownloadStatus.DOWNLOADING ||
                    voiceBody.downloadStatus() === ChatDownloadStatus.PENDING
                ) {
                    if (ChatClient.getInstance().options
                            .autodownloadThumbnail && isVoiceFileExit(voiceBody)
                    ) {
                        progressBar?.visibility = VISIBLE
                    } else {
                        progressBar?.visibility = INVISIBLE
                    }
                } else {
                    progressBar?.visibility = INVISIBLE
                }
            } else {
                // hide the unread icon
                if (readStatusView != null) {
                    readStatusView?.visibility = INVISIBLE
                }
            }

            // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
            val voicePlayer: EaseChatRowVoicePlayer = EaseChatRowVoicePlayer.getInstance(context)
            if (voicePlayer.isPlaying && msgId.equals(voicePlayer.currentPlayingId)) {
                startVoicePlayAnimation()
            }
        }
    }

    /**
     * Check if the voice file exits.
     * @param voiceBody
     * @return
     */
    protected fun isVoiceFileExit(voiceBody: ChatVoiceMessageBody): Boolean {
        return File(voiceBody.localUrl).exists()
    }

    fun getVoicePadding(voiceLen: Int): Int {
        return when(voiceLen) {
            in 1..9 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_3)
            in 10 .. 19 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_11)
            in 20 .. 29 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_31)
            in 30 .. 39 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_55)
            in 40 .. 49 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_80)
            in 50 .. 59 -> context.resources.getDimensionPixelSize(R.dimen.ease_size_100)
            else -> {
                if (voiceLen >= 60) {
                    context.resources.getDimensionPixelSize(R.dimen.ease_size_150)
                } else {
                    context.resources.getDimensionPixelSize(R.dimen.ease_size_0)
                }
            }
        }
    }

    protected fun onViewUpdate(msg: ChatMessage) {

        // Only the received message has the attachment download status.
        if (msg.isSend()) {
            return
        }
        val voiceBody = msg.body as ChatVoiceMessageBody
        if (voiceBody.downloadStatus() === ChatDownloadStatus.DOWNLOADING ||
            voiceBody.downloadStatus() === ChatDownloadStatus.PENDING
        ) {
            progressBar?.visibility = VISIBLE
        } else {
            progressBar?.visibility = INVISIBLE
        }
    }

    fun startVoicePlayAnimation() {
        message?.run {
            if (!isSender) {
                voiceImageView?.setImageResource(R.drawable.voice_from_icon)
            } else {
                voiceImageView?.setImageResource(R.drawable.voice_to_icon)
            }
            voiceAnimation = voiceImageView?.drawable as AnimationDrawable
            voiceAnimation?.start()

            // Hide the voice item not listened status view.
            if (!isSend()) {
                if (readStatusView != null) {
                    readStatusView?.visibility = INVISIBLE
                }
            }
        }
    }

    fun stopVoicePlayAnimation() {
        voiceAnimation?.stop()
        message?.run {
            if (!isSender) {
                voiceImageView?.setImageResource(R.drawable.ease_chatfrom_voice_playing)
            } else {
                voiceImageView?.setImageResource(R.drawable.ease_chatto_voice_playing)
            }
        }
    }

    companion object {
        private val TAG = EaseChatRowVoice::class.java.simpleName
    }
}