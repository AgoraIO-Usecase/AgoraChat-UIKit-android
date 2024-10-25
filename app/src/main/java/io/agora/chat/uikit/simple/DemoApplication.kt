package io.agora.chat.uikit.simple

import android.app.Application
import android.content.Intent
import android.util.Log
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatOptions
import io.agora.uikit.common.extensions.showToast
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.feature.thread.EaseChatThreadActivity
import io.agora.uikit.provider.EaseCustomActivityRoute

class DemoApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        val isAgreementAccepted = getAgreementStateFromSP()
        if (isAgreementAccepted) {
            initAgoraChatSDK()
        }


    }

    private fun getAgreementStateFromSP(): Boolean {
        return true
    }

    fun initAgoraChatSDK() {
        val appkey = BuildConfig.APPKEY
        if (appkey.isNullOrEmpty()) {
            showToast("APPKEY is null or empty")
            Log.e("app","APPKEY is null or empty")
            return
        }
        val options = ChatOptions()
        options.appKey = appkey
        options.acceptInvitationAlways = false
        options.requireDeliveryAck = true
        EaseIM.init(this, options)


        EaseIM.setCustomActivityRoute(object : EaseCustomActivityRoute {
            override fun getActivityRoute(intent: Intent): Intent? {
                intent.component?.className?.let {
                    when(it) {
                        EaseChatActivity::class.java.name -> {
                            intent.setClass(this@DemoApplication, ChatActivity::class.java)
                        }
                        else -> {
                            return intent
                        }
                    }
                }
                return intent
            }

        })
    }


}