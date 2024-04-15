package com.hyphenate.easeui.common.helper

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.hyphenate.easeui.base.EaseBaseFragmentBuilder
import com.hyphenate.easeui.widget.EaseTitleBar

/**
 * To deal with the common logic for [EaseTitleBar]
 */
object EaseTitleBarHelper {

    /**
     * Parse the bundle to set the title bar
     */
    fun parseBundleForTitleBar(bundle: Bundle?, titleBar: EaseTitleBar?, useTitleBarAction: (EaseTitleBar) -> Unit = {}) {
        // do something
        bundle?.let {
            val useHeader: Boolean = it.getBoolean(EaseBaseFragmentBuilder.BConstant.KEY_USE_TITLE, false)
            titleBar?.visibility = if (useHeader) View.VISIBLE else View.GONE
            if (useHeader) {
                titleBar?.run {
                    useTitleBarAction(this)
                    val title: String = it.getString(EaseBaseFragmentBuilder.BConstant.KEY_SET_TITLE, "")
                    if (!TextUtils.isEmpty(title)) {
                        setTitle(title)
                    }
                    val subTitle: String = it.getString(EaseBaseFragmentBuilder.BConstant.KEY_SET_SUB_TITLE, "")
                    if (!TextUtils.isEmpty(subTitle)) {
                        setSubtitle(subTitle)
                    }
                    val canBack: Boolean = it.getBoolean(EaseBaseFragmentBuilder.BConstant.KEY_ENABLE_BACK, false)
                    setDisplayHomeAsUpEnabled(canBack
                        , it.getBoolean(EaseBaseFragmentBuilder.BConstant.KEY_USE_TITLE_REPLACE, false))
                }
            }
        }
    }
}