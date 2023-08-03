package io.agora.chat.uikit.chathistory;

import android.view.ViewGroup;


import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.viewholder.EaseChatViewHolderFactory;
import io.agora.chat.uikit.chat.viewholder.EaseMessageViewType;

public class EaseChatHistoryAdapter extends EaseMessageAdapter {

    public EaseChatHistoryAdapter() {}

    @Override
    public int getItemNotEmptyViewType(int position) {
        return EaseChatViewHolderFactory.getViewType(mData.get(position));
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        return EaseChatViewHolderFactory.createHistoryViewHolder(parent, EaseMessageViewType.from(viewType));
    }

}
