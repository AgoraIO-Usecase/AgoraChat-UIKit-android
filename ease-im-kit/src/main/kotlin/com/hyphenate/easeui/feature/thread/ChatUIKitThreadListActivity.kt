package com.hyphenate.easeui.feature.thread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.base.ChatUIKitBaseActivity
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.UikitActivityChatThreadListBinding
import com.hyphenate.easeui.feature.thread.fragment.ChatUIKitThreadListFragment
import com.hyphenate.easeui.feature.thread.interfaces.OnChatThreadListItemClickListener

open class ChatUIKitThreadListActivity:ChatUIKitBaseActivity<UikitActivityChatThreadListBinding>() {
    private var fragment: ChatUIKitThreadListFragment? = null
    override fun getViewBinding(inflater: LayoutInflater): UikitActivityChatThreadListBinding? {
        return UikitActivityChatThreadListBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val conversationId = intent.getStringExtra(ChatUIKitConstant.EXTRA_CONVERSATION_ID)

        val builder = ChatUIKitThreadListFragment.Builder(conversationId)
            .useTitleBar(true)
            .enableTitleBarPressBack(true)
            .setOnChatThreadListItemClickListener(object : OnChatThreadListItemClickListener{
                override fun onChatThreadItemClick(view: View?, thread: ChatThread) {
                    ChatUIKitThreadActivity.actionStart(
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

    protected open fun setChildSettings(builder: ChatUIKitThreadListFragment.Builder) {}

    companion object {
        fun actionStart(context: Context,conversationId:String?) {
            val intent = Intent(context, ChatUIKitThreadListActivity::class.java)
            conversationId?.let {
                intent.putExtra(ChatUIKitConstant.EXTRA_CONVERSATION_ID,it)
            }
            ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    context.startActivity(it)
                    return
                }
            }
            context.startActivity(intent)
        }
    }
}