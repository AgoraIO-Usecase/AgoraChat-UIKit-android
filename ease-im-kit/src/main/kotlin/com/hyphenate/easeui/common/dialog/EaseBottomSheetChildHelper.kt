package com.hyphenate.easeui.common.dialog

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.hyphenate.easeui.R

interface EaseBottomSheetChildHelper {
    @get:StringRes
    val titleBarTitle: Int
        //more details by user choose
        get() = R.string.ease_cancel

    @get:StringRes
    val titleBarRightText: Int
        get() = R.string.ease_cancel

    @get:ColorRes
    val titleBarRightTextColor: Int
        get() = R.color.ease_color_primary

    fun onTitleBarRightTextViewClick(): Boolean {
        return false
    }

    val isShowTitleBarLeftLayout: Boolean
        get() = false

    fun getParentFragment(): Fragment?

    fun startFragment(fragment: Fragment?, tag: String?) {
        val parentFragment = getParentFragment()
        if (parentFragment != null && parentFragment is EaseBottomSheetContainerHelper) {
            (parentFragment as EaseBottomSheetContainerHelper).startFragment(fragment, tag)
        }
    }

    fun hide() {
        val parentFragment = getParentFragment()
        if (parentFragment != null && parentFragment is EaseBottomSheetContainerHelper) {
            (parentFragment as EaseBottomSheetContainerHelper).hide()
        }
    }

    fun back() {
        val parentFragment = getParentFragment()
        if (parentFragment != null && parentFragment is EaseBottomSheetContainerHelper) {
            (parentFragment as EaseBottomSheetContainerHelper).back()
        }
    }

    fun isChangeColor(isChange: Boolean) {
        val parentFragment = getParentFragment()
        if (parentFragment != null && parentFragment is EaseBottomSheetContainerHelper) {
            (parentFragment as EaseBottomSheetContainerHelper).changeNextColor(isChange)
        }
    }
}