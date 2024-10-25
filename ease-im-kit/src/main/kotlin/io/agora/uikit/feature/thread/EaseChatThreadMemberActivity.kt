package io.agora.uikit.feature.thread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseActivity
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.dialog.SimpleListSheetDialog
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.databinding.EaseActivityThreadMemberLayoutBinding
import io.agora.uikit.feature.thread.fragment.EaseChatThreadMemberFragment
import io.agora.uikit.feature.thread.interfaces.OnThreadMemberItemClickListener
import io.agora.uikit.interfaces.SimpleListSheetItemClickListener
import io.agora.uikit.model.EaseMenuItem

open class EaseChatThreadMemberActivity: EaseBaseActivity<EaseActivityThreadMemberLayoutBinding>() {
    private var deleteDialog:SimpleListSheetDialog? = null

    private val fragment:EaseChatThreadMemberFragment by lazy{
        EaseChatThreadMemberFragment()
    }
    override fun getViewBinding(inflater: LayoutInflater): EaseActivityThreadMemberLayoutBinding{
        return EaseActivityThreadMemberLayoutBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = Bundle()
        intent?.let {
            val threadId = it.getStringExtra(EaseConstant.THREAD_CHAT_THREAD_ID)
            val parentId = it.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)
            bundle.putString(EaseConstant.THREAD_CHAT_THREAD_ID,threadId)
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID,parentId)
        }
        fragment.let {
            it.arguments = bundle
            it.setOnThreadMemberItemClickListener(object : OnThreadMemberItemClickListener{
                override fun onThreadMemberItemClick(view: View?, userId: String) {
                    if (EaseIM.getCurrentUser()?.id != userId){
                        threadMemberItemClick(view,userId)
                    }
                }
            })
            supportFragmentManager.beginTransaction().add(binding.flFragment.id, it).commit()
        }
        binding.run {
            titleBar.setTitle(getString(R.string.ease_thread_member_title))
            titleBar.setNavigationOnClickListener { finish() }
        }
    }

    open fun threadMemberItemClick(view: View?, userId:String){
        val menu = mutableListOf(EaseMenuItem(
            menuId = R.id.thread_member_remove,
            title = getString(R.string.ease_thread_remove_from_topic),
            titleColor = ContextCompat.getColor(this@EaseChatThreadMemberActivity, R.color.ease_color_error),
        ))
        deleteDialog = SimpleListSheetDialog(
            context = this@EaseChatThreadMemberActivity,
            itemList = menu,
            itemListener = object : SimpleListSheetItemClickListener {
                override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                    when(menu.menuId){
                        R.id.thread_member_remove -> {
                            fragment.removeMember(userId)
                            deleteDialog?.dismiss()
                        }
                        else -> {}
                    }
                }
            })
        if (fragment.isGroupOwner()){
            supportFragmentManager.let { pm-> deleteDialog?.show(pm,"thread_member_delete_dialog") }
        }
    }

    companion object {
        fun actionStart(context: Context, conversationId:String?,threadId:String) {
            val intent = Intent(context, EaseChatThreadMemberActivity::class.java)
            intent.putExtra(EaseConstant.THREAD_CHAT_THREAD_ID,threadId)
            conversationId?.let {
                intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,it)
            }
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