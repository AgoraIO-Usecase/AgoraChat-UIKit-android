package io.agora.uikit.feature.invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.load
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.databinding.EaseLayoutNewRequestDetailsBinding
import io.agora.uikit.feature.contact.interfaces.IEaseContactResultView
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.getNickname
import io.agora.uikit.viewmodel.contacts.EaseContactListViewModel
import io.agora.uikit.viewmodel.contacts.IContactListRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class EaseNewRequestsDetailsActivity: EaseBaseActivity<EaseLayoutNewRequestDetailsBinding>(),
    IEaseContactResultView {
    private lateinit var user: EaseUser
    private var contactViewModel: IContactListRequest? = null
    override fun getViewBinding(inflater: LayoutInflater): EaseLayoutNewRequestDetailsBinding? {
        return EaseLayoutNewRequestDetailsBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val byteArray = intent.getByteArrayExtra(KEY_USER_INFO)
        val inputStream = ByteArrayInputStream(byteArray)
        val inputStreamReader = ObjectInputStream(inputStream)
        user = inputStreamReader.readObject() as EaseUser

        initView()
        initListener()
    }

    private fun initView(){
        contactViewModel = ViewModelProvider(this)[EaseContactListViewModel::class.java]
        contactViewModel?.attachView(this)

        binding.invitationNickName.text = user.getNickname()
        binding.invitationNumber.text = user.userId
        binding.invitationAction.text = resources.getString(R.string.ease_invitation_detail_action)
        binding.invitationAction.isSelected = true

        val builder = ImageLoader.Builder(this)
            .placeholder(R.drawable.ease_default_avatar)
            .error(R.drawable.ease_default_avatar)
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
            user: EaseUser
        ): Intent {
            val intent = Intent(context, EaseNewRequestsDetailsActivity::class.java)
            val outputStream = ByteArrayOutputStream()
            val outputStreamWriter = ObjectOutputStream(outputStream)
            outputStreamWriter.writeObject(user)
            outputStreamWriter.close()
            val byteArray = outputStream.toByteArray()
            intent.putExtra(KEY_USER_INFO, byteArray)

            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }
}