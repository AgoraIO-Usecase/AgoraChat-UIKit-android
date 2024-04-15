package com.hyphenate.easeui.interfaces

interface OnItemDataClickListener {
    /**
     * Item click.
     * @param position
     * @param data
     */
    fun onItemClick(data: Any?, position: Int)
}