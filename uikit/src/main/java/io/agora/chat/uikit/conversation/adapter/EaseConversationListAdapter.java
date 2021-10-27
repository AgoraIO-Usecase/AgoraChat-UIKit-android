package io.agora.chat.uikit.conversation.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.conversation.viewholder.EaseConversationType;
import io.agora.chat.uikit.conversation.viewholder.EaseConversationViewHolderFactory;

public class EaseConversationListAdapter extends EaseBaseRecyclerViewAdapter<EaseConversationInfo> {
    private EaseConversationSetStyle style;

    public EaseConversationListAdapter() {}

    public EaseConversationListAdapter(EaseConversationSetStyle style) {
        this.style = style;
    }

    @Override
    public int getItemViewType(int position) {
        if(mData != null && mData.size() > 0) {
            return EaseConversationViewHolderFactory.getViewType(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder<EaseConversationInfo> getViewHolder(ViewGroup parent, int viewType) {
        return EaseConversationViewHolderFactory.createViewHolder(LayoutInflater.from(parent.getContext())
                , parent, EaseConversationType.from(viewType), style);
    }

    public void setConversationSetStyle(EaseConversationSetStyle style) {
        this.style = style;
        notifyDataSetChanged();
    }

}

