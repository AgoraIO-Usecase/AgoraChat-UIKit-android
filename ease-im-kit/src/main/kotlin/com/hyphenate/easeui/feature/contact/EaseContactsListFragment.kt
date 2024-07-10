package com.hyphenate.easeui.feature.contact

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseFragment
import com.hyphenate.easeui.feature.contact.adapter.EaseContactListAdapter
import com.hyphenate.easeui.common.enums.EaseListViewType
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.configs.EaseHeaderItemConfig
import com.hyphenate.easeui.databinding.FragmentContactListLayoutBinding
import com.hyphenate.easeui.feature.contact.adapter.EaseCustomHeaderAdapter
import com.hyphenate.easeui.feature.contact.interfaces.OnHeaderItemClickListener
import com.hyphenate.easeui.feature.contact.interfaces.OnLoadContactListener
import com.hyphenate.easeui.feature.conversation.controllers.EaseConvDialogController
import com.hyphenate.easeui.feature.group.EaseGroupListActivity
import com.hyphenate.easeui.feature.invitation.EaseNewRequestsActivity
import com.hyphenate.easeui.feature.invitation.helper.EaseNotificationMsgManager
import com.hyphenate.easeui.feature.search.EaseSearchActivity
import com.hyphenate.easeui.feature.search.EaseSearchType
import com.hyphenate.easeui.feature.search.interfaces.OnContactSelectListener
import com.hyphenate.easeui.interfaces.EaseContactListener
import com.hyphenate.easeui.interfaces.OnContactSelectedListener
import com.hyphenate.easeui.interfaces.OnItemLongClickListener
import com.hyphenate.easeui.interfaces.OnUserListItemClickListener
import com.hyphenate.easeui.model.EaseCustomHeaderItem
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.viewmodel.contacts.EaseContactListViewModel
import kotlinx.coroutines.launch

open class EaseContactsListFragment: EaseBaseFragment<FragmentContactListLayoutBinding>(),
    Toolbar.OnMenuItemClickListener, OnItemLongClickListener, OnLoadContactListener {
    private var adapter: EaseContactListAdapter? = null
    private var headerAdapter:EaseCustomHeaderAdapter?=null
    private var headerItemClickListener: OnHeaderItemClickListener? = null
    private var userListItemClickListener: OnUserListItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null
    private var contactSelectedListener:OnContactSelectedListener?=null
    private var backPressListener: View.OnClickListener? = null
    private var viewType: EaseListViewType? = EaseListViewType.LIST_CONTACT
    private var headerList:List<EaseCustomHeaderItem>? = null
    private var searchType: EaseSearchType? = null
    private var selectedMembers:MutableList<String> = mutableListOf()
    private val contactViewModel by lazy { ViewModelProvider(this)[EaseContactListViewModel::class.java] }
    private val dialogController by lazy { EaseConvDialogController(mContext, this) }

    private val returnSearchClickResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> onClickResult(result) }

    private val contactListener = object : EaseContactListener() {
        override fun onContactAdded(username: String?) {
            refreshData()
        }

        override fun onContactDeleted(username: String?) {
            refreshData()
        }

        override fun onContactInvited(username: String?, reason: String?) {
            refreshRequest()
        }

        override fun onFriendRequestAccepted(username: String?) {
            refreshData()
            refreshRequest()
        }

        override fun onFriendRequestDeclined(username: String?) {
            refreshRequest()
        }
    }

    companion object{
        const val TAG:String = "ContactsList"
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactListLayoutBinding {
        return FragmentContactListLayoutBinding.inflate(inflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        arguments?.run {
            binding?.run {
                titleContact.visibility = if (getBoolean(Constant.KEY_USE_TITLE, false)) View.VISIBLE else View.GONE
                getString(Constant.KEY_SET_TITLE)?.let {
                    if (it.isNotEmpty()) {
                        titleContact.setTitle(it)
                    }
                }
                titleContact.setDisplayHomeAsUpEnabled(getBoolean(Constant.KEY_ENABLE_BACK, false)
                    , getBoolean(Constant.KEY_USE_TITLE_REPLACE, false))
                titleContact.setNavigationOnClickListener {
                    backPressListener?.onClick(it) ?: activity?.finish()
                }
                searchBar.visibility = if (getBoolean(Constant.KEY_USE_SEARCH, false)) View.VISIBLE else View.GONE
                if (getBoolean(Constant.KEY_SHOW_ITEM_HEADER, false)) {
                    if (headerList.isNullOrEmpty()) headerList = EaseHeaderItemConfig(mContext).getDefaultHeaderItemModels()
                    if (headerAdapter == null) {
                        headerAdapter = EaseCustomHeaderAdapter()
                    }
                    headerAdapter?.let {
                        it.setHasStableIds(true)
                        listContact.addHeaderAdapter(it)
                        if (headerList?.isNotEmpty() == true){
                            updateRequestCount()
                        }
                    }
                }
                getInt(Constant.KEY_EMPTY_LAYOUT, -1).takeIf { it != -1 }?.let {
                    listContact.getListAdapter()?.setEmptyView(it)
                }
                getString(Constant.KEY_SEARCH_TYPE)?.let {
                    searchType = EaseSearchType.valueOf(it)
                }

                listContact.setListViewType(viewType)
                val isShowSidebar = getBoolean(Constant.KEY_SIDEBAR_VISIBLE,true)
                listContact.setSideBarVisible(isShowSidebar)
            }
        }?:kotlin.run {
            refreshData()
        }
    }

    override fun initListener() {
        super.initListener()
        binding?.searchBar?.setOnClickListener {
            returnSearchClickResult.launch(
                EaseSearchActivity.createIntent(
                    context = mContext,
                    searchType = searchType
                )
            )
        }

        headerAdapter?.setOnHeaderItemClickListener(object : OnHeaderItemClickListener {
            override fun onHeaderItemClick(v: View, itemIndex: Int,itemId:Int?) {
                if (headerItemClickListener != null){
                    headerItemClickListener?.onHeaderItemClick(v,itemIndex,itemId)
                    return
                }
                when(itemId){
                    R.id.ease_contact_header_new_request -> {
                        EaseNotificationMsgManager.getInstance().markAllMessagesAsRead()
                        refreshRequest()
                        startActivity(EaseNewRequestsActivity.createIntent(mContext))
                    }
                    R.id.ease_contact_header_group -> {
                        EaseGroupListActivity.actionStart(mContext)
                    }
                    else -> {}
                }
            }
        })

        binding?.listContact?.getListAdapter()?.setOnUserListItemClickListener(object : OnUserListItemClickListener{
            override fun onUserListItemClick(v: View?, position: Int, user: EaseUser?) {
                if (userListItemClickListener != null){
                    userListItemClickListener?.onUserListItemClick(v, position, user) ?: defaultAction(user)
                    return
                }
                user?.let {
                    startActivity(
                        EaseContactDetailsActivity.createIntent(
                            context = mContext,
                            user = it,
                        )
                    )
                }
            }

            override fun onAvatarClick(v: View, position: Int) {
                if (userListItemClickListener != null){
                    userListItemClickListener?.onAvatarClick(v, position)
                    return
                }
            }
        })

        binding?.listContact?.getListAdapter()?.setCheckBoxSelectListener(object : OnContactSelectListener{
            override fun onContactSelectedChanged(v: View, userId: String, isSelected: Boolean) {
                if (isSelected){
                    if (!selectedMembers.contains(userId)){
                        selectedMembers.add(userId)
                    }
                }else{
                    if (selectedMembers.contains(userId)){
                        selectedMembers.remove(userId)
                    }
                }
                contactSelectedListener?.onContactSelectedChanged(v,selectedMembers)
            }
        })

        binding?.listContact?.setOnItemLongClickListener(this)
        binding?.listContact?.setLoadContactListener(this)
        binding?.titleContact?.getToolBar()?.setOnMenuItemClickListener(this)

        EaseIM.addContactListener(contactListener)
    }

    private fun defaultAction(user: EaseUser?) {
        user?.run {
            if (searchType == null) {
                startActivity(EaseContactDetailsActivity.createIntent(mContext, this))
            }
        }
    }

    override fun initData() {
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.REMOVE.name).register(viewLifecycleOwner) { event ->
            if (event.isContactChange) {
                refreshData()
            }
        }
        EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.ADD.name).register(viewLifecycleOwner) { event ->
            if (event.isContactChange) {
                refreshData()
            }
        }
        EaseFlowBus.with<EaseEvent>(EaseEvent.EVENT.UPDATE.name).register(viewLifecycleOwner) {
            if (it.isNotifyChange) {
                refreshRequest()
            }
        }
    }

    private fun refreshData() {
        binding?.listContact?.loadContactData(false)
    }

    private fun refreshRequest() {
        updateRequestCount()
    }

    private fun onClickResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            result.data?.getSerializableExtra(Constant.KEY_USER)?.let {
                if (it is EaseUser) {
                    userListItemClickListener?.onUserListItemClick(null, -1, it) ?: defaultAction(it)
                }
            }
            result.data?.getStringArrayListExtra(Constant.KEY_SELECT_USER)?.let { selectMembers->
                val adapter = binding?.listContact?.getListAdapter()
                for (selectMember in selectMembers) {
                    if (!selectedMembers.contains(selectMember)){
                        selectedMembers.add(selectMember)
                    }
                }
                adapter?.setSelectedMembers(selectedMembers)
            }
        }
    }

    fun fetchContactInfo(visibleList:List<EaseUser>?){
        binding?.listContact?.fetchContactInfo(visibleList)
    }

    override fun loadContactListSuccess(userList: MutableList<EaseUser>) {

    }

    override fun loadContactListFail(code: Int, error: String) {

    }

    override fun onItemLongClick(view: View?, position: Int): Boolean {
        return itemLongClickListener?.onItemLongClick(view, position) ?: false
    }

    private fun setCustomAdapter(adapter: EaseContactListAdapter?) {
        this.adapter = adapter
    }

    private fun setOnBackPressListener(backPressListener: View.OnClickListener?) {
        this.backPressListener = backPressListener
    }

    private fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener?) {
        this.itemLongClickListener = itemLongClickListener
    }

    private fun setListViewType(viewType: EaseListViewType?){
        this.viewType = viewType
    }

    private fun setHeaderAdapter(headerAdapter:EaseCustomHeaderAdapter?){
        this.headerAdapter = headerAdapter
    }

    private fun setOnHeaderItemClickListener(listener: OnHeaderItemClickListener?){
        this.headerItemClickListener = listener
    }

    private fun setOnUserListItemClickListener(listener:OnUserListItemClickListener?){
        this.userListItemClickListener = listener
    }

    private fun setHeaderItemList(headerList:List<EaseCustomHeaderItem>?){
        this.headerList = headerList
    }

    private fun setOnContactSelectedListener(listener: OnContactSelectedListener?){
        this.contactSelectedListener = listener
    }

    private fun updateRequestCount(){
        mContext.mainScope().launch {
            headerList?.map {
                if (it.headerTitle == getString(R.string.ease_contact_header_request)){
                    val systemConversation = EaseNotificationMsgManager.getInstance().getConversation()
                    systemConversation.let { cv->
                        it.headerUnReadCount = cv.unreadMsgCount
                    }
                }
            }
            headerAdapter?.setData(headerList?.toMutableList())
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId) {
                R.id.action_add_contact -> {
                    dialogController.showAddContactDialog { content ->
                        if (content.isNotEmpty()) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        EaseIM.removeContactListener(contactListener)
    }

    open class Builder {
        private val bundle: Bundle = Bundle()
        protected var customFragment: EaseContactsListFragment? = null
        protected var adapter: EaseContactListAdapter? = null
        protected var headerAdapter:EaseCustomHeaderAdapter? = null
        private var headerItemClickListener: OnHeaderItemClickListener? = null
        private var userListItemClickListener: OnUserListItemClickListener? = null
        private var itemLongClickListener: OnItemLongClickListener? = null
        private var contactSelectedListener:OnContactSelectedListener?=null
        private var backPressListener: View.OnClickListener? = null
        private var viewType: EaseListViewType? = EaseListViewType.LIST_CONTACT
        private var headerList:List<EaseCustomHeaderItem>? = mutableListOf()

        /**
         * Set custom fragment which should extends EaseContactsListFragment
         * @param fragment
         * @param <T>
         * @return
        </T> */
        fun <T : EaseContactsListFragment?> setCustomFragment(fragment: T): Builder {
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
         * If you have set [EaseContactsListFragment.Builder.enableTitleBarPressBack], you can set the listener
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
         * Set the search type for the contact list.
         */
        fun setSearchType(searchType: EaseSearchType): Builder {
            bundle.putString(Constant.KEY_SEARCH_TYPE, searchType.name)
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
        fun setCustomAdapter(adapter: EaseContactListAdapter?): Builder {
            this.adapter = adapter
            return this
        }

        /**
         * Set list view type
         * @param type
         * @return
         */
        fun setListViewType(type: EaseListViewType): Builder {
            this.viewType = type
            return this
        }

        /**
         * Set is show side bar
         * @param isVisible
         * @return
         */
        fun setSideBarVisible(isVisible:Boolean):Builder{
            bundle.putBoolean(Constant.KEY_SIDEBAR_VISIBLE,true)
            return this
        }

        /**
         * Set is show list header
         * @param isVisible
         * @return
         */
        fun setHeaderItemVisible(isVisible:Boolean): Builder{
            bundle.putBoolean(Constant.KEY_SHOW_ITEM_HEADER, isVisible)
            return this
        }

        /**
         * Set list header adapter
         * @param headerAdapter
         * @return
         */
        fun setHeaderAdapter(headerAdapter:EaseCustomHeaderAdapter): Builder{
            this.headerAdapter = headerAdapter
            return this
        }

        /**
         * Set list header item click listener
         * @param listener
         * @return
         */
        fun setOnHeaderItemClickListener(listener: OnHeaderItemClickListener):Builder{
            this.headerItemClickListener = listener
            return this
        }

        /**
         * Set contact list item click listener return EaseUser
         * @param listener
         * @return
         */
        fun setOnUserListItemClickListener(listener:OnUserListItemClickListener?): Builder{
            this.userListItemClickListener = listener
            return this
        }

        /**
         * Set contact item long click listener
         * @param itemLongClickListener
         */
        fun setOnItemLongClickListener(itemLongClickListener: OnItemLongClickListener?):Builder {
            this.itemLongClickListener = itemLongClickListener
            return this
        }

        /**
         * Set list header item
         * @param headerList
         * @return
         */
        fun setHeaderItemList(headerList:List<EaseCustomHeaderItem>) : Builder{
            this.headerList = headerList
            return this
        }

        /**
         * Set contact checkbox select listener
         * @param listener
         * @return
         */
        fun setOnContactSelectedListener(listener: OnContactSelectedListener): Builder{
            this.contactSelectedListener = listener
            return this
        }

        open fun build(): EaseContactsListFragment {
            val fragment =
                if (customFragment != null) customFragment else EaseContactsListFragment()
            fragment!!.arguments = bundle
            fragment.setCustomAdapter(adapter)
            fragment.setOnBackPressListener(backPressListener)
            fragment.setListViewType(viewType)
            fragment.setHeaderAdapter(headerAdapter)
            fragment.setOnHeaderItemClickListener(headerItemClickListener)
            fragment.setOnUserListItemClickListener(userListItemClickListener)
            fragment.setOnItemLongClickListener(itemLongClickListener)
            fragment.setHeaderItemList(headerList)
            fragment.setOnContactSelectedListener(contactSelectedListener)
            return fragment
        }
    }

    private object Constant {
        const val KEY_USE_TITLE = "key_use_title"
        const val KEY_USE_TITLE_REPLACE = "key_use_replace_action_bar"
        const val KEY_SET_TITLE = "key_set_title"
        const val KEY_USE_SEARCH = "key_use_search"
        const val KEY_SEARCH_TYPE = "key_search_type"
        const val KEY_EMPTY_LAYOUT = "key_empty_layout"
        const val KEY_ENABLE_BACK = "key_enable_back"
        const val KEY_SHOW_ITEM_HEADER = "key_show_item_header"
        const val KEY_SELECT_USER = "select_user"
        const val KEY_USER = "user"
        const val KEY_SIDEBAR_VISIBLE = "key_side_bar_visible"
    }

}