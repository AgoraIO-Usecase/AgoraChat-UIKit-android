package com.easemob.quickstart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.easemob.quickstart.databinding.ActivityMainBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatOptions
import io.agora.uikit.common.extensions.showToast
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.interfaces.EaseConnectionListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val connectListener by lazy {
        object : EaseConnectionListener() {
            override fun onConnected() {}

            override fun onDisconnected(errorCode: Int) {}

            override fun onLogout(errorCode: Int, info: String?) {
                super.onLogout(errorCode, info)
                showToast("You have been logged out, please log in again!")
                ChatLog.e(TAG, "")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSDK()
        initListener()
    }

    private fun initSDK() {
        val appkey = getString(R.string.app_key)
        if (appkey.isEmpty()) {
            applicationContext.showToast("You should set your AppKey first!")
            ChatLog.e(TAG, "You should set your AppKey first!")
            return
        }
        ChatOptions().apply {
            // Set your own appkey here
            this.appKey = appkey
            // Set not to log in automatically
            this.autoLogin = false
            // Set whether confirmation of delivery is required by the recipient. Default: false
            this.requireDeliveryAck = true
        }.let {
            EaseIM.init(applicationContext, it)
        }
    }

    private fun initListener() {
        EaseIM.addConnectionListener(connectListener)
    }

    fun login(view: View) {
        val username = binding.etUserId.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Username or password cannot be empty!")
            ChatLog.e(TAG, "Username or password cannot be empty!")
            return
        }
        if (!EaseIM.isInited()) {
            showToast("Please init first!")
            ChatLog.e(TAG, "Please init first!")
            return
        }
        EaseIM.login(username, password
            , onSuccess = {
                showToast("Login successfully!")
                ChatLog.e(TAG, "Login successfully!")
            }, onError = { code, message ->
                showToast("Login failed: $message")
                ChatLog.e(TAG, "Login failed: $message")
            }
        )
    }

    fun logout(view: View) {
        if (!EaseIM.isInited()) {
            showToast("Please init first!")
            ChatLog.e(TAG, "Please init first!")
            return
        }
        EaseIM.logout(false
            , onSuccess = {
                showToast("Logout successfully!")
                ChatLog.e(TAG, "Logout successfully!")
            }
        )
    }

    fun startChat(view: View) {
        val username = binding.etPeerId.text.toString().trim()
        if (username.isEmpty()) {
            showToast("Peer id cannot be empty!")
            ChatLog.e(TAG, "Peer id cannot be empty!")
            return
        }
        if (!EaseIM.isLoggedIn()) {
            showToast("Please login first!")
            ChatLog.e(TAG, "Please login first!")
            return
        }
        EaseChatActivity.actionStart(this, username, EaseChatType.SINGLE_CHAT)
    }

    override fun onDestroy() {
        EaseIM.removeConnectionListener(connectListener)
        EaseIM.releaseGlobalListener()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}