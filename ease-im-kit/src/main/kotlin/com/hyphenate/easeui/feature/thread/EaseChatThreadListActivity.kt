package com.hyphenate.easeui.feature.thread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.EaseActivityChatThreadListBinding
import com.hyphenate.easeui.feature.thread.fragment.EaseChatThreadListFragment
import com.hyphenate.easeui.feature.thread.interfaces.OnChatThreadListItemClickListener

open class EaseChatThreadListActivity:EaseBaseActivity<EaseActivityChatThreadListBinding>() {
    private var fragment: EaseChatThreadListFragment? = null
    override fun getViewBinding(inflater: LayoutInflater): EaseActivityChatThreadListBinding? {
        return EaseActivityChatThreadListBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val conversationId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)

        val builder = EaseChatThreadListFragment.Builder(conversationId)
            .useTitleBar(true)
            .enableTitleBarPressBack(true)
            .setOnChatThreadListItemClickListener(object : OnChatThreadListItemClickListener{
                override fun onChatThreadItemClick(view: View?, thread: ChatThread) {
                    EaseChatThreadActivity.actionStart(
                        context = mContext,
                        conversationId = thread.parentId,
                        threadId = thread.chatThreadId,
                        topicMsgId = thread.messageId,
                    )
                }
            })
        setChildSettings(builder)
        fragment = builder.build()
        fragment?.let {
            supportFragmentManager.beginTransaction().add(binding.root.id, it).commit()
        }
    }

    protected open fun setChildSettings(builder: EaseChatThreadListFragment.Builder) {}

    companion object {
        fun actionStart(context: Context,conversationId:String?) {
            val intent = Intent(context, EaseChatThreadListActivity::class.java)
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