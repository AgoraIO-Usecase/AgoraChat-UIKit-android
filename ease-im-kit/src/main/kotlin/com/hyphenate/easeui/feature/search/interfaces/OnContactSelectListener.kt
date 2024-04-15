package com.hyphenate.easeui.feature.search.interfaces

import android.view.View

interface OnContactSelectListener {
    fun onContactSelectedChanged(v: View,userId:String,isSelected:Boolean)
}