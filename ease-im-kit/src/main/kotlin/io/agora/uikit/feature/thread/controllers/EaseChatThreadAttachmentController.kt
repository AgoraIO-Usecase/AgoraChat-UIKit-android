package io.agora.uikit.feature.thread.controllers

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
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatCustomMessageBody
import io.agora.uikit.common.ChatImageUtils
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.ChatPathUtils
import io.agora.uikit.common.ChatVersionUtils
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.dialog.CustomDialog
import io.agora.uikit.common.dialog.EaseContactBottomSheetFragment
import io.agora.uikit.common.extensions.getUserInfo
import io.agora.uikit.common.extensions.isSdcardExist
import io.agora.uikit.common.helper.EaseDingMessageHelper
import io.agora.uikit.common.utils.EaseCompat
import io.agora.uikit.common.utils.EaseFileUtils
import io.agora.uikit.feature.chat.EaseChatFragment
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.chat.enums.getConversationType
import io.agora.uikit.feature.thread.fragment.EaseCreateChatThreadFragment
import io.agora.uikit.interfaces.OnUserListItemClickListener
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.getNickname
import io.agora.uikit.provider.getSyncProfile
import io.agora.uikit.provider.getSyncUser
import java.io.File
import java.io.IOException

class EaseChatThreadAttachmentController(
    private val mContext: Activity,
    private val mFragment: EaseCreateChatThreadFragment?,
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
                    MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(
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
        EaseCompat.openImageByLauncher(launcher, mContext)
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
    fun selectContact(fragmentManager: FragmentManager, chatType: EaseChatType) {
        val fragment = EaseContactBottomSheetFragment()
        fragment.setOnUserListItemClickListener(object : OnUserListItemClickListener {

            override fun onUserListItemClick(v: View?, position: Int, user: EaseUser?) {
                super.onUserListItemClick(v, position, user)
                var showName = when (chatType) {
                    EaseChatType.GROUP_CHAT -> {
                        val groupName = EaseIM.getGroupProfileProvider()?.getSyncProfile(conversationId)?.name
                        if (groupName.isNullOrEmpty().not()) {
                            groupName
                        } else {
                            ChatClient.getInstance().groupManager().getGroup(conversationId)?.groupName
                                ?: conversationId
                        }
                    }
                    EaseChatType.CHATROOM -> {
                        ChatClient.getInstance().chatroomManager().getChatRoom(conversationId)?.name
                            ?: conversationId
                    }
                    else -> {
                        val nickname = EaseIM.getUserProvider()?.getSyncUser(conversationId)?.getRemarkOrName()
                        if (nickname.isNullOrEmpty() || nickname == conversationId) {
                            ChatClient.getInstance().chatManager().getConversation(conversationId)?.latestMessageFromOthers?.getUserInfo()?.name
                                ?: conversationId
                        } else {
                            nickname
                        }
                    }
                }

                CustomDialog(mContext
                    , mContext.getString(R.string.ease_chat_message_user_card_select_title)
                    , mContext.getString(R.string.ease_chat_message_user_card_share_content, user?.getNickname(), showName)
                    , false
                    , onRightButtonClickListener = {
                        val message = ChatMessage.createSendMessage(ChatMessageType.CUSTOM)
                        val body = ChatCustomMessageBody(EaseConstant.USER_CARD_EVENT)
                        val params: MutableMap<String, String> = HashMap()
                        params[EaseConstant.USER_CARD_ID] = user?.userId ?: ""
                        params[EaseConstant.USER_CARD_NICK] = user?.nickname ?: ""
                        params[EaseConstant.USER_CARD_AVATAR] = user?.avatar ?: ""
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
            if (requestCode == EaseChatFragment.REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data)
            } else if (requestCode == EaseChatFragment.REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data)
            } else if (requestCode == EaseChatFragment.REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data)
            } else if (requestCode == EaseChatFragment.REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data)
            } else if (EaseChatFragment.REQUEST_CODE_SELECT_VIDEO == requestCode) {
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
                val filePath: String = EaseFileUtils.getFilePath(mContext, selectedImage)
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    mFragment?.sendImageMessage(Uri.parse(filePath), sendOriginalImage)
                } else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data)
                    mFragment?.sendImageMessage(selectedImage, sendOriginalImage)
                }
            }
        }
    }

    private fun onActivityResultForDingMsg(data: Intent?) {
        if (data != null) {
            val msgContent = data.getStringExtra("msg")
            ChatLog.i(EaseChatFragment.TAG, "To send the ding-type msg, content: $msgContent")
            // Send the ding-type msg.
            val dingMsg: ChatMessage =
                EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent)
            mFragment?.sendMessage(dingMsg)
        }
    }

    private fun onActivityResultForLocalFiles(data: Intent?) {
        if (data != null) {
            val uri = data.data
            if (uri != null) {
                val filePath: String = EaseFileUtils.getFilePath(mContext, uri)
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    mFragment?.sendFileMessage(Uri.parse(filePath))
                } else {
                    EaseFileUtils.saveUriPermission(mContext, uri, data)
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
            ChatLog.d(EaseChatFragment.TAG, "path = " + uri!!.path + ",duration=" + duration)
            EaseFileUtils.saveUriPermission(mContext, uri, data)
            mFragment?.sendVideoMessage(uri, duration)
        }
    }

}