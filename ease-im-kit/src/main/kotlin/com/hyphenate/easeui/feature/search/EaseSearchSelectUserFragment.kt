package com.hyphenate.easeui.feature.search

import android.os.Bundle
import android.view.View
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.search.adapter.EaseSearchUserAdapter
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.model.EaseUser

class EaseSearchSelectUserFragment:EaseSearchUserFragment(), OnContactSelectedListener {
    private lateinit var adapter:EaseSearchUserAdapter
    private var selectData:MutableList<String> = mutableListOf()
    private var selectListener:OnContactSelectedListener?=null

    override fun initAdapter(): EaseBaseRecyclerViewAdapter<EaseUser> {
        adapter = EaseSearchUserAdapter(true)
        return adapter
    }

    override fun initListener() {
        super.initListener()
        adapter.setCheckBoxSelectListener(this)
    }

    private fun setOnSelectListener(onSelectListener: OnContactSelectedListener?) {
        this.selectListener = onSelectListener
    }

    override fun onContactSelectedChanged(v: View, selectedMembers: MutableList<String>) {
        this.selectData = selectedMembers
    }

    override fun onTvRightClick(view: View) {
        selectListener?.onContactSelectedChanged(view,selectData)
        resetSelectList()
    }

    fun resetSelectList(){
        adapter.resetSelect()
    }

    class Builder {
        private val bundle: Bundle = Bundle()
        private var customFragment: EaseSearchSelectUserFragment? = null
        private var selectListener:OnContactSelectedListener?=null

        fun <T : EaseSearchSelectUserFragment?> setCustomFragment(fragment: T): Builder {
            customFragment = fragment
            return this
        }

        fun showRightCancel(showCancel: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_SHOW_RIGHT_CANCEL, showCancel)
            return this
        }

        fun setOnSelectListener(onSelectListener: OnContactSelectedListener?): Builder {
            this.selectListener = onSelectListener
            return this
        }

        fun build(): EaseSearchSelectUserFragment {
            val fragment =
                if (customFragment != null) customFragment else EaseSearchSelectUserFragment()
            fragment!!.arguments = bundle
            fragment.setOnSelectListener(selectListener)
            return fragment
        }

        private object Constant {
            const val KEY_SHOW_RIGHT_CANCEL = "key_show_right_cancel"
        }
    }

}