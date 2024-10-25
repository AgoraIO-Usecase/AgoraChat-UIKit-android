package io.agora.chat.uikit.simple

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.agora.chat.uikit.simple.databinding.ActivitySignInBinding
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.extensions.showToast

class SignInActivity : AppCompatActivity() {

    private val binding: ActivitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(ChatClient.getInstance().options.autoLogin && ChatClient.getInstance().isLoggedInBefore) {
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }else{

            binding.btnLogin.setOnClickListener{
                val username = binding.etUsername.text.toString().trim()
                val trim = binding.etPassword.text.toString().trim()
                val toChatName = binding.etToChatUsername.text.toString().trim()
                EaseIM.login(username,trim,
                    onSuccess = {
                        MainActivity.actionStart(this,toChatName)
                    },
                    onError = { code, error ->
                        runOnUiThread{
                            showToast("code: $code  error: $error")
                        }
                    })
            }

        }
    }

}