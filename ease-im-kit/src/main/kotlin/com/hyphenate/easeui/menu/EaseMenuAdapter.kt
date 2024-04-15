package com.hyphenate.easeui.menu

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.model.EaseMenuItem

class EaseMenuAdapter: EaseBaseRecyclerViewAdapter<EaseMenuItem>() {
    private var orientation: EaseMenuItemView.MenuOrientation? = null
    private var gravity: EaseMenuItemView.MenuGravity? = null
    private var hideDivider: Boolean = false

    override fun getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<EaseMenuItem> {
        return MenuViewHolder(EaseMenuItemView(parent.context))
    }

    private inner class MenuViewHolder(itemView: View) : ViewHolder<EaseMenuItem>(itemView) {
        private var imageView: AppCompatImageView? = null
        private var textView: TextView? = null

        override fun initView(itemView: View?) {
            super.initView(itemView)
            itemView?.let { it ->
                (it as EaseMenuItemView).let {
                    orientation?.let { orientation ->
                        it.setMenuOrientation(orientation)
                    }
                    gravity?.let { gravity ->
                        it.setMenuGravity(gravity)
                    }
                    it.hideDivider(hideDivider)
                }
                imageView = it.findViewById(R.id.image)
                textView = it.findViewById(R.id.text)
            }
        }

        @SuppressLint("RestrictedApi")
        override fun setData(item: EaseMenuItem?, position: Int) {
            item?.run {
                if (resourceId != -1) {
                    imageView?.setImageResource(resourceId)
                    imageView?.visibility = View.VISIBLE
                } else {
                    imageView?.visibility = View.GONE
                }
                textView?.text = title
                if (titleColor != -1) {
                    textView?.setTextColor(titleColor)
                    imageView?.supportImageTintList = ColorStateList.valueOf(titleColor)
                } else {
                    imageView?.supportImageTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext!!
                        , R.color.ease_chat_extend_menu_item_title_color))
                }
            }
        }
    }

    /**
     * Set the orientation of the menu.
     * After setting the orientation, you need to call [notifyDataSetChanged] to take effect.
     * @param orientation [EaseMenuItemView.MenuOrientation]
     */
    fun setMenuOrientation(orientation: EaseMenuItemView.MenuOrientation) {
        this.orientation = orientation
    }

    /**
     * Set the gravity of the menu.
     * After setting the gravity, you need to call [notifyDataSetChanged] to take effect.
     * @param gravity [EaseMenuItemView.MenuGravity]
     */
    fun setMenuGravity(gravity: EaseMenuItemView.MenuGravity) {
        this.gravity = gravity
    }

    /**
     * Set whether to hide the divider.
     * After setting the divider, you need to call [notifyDataSetChanged] to take effect.
     * @param hideDivider true to hide the divider, false otherwise.
     */
    fun hideDivider(hideDivider: Boolean) {
        this.hideDivider = hideDivider
    }

}