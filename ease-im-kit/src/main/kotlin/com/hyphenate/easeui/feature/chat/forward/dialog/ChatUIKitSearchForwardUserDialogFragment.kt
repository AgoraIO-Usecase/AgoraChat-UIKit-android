package com.hyphenate.easeui.feature.chat.forward.dialog

import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.hyphenate.easeui.base.ChatUIKitBaseFullDialogFragment
import com.hyphenate.easeui.feature.search.ChatUIKitSearchForwardUserFragment
import com.hyphenate.easeui.feature.search.ChatUIKitSearchUserFragment
import com.hyphenate.easeui.interfaces.OnForwardClickListener

class ChatUIKitSearchForwardUserDialogFragment: ChatUIKitBaseFullDialogFragment() {
    private var forwardClickListener: OnForwardClickListener? = null
    private var sentUserList: List<String>? = null

    override fun attachWithChild(
        childFragmentManager: FragmentManager,
        fragmentContainer: FrameLayout
    ) {
        val fragment = ChatUIKitSearchUserFragment.Builder()
            .showRightCancel(true)
            .setCustomFragment(ChatUIKitSearchForwardUserFragment())
            .setOnCancelListener(object : ChatUIKitSearchUserFragment.OnCancelClickListener {
                override fun onCancelClick(view: View) {
                    dismiss()
                }
            })
            .build()
        if (fragment is ChatUIKitSearchForwardUserFragment){
            fragment.setOnForwardClickListener(forwardClickListener)
            fragment.setSentUserList(sentUserList)
        }
        childFragmentManager.beginTransaction().replace(fragmentContainer.id, fragment).commit()
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }

    fun setSentUserList(userList: List<String>?) {
        this.sentUserList = userList
    }
}