package io.agora.chat.uikit.conversation.delegate;


import io.agora.chat.uikit.adapter.EaseAdapterDelegate;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;

public abstract class EaseBaseConversationDelegate<T, VH extends EaseBaseRecyclerViewAdapter.ViewHolder<T>> extends EaseAdapterDelegate<T, VH> {
    public EaseConversationSetStyle setModel;

    public void setSetModel(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }

    public EaseBaseConversationDelegate(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }
}

