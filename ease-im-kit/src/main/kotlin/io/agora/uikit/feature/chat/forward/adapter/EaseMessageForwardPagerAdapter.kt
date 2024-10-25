package io.agora.uikit.feature.chat.forward.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.agora.uikit.model.EasePager

class EaseMessageForwardPagerAdapter(
    private val manager: FragmentManager,
    private val lifecycle: Lifecycle
): FragmentStateAdapter(manager, lifecycle) {
    private var mData: List<EasePager>? = null

    fun setData(data: List<EasePager>?) {
        this.mData = data
        notifyDataSetChanged()
    }

    fun getData(): List<EasePager>? {
        return mData
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        mData!![position].let {
            return it.fragment
        }
    }
}