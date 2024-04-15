package com.hyphenate.easeui.feature.chat.forward.dialog

import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.hyphenate.easeui.base.EaseBaseFullDialogFragment
import com.hyphenate.easeui.feature.search.EaseSearchForwardUserFragment
import com.hyphenate.easeui.feature.search.EaseSearchUserFragment
import com.hyphenate.easeui.interfaces.OnForwardClickListener

class EaseSearchForwardUserDialogFragment: EaseBaseFullDialogFragment() {
    private var forwardClickListener: OnForwardClickListener? = null
    private var sentUserList: List<String>? = null

    override fun attachWithChild(
        childFragmentManager: FragmentManager,
        fragmentContainer: FrameLayout
    ) {
        val fragment = EaseSearchUserFragment.Builder()
            .showRightCancel(true)
            .setCustomFragment(EaseSearchForwardUserFragment())
            .setOnCancelListener(object : EaseSearchUserFragment.OnCancelClickListener {
                override fun onCancelClick(view: View) {
                    dismiss()
                }
            })
            .build()
        if (fragment is EaseSearchForwardUserFragment){
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