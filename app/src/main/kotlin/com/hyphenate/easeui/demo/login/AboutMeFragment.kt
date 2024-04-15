package com.hyphenate.easeui.demo.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.base.EaseBaseFragment
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.helper.EasePreferenceManager
import com.hyphenate.easeui.demo.DemoApplication
import com.hyphenate.easeui.demo.MainActivity
import com.hyphenate.easeui.demo.R
import com.hyphenate.easeui.demo.databinding.DemoFragmentAboutMeBinding
import com.hyphenate.easeui.demo.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class AboutMeFragment: EaseBaseFragment<DemoFragmentAboutMeBinding>() {

    private lateinit var loginViewModel: LoginViewModel
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DemoFragmentAboutMeBinding? {
        return DemoFragmentAboutMeBinding.inflate(inflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        EaseIM.getCurrentUser()?.let {
            binding?.run {
                avatar.load(it.avatar) {
                    error(com.hyphenate.easeui.R.drawable.ease_default_avatar)
                }
                tvNickName.text = if (it.name.isNullOrEmpty()) it.id else it.name
                if (!it.name.isNullOrBlank()) {
                    tvUserId.text = it.id
                }
            }
        } ?: kotlin.run {
            binding?.tvNickName?.text = ChatClient.getInstance().currentUser
        }
    }
    override fun initViewModel() {
        super.initViewModel()
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun initListener() {
        super.initListener()
        binding?.run {
            btnLogout.setOnClickListener {
                val clearDialog = CustomDialog(
                    context = mContext,
                    title = resources.getString(R.string.em_login_out_hint),
                    isEditTextMode = false,
                    onLeftButtonClickListener = {

                    },
                    onRightButtonClickListener = {
                        logout()
                    }
                )
                clearDialog.show()
            }
            itemCommonSet.setOnClickListener {
                val isBlack = EasePreferenceManager.getInstance().getBoolean("isBlack")
                AppCompatDelegate.setDefaultNightMode(if (isBlack) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
                EasePreferenceManager.getInstance().putBoolean("isBlack", !isBlack)
                MainActivity.actionStart(mContext)
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            loginViewModel.logout()
                .catchChatException { e ->
                    ChatLog.e("logout", "logout failed: ${e.description}")
                }
                .collect {
                    DemoApplication.getInstance().getLifecycleCallbacks()?.skipToTarget(LoginActivity::class.java)
                }
        }
    }

}