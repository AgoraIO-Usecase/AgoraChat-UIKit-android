package com.hyphenate.easeui.menu.select

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemSelectTextPopBinding
import com.hyphenate.easeui.model.EaseMenuItem

class EaseSelectPopAdapter : EaseBaseRecyclerViewAdapter<EaseMenuItem>(){

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseMenuItem> {
        return SelectMenuViewHolder(EaseItemSelectTextPopBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder<EaseMenuItem>, position: Int) {
        super.onBindViewHolder(holder, position)
    }

    private inner class SelectMenuViewHolder(
        private val viewBinding:EaseItemSelectTextPopBinding
    ) : ViewHolder<EaseMenuItem>(binding = viewBinding) {

        override fun initView(viewBinding: ViewBinding?) {
            super.initView(viewBinding)
        }

        @SuppressLint("RestrictedApi")
        override fun setData(item: EaseMenuItem?, position: Int) {
            item?.let { menu->
                viewBinding.run {
                    if (menu.resourceId != -1) {
                        ivPopIcon.setImageResource(menu.resourceId)
                        ivPopIcon.visibility = View.VISIBLE
                    } else {
                        ivPopIcon.visibility = View.GONE
                    }
                    tvPopFunc.text = menu.title
                    if (menu.titleColor != -1) {
                        tvPopFunc.setTextColor(menu.titleColor)
                        ivPopIcon.supportImageTintList = ColorStateList.valueOf(menu.titleColor)
                    } else {
                        ivPopIcon.supportImageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(mContext!!
                                , R.color.ease_neutral_30))
                    }
                }
            }
        }

    }
}