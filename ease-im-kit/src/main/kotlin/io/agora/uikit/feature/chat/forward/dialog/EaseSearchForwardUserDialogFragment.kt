package io.agora.uikit.feature.chat.forward.dialog

import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import io.agora.uikit.base.EaseBaseFullDialogFragment
import io.agora.uikit.feature.search.EaseSearchForwardUserFragment
import io.agora.uikit.feature.search.EaseSearchUserFragment
import io.agora.uikit.interfaces.OnForwardClickListener

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