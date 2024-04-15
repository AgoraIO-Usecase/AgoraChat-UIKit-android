package com.hyphenate.easeui.feature.chat.forward

import android.os.Bundle
import android.view.View
import com.hyphenate.easeui.feature.chat.forward.adapter.EaseGroupListForwardAdapter
import com.hyphenate.easeui.feature.group.adapter.EaseGroupListAdapter
import com.hyphenate.easeui.feature.group.fragment.EaseGroupListFragment
import com.hyphenate.easeui.interfaces.OnForwardClickListener

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