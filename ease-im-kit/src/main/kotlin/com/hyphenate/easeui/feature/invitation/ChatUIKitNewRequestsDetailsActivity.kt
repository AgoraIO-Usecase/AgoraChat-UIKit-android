package com.hyphenate.easeui.feature.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.load
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.UikitLayoutNewRequestDetailsBinding
import com.hyphenate.easeui.feature.contact.interfaces.IUIKitContactResultView
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.model.getNickname
import com.hyphenate.easeui.viewmodel.contacts.ChatUIKitContactListViewModel
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ChatUIKitNewRequestsDetailsActivity: ChatUIKitBaseActivity<UikitLayoutNewRequestDetailsBinding>(),
    IUIKitContactResultView {
    private lateinit var user: ChatUIKitUser
    private var contactViewModel: IContactListRequest? = null
    override fun getViewBinding(inflater: LayoutInflater): UikitLayoutNewRequestDetailsBinding? {
        return UikitLayoutNewRequestDetailsBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val byteArray = intent.getByteArrayExtra(KEY_USER_INFO)
        val inputStream = ByteArrayInputStream(byteArray)
        val inputStreamReader = ObjectInputStream(inputStream)
        user = inputStreamReader.readObject() as ChatUIKitUser

        initView()
        initListener()
    }

    private fun initView(){
        contactViewModel = ViewModelProvider(this)[ChatUIKitContactListViewModel::class.java]
        contactViewModel?.attachView(this)

        binding.invitationNickName.text = user.getNickname()
        binding.invitationNumber.text = user.userId
        binding.invitationAction.text = resources.getString(R.string.uikit_invitation_detail_action)
        binding.invitationAction.isSelected = true

        val builder = ImageLoader.Builder(this)
            .placeholder(R.drawable.uikit_default_avatar)
            .error(R.drawable.uikit_default_avatar)
            .build()
        binding.invitationAvatar.load(user.avatar,builder)
    }

    private fun initListener(){
        binding.invitationTitle.setNavigationOnClickListener {
            finish()
        }

        binding.invitationAction.setOnClickListener{
            contactViewModel?.addContact(user.userId)
        }
    }

    override fun addContactSuccess(userId: String) {
        finish()
    }

    override fun addContactFail(code: Int, error: String) {

    }

    companion object {
        private const val KEY_USER_INFO = "userInfo"
        fun createIntent(
            context: Context,
            user: ChatUIKitUser
        ): Intent {
            val intent = Intent(context, ChatUIKitNewRequestsDetailsActivity::class.java)
            val outputStream = ByteArrayOutputStream()
            val outputStreamWriter = ObjectOutputStream(outputStream)
            outputStreamWriter.writeObject(user)
            outputStreamWriter.close()
            val byteArray = outputStream.toByteArray()
            intent.putExtra(KEY_USER_INFO, byteArray)

            ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }
}