package com.hyphenate.easeui.feature.chat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hyphenate.easeui.R
import com.hyphenate.easeui.databinding.EaseLayoutUnreadNotificationBinding
import com.hyphenate.easeui.feature.chat.interfaces.IChatUnreadNotification

open class EaseUnreadNotificationView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), IChatUnreadNotification {

    val binding: EaseLayoutUnreadNotificationBinding by lazy { EaseLayoutUnreadNotificationBinding.inflate(
        LayoutInflater.from(context), this, true) }

    private var listener: OnClickListener? = null

    init {
        initView()
        initListener()
    }

    private fun initView() {
        binding.tvUnreadCount.visibility = GONE
    }

    private fun initListener() {
        binding.root.setOnClickListener {
            listener?.onClick(it)
        }
    }

    override fun updateUnreadCount(unreadCount: Int) {
        if (unreadCount > 0) {
            binding.tvUnreadCount.text = context.getString(R.string.ease_message_unread_count, unreadCount)
            binding.tvUnreadCount.visibility = VISIBLE
        } else {
            binding.tvUnreadCount.text = ""
            binding.tvUnreadCount.visibility = GONE
        }
    }

    override fun setOnNotificationClickListener(listener: OnClickListener?) {
        this.listener = listener
    }
}