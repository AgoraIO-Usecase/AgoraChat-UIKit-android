package io.agora.uikit.feature.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import coil.load
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter.ViewHolder
import io.agora.uikit.databinding.EaseRowChatEmojiBigExpressionBinding
import io.agora.uikit.databinding.EaseRowChatEmojiExpressionBinding
import io.agora.uikit.common.helper.EaseEmojiHelper
import io.agora.uikit.model.EaseEmojicon
import io.agora.uikit.model.EaseEmojicon.Type

class EaseChatEmojiGridAdapter: EaseBaseRecyclerViewAdapter<EaseEmojicon>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        getItem(position)?.run {
            return type.ordinal
        }
        return super.getItemNotEmptyViewType(position)
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseEmojicon> {
        return EaseChatEmojiViewHolderFactory.createViewHolder(parent, EaseEmojicon.Type.values()[viewType])
    }

}

open class EaseChatEmojiGridViewHolder(itemView: View): ViewHolder<EaseEmojicon>(itemView) {
    private var imageView: ImageView? = null
    private var textView: TextView? = null
    override fun initView(itemView: View?) {
        super.initView(itemView)
        imageView = findViewById<ImageView>(R.id.iv_expression)
        textView = findViewById<TextView>(R.id.tv_name)
    }
    override fun setData(item: EaseEmojicon?, position: Int) {
        item?.run {
            itemView.isEnabled = enableClick
            if (!enableClick) {
                imageView?.setImageDrawable(null)
                return
            }
            if (name != null) {
                textView?.text = name
            }
            if (EaseEmojiHelper.DELETE_KEY == emojiText) {
                imageView?.setImageResource(R.drawable.ease_delete_expression)
            } else {
                if (icon != 0) {
                    imageView?.setImageResource(icon)
                } else if (iconPath != null) {
                    imageView?.load(iconPath) {
                        placeholder(R.drawable.ease_default_expression)
                    }
                } else {
                    imageView?.setImageDrawable(null)
                }
            }
        }
    }

}

object EaseChatEmojiViewHolderFactory {
    fun createViewHolder(parent: ViewGroup, viewType: Type): ViewHolder<EaseEmojicon> {
        return when (viewType) {
            Type.BIG_EXPRESSION -> {
                EaseChatEmojiGridViewHolder(EaseRowChatEmojiBigExpressionBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false).root)
            }
            else -> {
                EaseChatEmojiGridViewHolder(EaseRowChatEmojiExpressionBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false).root)
            }
        }
    }
}