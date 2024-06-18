package com.hyphenate.easeui.feature.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseActivity
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.EaseActivitySearchLayoutBinding
import com.hyphenate.easeui.feature.chat.activities.EaseMessageSearchResultActivity
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.search.interfaces.OnSearchMsgItemClickListener
import com.hyphenate.easeui.feature.search.interfaces.OnSearchUserItemClickListener
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.model.EaseUser

class EaseSearchActivity:EaseBaseActivity<EaseActivitySearchLayoutBinding>(),
    EaseSearchUserFragment.OnCancelClickListener {

    private var searchType: EaseSearchType = EaseSearchType.USER
    private var conversationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            searchType = it.getIntExtra(Constant.KEY_SEARCH_TYPE, EaseSearchType.USER.ordinal).let { type->
                when(type){
                    EaseSearchType.USER.ordinal -> { EaseSearchType.USER }
                    EaseSearchType.SELECT_USER.ordinal -> { EaseSearchType.SELECT_USER }
                    EaseSearchType.CONVERSATION.ordinal -> { EaseSearchType.CONVERSATION }
                    EaseSearchType.MESSAGE.ordinal -> { EaseSearchType.MESSAGE}
                    EaseSearchType.BLOCK_USER.ordinal -> { EaseSearchType.BLOCK_USER}
                    else -> { EaseSearchType.CONVERSATION}
                }
            }

            if (it.hasExtra(Constant.KEY_CONVERSATION_ID)){
                conversationId = it.getStringExtra(Constant.KEY_CONVERSATION_ID)
            }
        }
        replaceSearchUserFragment()
    }

    override fun getViewBinding(inflater: LayoutInflater): EaseActivitySearchLayoutBinding {
        return EaseActivitySearchLayoutBinding.inflate(inflater)
    }

    private fun replaceSearchUserFragment() {
        val fragment = when (searchType) {
            EaseSearchType.USER -> {
                EaseSearchUserFragment.Builder()
                    .setItemClickListener(object : OnSearchUserItemClickListener{
                        override fun onSearchItemClick(view: View?, position: Int, user: EaseUser) {
                            Intent().apply {
                                putExtra(Constant.KEY_USER, user)
                                setResult(RESULT_OK, this)
                                finish()
                            }
                        }
                    })
                    .showRightCancel(true)
                    .setOnCancelListener(object : EaseSearchUserFragment.OnCancelClickListener{
                        override fun onCancelClick(view: View) {
                            finish()
                        }
                    }).build()
            }
            EaseSearchType.SELECT_USER -> {
                EaseSearchSelectUserFragment.Builder()
                    .setOnSelectListener(object : OnContactSelectedListener{
                        override fun onContactSelectedChanged(
                            v: View,
                            selectedMembers: MutableList<String>
                        ) {
                            Intent().apply {
                                putStringArrayListExtra(Constant.KEY_SELECT_USER,ArrayList(selectedMembers))
                                setResult(RESULT_OK, this)
                                finish()
                            }
                        }
                    })
                    .showRightCancel(true)
                    .build()
            }
            EaseSearchType.MESSAGE ->{
                EaseSearchMessageFragment.Builder()
                    .setConversationId(conversationId)
                    .setItemClickListener(object : OnSearchMsgItemClickListener{
                        override fun onSearchItemClick(
                            view: View?,
                            position: Int,
                            msg: ChatMessage
                        ) {
                            msg.chatType?.let {
                                val chatType = when(it) {
                                    ChatType.Chat ->  EaseChatType.SINGLE_CHAT
                                    ChatType.GroupChat -> EaseChatType.GROUP_CHAT
                                    ChatType.ChatRoom -> EaseChatType.CHATROOM
                                }
                                EaseMessageSearchResultActivity.actionStart(mContext, conversationId, chatType, msg.msgId)
                            }
                        }
                    })
                    .setOnCancelListener(object : EaseSearchMessageFragment.OnCancelClickListener{
                        override fun onCancelClick(view: View) {
                            finish()
                        }
                    })
                    .showRightCancel(true)
                    .build()
            }
            EaseSearchType.BLOCK_USER -> {
                EaseSearchUserFragment.Builder()
                    .setSearchBlockUser(true)
                    .setItemClickListener(object : OnSearchUserItemClickListener{
                        override fun onSearchItemClick(view: View?, position: Int, user: EaseUser) {
                            Intent().apply {
                                putExtra(Constant.KEY_USER, user)
                                setResult(RESULT_OK, this)
                                finish()
                            }
                        }
                    })
                    .showRightCancel(true)
                    .setOnCancelListener(object : EaseSearchUserFragment.OnCancelClickListener{
                        override fun onCancelClick(view: View) {
                            finish()
                        }
                    }).build()
            }
            else -> {
                EaseSearchConversationFragment()
            }
        }

        val t = supportFragmentManager.beginTransaction()
        t.add(R.id.fl_fragment, fragment, fragment::javaClass.name).show(fragment).commit()

    }

    override fun onCancelClick(view: View) {
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            for (fragment in supportFragmentManager.fragments) {
                if (fragment is EaseSearchSelectUserFragment){
                    fragment.resetSelectList()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private object Constant {
        const val KEY_SELECT_USER = "select_user"
        const val KEY_USER = "user"
        const val KEY_SEARCH_TYPE = "searchType"
        const val KEY_CONVERSATION_ID = "searchConversationId"
    }

    companion object {
        fun actionStart(context: Context, searchType: EaseSearchType) {
            Intent(context, EaseSearchActivity::class.java).apply {
                putExtra(Constant.KEY_SEARCH_TYPE, searchType.ordinal)
                context.startActivity(this)
            }
        }

        fun createIntent(context: Context, searchType: EaseSearchType? = null,conversationId:String? = null): Intent {
            val intent = Intent(context, EaseSearchActivity::class.java)
            if (searchType != null) {
                intent.putExtra(Constant.KEY_SEARCH_TYPE, searchType.ordinal)
            }
            if (conversationId != null){
                intent.putExtra(Constant.KEY_CONVERSATION_ID, conversationId)
            }
            EaseIM.getCustomActivityRoute()?.getActivityRoute(intent.clone() as Intent)?.let {
                if (it.hasRoute()) {
                    return it
                }
            }
            return intent
        }
    }
}

enum class EaseSearchType{
    USER,
    SELECT_USER,
    CONVERSATION,
    MESSAGE,
    BLOCK_USER,
}