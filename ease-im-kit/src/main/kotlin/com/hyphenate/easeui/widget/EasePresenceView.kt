package com.hyphenate.easeui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatPresence
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.common.utils.EasePresenceUtil
import com.hyphenate.easeui.common.extensions.loadAvatar
import com.hyphenate.easeui.configs.setAvatarStyle
import com.hyphenate.easeui.databinding.EasePresenceViewBinding
import com.hyphenate.easeui.model.EaseProfile

class EasePresenceView : ConstraintLayout{
    private val mViewBinding = EasePresenceViewBinding.inflate(LayoutInflater.from(context))

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(mViewBinding.root)
        init()
    }
    private fun init() {
        setOnClickListener { v ->
            if (listener != null) {
                listener?.onPresenceClick(v)
            }
        }
        mViewBinding.ivUserAvatar.setOnClickListener {
            listener?.onPresenceAvatarClick(it)
        }

        EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(mViewBinding.ivUserAvatar)
    }

    fun setPresenceStatusSize(size: Int) {
        mViewBinding.ivPresence.let {
            it.layoutParams.width = size
            it.layoutParams.height = size
        }
    }

    fun setPresenceStatusMargin(
        start:Int? = 0,
        top:Int? = 0,
        end:Int? = 0,
        bottom:Int? = 0
    ){
        val layoutParams = mViewBinding.ivPresence.layoutParams as MarginLayoutParams
        start?.let {
            layoutParams.marginStart = it.dpToPx(context)
        }
        top?.let {
            layoutParams.topMargin = it.dpToPx(context)
        }
        end?.let {
            layoutParams.marginEnd = it.dpToPx(context)
        }
        bottom?.let {
            layoutParams.bottomMargin = it.dpToPx(context)
        }
        mViewBinding.ivPresence.layoutParams = layoutParams
    }

    fun getStatusView(): EaseImageView {
        return mViewBinding.ivPresence
    }

    fun setPresenceData(profile: EaseProfile?, presence: ChatPresence? = null) {
        mViewBinding.ivUserAvatar.loadAvatar(profile)
        presence?.let {
            mViewBinding.ivPresence.setImageResource(EasePresenceUtil.getPresenceIcon(context, it))
        }
    }

    fun setPresenceData(@DrawableRes avatar: Int?,nickname: String?,presence: ChatPresence? = null){
        mViewBinding.ivUserAvatar.load(avatar) {
            placeholder(R.drawable.ease_default_avatar)
            error(R.drawable.ease_default_avatar)
        }
        presence?.let {
            mViewBinding.ivPresence.setImageResource(EasePresenceUtil.getPresenceIcon(context, it))
        }
    }

    interface OnPresenceClickListener {
        fun onPresenceClick(v: View?){}
        fun onPresenceAvatarClick(v: View){}
    }

    private var listener: OnPresenceClickListener? = null

    fun setOnPresenceClickListener(listener: OnPresenceClickListener?) {
        this.listener = listener
    }

    fun getUserAvatar():EaseImageView{
        return mViewBinding.ivUserAvatar
    }
}