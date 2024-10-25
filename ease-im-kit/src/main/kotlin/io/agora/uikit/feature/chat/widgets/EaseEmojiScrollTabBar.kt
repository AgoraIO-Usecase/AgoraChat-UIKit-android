package io.agora.uikit.feature.chat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import io.agora.uikit.R
import io.agora.uikit.common.extensions.dpToPx
import io.agora.uikit.databinding.EaseWidgetEmojiconTabBarBinding

class EaseEmojiScrollTabBar @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private val binding: EaseWidgetEmojiconTabBarBinding =
        EaseWidgetEmojiconTabBarBinding.inflate(LayoutInflater.from(context), this, true)

    private val tabList: MutableList<ImageView> = ArrayList()
    private var itemClickListener: EaseScrollTabBarItemClickListener? = null


    /**
     * add tab
     * @param icon
     */
    fun addTab(icon: Int) {
        val tabView = View.inflate(context, R.layout.ease_chat_emoji_scroll_tab_item, null)
        val imageView = tabView.findViewById<View>(R.id.iv_icon) as ImageView
        imageView.setImageResource(icon)
        val tabWidth = 60
        val imgParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            tabWidth.dpToPx(context),
            LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = imgParams
        binding.tabContainer.addView(tabView)
        tabList.add(imageView)
        val position = tabList.size - 1
        imageView.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(position)
            }
        }
    }

    /**
     * remove tab
     * @param position
     */
    fun removeTab(position: Int) {
        binding.tabContainer.removeViewAt(position)
        tabList.removeAt(position)
    }

    fun selectedTo(position: Int) {
        scrollTo(position)
        for (i in tabList.indices) {
            if (position == i) {
                tabList[i].setBackgroundColor(ContextCompat.getColor(context, R.color.ease_chat_emoji_tab_selected))
            } else {
                tabList[i].setBackgroundColor(ContextCompat.getColor(context, R.color.ease_chat_emoji_tab_normal))
            }
        }
    }

    private fun scrollTo(position: Int) {
        val childCount: Int = binding.tabContainer.childCount
        if (position < childCount) {
            binding.scrollView.post(Runnable {
                val mScrollX: Int = binding.tabContainer.scrollX
                val childX = ViewCompat.getX(binding.tabContainer.getChildAt(position)).toInt()
                if (childX < mScrollX) {
                    binding.scrollView.scrollTo(childX, 0)
                    return@Runnable
                }
                val childWidth = binding.tabContainer.getChildAt(position).width
                val hsvWidth: Int = binding.scrollView.width
                val childRight = childX + childWidth
                val scrollRight = mScrollX + hsvWidth
                if (childRight > scrollRight) {
                    binding.scrollView.scrollTo(childRight - scrollRight, 0)
                    return@Runnable
                }
            })
        }
    }

    fun setTabBarItemClickListener(itemClickListener: EaseScrollTabBarItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface EaseScrollTabBarItemClickListener {
        fun onItemClick(position: Int)
    }
}