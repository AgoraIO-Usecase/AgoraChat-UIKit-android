package io.agora.uikit.feature.chat.reaction.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.agora.uikit.feature.chat.reaction.EaseReactionUserListFragment
import io.agora.uikit.model.EaseReaction

class EaseReactionUserPagerAdapter(
    private val messageId: String,
    private val manager: FragmentManager,
    private val lifecycle: Lifecycle
): FragmentStateAdapter(manager, lifecycle) {
    private var reactions: List<EaseReaction>? = null

    fun setData(reactions: List<EaseReaction>?) {
        this.reactions = reactions
        notifyDataSetChanged()
    }

    fun getData(): List<EaseReaction>? {
        return reactions
    }

    override fun getItemCount(): Int {
        return reactions?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = EaseReactionUserListFragment()
        val bundle = reactions?.get(position)?.let {
            val bundle = Bundle()
            bundle.putString("reaction", it.identityCode)
            bundle.putString("messageId", messageId)
            bundle
        }
        fragment.arguments = bundle
        return fragment
    }
}