package com.hyphenate.easeui.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatFileMessageBody
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.utils.ChatUIKitCompat
import com.hyphenate.easeui.databinding.UikitActivityShowFileBinding
import kotlinx.coroutines.launch

class ChatUIKitShowNormalFileActivity : ChatUIKitBaseActivity<UikitActivityShowFileBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message: ChatMessage? = intent.getParcelableExtra("msg")
        if (message?.body !is ChatFileMessageBody) {
            Toast.makeText(
                this@ChatUIKitShowNormalFileActivity,
                "Unsupported message body",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        message.setMessageStatusCallback(CallbackImpl(
            onSuccess = {
                mainScope().launch{
                    ChatUIKitCompat.openFile(
                        mContext,
                        (message.body as ChatFileMessageBody).localUri
                    )
                    finish()
                }
            },
            onError = { code, error ->
                mainScope().launch {
                    ChatUIKitCompat.deleteFile(
                        mContext,
                        (message.body as ChatFileMessageBody).localUri
                    )
                    var str4: String =
                        resources.getString(R.string.uikit_failed_to_download_file)
                    if (code == ChatError.FILE_NOT_FOUND) {
                        str4 = resources.getString(R.string.uikit_file_expired)
                    }
                    Toast.makeText(mContext, str4 + message, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            },
            onProgress = { progress ->
                mainScope().launch {
                    this@ChatUIKitShowNormalFileActivity.binding.progressBar.progress = progress
                }
            }
        ))
        ChatClient.getInstance().chatManager().downloadAttachment(message)
    }

    override fun getViewBinding(inflater: LayoutInflater): UikitActivityShowFileBinding {
        return UikitActivityShowFileBinding.inflate(inflater)
    }


    companion object {
        private val TAG = ChatUIKitShowNormalFileActivity::class.java.simpleName
        fun actionStart(context: Context, message: ChatMessage?) {
            val intent = Intent(context, ChatUIKitShowNormalFileActivity::class.java)
            intent.putExtra("msg", message)
            ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }
}