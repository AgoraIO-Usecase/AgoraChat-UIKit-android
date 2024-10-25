package io.agora.uikit.feature.chat.forward

import android.os.Bundle
import android.view.View
import io.agora.uikit.common.ChatType
import io.agora.uikit.feature.chat.forward.adapter.EaseContactForwardAdapter
import io.agora.uikit.feature.chat.forward.dialog.EaseSearchForwardUserDialogFragment
import io.agora.uikit.feature.contact.EaseContactsListFragment
import io.agora.uikit.interfaces.OnForwardClickListener

class EaseContactForwardFragmentEvent: EaseContactsListFragment() {
    private var forwardClickListener: OnForwardClickListener? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding?.titleContact?.visibility = View.GONE
        binding?.listContact?.setSideBarVisible(false)
        val adapter = EaseContactForwardAdapter()
        adapter.setHasStableIds(true)
        binding?.listContact?.setListAdapter(adapter)
    }

    override fun initListener() {
        super.initListener()
        (binding?.listContact?.getListAdapter() as? EaseContactForwardAdapter)
            ?.setOnForwardClickListener(forwardClickListener)

        binding?.searchBar?.setOnClickListener {
            EaseSearchForwardUserDialogFragment().apply {
                setOnForwardClickListener(object : OnForwardClickListener {
                    override fun onForwardClick(view: View?, id: String, chatType: ChatType) {
                        (this@EaseContactForwardFragmentEvent.binding?.listContact?.getListAdapter() as? EaseContactForwardAdapter)
                            ?.setSentUserList(listOf(id))
                    }
                })
                setSentUserList((this@EaseContactForwardFragmentEvent.binding?.listContact?.getListAdapter()
                        as? EaseContactForwardAdapter)?.getSentUserList())
                show(this@EaseContactForwardFragmentEvent.childFragmentManager, "search_forward_user")
            }
        }
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener?){
        this.forwardClickListener = listener
    }

    override fun onDestroyView() {
        forwardClickListener = null
        super.onDestroyView()
    }
}