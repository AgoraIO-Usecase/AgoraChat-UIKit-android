package com.hyphenate.easeui.feature.contact.interfaces

import com.hyphenate.easeui.feature.contact.adapter.ChatUIKitContactListAdapter
import com.hyphenate.easeui.common.enums.ChatUIKitListViewType
import com.hyphenate.easeui.common.interfaces.IRecyclerView
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest
import java.util.concurrent.ConcurrentHashMap

interface IContactListLayout : IRecyclerView {
    fun setViewModel(viewModel: IContactListRequest?)

    /**
     * Set custom list adapter
     * @param adapter
     */
    fun setListAdapter(adapter: ChatUIKitContactListAdapter?)

    /**
     * Get data adapter
     * @return
     */
    fun getListAdapter(): ChatUIKitContactListAdapter?

    /**
     * Get item data
     * @param position
     * @return
     */
    fun getItem(position: Int): ChatUIKitUser?

    /**
     * Set List View Type
     * @param type
     * @return
     */
    fun setListViewType(type: ChatUIKitListViewType?)

    /**
     * Set side bar view visible
     * @param isVisible
     * @return
     */
    fun setSideBarVisible(isVisible:Boolean?)

    /**
     * Set user presence
     * @param info
     * @return
     */
    fun setUserAvatarInfo(info: ConcurrentHashMap<String, Int>?)

    /**
     * load contact data listener
     * @param listener
     * @return
     */
    fun setLoadContactListener(listener: OnContactEventListener)

}