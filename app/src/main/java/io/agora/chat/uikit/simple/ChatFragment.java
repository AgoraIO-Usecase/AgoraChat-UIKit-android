package io.agora.chat.uikit.simple;

import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.widget.EaseChatInputMenu;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;

public class ChatFragment extends EaseChatFragment {
    @Override
    public void initView() {
        super.initView();
        EaseChatMessageListLayout messageListLayout = chatLayout.getChatMessageListLayout();
        EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
    }
}
