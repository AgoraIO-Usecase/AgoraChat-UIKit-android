package io.agora.uikit.feature.chat.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.agora.uikit.R
import io.agora.uikit.databinding.EaseWidgetChatEmojiconBinding
import io.agora.uikit.feature.chat.interfaces.EaseEmojiconMenuListener
import io.agora.uikit.feature.chat.interfaces.IChatEmojiconMenu
import io.agora.uikit.feature.chat.widgets.EaseEmojiconPagerView.EaseEmojiconPagerViewListener
import io.agora.uikit.model.EaseDefaultEmojiIconData
import io.agora.uikit.model.EaseEmojicon
import io.agora.uikit.model.EaseEmojiconGroupEntity

class EaseEmojiconMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IChatEmojiconMenu {

    private val binding: EaseWidgetChatEmojiconBinding by lazy {
        EaseWidgetChatEmojiconBinding.inflate(LayoutInflater.from(context), this, true) }
    private var emojiconColumns = 0
    private var bigEmojiconColumns = 0
    private val emojiconGroupList: MutableList<EaseEmojiconGroupEntity> = ArrayList()
    private var listener: EaseEmojiconMenuListener? = null

    init {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EaseEmojiconMenu)
        emojiconColumns = ta.getInt(R.styleable.EaseEmojiconMenu_emojiconColumns, defaultColumns)
        bigEmojiconColumns =
            ta.getInt(R.styleable.EaseEmojiconMenu_bigEmojiconRows, defaultBigColumns)
        ta.recycle()
    }

    @JvmOverloads
    fun init(groupEntities: MutableList<EaseEmojiconGroupEntity>? = null) {
        val entities = mutableListOf<EaseEmojiconGroupEntity>()
        if (groupEntities == null || groupEntities.size == 0) {
            entities.add(
                EaseEmojiconGroupEntity(
                    R.drawable.emoji_1,
                    EaseDefaultEmojiIconData.data.toList()
                )
            )
        } else {
            entities.addAll(groupEntities)
        }
        entities.forEach { groupEntity ->
            emojiconGroupList.add(groupEntity)
            binding.tabBar.addTab(groupEntity.icon)
        }
        binding.pagerView.setPagerViewListener(EmojiconPagerViewListener())
        binding.pagerView.init(emojiconGroupList, emojiconColumns, bigEmojiconColumns)
        binding.tabBar.setTabBarItemClickListener(object : EaseEmojiScrollTabBar.EaseScrollTabBarItemClickListener {
            override fun onItemClick(position: Int) {
                binding.pagerView.setGroupPosition(position)
            }
        })
    }

    /**
     * add emojicon group
     * @param groupEntity
     */
    override fun addEmojiconGroup(groupEntity: EaseEmojiconGroupEntity) {
        emojiconGroupList.add(groupEntity)
        binding.pagerView.addEmojiconGroup(groupEntity, true)
        binding.tabBar.addTab(groupEntity.icon)
    }

    /**
     * add emojicon group list
     * @param groupEntitieList
     */
    override fun addEmojiconGroup(groupEntitieList: List<EaseEmojiconGroupEntity>?) {
        if (groupEntitieList.isNullOrEmpty()) {
            return
        }
        val list = arrayListOf<EaseEmojiconGroupEntity>()
        list.addAll(groupEntitieList)
        list.forEachIndexed { i, groupEntity ->
            emojiconGroupList.add(groupEntity)
            binding.pagerView.addEmojiconGroup(
                groupEntity,
                i == groupEntitieList.size - 1
            )
            binding.tabBar.addTab(groupEntity.icon)
        }
    }

    /**
     * remove emojicon group
     * @param position
     */
    override fun removeEmojiconGroup(position: Int) {
        emojiconGroupList.removeAt(position)
        binding.pagerView.removeEmojiconGroup(position)
        binding.tabBar.removeTab(position)
    }

    override fun setTabBarVisibility(isVisible: Boolean) {
        if (!isVisible) {
            binding.tabBar.visibility = GONE
        } else {
            binding.tabBar.visibility = VISIBLE
        }
    }

    override fun setEmojiconMenuListener(listener: EaseEmojiconMenuListener?) {
        this.listener = listener
    }

    private inner class EmojiconPagerViewListener : EaseEmojiconPagerViewListener {
        override fun onPagerViewInited(groupMaxPageSize: Int, firstGroupPageSize: Int) {
            binding.indicatorView.init(groupMaxPageSize)
            binding.indicatorView.updateIndicator(firstGroupPageSize)
            binding.tabBar.selectedTo(0)
        }

        override fun onGroupPositionChanged(groupPosition: Int, pagerSizeOfGroup: Int) {
            binding.indicatorView.updateIndicator(pagerSizeOfGroup)
            binding.tabBar.selectedTo(groupPosition)
        }

        override fun onGroupInnerPagePostionChanged(oldPosition: Int, newPosition: Int) {
            binding.indicatorView.selectTo(oldPosition, newPosition)
        }

        override fun onGroupPagePostionChangedTo(position: Int) {
            binding.indicatorView.selectTo(position)
        }

        override fun onGroupMaxPageSizeChanged(maxCount: Int) {
            binding.indicatorView.updateIndicator(maxCount)
        }

        override fun onDeleteImageClicked() {
            listener?.onDeleteImageClicked()
        }

        override fun onExpressionClicked(emojicon: EaseEmojicon?) {
            listener?.onExpressionClicked(emojicon)
        }

        override fun onSendIconClicked() {
            listener?.onSendIconClicked()
        }
    }

    companion object {
        private const val defaultColumns = 7
        private const val defaultBigColumns = 4
    }
}