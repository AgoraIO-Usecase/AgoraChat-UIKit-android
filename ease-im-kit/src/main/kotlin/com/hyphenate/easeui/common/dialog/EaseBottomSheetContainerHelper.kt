package com.hyphenate.easeui.common.dialog

import androidx.fragment.app.Fragment

interface EaseBottomSheetContainerHelper {
    /**
     * Start fragment
     * @param fragment
     * @param tag
     */
    fun startFragment(fragment: Fragment?, tag: String?)

    /**
     * Dismiss bottom sheet.
     */
    fun hide()

    /**
     * Back to previous fragment.
     */
    fun back()
    fun changeNextColor(isChange: Boolean)
}