package io.agora.uikit.feature.chat.forward

import android.os.Bundle
import android.view.View
import io.agora.uikit.feature.chat.forward.adapter.EaseGroupListForwardAdapter
import io.agora.uikit.feature.group.adapter.EaseGroupListAdapter
import io.agora.uikit.feature.group.fragments.EaseGroupListFragment
import io.agora.uikit.interfaces.OnForwardClickListener

class EaseGroupListForwardFragment: EaseGroupListFragment() {
    private var forwardClickListener: OnForwardClickListener? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.titleContact?.visibility = View.GONE
    }

    override fun getCustomAdapter(): EaseGroupListAdapter {
        return EaseGroupListForwardAdapter()
    }

    override fun initListener() {
        super.initListener()
        (this.adapter as? EaseGroupListForwardAdapter)
            ?.setOnForwardClickListener(forwardClickListener)
    }

    override fun onItemClick(view: View?, position: Int) {
        // do nothing
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }
}