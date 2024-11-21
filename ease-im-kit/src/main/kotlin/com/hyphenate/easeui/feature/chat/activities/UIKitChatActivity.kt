package com.hyphenate.easeui.feature.chat.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.common.extensions.showToast
import com.hyphenate.easeui.common.permission.PermissionCompat
import com.hyphenate.easeui.common.permission.PermissionsManager
import com.hyphenate.easeui.databinding.UikitActivityChatBinding
import com.hyphenate.easeui.feature.chat.UIKitChatFragment
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.chat.interfaces.OnChatExtendMenuItemClickListener
import com.hyphenate.easeui.feature.chat.interfaces.OnChatRecordTouchListener
import com.hyphenate.easeui.feature.chat.interfaces.OnMessageSendCallback
import com.hyphenate.easeui.provider.getSyncProfile
import com.hyphenate.easeui.provider.getSyncUser

open class UIKitChatActivity: ChatUIKitBaseActivity<UikitActivityChatBinding>() {

    private var fragment: UIKitChatFragment? = null
    private var permissionDescView:View? = null
    private val requestCameraPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            onRequestResult(
                result,
                0
            )
        }
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

    override fun getViewBinding(inflater: LayoutInflater): UikitActivityChatBinding? {
        return UikitActivityChatBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            val conversationId = it.getStringExtra(ChatUIKitConstant.EXTRA_CONVERSATION_ID)
            val chatType = ChatUIKitType.values()[it.getIntExtra(ChatUIKitConstant.EXTRA_CHAT_TYPE, ChatUIKitType.SINGLE_CHAT.ordinal)]
            val title = getChatTitle(conversationId, chatType) ?: conversationId
            val builder = UIKitChatFragment.Builder(conversationId, chatType)
                .useTitleBar(true)
                .setTitleBarTitle(title)
                .enableTitleBarPressBack(true)
                .setEmptyLayout(R.layout.uikit_layout_no_data_show_nothing)
                .setOnChatExtendMenuItemClickListener(object : OnChatExtendMenuItemClickListener {
                    override fun onChatExtendMenuItemClick(view: View?, itemId: Int): Boolean {
                        when (itemId) {
                            R.id.extend_item_take_picture -> {
                                permissionDescView =
                                    PermissionsManager.getInstance().showPermissionDescView(
                                        mContext,
                                        Manifest.permission.CAMERA
                                    )

                                if (!PermissionsManager.getInstance()
                                        .hasPermission(mContext, Manifest.permission.CAMERA)
                                ) {
                                    requestCameraPermission.launch(arrayOf(Manifest.permission.CAMERA))
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
                                    permissionDescView =
                                        PermissionsManager.getInstance().showPermissionDescView(
                                            mContext,
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        )
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
                                    permissionDescView =
                                        PermissionsManager.getInstance().showPermissionDescView(
                                            mContext,
                                            Manifest.permission.READ_MEDIA_VIDEO
                                        )
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
                                    permissionDescView =
                                        PermissionsManager.getInstance().showPermissionDescView(
                                            mContext,
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        )
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
                        when (code) {
                            ChatError.REACTION_REACH_LIMIT -> {
                                mContext.showToast(R.string.uikit_chat_reaction_reach_limit)
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

    protected open fun setChildSettings(builder: UIKitChatFragment.Builder) {}

    protected open fun getFragmentTag(): String {
        return "ease_chat_fragment"
    }

    private fun getChatTitle(conversationId: String?, chatType: ChatUIKitType): String? {
        return when (chatType) {
            ChatUIKitType.SINGLE_CHAT -> {
                ChatUIKitClient.getUserProvider()?.getSyncUser(conversationId)?.getRemarkOrName() ?: conversationId
            }
            ChatUIKitType.GROUP_CHAT -> {
                ChatUIKitClient.getGroupProfileProvider()?.getSyncProfile(conversationId)?.name
                ?: ChatClient.getInstance().groupManager().getGroup(conversationId)?.groupName
                ?: conversationId
            }
            ChatUIKitType.CHATROOM -> {
                ChatClient.getInstance().chatroomManager().getChatRoom(conversationId)?.name ?: conversationId
            }
        }
    }

    private fun onRequestResult(result: Map<String, Boolean>?, requestCode: Int) {
        //dimiss permission explain dialog
        PermissionsManager.getInstance().hidePermissionDescView(permissionDescView)
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
        fun actionStart(context: Context, conversationId: String, chatType: ChatUIKitType) {
            Intent(context, UIKitChatActivity::class.java).apply {
                putExtra(ChatUIKitConstant.EXTRA_CONVERSATION_ID, conversationId)
                putExtra(ChatUIKitConstant.EXTRA_CHAT_TYPE, chatType.ordinal)
                ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(this.clone() as Intent)?.let {
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