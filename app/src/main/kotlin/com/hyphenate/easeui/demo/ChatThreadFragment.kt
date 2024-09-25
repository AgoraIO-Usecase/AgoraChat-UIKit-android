package com.hyphenate.easeui.demo

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.common.dialog.CustomDialog
import com.hyphenate.easeui.common.dialog.SimpleListSheetDialog
import com.hyphenate.easeui.common.extensions.isAdmin
import com.hyphenate.easeui.common.extensions.isOwner
import com.hyphenate.easeui.feature.group.EaseGroupDetailEditActivity
import com.hyphenate.easeui.feature.group.EditType
import com.hyphenate.easeui.feature.thread.EaseChatThreadMemberActivity
import com.hyphenate.easeui.feature.thread.fragment.EaseChatThreadFragment
import com.hyphenate.easeui.interfaces.SimpleListSheetItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem

class ChatThreadFragment:EaseChatThreadFragment() {
    private var moreDialog:SimpleListSheetDialog? = null

    companion object{
        private val TAG = ChatThreadFragment::class.java.simpleName
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.run {
            titleBar.inflateMenu(R.menu.demo_thread_action_more)
            titleBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_more -> {
                        showDialog()
                    }
                }
                true
            }
        }
    }

     override fun showDialog(){
        context?.let {
            val menu = mutableListOf(
                EaseMenuItem(
                    order = 0,
                    menuId = R.id.thread_more_edit,
                    title = resources.getString(R.string.thread_edit),
                    resourceId = R.drawable.icon_thread_edit,
                    titleColor = ContextCompat.getColor(mContext, R.color.em_color_on_background),
                    isVisible = (parentInfo?.isOwner() == true || parentInfo?.isAdmin() == true || mThread?.isOwner() == true)
                ),
                EaseMenuItem(
                    order = 1*10,
                    menuId = R.id.thread_more_member,
                    title = resources.getString(R.string.thread_member),
                    resourceId = R.drawable.icon_thread_member,
                    titleColor = ContextCompat.getColor(mContext, R.color.em_color_on_background)
                ),
                EaseMenuItem(
                    order = 2*10,
                    menuId = R.id.thread_more_leave,
                    title =  resources.getString(R.string.thread_leave),
                    resourceId = R.drawable.icon_thread_leave,
                    titleColor = ContextCompat.getColor(mContext, R.color.em_color_on_background),
                ),
                EaseMenuItem(
                    order = 3*10,
                    menuId = R.id.thread_more_destroy,
                    title =  resources.getString(R.string.thread_destroy) ,
                    resourceId = R.drawable.icon_thread_destroy ,
                    titleColor = ContextCompat.getColor(mContext, R.color.em_color_error),
                    isVisible = (parentInfo?.isOwner() == true || parentInfo?.isAdmin() == true)
                )
            )
            moreDialog = SimpleListSheetDialog(
                context = it,
                itemList = menu,
                itemListener = object : SimpleListSheetItemClickListener {
                    override fun onItemClickListener(position: Int, menu: EaseMenuItem) {
                        when(menu.menuId){
                            R.id.thread_more_edit -> {
                                startActivity(EaseGroupDetailEditActivity.createIntent(
                                    context = mContext,
                                    groupId = mThread?.parentId,
                                    type = EditType.ACTION_EDIT_THREAD_NAME,
                                    threadName = mThread?.chatThreadName,
                                    threadId = mThread?.chatThreadId
                                ))
                                moreDialog?.dismiss()
                            }
                            R.id.thread_more_member -> {
                                chatThreadId?.let { threadId ->
                                    EaseChatThreadMemberActivity.actionStart(mContext,parentId,threadId)
                                }
                                moreDialog?.dismiss()
                            }
                            R.id.thread_more_leave -> {
                                leaveChatThread()
                            }
                            R.id.thread_more_destroy -> {
                                showDestroyChatThreadDialog()
                            }
                            else -> {}
                        }
                    }
                })
            parentFragmentManager.let { pm-> moreDialog?.show(pm,"thread_more_dialog") }
        }

    }

    override fun showDestroyChatThreadDialog() {
        val clearDialog = CustomDialog(
            context = mContext,
            title = mContext.resources.getString(com.hyphenate.easeui.R.string.ease_thread_delete_topic_title),
            subtitle = mContext.resources.getString(com.hyphenate.easeui.R.string.ease_thread_delete_topic_subtitle),
            isEditTextMode = false,
            onLeftButtonClickListener = {

            },
            onRightButtonClickListener = {
                destroyChatThread()
            }
        )
        clearDialog.show()
    }

    override fun leaveChatThreadSuccess() {
        super.leaveChatThreadSuccess()
        Log.e(TAG,"leaveChatThreadSuccess")
    }

    override fun leaveChatThreadFail(code: Int, message: String?) {
        super.leaveChatThreadFail(code, message)
        Log.e(TAG,"leaveChatThreadFail $code $message")
    }

    override fun destroyChatThreadSuccess() {
        super.destroyChatThreadSuccess()
        Log.e(TAG,"destroyChatThreadSuccess")
    }

    override fun destroyChatThreadFail(code: Int, message: String?) {
        super.destroyChatThreadFail(code, message)
        Log.e(TAG,"destroyChatThreadFail $code $message")
    }
}