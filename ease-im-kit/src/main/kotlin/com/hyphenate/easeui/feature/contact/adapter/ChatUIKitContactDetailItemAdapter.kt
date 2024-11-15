package com.hyphenate.easeui.feature.contact.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.ChatUIKitMenuItem


class ChatUIKitContactDetailItemAdapter(
    context: Context,
    resource: Int,
    private val objects: MutableList<ChatUIKitMenuItem>
) : ArrayAdapter<ChatUIKitMenuItem>(context, resource, objects){
    private var listener: OnMenuItemClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_contact_detail_item, null)
        }

        val bubble = convertView!!.findViewById<LinearLayout>(R.id.item_bubble)
        val title = convertView.findViewById<TextView>(R.id.itemTitle)
        val icon = convertView.findViewById<AppCompatImageView>(R.id.itemIcon)
        val item: ChatUIKitMenuItem? = getItem(position)
        if (count == 1 || count == 2 || count == 3){
            val layoutParams = LinearLayout.LayoutParams(114.dpToPx(context), LinearLayout.LayoutParams.WRAP_CONTENT)
            bubble.layoutParams = layoutParams
        }
        item?.let {
            title.text = it.title
            title.setTextColor(it.titleColor)
            icon.setImageResource(it.resourceId)
            if (it.resourceTintColor != -1) {
                icon.imageTintList = ColorStateList.valueOf(it.resourceTintColor)
            }
            if (it.isVisible){
                convertView.visibility = View.VISIBLE
            }else{
                convertView.visibility = View.GONE
            }
        }
        convertView.setOnClickListener{
            listener?.onMenuItemClick(item,position)
        }
        return convertView
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    fun setContactDetailItemClickListener(listener: OnMenuItemClickListener){
        this.listener = listener
    }
}