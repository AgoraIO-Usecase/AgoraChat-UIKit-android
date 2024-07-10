package com.hyphenate.easeui.feature.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.feature.chat.activities.EaseChatActivity
import com.hyphenate.easeui.base.EaseBaseFragment
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatMultiDeviceListener
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.databinding.FragmentConversationListLayoutBinding
import com.hyphenate.easeui.feature.contact.interfaces.OnLoadConversationListener
import com.hyphenate.easeui.feature.conversation.adapter.EaseConversationListAdapter
import com.hyphenate.easeui.feature.conversation.controllers.EaseConvDialogController
import com.hyphenate.easeui.feature.conversation.interfaces.OnConversationListChangeListener
import com.hyphenate.easeui.feature.conversation.interfaces.UnreadDotPosition
import com.hyphenate.easeui.feature.conversation.interfaces.UnreadStyle
import com.hyphenate.easeui.feature.search.EaseSearchActivity
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.interfaces.EaseContactListener
import com.hyphenate.easeui.interfaces.EaseConversationListener
import com.hyphenate.easeui.interfaces.EaseGroupListener
import com.hyphenate.easeui.interfaces.EaseMultiDeviceListener
import com.hyphenate.easeui.interfaces.OnItemClickListener
import com.hyphenate.easeui.interfaces.OnItemDataClickListener
import com.hyphenate.easeui.interfaces.OnItemLongClickListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseMenuItem
import com.hyphenate.easeui.model.getChatType
import com.hyphenate.easeui.viewmodel.contacts.EaseContactListViewModel

open class EaseConversationListFragment: EaseBaseFragment<FragmentConversationListLayoutBinding>(),
    OnItemClickListener, OnItemLongClickListener, OnMenuItemClickListener,
    Toolbar.OnMenuItemClickListener, OnLoadConversationListener {

    private val dialogController by lazy { EaseConvDialogController(mContext, this) }
    private val contactViewModel by lazy { ViewModelProvider(this)[EaseContactListViewModel::class.java] }

    private var menuItemClickListener: OnMenuItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null
    private var adapter: EaseConversationListAdapter? = null
    private var itemClickListener: OnItemDataClickListener? = null
    private var conversationListChangeListener: OnConversationListChangeListener? = null
    private var backPressListener: View.OnClickListener? = null
    private val groupChangeListener = object : EaseGroupListener() {

        override fun onGroupDestroyed(groupId: String?, groupName: String?) {
            checkDeleteEvent(groupId)
        }
    }
    private val contactListener = object : EaseContactListener() {

        override fun onContactDeleted(username: String?) {
            checkDeleteEvent(username)
        }
    }

    private val multiDeviceListener = object : EaseMultiDeviceListener() {

        override fun onContactEvent(event: Int, target: String?, ext: String?) {
            if (event == ChatMultiDeviceListener.CONTACT_REMOVE) {
                checkDeleteEvent(target)
            }
        }

        override fun onGroupEvent(event: Int, target: String?, usernames: MutableList<String>?) {
            if (event == ChatMultiDeviceListener.GROUP_DESTROY
                || event == ChatMultiDeviceListener.GROUP_LEAVE) {
                checkDeleteEvent(target)
            }
        }

        override fun onConversationEvent(
            event: Int,
            conversationId: String?,
            type: ChatConversationType?
        ) {
            if (event == ChatMultiDeviceListener.CONVERSATION_DELETED) {
                checkDeleteEvent(conversationId)
            }
        }
    }

    private val conversationListener = object : EaseConversationListener() {
        override fun onConversationRead(from: String?, to: String?) {
            refreshData()
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConversationListLayoutBinding? {
        return FragmentConversationListLayoutBinding.inflate(inflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.run {
            binding?.run {
                titleConversations.visibility = if (getBoolean(Constant.KEY_USE_TITLE, false)) View.VISIBLE else View.GONE
                getString(Constant.KEY_SET_TITLE)?.let {
                    if (it.isNotEmpty()) {
                        titleConversations.setTitle(it)
                    }
                }
                titleConversations.setDisplayHomeAsUpEnabled(getBoolean(Constant.KEY_ENABLE_BACK, false)
                    , getBoolean(Constant.KEY_USE_TITLE_REPLACE, false))
                if (getBoolean(Constant.KEY_ENABLE_BACK, false)) {
                    titleConversations.setNavigationOnClickListener {
                        backPressListener?.onClick(it) ?: activity?.finish()
                    }
                }
                searchBar.visibility = if (getBoolean(Constant.KEY_USE_SEARCH, false)) View.VISIBLE else View.GONE
                listConversation.showUnreadDotPosition(UnreadDotPosition.valueOf(getString(Constant.KEY_UNREAD_POSITION, UnreadDotPosition.RIGHT.name)))
                listConversation.setUnreadStyle(UnreadStyle.valueOf(getString(Constant.KEY_UNREAD_STYLE, UnreadStyle.NUM.name)))
                getInt(Constant.KEY_EMPTY_LAYOUT, -1).takeIf { it != -1 }?.let {
                    listConversation.getListAdapter()?.setEmptyView(it)
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        binding?.searchBar?.setOnClickListener {
            mContext.startActivity(EaseSearchActivity.createIntent(mContext, EaseSearchType.CONVERSATION))
        }
        binding?.listConversation?.setOnItemClickListener(this)
        binding?.listConversation?.setOnItemLongClickListener(this)
        binding?.listConversation?.setOnMenuItemClickListener(this)
        binding?.titleConversations?.setOnMenuItemClickListener(this)
        binding?.listConversation?.setLoadConversationListener(this)
        EaseIM.addContactListener(contactListener)
        EaseIM.addConversationListener(conversationListener)
        EaseIM.addGroupChangeListener(groupChangeListener)
        EaseIM.addMultiDeviceListener(multiDeviceListener)
    }

    override fun initData() {
        super.initData()
        binding?.listConversation?.loadData()
        initEventBus()
    }

    private fun initEventBus() {
        // Listener the conversation remove event
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.REMOVE.name).register(viewLifecycleOwner) {
            if (it.isConversationChange) {
                refreshData()
            }
        }
        // Listener the conversation update event
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.UPDATE.name).register(viewLifecycleOwner) {
            if (it.isConversationChange) {
                refreshData()
            }
        }
        // Listener the conversation update event
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE.name).register(viewLifecycleOwner) {
            if (it.isConversationChange) {
                refreshData()
            }
        }
        // Listener the group leave event
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.LEAVE.name).register(viewLifecycleOwner) {
            if (it.isGroupChange) {
                refreshData()
            }
        }
        // Listener the group destroy event
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.DESTROY.name).register(viewLifecycleOwner) {
            if (it.isGroupChange) {
                refreshData()
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE + EaseEvent.TYPE.GROUP).register(this) {
            if (it.isGroupChange && it.event == EaseConstant.EVENT_UPDATE_GROUP_NAME) {
                refreshData()
            }
        }
    }

    fun refreshData() {
        binding?.listConversation?.loadData()
    }

    private fun checkDeleteEvent(conversationId: String?) {
        binding?.listConversation?.getListAdapter()?.mData?.forEach {
            if (it.conversationId == conversationId) {
                refreshData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EaseIM.removeContactListener(contactListener)
        EaseIM.removeConversationListener(conversationListener)
        EaseIM.removeGroupChangeListener(groupChangeListener)
        EaseIM.removeMultiDeviceListener(multiDeviceListener)
    }

    override fun onItemClick(view: View?, position: Int) {
        if (itemClickListener != null) {
            itemClickListener?.onItemClick(binding?.listConversation?.getItem(position), position)
            return
        }
        val conversation = binding?.listConversation?.getItem(position)
        conversation?.let {
            EaseChatActivity.actionStart(mContext, it.conversationId, it.getChatType())
        } ?: kotlin.run {
            ChatLog.e("conversation", "onItemClick: conversation is null.")
        }
    }

    /**
     * For [EaseTitleBar] menu item click.
     */
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId) {
                R.id.action_more -> {
                    dialogController.showMoreDialog { content ->
                        if (!content.isNullOrEmpty()) {
                            contactViewModel.addContact(content)
                        }
                    }
                    return true
                }
                else -> {}
            }
        }
        return false
    }

    override fun onItemLongClick(view: View?, position: Int): Boolean {
        return itemLongClickListener?.onItemLongClick(view, position) ?: false
    }

    override fun loadConversationListSuccess(userList: List<EaseConversation>) {

    }

    override fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean {
        return menuItemClickListener?.onMenuItemClick(item, position) ?: false
    }

    private fun setCustomAdapter(adapter: EaseConversationListAdapter?) {
        this.adapter = adapter
    }

    private fun setItemClickListener(itemClickListener: OnItemDataClickListener?) {
        this.itemClickListener = itemClickListener
    }

    private fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener?) {
        this.itemLongClickListener = itemLongClickListener
    }

    private fun setOnBackPressListener(backPressListener: View.OnClickListener?) {
        this.backPressListener = backPressListener
    }

    private fun setConversationChangeListener(listener: OnConversationListChangeListener?) {
        this.conversationListChangeListener = listener
    }

    private fun setOnMenuItemClickListener(menuItemClickListener: OnMenuItemClickListener?) {
        this.menuItemClickListener = menuItemClickListener
    }

    class Builder {
        private val bundle: Bundle = Bundle()
        private var customFragment: EaseConversationListFragment? = null
        private var itemClickListener: OnItemDataClickListener? = null
        private var adapter: EaseConversationListAdapter? = null
        private var conversationChangeListener: OnConversationListChangeListener? = null
        private var backPressListener: View.OnClickListener? = null
        private var itemLongClickListener: OnItemLongClickListener? = null
        private var menuItemClickListener: OnMenuItemClickListener? = null

        /**
         * Set custom fragment which should extends EaseConversationListFragment
         * @param fragment
         * @param <T>
         * @return
        </T> */
        fun <T : EaseConversationListFragment?> setCustomFragment(fragment: T): Builder {
            customFragment = fragment
            return this
        }

        /**
         * Whether to use default titleBar which is [com.hyphenate.easeui.widget.EaseTitleBar]
         * @param useTitle
         * @return
         */
        fun useTitleBar(useTitle: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_USE_TITLE, useTitle)
            return this
        }

        /**
         * Whether to use default titleBar to replace actionBar when activity is a AppCompatActivity.
         * If set true, will call [androidx.appcompat.app.AppCompatActivity.setSupportActionBar].
         * @param replace
         * @return
         */
        fun useTitleBarToReplaceActionBar(replace: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_USE_TITLE_REPLACE, replace)
            return this
        }

        /**
         * Set titleBar's title
         * @param title
         * @return
         */
        fun setTitleBarTitle(title: String?): Builder {
            bundle.putString(Constant.KEY_SET_TITLE, title)
            return this
        }

        /**
         * Whether show back icon in titleBar
         * @param canBack
         * @return
         */
        fun enableTitleBarPressBack(canBack: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_ENABLE_BACK, canBack)
            return this
        }

        /**
         * If you have set [EaseConversationListFragment.Builder.enableTitleBarPressBack], you can set the listener
         * @param listener
         * @return
         */
        fun setTitleBarBackPressListener(listener: View.OnClickListener?): Builder {
            backPressListener = listener
            return this
        }

        /**
         * Whether to use search bar.
         * @param useSearchBar
         */
        fun useSearchBar(useSearchBar: Boolean): Builder {
            bundle.putBoolean(Constant.KEY_USE_SEARCH, useSearchBar)
            return this
        }

        /**
         * Set conversation item click listener
         * @param itemClickListener
         * @return
         */
        fun setItemClickListener(itemClickListener: OnItemDataClickListener?): Builder {
            this.itemClickListener = itemClickListener
            return this
        }

        /**
         * Set conversation item long click listener
         * @param itemLongClickListener
         */
        fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener?): Builder {
            this.itemLongClickListener = itemLongClickListener
            return this
        }

        /**
         * Set menu item click listener
         */
        fun setOnMenuItemClickListener(menuItemClickListener: OnMenuItemClickListener?) {
            this.menuItemClickListener = menuItemClickListener
        }

        /**
         * Set conversation change listener, such as conversation was been removed
         * @param listener
         * @return
         */
        fun setConversationChangeListener(listener: OnConversationListChangeListener?): Builder {
            conversationChangeListener = listener
            return this
        }

        /**
         * Set unread icon position
         * @param position
         * @return
         */
        fun setUnreadPosition(position: UnreadDotPosition): Builder {
            bundle.putString(Constant.KEY_UNREAD_POSITION, position.name)
            return this
        }

        /**
         * Set unread icon's show style
         * @param style
         * @return
         */
        fun setUnreadStyle(style: UnreadStyle): Builder {
            bundle.putString(Constant.KEY_UNREAD_STYLE, style.name)
            return this
        }

        /**
         * Set chat list's empty layout if you want replace the default
         * @param emptyLayout
         * @return
         */
        fun setEmptyLayout(@LayoutRes emptyLayout: Int): Builder {
            bundle.putInt(Constant.KEY_EMPTY_LAYOUT, emptyLayout)
            return this
        }

        /**
         * Set custom adapter which should extends EaseConversationListAdapter
         * @param adapter
         * @return
         */
        fun setCustomAdapter(adapter: EaseConversationListAdapter?): Builder {
            this.adapter = adapter
            return this
        }

        fun build(): EaseConversationListFragment {
            val fragment =
                if (customFragment != null) customFragment else EaseConversationListFragment()
            fragment!!.arguments = bundle
            fragment.setCustomAdapter(adapter)
            fragment.setItemClickListener(itemClickListener)
            fragment.setOnItemLongClickListener(itemLongClickListener)
            fragment.setOnBackPressListener(backPressListener)
            fragment.setConversationChangeListener(conversationChangeListener)
            return fragment
        }
    }

    private object Constant {
        const val KEY_USE_TITLE = "key_use_title"
        const val KEY_USE_TITLE_REPLACE = "key_use_replace_action_bar"
        const val KEY_SET_TITLE = "key_set_title"
        const val KEY_USE_SEARCH = "key_use_search"
        const val KEY_UNREAD_POSITION = "key_unread_position"
        const val KEY_UNREAD_STYLE = "key_unread_style"
        const val KEY_EMPTY_LAYOUT = "key_empty_layout"
        const val KEY_ENABLE_BACK = "key_enable_back"
    }

}