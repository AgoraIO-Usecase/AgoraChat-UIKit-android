package com.hyphenate.easeui.feature.contact.interfaces

import com.hyphenate.easeui.common.ChatPresence
import com.hyphenate.easeui.feature.contact.adapter.EaseContactListAdapter
import com.hyphenate.easeui.common.enums.EaseListViewType
import com.hyphenate.easeui.common.interfaces.IRecyclerView
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.viewmodel.contacts.IContactListRequest
import java.util.concurrent.ConcurrentHashMap

interface IContactListLayout : IRecyclerView {
    fun setViewModel(viewModel: IContactListRequest?)

    /**
     * Set custom list adapter
     * @param adapter
     */
    fun setListAdapter(adapter: EaseContactListAdapter?)

    /**
     * Get data adapter
     * @return
     */
    fun getListAdapter(): EaseContactListAdapter?

    /**
     * Get item data
     * @param position
     * @return
     */
    fun getItem(position: Int): EaseUser?

    /**
     * Set List View Type
     * @param type
     * @return
     */
    fun setListViewType(type: EaseListViewType?)

    /**
     * Set side bar view visible
     * @param isVisible
     * @return
     */
    fun setSideBarVisible(isVisible:Boolean?)

    /**
     * Set user presence
     * @param presence
     * @return
     */
    fun setPresence(presence: ConcurrentHashMap<String, ChatPresence>?)

    /**
     * load contact data listener
     * @param listener
     * @return
     */
    fun setLoadContactListener(listener: OnLoadContactListener)

}