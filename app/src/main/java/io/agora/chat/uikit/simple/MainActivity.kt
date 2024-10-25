package io.agora.chat.uikit.simple

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.agora.uikit.EaseIM
import io.agora.uikit.common.extensions.showToast
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.conversation.EaseConversationListFragment

class MainActivity : AppCompatActivity() {
    private var toChatUsername:String? = ""
    companion object{
        fun actionStart(context: Context, toChatUsername: String?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("toChatUsername", toChatUsername)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toChatUsername = intent.getStringExtra("toChatUsername")
        if (!TextUtils.isEmpty(toChatUsername)) {
            EaseChatActivity.actionStart(
                this@MainActivity, toChatUsername?:"", EaseChatType.SINGLE_CHAT
            )
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fl_fragment,
                EaseConversationListFragment.Builder()
                    .useTitleBar(true)
                    .enableTitleBarPressBack(false)
                    .useSearchBar(true)
                    .build()
            )
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_sign_out -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun signOut(){
        EaseIM.logout(false,
            onSuccess = {
                startActivity(Intent(this,SignInActivity::class.java))
                finish()
            },
            onError = {code, error ->
                runOnUiThread{
                    showToast("code: $code  error: $error")
                }
            })
    }
}