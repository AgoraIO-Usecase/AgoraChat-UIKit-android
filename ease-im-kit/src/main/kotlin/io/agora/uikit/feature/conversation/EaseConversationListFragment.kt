package io.agora.uikit.feature.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModelProvider
import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.base.EaseBaseFragment
import io.agora.uikit.common.ChatConversationType
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMultiDeviceListener
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.bus.EaseFlowBus
import io.agora.uikit.databinding.FragmentConversationListLayoutBinding
import io.agora.uikit.feature.contact.interfaces.IEaseContactResultView
import io.agora.uikit.feature.conversation.interfaces.OnLoadConversationListener
import io.agora.uikit.feature.conversation.adapter.EaseConversationListAdapter
import io.agora.uikit.feature.conversation.controllers.EaseConvDialogController
import io.agora.uikit.feature.conversation.interfaces.OnConversationListChangeListener
import io.agora.uikit.feature.conversation.interfaces.UnreadDotPosition
import io.agora.uikit.feature.conversation.interfaces.UnreadStyle
import io.agora.uikit.feature.search.EaseSearchActivity
import io.agora.uikit.feature.search.EaseSearchType
import io.agora.uikit.interfaces.EaseContactListener
import io.agora.uikit.interfaces.EaseConversationListener
import io.agora.uikit.interfaces.EaseGroupListener
import io.agora.uikit.interfaces.EaseMultiDeviceListener
import io.agora.uikit.interfaces.OnItemClickListener
import io.agora.uikit.interfaces.OnItemDataClickListener
import io.agora.uikit.interfaces.OnItemLongClickListener
import io.agora.uikit.interfaces.OnMenuItemClickListener
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.model.EaseEvent
import io.agora.uikit.model.EaseMenuItem
import io.agora.uikit.model.getChatType
import io.agora.uikit.viewmodel.contacts.EaseContactListViewModel

open class EaseConversationListFragment: EaseBaseFragment<FragmentConversationListLayoutBinding>(),
    OnItemClickListener, OnItemLongClickListener, OnMenuItemClickListener,
    OnLoadConversationListener,IEaseContactResultView {

    val dialogController by lazy { EaseConvDialogController(mContext, this) }
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

        override fun onUserRemoved(groupId: String?, groupName: String?) {
            binding?.listConversation?.getListAdapter()?.mData?.map {
                if (it.conversationId == groupId){
                    refreshData()
                }
            }
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
        contactViewModel.attachView(this)
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

                defaultMenu()
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
        binding?.listConversation?.setLoadConversationListener(this)
        setMenuItemClickListener()
        EaseIM.addContactListener(contactListener)
        EaseIM.addConversationListener(conversationListener)
        EaseIM.addGroupChangeListener(groupChangeListener)
        EaseIM.addMultiDeviceListener(multiDeviceListener)
    }

    override fun initData() {
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
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.REMOVE + EaseEvent.TYPE.GROUP + EaseEvent.TYPE.CONTACT).register(this) {
            if (it.isGroupChange && it.event == EaseConstant.EVENT_REMOVE_GROUP_MEMBER) {
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

    open fun defaultMenu(){
        binding?.titleConversations?.inflateMenu(R.menu.menu_conversation_actions)
    }

    open fun defaultActionMoreDialog(){
        dialogController.showMoreDialog { content ->
            if (content.isNotEmpty()) {
                contactViewModel.addContact(content)
            }
        }
    }

    private fun setMenuItemClickListener() {
        binding?.titleConversations?.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener setMenuItemClick(it)
        }
    }

    open fun setMenuItemClick(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_more -> {
                defaultActionMoreDialog()
                return true
            }
            else -> return false
        }
    }

    override fun onDestroyView() {
        EaseIM.removeContactListener(contactListener)
        EaseIM.removeConversationListener(conversationListener)
        EaseIM.removeGroupChangeListener(groupChangeListener)
        EaseIM.removeMultiDeviceListener(multiDeviceListener)
        super.onDestroyView()
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

    override fun addContactFail(code: Int, error: String) {
        ChatLog.e("EaseConversationListFragment","addContactFail $code $error")
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
         * Whether to use default titleBar which is [io.agora.uikit.widget.EaseTitleBar]
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