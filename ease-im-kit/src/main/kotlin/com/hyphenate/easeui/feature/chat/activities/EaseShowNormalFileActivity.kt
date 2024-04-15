package com.hyphenate.easeui.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatFileMessageBody
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.utils.EaseCompat
import com.hyphenate.easeui.databinding.EaseActivityShowFileBinding
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