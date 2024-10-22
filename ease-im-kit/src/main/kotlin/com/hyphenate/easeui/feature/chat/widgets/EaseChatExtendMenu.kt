package com.hyphenate.easeui.feature.chat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.HorizontalPageLayoutManager
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.databinding.EaseLayoutChatExtendMenuBinding
import com.hyphenate.easeui.feature.chat.adapter.EaseChatExtendMenuAdapter
import com.hyphenate.easeui.feature.chat.adapter.EaseChatExtendMenuIndicatorAdapter
import com.hyphenate.easeui.feature.chat.interfaces.EaseChatExtendMenuItemClickListener
import com.hyphenate.easeui.feature.chat.interfaces.IChatExtendMenu
import com.hyphenate.easeui.common.helper.PagingScrollHelper
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem
import java.util.Collections
import kotlin.math.ceil

/**
 * Extend menu when user want send image, voice clip, etc
 *
 */
class EaseChatExtendMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), PagingScrollHelper.OnPageChangeListener, IChatExtendMenu,
    OnItemClickListener {

    private val binding: EaseLayoutChatExtendMenuBinding by lazy {
        EaseLayoutChatExtendMenuBinding.inflate(LayoutInflater.from(context), this, true) }
    private val itemModels: MutableList<EaseMenuItem> = ArrayList()
    private val itemMap: MutableMap<Int, EaseMenuItem?> = HashMap()
    private val adapter: EaseChatExtendMenuAdapter by lazy { EaseChatExtendMenuAdapter() }
    private var numColumns = 0
    private var numRows = 0
    private var currentPosition = 0
    private val helper: PagingScrollHelper by lazy { PagingScrollHelper() }
    private val indicatorAdapter: EaseChatExtendMenuIndicatorAdapter by lazy { EaseChatExtendMenuIndicatorAdapter() }
    private var itemListener: EaseChatExtendMenuItemClickListener? = null
    private val itemStrings = intArrayOf(
        R.string.ease_attach_take_pic, R.string.ease_attach_picture,
        R.string.ease_attach_video, R.string.ease_attach_file
    )
    private val itemdrawables = intArrayOf(
        R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
        R.drawable.em_chat_video_selector, R.drawable.em_chat_file_selector
    )
    private val itemIds = intArrayOf(
        R.id.extend_item_take_picture,
        R.id.extend_item_picture,
        R.id.extend_item_video,
        R.id.extend_item_file
    )

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatExtendMenu)
        numColumns = ta.getInt(R.styleable.EaseChatExtendMenu_numColumns, 4)
        numRows = ta.getInt(R.styleable.EaseChatExtendMenu_numRows, 2)
        ta.recycle()
    }

    /**
     * init
     */
    fun init() {
        initChatExtendMenu()
        initChatExtendMenuIndicator()
        addDefaultData()
    }

    private fun initChatExtendMenu() {
        val manager = HorizontalPageLayoutManager(numRows, numColumns)
        manager.setItemHeight(110.dpToPx(context))
        binding.rvExtendMenu.layoutManager = manager
        binding.rvExtendMenu.setHasFixedSize(true)
        val concatAdapter = ConcatAdapter()
        concatAdapter.addAdapter(adapter)
        binding.rvExtendMenu.adapter = concatAdapter
        adapter.setData(itemModels)
        helper.setUpRecycleView(binding.rvExtendMenu)
        helper.updateLayoutManger()
        helper.scrollToPosition(0)
        isHorizontalFadingEdgeEnabled = true
        helper.setOnPageChangeListener(this)
        adapter.setOnItemClickListener(this)
    }

    private fun initChatExtendMenuIndicator() {
        binding.rvIndicator.adapter = indicatorAdapter
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        itemDecoration.setDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.ease_chat_extend_menu_indicator_divider
            )!!
        )
        binding.rvIndicator.addItemDecoration(itemDecoration)
        indicatorAdapter.setSelectedPosition(currentPosition)
    }

    private fun addDefaultData() {
        for (i in itemStrings.indices) {
            registerMenuItem(
                itemStrings[i],
                itemdrawables[i],
                itemIds[i],
                titleColor = ContextCompat.getColor(context,R.color.ease_color_wx_style_extend_menu_text_tint),
                resourceTintColor = ContextCompat.getColor(context,R.color.ease_color_wx_style_extend_menu_tint)
            )
        }
    }

    override fun clear() {
        itemModels.clear()
        itemMap.clear()
        adapter.notifyDataSetChanged()
        indicatorAdapter.setPageCount(0)
    }

    override fun setMenuOrder(itemId: Int, order: Int) {
        if (itemMap.containsKey(itemId)) {
            val model = itemMap[itemId]
            if (model != null) {
                model.order = order
                sortByOrder(itemModels)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sortByOrder(itemModels: List<EaseMenuItem>) {
        Collections.sort(itemModels) { o1, o2 ->
            val `val` = o1.order - o2.order
            if (`val` > 0) {
                1
            } else if (`val` == 0) {
                0
            } else {
                -1
            }
        }
    }

    override fun onPageChange(index: Int) {
        currentPosition = index
        indicatorAdapter.setSelectedPosition(index)
    }

    override fun onItemClick(view: View?, position: Int) {
        val itemModel = itemModels[position]
        itemListener?.onChatExtendMenuItemClick(itemModel.menuId, view)
    }

    /**
     * register menu item
     *
     * @param name
     * item name
     * @param drawableRes
     * background of item
     * @param itemId
     * id
     * @param order
     * order by
     */
    override fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int, order: Int,titleColor:Int,resourceTintColor:Int) {
        if (!itemMap.containsKey(itemId)) {
            val item = EaseMenuItem(title = name ?: "", resourceId = drawableRes, menuId = itemId, order = order, titleColor = titleColor, resourceTintColor = resourceTintColor)
            itemMap[itemId] = item
            itemModels.add(item)
            sortByOrder(itemModels)
            adapter.notifyDataSetChanged()
            // Set the number of indicators to be displayed
            indicatorAdapter.setPageCount(
                ceil((itemModels.size * 1.0f / (numColumns * numRows)).toDouble()).toInt()
            )
        }
    }

    /**
     * register menu item
     *
     * @param nameRes
     * resource id of item name
     * @param drawableRes
     * background of item
     * @param itemId
     * id
     * @param order
     * order by
     */
    override fun registerMenuItem(nameRes: Int, drawableRes: Int, itemId: Int, order: Int,titleColor:Int,resourceTintColor:Int) {
        registerMenuItem(context.getString(nameRes), drawableRes, itemId, order,titleColor,resourceTintColor)
    }

    override fun setEaseChatExtendMenuItemClickListener(listener: EaseChatExtendMenuItemClickListener?) {
        itemListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        helper.scrollToPosition(0)
        helper.checkCurrentStatus()
    }
}