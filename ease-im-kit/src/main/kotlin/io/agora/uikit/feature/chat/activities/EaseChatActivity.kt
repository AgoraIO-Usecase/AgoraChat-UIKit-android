package io.agora.uikit.feature.chat.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.common.extensions.showToast
import io.agora.uikit.common.permission.PermissionCompat
import io.agora.uikit.common.permission.PermissionsManager
import io.agora.uikit.databinding.EaseActivityChatBinding
import io.agora.uikit.feature.chat.EaseChatFragment
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.chat.interfaces.OnChatExtendMenuItemClickListener
import io.agora.uikit.feature.chat.interfaces.OnChatRecordTouchListener
import io.agora.uikit.feature.chat.interfaces.OnMessageSendCallback
import io.agora.uikit.provider.getSyncProfile
import io.agora.uikit.provider.getSyncUser

open class EaseChatActivity: EaseBaseActivity<EaseActivityChatBinding>() {

    private var fragment: EaseChatFragment? = null

    private val requestImagePermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onRequestResult(
                result,
                REQUEST_CODE_STORAGE_PICTURE
            )
        }
    private val requestVideoPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onRequestResult(
                result,
                REQUEST_CODE_STORAGE_VIDEO
            )
        }
    private val requestFilePermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onRequestResult(
                result,
                REQUEST_CODE_STORAGE_FILE
            )
        }

    override fun getViewBinding(inflater: LayoutInflater): EaseActivityChatBinding? {
        return EaseActivityChatBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            val conversationId = it.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)
            val chatType = EaseChatType.values()[it.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseChatType.SINGLE_CHAT.ordinal)]
            val title = getChatTitle(conversationId, chatType) ?: conversationId
            val builder = EaseChatFragment.Builder(conversationId, chatType)
                .useTitleBar(true)
                .setTitleBarTitle(title)
                .enableTitleBarPressBack(true)
                .setEmptyLayout(R.layout.ease_layout_no_data_show_nothing)
                .setOnChatExtendMenuItemClickListener(object : OnChatExtendMenuItemClickListener {
                    override fun onChatExtendMenuItemClick(view: View?, itemId: Int): Boolean {
                        when(itemId) {
                            R.id.extend_item_take_picture -> {
                                if (!PermissionsManager.getInstance()
                                        .hasPermission(mContext, Manifest.permission.CAMERA)
                                ) {
                                    PermissionsManager.getInstance()
                                        .requestPermissionsIfNecessaryForResult(
                                            mContext,
                                            arrayOf(Manifest.permission.CAMERA),
                                            null
                                        )
                                    return true
                                }
                            }
                            R.id.extend_item_picture -> {
                                if (!PermissionCompat.checkMediaPermission(
                                        mContext,
                                        requestImagePermission,
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    )
                                ) {
                                    return true
                                }
                            }

                            R.id.extend_item_video -> {
                                if (!PermissionCompat.checkMediaPermission(
                                        mContext,
                                        requestVideoPermission,
                                        Manifest.permission.READ_MEDIA_VIDEO,
                                        Manifest.permission.CAMERA
                                    )
                                ) {
                                    return true
                                }
                            }

                            R.id.extend_item_file -> {
                                if (!PermissionCompat.checkMediaPermission(
                                        mContext,
                                        requestFilePermission,
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.READ_MEDIA_VIDEO
                                    )
                                ) {
                                    return true
                                }
                            }
                        }
                        return false
                    }
                })
                .setOnChatRecordTouchListener(object : OnChatRecordTouchListener {
                    override fun onRecordTouch(v: View?, event: MotionEvent?): Boolean {
                        if (!PermissionsManager.getInstance()
                                .hasPermission(mContext, Manifest.permission.RECORD_AUDIO)
                        ) {
                            PermissionsManager.getInstance()
                                .requestPermissionsIfNecessaryForResult(
                                    mContext,
                                    arrayOf(Manifest.permission.RECORD_AUDIO),
                                    null
                                )
                            return true
                        }
                        return false
                    }
                })
                .setOnMessageSendCallback(object : OnMessageSendCallback {

                    override fun onError(code: Int, errorMsg: String?) {
                        when(code) {
                            ChatError.REACTION_REACH_LIMIT -> {
                                mContext.showToast(R.string.ease_chat_reaction_reach_limit)
                            }
                        }
                    }
                })
            setChildSettings(builder)
            fragment = builder.build()
            fragment?.let { fragment ->
                supportFragmentManager.beginTransaction().replace(binding.flFragment.id, fragment, getFragmentTag()).commit()
            }
        }
    }

    protected open fun setChildSettings(builder: EaseChatFragment.Builder) {}

    protected open fun getFragmentTag(): String {
        return "ease_chat_fragment"
    }

    private fun getChatTitle(conversationId: String?, chatType: EaseChatType): String? {
        return when (chatType) {
            EaseChatType.SINGLE_CHAT -> {
                EaseIM.getUserProvider()?.getSyncUser(conversationId)?.getRemarkOrName() ?: conversationId
            }
            EaseChatType.GROUP_CHAT -> {
                EaseIM.getGroupProfileProvider()?.getSyncProfile(conversationId)?.name
                ?: ChatClient.getInstance().groupManager().getGroup(conversationId)?.groupName
                ?: conversationId
            }
            EaseChatType.CHATROOM -> {
                ChatClient.getInstance().chatroomManager().getChatRoom(conversationId)?.name ?: conversationId
            }
        }
    }

    private fun onRequestResult(result: Map<String, Boolean>?, requestCode: Int) {
        if (!result.isNullOrEmpty()) {
            for ((key, value) in result) {
                ChatLog.e("chat", "onRequestResult: $key  $value")
            }
            if (PermissionCompat.getMediaAccess(mContext) !== PermissionCompat.StorageAccess.Denied) {
                if (requestCode == REQUEST_CODE_STORAGE_PICTURE) {
                    fragment?.selectPicFromLocal()
                } else if (requestCode == REQUEST_CODE_STORAGE_VIDEO) {
                    fragment?.selectVideoFromLocal()
                } else if (requestCode == REQUEST_CODE_STORAGE_FILE) {
                    fragment?.selectFileFromLocal()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (fragment?.onBackPressed() == true) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PICTURE = 111
        private const val REQUEST_CODE_STORAGE_VIDEO = 112
        private const val REQUEST_CODE_STORAGE_FILE = 113
        fun actionStart(context: Context, conversationId: String, chatType: EaseChatType) {
            Intent(context, EaseChatActivity::class.java).apply {
                putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
                putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType.ordinal)
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