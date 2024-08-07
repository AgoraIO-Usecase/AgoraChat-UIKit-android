package com.hyphenate.easeui.feature.conversation.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatRecallMessageInfo
import com.hyphenate.easeui.common.RefreshHeader
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.lifecycleScope
import com.hyphenate.easeui.common.impl.OnItemClickListenerImpl
import com.hyphenate.easeui.common.impl.OnItemLongClickListenerImpl
import com.hyphenate.easeui.common.impl.OnMenuItemClickListenerImpl
import com.hyphenate.easeui.databinding.EaseConversationListBinding
import com.hyphenate.easeui.feature.conversation.interfaces.OnLoadConversationListener
import com.hyphenate.easeui.feature.conversation.adapter.EaseConversationListAdapter
import com.hyphenate.easeui.feature.conversation.config.EaseConvItemConfig
import com.hyphenate.easeui.feature.conversation.interfaces.IConvItemStyle
import com.hyphenate.easeui.feature.conversation.interfaces.IConversationListLayout
import com.hyphenate.easeui.feature.conversation.interfaces.IEaseConvListResultView
import com.hyphenate.easeui.feature.conversation.interfaces.OnConversationListChangeListener
import com.hyphenate.easeui.feature.conversation.interfaces.UnreadDotPosition
import com.hyphenate.easeui.feature.conversation.interfaces.UnreadStyle
import com.hyphenate.easeui.feature.conversation.interfaces.IConvMenu
import com.hyphenate.easeui.interfaces.EaseMessageListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.interfaces.OnItemLongClickListener
import com.hyphenate.easeui.interfaces.OnMenuDismissListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.interfaces.OnMenuPreShowListener
import com.hyphenate.easeui.menu.EaseMenuHelper
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.chatConversation
import com.hyphenate.easeui.viewmodel.conversations.EaseConversationListViewModel
import com.hyphenate.easeui.viewmodel.conversations.IConversationListRequest
import com.hyphenate.easeui.widget.EaseImageView

class EaseConversationListLayout @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), IConversationListLayout, 
    IConvItemStyle, IEaseConvListResultView, IConvMenu {

    private val binding: EaseConversationListBinding by lazy {
        EaseConversationListBinding.inflate(LayoutInflater.from(context), this, true)
    }
    /**
     * Conversation item configuration
     */
    private lateinit var itemConfig: EaseConvItemConfig

    private val menuHelper: EaseMenuHelper by lazy { EaseMenuHelper() }

    /**
     * Item click listener set by user.
     */
    private var itemClickListener: OnItemClickListener? = null

    /**
     * Item long click listener set by user.
     */
    private var itemLongClickListener: OnItemLongClickListener? = null

    /**
     * Menu pre show listener.
     */
    private var menuPreShowListener: OnMenuPreShowListener? = null

    /**
     * Menu item click listener.
     */
    private var itemMenuClickListener: OnMenuItemClickListener? = null

    /**
     * Conversation list change listener.
     */
    private var conversationChangeListener: OnConversationListChangeListener? = null

    private var listViewModel: IConversationListRequest? = null

    /**
     * Conversation list load listener.
     */
    private var conversationLoadListener: OnLoadConversationListener? = null

    val conversationList: RecyclerView get() = binding.rvList

    /**
     * Concat adapter
     */
    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()
        ConcatAdapter(config)
    }

    /**
     * Conversation list adapter
     */
    private var listAdapter: EaseConversationListAdapter? = null

    private val chatMessageListener = object : EaseMessageListener() {
        override fun onMessageReceived(messages: MutableList<ChatMessage>?) {
            listViewModel?.loadData()
        }

        override fun onMessageRecalledWithExt(recallMessageInfo: MutableList<ChatRecallMessageInfo>?) {
            super.onMessageRecalledWithExt(recallMessageInfo)
            listViewModel?.loadData()
        }
    }
    
    init {
        initAttrs(context, attrs)
        initViews()
        initListener()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        itemConfig = EaseConvItemConfig(context, attrs)
    }

    private fun initViews() {
        binding.rvList.layoutManager = LinearLayoutManager(context)
        listAdapter = EaseConversationListAdapter(itemConfig)
        listAdapter?.setHasStableIds(true)
        concatAdapter.addAdapter(listAdapter!!)
        binding.rvList.adapter = concatAdapter

        // Set refresh layout
        // Can not load more
        binding.refreshLayout.setEnableLoadMore(false)

        if (binding.refreshLayout.refreshHeader == null) {
            binding.refreshLayout.setRefreshHeader(RefreshHeader(context))
        }

        // init view model
        listViewModel = ViewModelProvider(context as AppCompatActivity)[EaseConversationListViewModel::class.java]
        listViewModel?.attachView(this)
    }

    private fun initListener() {
        binding.refreshLayout.setOnRefreshListener {
            listViewModel?.loadData()
        }

        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // When scroll to bottom, load more data
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val visibleList = listAdapter?.mData?.filterIndexed { index, _ ->
                        index in firstVisibleItemPosition..lastVisibleItemPosition
                    }
                    if (!visibleList.isNullOrEmpty()) {
                        listViewModel?.fetchConvGroupInfo(visibleList)
                        listViewModel?.fetchConvUserInfo(visibleList)
                    }
                }
            }
        })

        listAdapter?.setOnItemClickListener(OnItemClickListenerImpl {
            view, position ->
            itemClickListener?.onItemClick(view, position)
        })

        listAdapter?.setOnItemLongClickListener(OnItemLongClickListenerImpl {
            view, position ->
            if (itemLongClickListener != null && itemLongClickListener?.onItemLongClick(view, position) == true) {
                return@OnItemLongClickListenerImpl true
            }
            showDefaultMenu(view, position)
            return@OnItemLongClickListenerImpl true
        })

        EaseIM.addChatMessageListener(chatMessageListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        menuHelper.setOnMenuDismissListener(null)
        menuHelper.dismiss()
        menuHelper.clear()
        EaseIM.removeChatMessageListener(chatMessageListener)
    }
    private fun showDefaultMenu(view: View?, position: Int) {
        val conv = listAdapter?.getItem(position)
        conv?.setSelected(true)
        menuHelper.initMenu(view)
        menuHelper.clear()

        menuHelper.addItemMenu(R.id.ease_action_conv_menu_silent, 0, context.getString(R.string.ease_conv_menu_item_silent))
        menuHelper.addItemMenu(R.id.ease_action_conv_menu_unsilent, 1, context.getString(R.string.ease_conv_menu_item_unsilent))
        menuHelper.addItemMenu(R.id.ease_action_conv_menu_pin, 2, context.getString(R.string.ease_conv_menu_item_pin))
        menuHelper.addItemMenu(R.id.ease_action_conv_menu_unpin, 3, context.getString(R.string.ease_conv_menu_item_unpin))
        menuHelper.addItemMenu(R.id.ease_action_conv_menu_read, 4, context.getString(R.string.ease_conv_menu_item_read))
        menuHelper.addItemMenu(R.id.ease_action_conv_menu_delete, 5, context.getString(R.string.ease_conv_menu_item_delete),
            titleColor = ContextCompat.getColor(context, R.color.ease_color_error))

        menuHelper.findItemVisible(R.id.ease_action_conv_menu_read, false)
        conv?.run {
            chatConversation()?.let {
                if (it.unreadMsgCount > 0) {
                    menuHelper.findItemVisible(R.id.ease_action_conv_menu_read, true)
                }
            }
            if (isSilent()) {
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_silent, false)
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_unsilent, true)
            } else {
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_silent, true)
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_unsilent, false)
            }
            if (isPinned) {
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_pin, false)
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_unpin, true)
            } else {
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_pin, true)
                menuHelper.findItemVisible(R.id.ease_action_conv_menu_unpin, false)
            }
        }

        menuPreShowListener?.let {
            it.onMenuPreShow(menuHelper, position)
        }

        menuHelper.setOnMenuItemClickListener(OnMenuItemClickListenerImpl {
            item, _ ->
            itemMenuClickListener?.let {
                if (it.onMenuItemClick(item, position)) {
                    return@OnMenuItemClickListenerImpl true
                }
            }
            item?.run {
                when (menuId) {
                    R.id.ease_action_conv_menu_silent -> {
                        conv?.let {
                            listViewModel?.makeSilentForConversation(position, conv)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    R.id.ease_action_conv_menu_unsilent -> {
                        conv?.let {
                            listViewModel?.cancelSilentForConversation(position, conv)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    R.id.ease_action_conv_menu_pin -> {
                        conv?.let {
                            listViewModel?.pinConversation(position, it)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    R.id.ease_action_conv_menu_unpin -> {
                        conv?.let {
                            listViewModel?.unpinConversation(position, it)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    R.id.ease_action_conv_menu_read -> {
                        conv?.let {
                            listViewModel?.makeConversionRead(position, it)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    R.id.ease_action_conv_menu_delete -> {
                        conv?.let {
                            listViewModel?.deleteConversation(position, it)
                            return@OnMenuItemClickListenerImpl true
                        }
                    }
                    else -> {
                        return@OnMenuItemClickListenerImpl false
                    }
                }
            }

            return@OnMenuItemClickListenerImpl false
        })

        menuHelper.setOnMenuDismissListener(object : OnMenuDismissListener {
            override fun onDismiss() {
                conv?.setSelected(false)
            }
        })

        menuHelper.show()
    }

    fun loadData() {
        listViewModel?.loadData()
    }

    fun fetchConvUserInfo(visibleList:List<EaseConversation>){
        listViewModel?.fetchConvUserInfo(visibleList)
    }

    override fun setLoadConversationListener(listener: OnLoadConversationListener) {
       this.conversationLoadListener = listener
    }

    /**
     * Notify data changed
     */
    override fun notifyDataSetChanged() {
        listAdapter?.setConversationItemConfig(itemConfig)
    }

    override fun setItemBackGround(backGround: Drawable?) {
        
    }

    override fun setItemHeight(height: Int) {
        itemConfig.itemHeight = height.toFloat()
        notifyDataSetChanged()
    }

    override fun showUnreadDotPosition(position: UnreadDotPosition) {
        itemConfig.unreadDotPosition = position
        notifyDataSetChanged()
    }

    override fun setUnreadStyle(style: UnreadStyle) {
        itemConfig.unreadStyle = style
        notifyDataSetChanged()
    }

    override fun setAvatarSize(avatarSize: Float) {
        itemConfig.avatarSize = avatarSize.toInt()
        notifyDataSetChanged()
    }

    override fun setAvatarShapeType(shapeType: EaseImageView.ShapeType) {
        itemConfig.avatarConfig.avatarShape = shapeType
        notifyDataSetChanged()
    }

    override fun setAvatarRadius(radius: Int) {
        itemConfig.avatarConfig.avatarRadius = radius
        notifyDataSetChanged()
    }

    override fun setAvatarBorderWidth(borderWidth: Int) {
        itemConfig.avatarConfig.avatarBorderWidth = borderWidth
        notifyDataSetChanged()
    }

    override fun setAvatarBorderColor(borderColor: Int) {
        itemConfig.avatarConfig.avatarBorderColor = borderColor
        notifyDataSetChanged()
    }

    override fun setNameTextSize(textSize: Int) {
        itemConfig.itemNameTextSize = textSize
        notifyDataSetChanged()
    }

    override fun setNameTextColor(textColor: Int) {
        itemConfig.itemNameTextColor = textColor
        notifyDataSetChanged()
    }

    override fun setMessageTextSize(textSize: Int) {
        itemConfig.itemMessageTextSize = textSize
        notifyDataSetChanged()
    }

    override fun setMessageTextColor(textColor: Int) {
        itemConfig.itemMessageTextColor = textColor
        notifyDataSetChanged()
    }

    override fun setDateTextSize(textSize: Int) {
        itemConfig.itemDateTextSize = textSize
        notifyDataSetChanged()
    }

    override fun setDateTextColor(textColor: Int) {
        itemConfig.itemDateTextColor = textColor
        notifyDataSetChanged()
    }

    override fun setViewModel(viewModel: IConversationListRequest?) {
        this.listViewModel = viewModel
        this.listViewModel?.attachView(this)
    }

    override fun setListAdapter(adapter: EaseConversationListAdapter?) {
        adapter?.run {
            setHasStableIds(true)
            listAdapter?.let {
                if (concatAdapter.adapters.contains(it)) {
                    val index = concatAdapter.adapters.indexOf(it)
                    concatAdapter.removeAdapter(it)
                    concatAdapter.addAdapter(index, adapter)
                } else {
                    concatAdapter.addAdapter(adapter)
                }
            } ?: concatAdapter.addAdapter(adapter)
            listAdapter = this
            listAdapter!!.setConversationItemConfig(itemConfig)
        }
    }

    override fun getListAdapter(): EaseConversationListAdapter? {
        return listAdapter
    }

    override fun getItem(position: Int): EaseConversation? {
        return listAdapter?.getItem(position)
    }

    override fun makeConversionRead(position: Int, info: EaseConversation?) {
        listViewModel?.makeConversionRead(position, info!!)
    }

    override fun makeConversationTop(position: Int, info: EaseConversation?) {
        listViewModel?.pinConversation(position, info!!)
    }

    override fun cancelConversationTop(position: Int, info: EaseConversation?) {
        listViewModel?.unpinConversation(position, info!!)
    }

    override fun deleteConversation(position: Int, info: EaseConversation?) {
        listViewModel?.deleteConversation(position, info!!)
    }

    override fun setOnConversationChangeListener(listener: OnConversationListChangeListener?) {
        conversationChangeListener = listener
    }

    override fun addHeaderAdapter(adapter: RecyclerView.Adapter<*>?) {
        concatAdapter.addAdapter(0, adapter!!)
    }

    override fun addFooterAdapter(adapter: RecyclerView.Adapter<*>?) {
        concatAdapter.addAdapter(adapter!!)
    }

    override fun removeAdapter(adapter: RecyclerView.Adapter<*>?) {
        concatAdapter.removeAdapter(adapter!!)
    }

    override fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        binding.rvList.addItemDecoration(decor)
    }

    override fun removeItemDecoration(decor: RecyclerView.ItemDecoration) {
        binding.rvList.removeItemDecoration(decor)
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.itemClickListener = listener
    }

    override fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.itemLongClickListener = listener
    }

    override fun loadConversationListSuccess(list: List<EaseConversation>) {
        binding.refreshLayout.finishRefresh()
        listAdapter?.setData(list.toMutableList())
        conversationChangeListener?.notifyAllChange()
        conversationLoadListener?.loadConversationListSuccess(list)
        // Notify to load conversation successfully
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.ADD + EaseEvent.TYPE.CONVERSATION)
            .post(lifecycleScope, EaseEvent(EaseEvent.EVENT.ADD + EaseEvent.TYPE.CONVERSATION, EaseEvent.TYPE.CONVERSATION))

    }

    override fun loadConversationListFail(code: Int, error: String) {
        binding.refreshLayout.finishRefresh()
        conversationLoadListener?.loadConversationListFail(code,error)
    }

    override fun sortConversationListFinish(conversations: List<EaseConversation>) {
        listAdapter?.setData(conversations.toMutableList())
        conversationLoadListener?.loadConversationListSuccess(conversations)
    }

    override fun makeConversionReadSuccess(position: Int, conversation: EaseConversation) {
        listViewModel?.loadData()
        conversationChangeListener?.notifyItemChange(position, conversation.conversationId)
    }

    override fun pinConversationSuccess(position: Int, conversation: EaseConversation) {
        listViewModel?.loadData()
        conversationChangeListener?.notifyAllChange()
    }

    override fun pinConversationFail(conversation: EaseConversation, code: Int, error: String) {

    }

    override fun unpinConversationSuccess(position: Int, conversation: EaseConversation) {
        listViewModel?.loadData()
        conversationChangeListener?.notifyAllChange()
    }

    override fun unpinConversationFail(conversation: EaseConversation, code: Int, error: String) {

    }

    override fun deleteConversationSuccess(position: Int, conversation: EaseConversation) {
        conversationChangeListener?.notifyItemRemove(position, conversation.conversationId)
        listViewModel?.loadData()
    }

    override fun deleteConversationFail(conversation: EaseConversation, code: Int, error: String) {

    }

    override fun makeSilentForConversationSuccess(position: Int, conversation: EaseConversation) {
        conversationChangeListener?.notifyItemRemove(position, conversation.conversationId)
        listAdapter?.notifyItemChanged(position)
    }

    override fun makeSilentForConversationFail(
        conversation: EaseConversation,
        errorCode: Int,
        description: String?
    ) {

    }

    override fun cancelSilentForConversationSuccess(position: Int, conversation: EaseConversation) {
        conversationChangeListener?.notifyItemRemove(position, conversation.conversationId)
        listAdapter?.notifyItemChanged(position)
    }

    override fun cancelSilentForConversationFail(
        conversation: EaseConversation,
        errorCode: Int,
        description: String?
    ) {

    }

    override fun fetchConversationInfoByUserSuccess(profiles: List<EaseProfile>?) {
        if (!profiles.isNullOrEmpty()) notifyDataSetChanged()
    }

    override fun clearMenu() {
        menuHelper.clear()
    }

    override fun addItemMenu(itemId: Int, order: Int, title: String, groupId: Int) {
        menuHelper.addItemMenu(itemId, order, title, groupId)
    }

    override fun findItemVisible(id: Int, visible: Boolean) {
        menuHelper.findItemVisible(id, visible)
    }

    override fun setOnMenuPreShowListener(preShowListener: OnMenuPreShowListener?) {
        menuPreShowListener = preShowListener
    }

    override fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        itemMenuClickListener = listener
    }

    override fun getConvMenuHelper(): EaseMenuHelper {
        return menuHelper
    }

}