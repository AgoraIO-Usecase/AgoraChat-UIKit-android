package com.hyphenate.easeui.configs

import android.content.Context
import com.hyphenate.easeui.R
import com.hyphenate.easeui.model.ChatUIKitCustomHeaderItem

class ChatUIKitCustomMentionItemConfig(
    private val context: Context,
    private val mentionHeaderItemList: MutableList<ChatUIKitCustomHeaderItem>? = mutableListOf(
        ChatUIKitCustomHeaderItem(
            headerId = R.id.ease_mention_at_all,
            headerIconRes= R.drawable.uikit_mention_all,
            headerTitle = context.resources.getString(
                R.string.uikit_group_mention_all,
            )
        )
    )
){
    fun getDefaultMentionItemList():MutableList<ChatUIKitCustomHeaderItem>?{
        return mentionHeaderItemList
    }
}