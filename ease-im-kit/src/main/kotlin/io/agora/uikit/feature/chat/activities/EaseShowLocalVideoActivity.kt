package io.agora.uikit.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.databinding.EaseActivityShowLocalVideoBinding
import io.agora.uikit.common.player.EasyVideoCallback
import io.agora.uikit.common.player.EasyVideoPlayer

class EaseShowLocalVideoActivity : EaseBaseActivity<EaseActivityShowLocalVideoBinding>(),
    EasyVideoCallback {
    private var evpPlayer: EasyVideoPlayer? = null
    private var uri: Uri? = null
    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        initIntent(intent)
        initView()
        initListener()
        initData()
    }

    override fun getViewBinding(inflater: LayoutInflater): EaseActivityShowLocalVideoBinding {
        return EaseActivityShowLocalVideoBinding.inflate(inflater)
    }

    override fun setActivityTheme() {
        setFitSystemForTheme(false, ContextCompat.getColor(mContext, R.color.transparent), false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initIntent(intent)
        if (uri != null) {
            evpPlayer?.setSource(uri!!)
        }
    }

    private fun initIntent(intent: Intent) {
        val path: String? = intent.getStringExtra("path")
        val u: Uri? = intent.getParcelableExtra("uri")
        if (u != null) {
            uri = u
        } else if (!TextUtils.isEmpty(path)) {
            uri = Uri.parse(path)
        }
        if (uri == null) {
            finish()
        }
    }

    fun initView() {
        evpPlayer = findViewById(R.id.evp_player)
    }

    fun initListener() {
        evpPlayer?.setCallback(this)
    }

    fun initData() {
        evpPlayer?.setAutoPlay(true)
        if (uri != null) {
            evpPlayer?.setSource(uri!!)
        }
    }

    override fun onPause() {
        super.onPause()
        evpPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (evpPlayer != null) {
            evpPlayer?.release()
            evpPlayer = null
        }
    }

    override fun onStarted(player: EasyVideoPlayer?) {}
    override fun onPaused(player: EasyVideoPlayer?) {}
    override fun onPreparing(player: EasyVideoPlayer?) {}
    override fun onPrepared(player: EasyVideoPlayer?) {}
    override fun onBuffering(percent: Int) {}
    override fun onError(player: EasyVideoPlayer?, e: Exception?) {}
    override fun onCompletion(player: EasyVideoPlayer?) {
        finish()
    }

    override fun onClickVideoFrame(player: EasyVideoPlayer?) {}

    companion object {
        fun actionStart(context: Context, uri: Uri?) {
            val intent = Intent(context, EaseShowLocalVideoActivity::class.java)
            intent.putExtra("uri", uri)
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }
}