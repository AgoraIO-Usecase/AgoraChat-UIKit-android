package io.agora.uikit.feature.chat.forward

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseSheetFragmentDialog
import io.agora.uikit.databinding.EaseDialogMessageForwardBinding
import io.agora.uikit.feature.chat.forward.adapter.EaseMessageForwardPagerAdapter
import io.agora.uikit.interfaces.OnForwardClickListener
import io.agora.uikit.model.EasePager

class EaseMessageForwardDialogFragment: EaseBaseSheetFragmentDialog<EaseDialogMessageForwardBinding>() {
    private var forwardClickListener: OnForwardClickListener? = null

    private val pagerAdapter by lazy {
        EaseMessageForwardPagerAdapter(childFragmentManager, lifecycle)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): EaseDialogMessageForwardBinding? {
        return EaseDialogMessageForwardBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        super.initView()
        binding?.run {
            titleBar.setTitle(R.string.ease_message_forward)
            vpForward.adapter = pagerAdapter
            TabLayoutMediator(tlForward, vpForward) { tab, position ->
                tab.text = pagerAdapter.getData()?.get(position)?.title
            }.attach()
        }
    }

    override fun initListener() {
        super.initListener()
        binding?.titleBar?.setNavigationOnClickListener{
            dismiss()
        }
    }

    override fun initData() {
        super.initData()
        val contact = EaseContactForwardFragmentEvent()
        val group = EaseGroupListForwardFragment()
        contact.setOnForwardClickListener(forwardClickListener)
        group.setOnForwardClickListener(forwardClickListener)
        val list = mutableListOf(EasePager(getString(R.string.ease_message_forward_to_contact), contact)
            , EasePager(getString(R.string.ease_message_forward_to_group), group))
        pagerAdapter.setData(list)
    }

    fun setOnForwardClickListener(listener: OnForwardClickListener){
        this.forwardClickListener = listener
    }

    override fun showExpandedState(): Boolean {
        return true
    }
}