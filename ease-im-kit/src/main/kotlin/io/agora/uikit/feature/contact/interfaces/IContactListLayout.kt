package io.agora.uikit.feature.contact.interfaces

import io.agora.uikit.feature.contact.adapter.EaseContactListAdapter
import io.agora.uikit.common.enums.EaseListViewType
import io.agora.uikit.common.interfaces.IRecyclerView
import io.agora.uikit.model.EaseUser
import io.agora.uikit.viewmodel.contacts.IContactListRequest
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