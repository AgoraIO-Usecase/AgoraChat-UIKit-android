package io.agora.chat.uikit.conversation.interfaces;


import io.agora.chat.uikit.conversation.adapter.EaseConversationListAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.presenter.EaseConversationPresenter;
import io.agora.chat.uikit.interfaces.IRecyclerView;

public interface IConversationListLayout extends IRecyclerView {

    /**
     * 添加其他类型的代理类
     * @param delegate
     */
    //void addDelegate(EaseBaseConversationDelegate delegate);

    /**
     * 设置presenter
     * @param presenter
     */
    void setPresenter(EaseConversationPresenter presenter);

    /**
     * 是否展示默认的条目菜单
     * @param showDefault
     */
    void showItemDefaultMenu(boolean showDefault);

    /**
     * Set custom list adapter
     * @param listAdapter
     */
    void setListAdapter(EaseConversationListAdapter listAdapter);

    /**
     * 获取数据适配器
     * @return
     */
    EaseConversationListAdapter getListAdapter();

    /**
     * 获取条目数据
     * @param position
     * @return
     */
    EaseConversationInfo getItem(int position);


    /**
     * 将对话置为已读
     * @param position
     * @param info
     */
    void makeConversionRead(int position, EaseConversationInfo info);

    /**
     * 置顶
     * @param position
     * @param info
     */
    void makeConversationTop(int position, EaseConversationInfo info);

    /**
     * 取消置顶
     * @param position
     * @param info
     */
    void cancelConversationTop(int position, EaseConversationInfo info);

    /**
     * 删除会话
     * @param position
     * @param info
     */
    void deleteConversation(int position, EaseConversationInfo info);

    /**
     * 设置会话变化的监听
     * @param listener
     */
    void setOnConversationChangeListener(OnConversationChangeListener listener);

    /**
     * 设置加载会话状态监听
     * @param loadListener
     */
    void setOnConversationLoadListener(OnConversationLoadListener loadListener);
}
