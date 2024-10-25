package io.agora.uikit.feature.chat.search

import android.os.Bundle
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.feature.chat.EaseChatFragment

class EaseMessageSearchResultFragment: EaseChatFragment() {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.layoutChat?.dismissNotificationView(true)
    }

    override fun addMenu() {}

    override fun initListener() {
        super.initListener()
        binding?.titleBar?.let {
            it.setLogoClickListener(null)
            it.setTitleClickListener(null)
        }
    }

    override fun onUserAvatarClick(userId: String?) {
        // do nothing
    }

}