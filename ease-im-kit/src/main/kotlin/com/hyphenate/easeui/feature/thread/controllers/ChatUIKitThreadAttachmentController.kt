package com.hyphenate.easeui.feature.thread.controllers

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentManager
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatImageUtils
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatPathUtils
import com.hyphenate.easeui.common.ChatVersionUtils
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.dialog.ChatUIKitContactBottomSheetFragment
import com.hyphenate.easeui.common.extensions.getUserInfo
import com.hyphenate.easeui.common.extensions.isSdcardExist
import com.hyphenate.easeui.common.helper.ChatUIKitDingMessageHelper
import com.hyphenate.easeui.common.utils.ChatUIKitCompat
import com.hyphenate.easeui.common.utils.ChatUIKitFileUtils
import com.hyphenate.easeui.feature.chat.UIKitChatFragment
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.chat.enums.getConversationType
import com.hyphenate.easeui.feature.thread.fragment.ChatUIKitCreateThreadFragment
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.provider.getSyncProfile
import com.hyphenate.easeui.provider.getSyncUser
import java.io.File
import java.io.IOException

class ChatUIKitThreadAttachmentController(
    private val mContext: Activity,
    private val mFragment: ChatUIKitCreateThreadFragment?,
    private val conversationId: String?,
    private val sendOriginalImage: Boolean,
) {
    private var cameraFile: File? = null

    /**
     * select picture from camera
     */
    fun selectPicFromCamera(launcher: ActivityResultLauncher<Intent>?) {
        if (!isSdcardExist()) {
            return
        }
        cameraFile = File(
            ChatPathUtils.getInstance().imagePath, (ChatClient.getInstance().currentUser
                    + System.currentTimeMillis()) + ".jpg"
        )
        cameraFile?.let {
            it.parentFile?.mkdirs()
            launcher?.launch(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                    MediaStore.EXTRA_OUTPUT, ChatUIKitCompat.getUriForFile(
                        mContext, it
                    )
                )
            )
        }

    }

    /**
     * select local image
     */
    fun selectPicFromLocal(launcher: ActivityResultLauncher<Intent>?) {
        ChatUIKitCompat.openImageByLauncher(launcher, mContext)
    }

    /**
     * select local video
     */
    fun selectVideoFromLocal(launcher: ActivityResultLauncher<Intent>?) {
        val intent = Intent()
        if (ChatVersionUtils.isTargetQ(mContext)) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        launcher?.launch(intent)
    }

    /**
     * select local file
     */
    fun selectFileFromLocal(launcher: ActivityResultLauncher<Intent>?) {
        val intent = Intent()
        if (ChatVersionUtils.isTargetQ(mContext)) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        launcher?.launch(intent)
    }

    /**
     * Select contact from contact list fragment.
     */
    fun selectContact(fragmentManager: FragmentManager, chatType: ChatUIKitType) {
        val fragment = ChatUIKitContactBottomSheetFragment()
        fragment.setOnUserListItemClickListener(object : OnUserListItemClickListener {

            override fun onUserListItemClick(v: View?, position: Int, user: ChatUIKitUser?) {
                super.onUserListItemClick(v, position, user)
                var showName = when (chatType) {
                    ChatUIKitType.GROUP_CHAT -> {
                        val groupName = ChatUIKitClient.getGroupProfileProvider()?.getSyncProfile(conversationId)?.name
                        if (groupName.isNullOrEmpty().not()) {
                            groupName
                        } else {
                            ChatClient.getInstance().groupManager().getGroup(conversationId)?.groupName
                                ?: conversationId
                        }
                    }
                    ChatUIKitType.CHATROOM -> {
                        ChatClient.getInstance().chatroomManager().getChatRoom(conversationId)?.name
                            ?: conversationId
                    }
                    else -> {
                        val nickname = ChatUIKitClient.getUserProvider()?.getSyncUser(conversationId)?.getRemarkOrName()
                        if (nickname.isNullOrEmpty() || nickname == conversationId) {
                            ChatClient.getInstance().chatManager().getConversation(conversationId)?.latestMessageFromOthers?.getUserInfo()?.name
                                ?: conversationId
                        } else {
                            nickname
                        }
                    }
                }

                CustomDialog(mContext
                    , mContext.getString(R.string.uikit_chat_message_user_card_select_title)
                    , mContext.getString(R.string.uikit_chat_message_user_card_share_content, user?.getNickname(), showName)
                    , false
                    , onRightButtonClickListener = {
                        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
                        val body = ChatCustomMessageBody(ChatUIKitConstant.USER_CARD_EVENT)
                        val params: MutableMap<String, String> = HashMap()
                        params[ChatUIKitConstant.USER_CARD_ID] = user?.userId ?: ""
                        params[ChatUIKitConstant.USER_CARD_NICK] = user?.nickname ?: ""
                        params[ChatUIKitConstant.USER_CARD_AVATAR] = user?.avatar ?: ""
                        body.params = params
                        message.body = body
                        message.to = conversationId
                        mFragment?.sendMessage(message)
                        fragment.hide()
                    }).show()
            }
        })
        fragment.show(fragmentManager, "selectContact")
    }


    /**
     * It's the result from ActivityResultLauncher.
     * @param result
     * @param requestCode
     */
    fun onActivityResult(result: ActivityResult, requestCode: Int) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            mFragment?.chatInputMenu?.hideExtendContainer()
            if (requestCode == UIKitChatFragment.REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data)
            } else if (requestCode == UIKitChatFragment.REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data)
            } else if (requestCode == UIKitChatFragment.REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data)
            } else if (requestCode == UIKitChatFragment.REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data)
            } else if (UIKitChatFragment.REQUEST_CODE_SELECT_VIDEO == requestCode) {
                onActivityResultForLocalVideos(data)
            }
        }
    }

    private fun onActivityResultForCamera(data: Intent?) {
        if (cameraFile != null && cameraFile!!.exists()) {
            var uri = Uri.parse(cameraFile!!.getAbsolutePath())
            // Check if the image is rotated and restore it
            if (sendOriginalImage) {
                uri = ChatImageUtils.checkDegreeAndRestoreImage(mContext, uri)
            }
            mFragment?.sendImageMessage(uri, sendOriginalImage)
        }
    }

    private fun onActivityResultForLocalPhotos(data: Intent?) {
        if (data != null) {
            val selectedImage = data.data
            if (selectedImage != null) {
                val filePath: String = ChatUIKitFileUtils.getFilePath(mContext, selectedImage)
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    mFragment?.sendImageMessage(Uri.parse(filePath), sendOriginalImage)
                } else {
                    ChatUIKitFileUtils.saveUriPermission(mContext, selectedImage, data)
                    mFragment?.sendImageMessage(selectedImage, sendOriginalImage)
                }
            }
        }
    }

    private fun onActivityResultForDingMsg(data: Intent?) {
        if (data != null) {
            val msgContent = data.getStringExtra("msg")
            ChatLog.i(UIKitChatFragment.TAG, "To send the ding-type msg, content: $msgContent")
            // Send the ding-type msg.
            val dingMsg: ChatMessage =
                ChatUIKitDingMessageHelper.get().createDingMessage(conversationId, msgContent)
            mFragment?.sendMessage(dingMsg)
        }
    }

    private fun onActivityResultForLocalFiles(data: Intent?) {
        if (data != null) {
            val uri = data.data
            if (uri != null) {
                val filePath: String = ChatUIKitFileUtils.getFilePath(mContext, uri)
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    mFragment?.sendFileMessage(Uri.parse(filePath))
                } else {
                    ChatUIKitFileUtils.saveUriPermission(mContext, uri, data)
                    mFragment?.sendFileMessage(uri)
                }
            }
        }
    }

    private fun onActivityResultForLocalVideos(data: Intent?) {
        if (data != null) {
            val uri = data.data
            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(mContext, uri!!)
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val duration = mediaPlayer.duration
            ChatLog.d(UIKitChatFragment.TAG, "path = " + uri!!.path + ",duration=" + duration)
            ChatUIKitFileUtils.saveUriPermission(mContext, uri, data)
            mFragment?.sendVideoMessage(uri, duration)
        }
    }

}