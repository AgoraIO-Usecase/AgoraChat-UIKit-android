package com.hyphenate.easeui.demo

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.common.ChatConnectionListener
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatOptions
import com.hyphenate.easeui.common.impl.OnValueSuccess
import com.hyphenate.easeui.common.extensions.showToast
import com.hyphenate.easeui.common.helper.EasePreferenceManager
import com.hyphenate.easeui.demo.base.UserActivityLifecycleCallbacks
import com.hyphenate.easeui.demo.login.LoginActivity
import com.hyphenate.easeui.feature.thread.EaseChatThreadActivity
import com.hyphenate.easeui.interfaces.EaseConnectionListener
import com.hyphenate.easeui.provider.EaseCustomActivityRoute
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.provider.EaseUserProfileProvider
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class DemoApplication: Application() {
    private val mLifecycleCallbacks = UserActivityLifecycleCallbacks()
    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks()

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
            override fun getActivityRoute(intent: Intent): Intent {
                if (intent.component?.className == EaseChatActivity::class.java.name) {
                    intent.setClass(this@DemoApplication, ChatActivity::class.java)
                }else if (intent.component?.className == EaseChatThreadActivity::class.java.name){
                    intent.setClass(this@DemoApplication, ChatThreadActivity::class.java)
                }
                return intent
            }

        })

        EaseIM.setUserProfileProvider(object : EaseUserProfileProvider{
            override fun getUser(userId: String?): EaseProfile? {
                return getLocalGroupMemberInfo(userId)
            }

            override fun fetchUsers(
                userIds: List<String>,
                onValueSuccess: OnValueSuccess<List<EaseProfile>>
            ) {

            }

        })

        EaseIM.addConnectionListener(object : EaseConnectionListener() {
            override fun onConnected() {

            }

            override fun onDisconnected(errorCode: Int) {
                ChatLog.e("app","onDisconnected: $errorCode")
            }

            override fun onLogout(errorCode: Int, info: String?) {
                super.onLogout(errorCode, info)
                ChatLog.e("app","onLogout: $errorCode")
                mLifecycleCallbacks.activityList.forEach {
                    it.finish()
                }
                LoginActivity.startAction(instance)
            }

        })

        // Call this method after EaseIM#init
        val isBlack = EasePreferenceManager.getInstance().getBoolean("isBlack")
        AppCompatDelegate.setDefaultNightMode(if (isBlack) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks)
    }

    fun getLifecycleCallbacks(): UserActivityLifecycleCallbacks? {
        return mLifecycleCallbacks
    }

    /**
     * Set default settings for SmartRefreshLayout
     */

    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(context)
        }
    }

    companion object {
        private lateinit var instance: DemoApplication
        fun getInstance(): DemoApplication {
            return instance
        }
    }


    fun getLocalGroupMemberInfo(username:String?): EaseProfile?{
        var profile:EaseProfile? = null
        when(username){
            "apex" -> {
                profile =  EaseProfile(
                    id = "apex",
                    name = "房主Host",
                    avatar = "https://a1.easemob.com/easemob/chatroom-uikit/chatfiles/b837f7b0-79f8-11ee-b817-23850e48ca47"
                )
            }
            "apex1" -> {
                profile =  EaseProfile(
                    id = "apex1",
                    name = "测试昵称",
                    avatar = "https://a1.easemob.com/easemob/chatroom-uikit/chatfiles/99296020-79f8-11ee-8475-c7a7b59db79f"
                )
            }
            "lxm" -> {
                profile =  EaseProfile(
                    id = "lxm",
                    name = "大威天龙",
                    avatar = "https://a1.easemob.com/easemob/chatroom-uikit/chatfiles/16bc4980-79f9-11ee-b272-3568dd301252"
                )
            }
            else -> { }
        }
        return profile
    }
}