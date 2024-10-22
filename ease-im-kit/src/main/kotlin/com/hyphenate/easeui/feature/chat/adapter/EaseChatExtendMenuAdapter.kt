package com.hyphenate.easeui.feature.chat.adapter

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseChatExtendMenuAdapter
import com.hyphenate.easeui.feature.chat.interfaces.EaseChatExtendMenuItemClickListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem

class EaseChatExtendMenuAdapter(
    private val isHorizontal: Boolean = false
) :
    EaseBaseChatExtendMenuAdapter<EaseChatExtendMenuAdapter.ViewHolder?, EaseMenuItem?>() {
    private var itemListener: OnItemClickListener? = null
    private var chatMenuItemClickListener: EaseChatExtendMenuItemClickListener? = null
    override val itemLayoutId: Int
        protected get() = if (isHorizontal) R.layout.ease_chat_menu_item_horizontal else R.layout.ease_chat_menu_item

    override fun easeCreateViewHolder(view: View?): ViewHolder {
        return ViewHolder(
            view!!
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mData?.let {
            if (position >= it.size) return
            it[position]?.run {
                if (resourceId != -1) holder.imageView.setImageResource(resourceId)
                holder.textView.text = title
                if (titleColor != -1){
                    holder.textView.setTextColor(titleColor)
                }
                if (resourceTintColor != -1) {
                    holder.imageView.imageTintList = ColorStateList.valueOf(resourceTintColor)
                }
                holder.itemView.setOnClickListener { view ->
                    chatMenuItemClickListener?.onChatExtendMenuItemClick(menuId, view)
                    itemListener?.onItemClick(view, position)
                }
            }

        }
    }

    /**
     * Set item click listener.
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemListener = listener
    }

    /**
     * Set chat extend menu item click listener.
     */
    fun setEaseChatExtendMenuItemClickListener(listener: EaseChatExtendMenuItemClickListener?) {
        chatMenuItemClickListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView
        val textView: TextView

        init {
            imageView = itemView.findViewById<View>(R.id.image) as AppCompatImageView
            textView = itemView.findViewById<View>(R.id.text) as TextView
        }
    }
}