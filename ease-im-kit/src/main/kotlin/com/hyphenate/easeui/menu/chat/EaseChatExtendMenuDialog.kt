package com.hyphenate.easeui.menu.chat

import android.content.Context
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.interfaces.EaseChatExtendMenuItemClickListener
import com.hyphenate.easeui.feature.chat.interfaces.IChatExtendMenu
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.menu.EaseMenuDialog
import com.hyphenate.easeui.menu.EaseMenuItemView
import com.hyphenate.easeui.model.EaseMenuItem

class EaseChatExtendMenuDialog(
    private val context: Context
): EaseMenuDialog(), IChatExtendMenu {

    private val itemStrings = intArrayOf(
        R.string.ease_attach_take_pic,
        R.string.ease_attach_picture,
        R.string.ease_attach_video,
        R.string.ease_attach_file,
        R.string.ease_attach_contact_card
    )
    private val itemdrawables = intArrayOf(
        R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
        R.drawable.em_chat_video_selector, R.drawable.em_chat_file_selector,
        R.drawable.em_chat_card_selector
    )
    private val itemIds = intArrayOf(
        R.id.extend_item_take_picture,
        R.id.extend_item_picture,
        R.id.extend_item_video,
        R.id.extend_item_file,
        R.id.extend_item_contact_card
    )
    private var itemClickListener: EaseChatExtendMenuItemClickListener? = null

    fun init() {
        for (i in itemStrings.indices) {
            registerMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], i * 100)
        }

        getMenuAdapter()?.let {
            it.setMenuGravity(EaseMenuItemView.MenuGravity.LEFT)
            it.setMenuOrientation(EaseMenuItemView.MenuOrientation.HORIZONTAL)
            it.notifyDataSetChanged()
        }

        setOnMenuItemClickListener(object : OnMenuItemClickListener {

            override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
                item?.let {
                    itemClickListener?.onChatExtendMenuItemClick(it.menuId, null)
                    return true
                }
                return false
            }
        })
    }
    override fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int, order: Int) {
        registerMenuItem(title = name ?: "", resourceId = drawableRes, menuId = itemId, order = order)
    }

    override fun registerMenuItem(nameRes: Int, drawableRes: Int, itemId: Int, order: Int) {
        registerMenuItem(title = context.getString(nameRes) ?: "", resourceId = drawableRes, menuId = itemId, order = order)
    }

    override fun setEaseChatExtendMenuItemClickListener(listener: EaseChatExtendMenuItemClickListener?) {
        itemClickListener = listener
    }
}