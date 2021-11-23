package io.agora.chat.uikit.conversation.interfaces;


import io.agora.chat.uikit.conversation.adapter.EaseConversationListAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.presenter.EaseConversationPresenter;
import io.agora.chat.uikit.interfaces.IRecyclerView;

public interface IConversationListLayout extends IRecyclerView {

    void setPresenter(EaseConversationPresenter presenter);

    /**
     * Whether to show the default entry menu
     * @param showDefault
     */
    void showItemDefaultMenu(boolean showDefault);

    /**
     * Set custom list adapter
     * @param listAdapter
     */
    void setListAdapter(EaseConversationListAdapter listAdapter);

    /**
     * Get data adapter
     * @return
     */
    EaseConversationListAdapter getListAdapter();

    /**
     * Get item data
     * @param position
     * @return
     */
    EaseConversationInfo getItem(int position);


    /**
     * Make conversation read
     * @param position
     * @param info
     */
    void makeConversionRead(int position, EaseConversationInfo info);

    void makeConversationTop(int position, EaseConversationInfo info);

    void cancelConversationTop(int position, EaseConversationInfo info);

    /**
     * Delete conversation
     * @param position
     * @param info
     */
    void deleteConversation(int position, EaseConversationInfo info);

    /**
     * Set up monitoring of session changes
     * @param listener
     */
    void setOnConversationChangeListener(OnConversationChangeListener listener);

    /**
     * Set up the loading session state monitor
     * @param loadListener
     */
    void setOnConversationLoadListener(OnConversationLoadListener loadListener);
}
