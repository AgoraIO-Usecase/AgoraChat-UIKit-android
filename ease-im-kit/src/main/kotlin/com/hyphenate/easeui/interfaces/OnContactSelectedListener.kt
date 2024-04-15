package com.hyphenate.easeui.interfaces

import android.view.View

interface OnContactSelectedListener {
    fun onContactSelectedChanged(v: View, selectedMembers: MutableList<String>)
}
