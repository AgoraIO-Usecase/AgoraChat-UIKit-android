package io.agora.uikit.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatFileMessageBody
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.common.extensions.mainScope
import io.agora.uikit.common.impl.CallbackImpl
import io.agora.uikit.common.utils.EaseCompat
import io.agora.uikit.databinding.EaseActivityShowFileBinding
import kotlinx.coroutines.launch

class EaseShowNormalFileActivity : EaseBaseActivity<EaseActivityShowFileBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message: ChatMessage? = intent.getParcelableExtra("msg")
        if (message?.body !is ChatFileMessageBody) {
            Toast.makeText(
                this@EaseShowNormalFileActivity,
                "Unsupported message body",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        message.setMessageStatusCallback(CallbackImpl(
            onSuccess = {
                mainScope().launch{
                    EaseCompat.openFile(
                        mContext,
                        (message.body as ChatFileMessageBody).localUri
                    )
                    finish()
                }
            },
            onError = { code, error ->
                mainScope().launch {
                    EaseCompat.deleteFile(
                        mContext,
                        (message.body as ChatFileMessageBody).localUri
                    )
                    var str4: String =
                        resources.getString(R.string.ease_failed_to_download_file)
                    if (code == ChatError.FILE_NOT_FOUND) {
                        str4 = resources.getString(R.string.ease_file_expired)
                    }
                    Toast.makeText(mContext, str4 + message, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            },
            onProgress = { progress ->
                mainScope().launch {
                    this@EaseShowNormalFileActivity.binding.progressBar.progress = progress
                }
            }
        ))
        ChatClient.getInstance().chatManager().downloadAttachment(message)
    }

    override fun getViewBinding(inflater: LayoutInflater): EaseActivityShowFileBinding {
        return EaseActivityShowFileBinding.inflate(inflater)
    }


    companion object {
        private val TAG = EaseShowNormalFileActivity::class.java.simpleName
        fun actionStart(context: Context, message: ChatMessage?) {
            val intent = Intent(context, EaseShowNormalFileActivity::class.java)
            intent.putExtra("msg", message)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }
}