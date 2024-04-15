package com.hyphenate.easeui.base

import androidx.recyclerview.widget.RecyclerView

abstract class EaseBaseAdapter<VH : RecyclerView.ViewHolder?> :
    RecyclerView.Adapter<VH>() {
    /**
     * Get the data item associated with the specified position in the data set.
     * @param position
     * @return
     */
    abstract fun getItem(position: Int): Any?
}