package com.hyphenate.easeui.feature.chat.urlpreview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.toSpannable
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.extensions.isUrlPreviewMessage
import com.hyphenate.easeui.common.extensions.parseUrlPreview
import com.hyphenate.easeui.common.helper.URLPreviewHelper
import com.hyphenate.easeui.interfaces.UrlPreviewStatusCallback
import com.hyphenate.easeui.model.EasePreview
import com.hyphenate.easeui.widget.EaseImageView

class EaseChatMessageUrlPreview @JvmOverloads constructor(
    var isSender:Boolean? = null,
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(
    context, attrs, defStyleAttr
) {
    private val ivIcon: EaseImageView by lazy { findViewById(R.id.iv_icon) }
    private val tvPreviewTitle: TextView by lazy { findViewById(R.id.tv_title) }
    private val tvPreviewContent: TextView by lazy { findViewById(R.id.tv_describe) }
    private val tvUrl: TextView by lazy { findViewById(R.id.tv_url) }
    private val layout: ConstraintLayout by lazy { findViewById(R.id.describe_layout) }
    private val divider: View by lazy { findViewById(R.id.v_divider) }

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageUrlPreview).let { a ->
            if (this.isSender == null){
                isSender = a.getBoolean(R.styleable.EaseChatMessageUrlPreview_ease_chat_message_preview_is_sender, false)
            }
            a.recycle()
        }

        isSender?.let {
            if (it) {
                inflate(context, R.layout.ease_row_sent_url_preview_layout, this)
            } else {
                inflate(context, R.layout.ease_row_received_url_preview_layout, this)
            }
        }
    }

    fun showParsing(parsing:String?=null){
        tvPreviewTitle.text = context.getString(R.string.ease_url_preview_parsing)
        parsing?.let {
            tvPreviewTitle.text = it
        }
        layout.visibility = VISIBLE
        tvPreviewTitle.visibility = VISIBLE
    }

    fun hideAllView(){
        layout.visibility = GONE
        tvPreviewTitle.visibility = GONE
        tvPreviewContent.visibility = GONE
        hideImage()
    }

    fun showImage(){
        ivIcon.visibility = VISIBLE
        divider.visibility = GONE
    }

    fun hideImage(){
        ivIcon.visibility = GONE
        divider.visibility = VISIBLE
    }

    fun checkPreview(message: ChatMessage?,statusCallback: UrlPreviewStatusCallback? = null){
        hideAllView()
        message?.let {
            if (it.type != ChatMessageType.TXT){
                return
            }
            val bean = EaseIM.getCache().getUrlPreviewInfo(it.msgId)
            bean?.let {
                updateView(bean,statusCallback)
            }?:kotlin.run {
                val content = (it.body as ChatTextMessageBody).message
                tvUrl.text = content
                val spannable = tvUrl.text.toSpannable()
                val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
                if ( it.isUrlPreviewMessage() ){
                    val preview = it.parseUrlPreview()
                    updateView(preview, statusCallback)
                }else if (spans.isNotEmpty() && EaseIM.getCache().isFirstLoadedUrlPreview(it.msgId)){
                    spans.let { sp->
                        statusCallback?.onParsing()?: kotlin.run { showParsing() }
                        URLPreviewHelper.downLoadHtmlByUrl(sp[0].url,object : UrlPreviewStatusCallback{
                            override fun onDownloadFinish(preview: EasePreview?) {
                                ChatLog.e("UrlPreview","parsing url:${preview?.url} - title:${preview?.title} - description:${preview?.description} - imageURL:${preview?.imageURL}")
                                statusCallback?.onDownloadFinish(preview)?:kotlin.run {
                                    preview?.let { pv->
                                        pv.title?.let {
                                            EaseIM.getCache().saveUrlPreviewInfo(message.msgId,pv)
                                            updateView(preview,statusCallback)
                                        }
                                    }?:kotlin.run {
                                        EaseIM.getCache().checkUrlPreview(it.msgId,false)
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            hideAllView()
                                        }, 500)
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    fun updateView(preview:EasePreview?,statusCallback:UrlPreviewStatusCallback?=null){
        preview?.let { ep->
            if (ep.title.isNullOrEmpty()){
                statusCallback?.onParseFile() ?:kotlin.run { hideAllView() }
            }else{
                ep.description?.let { des->
                    if ( des.isEmpty() ){
                        tvPreviewContent.visibility = GONE
                    }else{
                        tvPreviewContent.text = des
                        tvPreviewContent.visibility = VISIBLE
                    }
                }?:kotlin.run {
                    tvPreviewContent.visibility = GONE
                }
                ep.imageURL?.let { url->
                    ivIcon.load(url){
                        this.listener(
                            onError = { _,_-> hideImage() },
                            onCancel = { hideImage() }
                        )
                    }
                    showImage()
                }?:kotlin.run {
                    hideImage()
                }
                ep.title?.let { title->
                    tvPreviewTitle.text = title
                    tvPreviewTitle.visibility = VISIBLE
                }
                layout.visibility = VISIBLE
            }
        }?:kotlin.run {
            hideAllView()
        }
    }

}