package com.hyphenate.easeui.common.helper

import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.widget.EaseSidebar

class SidebarHelper : EaseSidebar.OnTouchEventListener {
    private var mFloatingHeader: TextView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var isShowFloating:Boolean = false

    fun setupWithRecyclerView(
        recyclerView: RecyclerView?,
        adapter: RecyclerView.Adapter<*>?,
        floatingHeader: TextView? = null,
        isShowFloating:Boolean = false
    ) {
        mRecyclerView = recyclerView
        mAdapter = adapter
        mFloatingHeader = floatingHeader
        this.isShowFloating = isShowFloating
    }

    override fun onActionDown(event: MotionEvent?, pointer: String?) {
        if (isShowFloating){
            showFloatingHeader(pointer)
        }
        moveToRecyclerItem(pointer)
    }

    override fun onActionMove(event: MotionEvent?, pointer: String?) {
        if (isShowFloating){
            showFloatingHeader(pointer)
        }
        moveToRecyclerItem(pointer)
    }

    override fun onActionUp(event: MotionEvent?) {
        hideFloatingHeader()
    }

    private fun moveToRecyclerItem(pointer: String?) {
        if (mAdapter == null) {
            return
        }
        if (mAdapter !is EaseBaseRecyclerViewAdapter<*>) {
            return
        }
        val data: List<*>? = (mAdapter as EaseBaseRecyclerViewAdapter<*>).data
        if (data == null || data.isEmpty()) {
            return
        }
        if (data[0] !is EaseUser) return
        for (i in data.indices) {
            val item: EaseUser = data[i] as EaseUser
            item.initialLetter.let {
                if (TextUtils.equals(it, pointer)) {
                    val manager = mRecyclerView?.layoutManager as LinearLayoutManager?
                    manager?.scrollToPositionWithOffset(i, 0)
                }
            }
        }
    }

    /**
     * Show sliding characters
     * @param pointer
     */
    private fun showFloatingHeader(pointer: String?) {
        if (TextUtils.isEmpty(pointer)) {
            hideFloatingHeader()
            return
        }
        mFloatingHeader?.text = pointer
        mFloatingHeader?.visibility = View.VISIBLE
    }

    private fun hideFloatingHeader() {
        mFloatingHeader?.visibility = View.GONE
    }
}