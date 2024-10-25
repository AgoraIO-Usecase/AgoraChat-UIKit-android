package io.agora.uikit.configs

import android.content.Context
import io.agora.uikit.R
import io.agora.uikit.model.EaseCustomHeaderItem

class EaseCustomMentionItemConfig(
    private val context: Context,
    private val mentionHeaderItemList: MutableList<EaseCustomHeaderItem>? = mutableListOf(
        EaseCustomHeaderItem(
            headerId = R.id.ease_mention_at_all,
            headerIconRes= R.drawable.ease_mention_all,
            headerTitle = context.resources.getString(
                R.string.ease_group_mention_all,
            )
        )
    )
){
    fun getDefaultMentionItemList():MutableList<EaseCustomHeaderItem>?{
        return mentionHeaderItemList
    }
}