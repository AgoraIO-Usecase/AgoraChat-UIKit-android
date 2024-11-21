package com.hyphenate.easeui.common.helper

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.hyphenate.easeui.base.ChatUIKitBaseFragmentBuilder
import com.hyphenate.easeui.widget.ChatUIKitTitleBar

/**
 * To deal with the common logic for [ChatUIKitTitleBar]
 */
object ChatUIKitTitleBarHelper {

    /**
     * Parse the bundle to set the title bar
     */
    fun parseBundleForTitleBar(bundle: Bundle?, titleBar: ChatUIKitTitleBar?, useTitleBarAction: (ChatUIKitTitleBar) -> Unit = {}) {
        // do something
        bundle?.let {
            val useHeader: Boolean = it.getBoolean(ChatUIKitBaseFragmentBuilder.BConstant.KEY_USE_TITLE, false)
            titleBar?.visibility = if (useHeader) View.VISIBLE else View.GONE
            if (useHeader) {
                titleBar?.run {
                    useTitleBarAction(this)
                    val title: String = it.getString(ChatUIKitBaseFragmentBuilder.BConstant.KEY_SET_TITLE, "")
                    if (!TextUtils.isEmpty(title)) {
                        setTitle(title)
                    }
                    val subTitle: String = it.getString(ChatUIKitBaseFragmentBuilder.BConstant.KEY_SET_SUB_TITLE, "")
                    if (!TextUtils.isEmpty(subTitle)) {
                        setSubtitle(subTitle)
                    }
                    val canBack: Boolean = it.getBoolean(ChatUIKitBaseFragmentBuilder.BConstant.KEY_ENABLE_BACK, false)
                    setDisplayHomeAsUpEnabled(canBack
                        , it.getBoolean(ChatUIKitBaseFragmentBuilder.BConstant.KEY_USE_TITLE_REPLACE, false))
                }
            }
        }
    }
}