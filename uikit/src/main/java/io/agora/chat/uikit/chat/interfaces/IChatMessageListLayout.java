package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.presenter.EaseChatMessagePresenter;
import io.agora.chat.uikit.interfaces.IRecyclerView;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;

public interface IChatMessageListLayout extends IRecyclerView {

    /**
     * 设置presenter
     * @param presenter
     */
    void setPresenter(EaseChatMessagePresenter presenter);

    /**
     * 获取adapter
     * @return
     */
    EaseMessageAdapter getMessageAdapter();

    /**
     * 设置聊天区域的touch监听，判断是否点击在条目消息外，是否正在拖拽列表
     * @param listener
     */
    void setOnMessageTouchListener(EaseChatMessageListLayout.OnMessageTouchListener listener);

    /**
     * 设置聊天过程中的错误监听
     * @param listener
     */
    void setOnChatErrorListener(EaseChatMessageListLayout.OnChatErrorListener listener);

    /**
     * 设置聊天列表条目中各个控件的点击事件
     * @param listener
     */
    void setMessageListItemClickListener(MessageListItemClickListener listener);
}
