package io.agora.uikit.feature.contact.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.extensions.dpToPx
import io.agora.uikit.common.extensions.toProfile
import io.agora.uikit.configs.setAvatarStyle
import io.agora.uikit.configs.setStatusStyle
import io.agora.uikit.databinding.EaseLayoutContactItemBinding
import io.agora.uikit.interfaces.OnUserListItemClickListener
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.getSyncUser
import io.agora.uikit.widget.EaseCustomAvatarView
import java.util.concurrent.ConcurrentHashMap

class EaseUserContactItem: ConstraintLayout, View.OnClickListener {

    val mViewBinding = EaseLayoutContactItemBinding.inflate(LayoutInflater.from(context))
    private var userAvatarInfo: ConcurrentHashMap<String, Int>? = null
    private var listener:OnUserListItemClickListener?=null
    private var user: EaseUser? = null
    private var position : Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addView(mViewBinding.root)
        initView(context, attrs)
        initListener()
    }

    fun initView(context: Context, attrs: AttributeSet?){
        mViewBinding.let {
            EaseIM.getConfig()?.avatarConfig?.setAvatarStyle(it.emPresence.getUserAvatar())
            EaseIM.getConfig()?.avatarConfig?.setStatusStyle(it.emPresence.getStatusView(),2.dpToPx(context),
                ContextCompat.getColor(context, R.color.ease_color_background))
            it.emPresence.setPresenceStatusMargin(end = -2, bottom = -2)
            it.emPresence.setPresenceStatusSize(resources.getDimensionPixelSize(R.dimen.ease_contact_list_status_icon_size))
            val layoutParams = it.emPresence.getUserAvatar().layoutParams
            layoutParams.width = 40.dpToPx(context)
            layoutParams.height = 40.dpToPx(context)
            it.emPresence.getUserAvatar().layoutParams = layoutParams
        }
    }

    private fun initListener(){
        mViewBinding.itemLayout.setOnClickListener(this)
        mViewBinding.emPresence.setOnPresenceClickListener(object :
            EaseCustomAvatarView.OnPresenceClickListener{
                override fun onPresenceAvatarClick(v: View) {
                    listener?.onAvatarClick(v,position)
                }
            })
    }

    fun setUpView(user: EaseUser?, position:Int, isGroupMember: Boolean = false){
        this.user = user
        this.position = position

        mViewBinding.let {
            it.emPresence.setUserAvatarData(user?.toProfile())
            it.tvName.text = user?.toProfile()?.getRemarkOrName()

            userAvatarInfo?.let { presence->
                user?.let {user->
                    mViewBinding.emPresence.setUserAvatarData(user.toProfile())
                    mViewBinding.emPresence.setUserStatusData(presence[user.userId])
                }
            }

            // Set custom data provided by user
            if (!isGroupMember) {
                EaseIM.getUserProvider()?.getSyncUser(user?.userId)?.let { profile ->
                    it.emPresence.setUserAvatarData(profile)
                    it.tvName.text = profile.getRemarkOrName()
                }
            }

        }
    }

    fun setUserAvatarInfo(info: ConcurrentHashMap<String, Int>?) {
        info?.let {
            this.userAvatarInfo = it
        }
    }

    fun setOnUserListItemClickListener(listener: OnUserListItemClickListener?){
        this.listener = listener
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.item_layout -> {
                listener?.onUserListItemClick(v,position,user)
            }
        }

    }


}