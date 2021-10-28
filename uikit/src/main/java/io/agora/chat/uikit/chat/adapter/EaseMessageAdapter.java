package io.agora.chat.uikit.chat.adapter;

import android.view.ViewGroup;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.chat.viewholder.EaseChatType;
import io.agora.chat.uikit.chat.viewholder.EaseChatViewHolderFactory;
import io.agora.chat.uikit.conversation.viewholder.EaseConversationViewHolderFactory;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;

public class EaseMessageAdapter extends EaseBaseRecyclerViewAdapter<ChatMessage> {
    private MessageListItemClickListener listener;
    
    public EaseMessageAdapter() {}
    
    public EaseMessageAdapter(MessageListItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if(mData != null && mData.size() > 0) {
            return EaseChatViewHolderFactory.getViewType(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        return EaseChatViewHolderFactory.createViewHolder(parent, EaseChatType.from(viewType), listener);
    }
    
    public void setListItemClickListener(MessageListItemClickListener listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }
}
