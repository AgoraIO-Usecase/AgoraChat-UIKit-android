package com.hyphenate.easeui.feature.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter.ViewHolder
import com.hyphenate.easeui.databinding.UikitRowChatEmojiBigExpressionBinding
import com.hyphenate.easeui.databinding.UikitRowChatEmojiExpressionBinding
import com.hyphenate.easeui.common.helper.ChatUIKitEmojiHelper
import com.hyphenate.easeui.model.ChatUIKitEmojicon
import com.hyphenate.easeui.model.ChatUIKitEmojicon.Type

class ChatUIKitEmojiGridAdapter: ChatUIKitBaseRecyclerViewAdapter<ChatUIKitEmojicon>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        getItem(position)?.run {
            return type.ordinal
        }
        return super.getItemNotEmptyViewType(position)
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatUIKitEmojicon> {
        return UIKitChatEmojiViewHolderFactory.createViewHolder(parent, ChatUIKitEmojicon.Type.values()[viewType])
    }

}

open class UIKitChatEmojiGridViewHolder(itemView: View): ViewHolder<ChatUIKitEmojicon>(itemView) {
    private var imageView: ImageView? = null
    private var textView: TextView? = null
    override fun initView(itemView: View?) {
        super.initView(itemView)
        imageView = findViewById<ImageView>(R.id.iv_expression)
        textView = findViewById<TextView>(R.id.tv_name)
    }
    override fun setData(item: ChatUIKitEmojicon?, position: Int) {
        item?.run {
            itemView.isEnabled = enableClick
            if (!enableClick) {
                imageView?.setImageDrawable(null)
                return
            }
            if (name != null) {
                textView?.text = name
            }
            if (ChatUIKitEmojiHelper.DELETE_KEY == emojiText) {
                imageView?.setImageResource(R.drawable.uikit_delete_expression)
            } else {
                if (icon != 0) {
                    imageView?.setImageResource(icon)
                } else if (iconPath != null) {
                    imageView?.load(iconPath) {
                        placeholder(R.drawable.uikit_default_expression)
                    }
                } else {
                    imageView?.setImageDrawable(null)
                }
            }
        }
    }

}

object UIKitChatEmojiViewHolderFactory {
    fun createViewHolder(parent: ViewGroup, viewType: Type): ViewHolder<ChatUIKitEmojicon> {
        return when (viewType) {
            Type.BIG_EXPRESSION -> {
                UIKitChatEmojiGridViewHolder(UikitRowChatEmojiBigExpressionBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false).root)
            }
            else -> {
                UIKitChatEmojiGridViewHolder(UikitRowChatEmojiExpressionBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false).root)
            }
        }
    }
}