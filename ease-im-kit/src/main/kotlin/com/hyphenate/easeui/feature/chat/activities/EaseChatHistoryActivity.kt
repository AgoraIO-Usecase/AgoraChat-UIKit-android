package com.hyphenate.easeui.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.common.extensions.showToast
import com.hyphenate.easeui.databinding.EaseActivityChatBinding
import com.hyphenate.easeui.feature.chat.chathistory.EaseChatHistoryFragment
import com.hyphenate.easeui.feature.chat.interfaces.OnCombineMessageDownloadCallback
import com.hyphenate.easeui.feature.chat.interfaces.OnSendCombineMessageCallback

open class EaseChatHistoryActivity: EaseBaseActivity<EaseActivityChatBinding>() {
    override fun getViewBinding(inflater: LayoutInflater): EaseActivityChatBinding? {
        return EaseActivityChatBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message = intent.getParcelableExtra(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE) as? ChatMessage
        if (message == null) {
            mContext.showToast("Message is null.")
            finish()
            return
        }
        var fragment = supportFragmentManager.findFragmentByTag("ease_chat_history")
        if (fragment == null) {
            val builder = EaseChatHistoryFragment.Builder(message)
                .useTitleBar(true)
                .setTitleBarTitle(getString(R.string.ease_combine_default))
                .enableTitleBarPressBack(true)
                .setTitleBarBackPressListener { finish() }
                .setEmptyLayout(R.layout.ease_layout_no_data_show_nothing)
                .setOnSendCombineMessageCallback(object : OnSendCombineMessageCallback {

                    override fun onSendCombineError(
                        message: ChatMessage?,
                        code: Int,
                        errorMsg: String?
                    ) {
                        // Handle the error message
                    }
                })
                .setOnCombineMessageDownloadCallback(object: OnCombineMessageDownloadCallback {
                    override fun onDownloadSuccess(messages: List<ChatMessage>) {

                    }

                    override fun onDownloadFail(error: Int, errorMsg: String?) {
                        errorMsg?.apply {
                            mContext.showToast(this)
                        }
                    }
                })
            setChildSettings(builder)
            fragment = builder.build()
        }
        fragment?.let { fragment ->
            supportFragmentManager.beginTransaction().replace(binding.flFragment.id, fragment).commit()
        }
    }

    protected open fun setChildSettings(builder: EaseChatHistoryFragment.Builder) {}

    companion object {
        fun actionStart(context: Context, combineMessage: ChatMessage?) {
            Intent(context, EaseChatHistoryActivity::class.java).apply {
                putExtra(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE, combineMessage)
                EaseIM.getCustomActivityRoute()?.getActivityRoute(this.clone() as Intent)?.let {
                    if (it.hasRoute()) {
                        context.startActivity(it)
                        return
                    }
                }
                context.startActivity(this)
            }
        }
    }
}