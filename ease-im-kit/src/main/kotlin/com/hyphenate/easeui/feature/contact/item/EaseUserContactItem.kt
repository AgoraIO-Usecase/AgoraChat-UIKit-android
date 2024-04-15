package com.hyphenate.easeui.feature.contact.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatPresence
import com.hyphenate.easeui.common.extensions.toProfile
import com.hyphenate.easeui.databinding.EaseLayoutContactItemBinding
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.provider.getSyncUser
import com.hyphenate.easeui.widget.EasePresenceView
import java.util.concurrent.ConcurrentHashMap

class EaseUserContactItem: ConstraintLayout, View.OnClickListener {

    val mViewBinding = EaseLayoutContactItemBinding.inflate(LayoutInflater.from(context))
    private var userPresence: ConcurrentHashMap<String, ChatPresence>? = null
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

    }

    private fun initListener(){
        mViewBinding.itemLayout.setOnClickListener(this)
        mViewBinding.emPresence.setOnPresenceClickListener(object :
            EasePresenceView.OnPresenceClickListener{
                override fun onPresenceAvatarClick(v: View) {
                    listener?.onAvatarClick(v,position)
                }
            })
    }

    fun setUpView(user: EaseUser?, position:Int, isGroupMember: Boolean = false){
        this.user = user
        this.position = position

        mViewBinding.let {
            it.emPresence.setPresenceData(user?.toProfile())
            it.tvName.text = user?.toProfile()?.getRemarkOrName()

            userPresence?.let { presence->
                user?.let {user->
                    mViewBinding.emPresence.setPresenceData(user.toProfile(),presence[user.userId])
                }
            }

            // Set custom data provided by user
            if (!isGroupMember) {
                EaseIM.getUserProvider()?.getSyncUser(user?.userId)?.let { profile ->
                    it.emPresence.setPresenceData(profile)
                    it.tvName.text = profile.getRemarkOrName()
                }
            }

        }
    }

    fun setUserPresences(userPresence: ConcurrentHashMap<String, ChatPresence>?) {
        userPresence?.let {
            this.userPresence = it
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