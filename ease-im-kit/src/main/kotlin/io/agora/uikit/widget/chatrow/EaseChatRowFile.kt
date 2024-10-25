package io.agora.uikit.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatDownloadStatus
import io.agora.uikit.common.ChatFileMessageBody
import io.agora.uikit.common.ChatImageMessageBody
import io.agora.uikit.common.ChatNormalFileMessageBody
import io.agora.uikit.common.ChatTextFormater
import io.agora.uikit.common.ChatVideoMessageBody
import io.agora.uikit.common.extensions.isSend
import io.agora.uikit.common.extensions.mainScope
import io.agora.uikit.common.impl.CallbackImpl
import kotlinx.coroutines.launch

/**
 * file for row
 */
open class EaseChatRowFile @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRow(context, attrs, defStyleAttr, isSender) {
    /**
     * file name
     */
    protected val fileNameView: TextView? by lazy { findViewById(R.id.tv_file_name) }

    /**
     * file's size
     */
    protected val fileSizeView: TextView? by lazy { findViewById(R.id.tv_file_size) }

    /**
     * file state
     */
    protected val fileStateView: TextView? by lazy { findViewById(R.id.tv_file_state) }
    private val ivFileIcon: ImageView? by lazy { findViewById(R.id.iv_file_icon) }

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_file else R.layout.ease_row_sent_file,
            this
        )
    }

    override fun onSetUpView() {
        message?.run {
            (body as? ChatNormalFileMessageBody)?.let {
                fileStateView?.visibility = View.GONE
                fileNameView?.text = it.fileName
                fileSizeView?.text = ChatTextFormater.getDataSize(it.fileSize)
            }
        }
    }

    override fun onMessageSuccess() {
        super.onMessageSuccess()
        message?.run {
            if (isSend()) {
                fileStateView?.setText(R.string.ease_have_uploaded)
            }
        }
    }

    /**
     * Download file or thumbnail.
     * @param isThumbnail   Whether to download thumbnail
     */
    protected fun downloadAttachment(isThumbnail: Boolean) {
        message?.let {
            setMessageDownloadCallback(isThumbnail)
            if (isThumbnail) {
                ChatClient.getInstance().chatManager().downloadThumbnail(it)
            } else {
                ChatClient.getInstance().chatManager().downloadAttachment(it)
            }
        }
    }

    /**
     * Set message download callback.
     */
    protected fun setMessageDownloadCallback(isThumbnail: Boolean) {
        message?.run {
            var canDownload = false
            val body = body
            if (isThumbnail) {
                if (body is ChatImageMessageBody || body is ChatVideoMessageBody) {
                    if (body is ChatImageMessageBody
                        && body.thumbnailDownloadStatus() != ChatDownloadStatus.SUCCESSED) {
                        canDownload = true
                    }
                    if (body is ChatVideoMessageBody
                        && body.thumbnailDownloadStatus() != ChatDownloadStatus.SUCCESSED) {
                        canDownload = true
                    }
                }
            } else {
                if (body is ChatFileMessageBody) {
                    if (body.downloadStatus() != ChatDownloadStatus.SUCCESSED) {
                        canDownload = true
                    }
                }
            }
            if (canDownload) {
                setMessageStatusCallback(CallbackImpl(
                    onSuccess = {
                        context.mainScope().launch {
                            onDownloadAttachmentSuccess()
                        }
                    },
                    onError = { code, error ->
                        context.mainScope().launch {
                            onDownloadAttachmentError(code, error)
                        }
                    },
                    onProgress = { progress ->
                        context.mainScope().launch {
                            onDownloadAttachmentProgress(progress)
                        }
                    }
                ))
            }
        }
    }

    protected open fun onDownloadAttachmentSuccess() {}
    protected open fun onDownloadAttachmentError(code: Int, error: String?) {}
    protected open fun onDownloadAttachmentProgress(progress: Int) {}

    companion object {
        private val TAG = EaseChatRowFile::class.java.simpleName
    }
}