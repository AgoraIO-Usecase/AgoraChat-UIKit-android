package io.agora.chat.uikit.chathistory.presenter;

import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.interfaces.OnCombineMessageDownloadAndParseListener;

public interface IHandleChatHistory {
    /**
     * Set custom presenter.
     * @param presenter
     */
    void setPresenter(EaseChatHistoryPresenter presenter);

    /**
     * Set custom message adapter.
     * @param adapter
     */
    void setMessageAdapter(EaseMessageAdapter adapter);

    /**
     * Get message list layout.
     * @return
     */
    EaseChatMessageListLayout getChatMessageListLayout();

    /**
     * Set combine message listener.
     * @param listener
     */
    void setOnCombineMessageDownloadAndParseListener(OnCombineMessageDownloadAndParseListener listener);
}
