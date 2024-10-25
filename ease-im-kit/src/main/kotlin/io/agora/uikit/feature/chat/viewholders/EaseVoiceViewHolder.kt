package io.agora.uikit.feature.chat.viewholders

import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatDownloadStatus
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageDirection
import io.agora.uikit.common.ChatMessageStatus
import io.agora.uikit.common.ChatVoiceMessageBody
import io.agora.uikit.common.helper.EaseChatRowVoicePlayer
import io.agora.uikit.widget.chatrow.EaseChatRowVoice
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatType
import java.io.File

class EaseVoiceViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    private val voicePlayer: EaseChatRowVoicePlayer = EaseChatRowVoicePlayer.getInstance(getContext())

    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        message?.run {
            val msgId: String = msgId
            if (voicePlayer.isPlaying) {
                // Stop the voice play first, no matter the playing voice item is this or others.
                voicePlayer.stop()
                // Stop the voice play animation.
                (getChatRow() as EaseChatRowVoice?)?.stopVoicePlayAnimation()

                // If the playing voice item is this item, only need stop play.
                val playingId: String? = voicePlayer.currentPlayingId
                if (msgId == playingId) {
                    return
                }
            }
            if (direct() === ChatMessageDirection.SEND) {
                // Play the voice
                val localPath: String = (body as ChatVoiceMessageBody).localUrl
                val file = File(localPath)
                if (file.exists() && file.isFile) {
                    playVoice(this)
                    // Start the voice play animation.
                    (getChatRow() as EaseChatRowVoice?)?.startVoicePlayAnimation()
                } else {
                    asyncDownloadVoice(this)
                }
            } else {
                val st = getContext().resources.getString(R.string.ease_is_download_voice_click_later)
                val voiceBody = body as ChatVoiceMessageBody
                val downloadStatus = voiceBody.downloadStatus()
                if (status() === ChatMessageStatus.SUCCESS) {
                    if (ChatClient.getInstance().options.autodownloadThumbnail && downloadStatus == ChatDownloadStatus.SUCCESSED) {
                        play(this)
                    } else {
                        ChatLog.i("TAG", "Voice body download status: $downloadStatus")
                        when (downloadStatus) {
                            ChatDownloadStatus.PENDING, ChatDownloadStatus.FAILED -> {
                                getChatRow()?.updateView()
                                asyncDownloadVoice(this)
                            }

                            ChatDownloadStatus.DOWNLOADING -> Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show()
                            ChatDownloadStatus.SUCCESSED -> play(this)
                        }
                    }
                } else if (status() === ChatMessageStatus.INPROGRESS) {
                    Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show()
                } else if (status() === ChatMessageStatus.FAIL) {
                    Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show()
                    asyncDownloadVoice(this)
                } else {

                }
            }
        }

    }

    private fun playVoice(msg: ChatMessage) {
        voicePlayer.play(msg) { // Stop the voice play animation.
            (getChatRow() as EaseChatRowVoice?)?.stopVoicePlayAnimation()
        }
    }

    private fun asyncDownloadVoice(message: ChatMessage) {
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                ChatClient.getInstance().chatManager().downloadAttachment(message)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                getChatRow()?.updateView()
            }
        }.execute()
    }

    private fun play(message: ChatMessage) {
        val localPath: String = (message.body as ChatVoiceMessageBody).localUrl
        val file = File(localPath)
        if (file.exists() && file.isFile) {
            ackMessage(message)
            playVoice(message)
            // Start the voice play animation.
            (getChatRow() as EaseChatRowVoice?)?.startVoicePlayAnimation()
        } else {
            ChatLog.e("TAG", "file not exist")
        }
    }

    private fun ackMessage(message: ChatMessage) {
        val chatType = message.chatType
        if (!message.isAcked && chatType === ChatType.Chat) {
            try {
                ChatClient.getInstance().chatManager()
                    .ackMessageRead(message.from, message.msgId)
            } catch (e: ChatException) {
                e.printStackTrace()
            }
        }
        if (!message.isListened) {
            ChatClient.getInstance().chatManager().setVoiceMessageListened(message)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (voicePlayer.isPlaying) {
            voicePlayer.stop()
            voicePlayer.release()
        }
    }
}