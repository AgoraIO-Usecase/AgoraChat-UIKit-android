package io.agora.chat.uikit.conversation.adapter;


import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseDelegateAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;

public class EaseConversationListAdapter extends EaseBaseDelegateAdapter<EaseConversationInfo> {
    private int emptyLayoutId;

    @Override
    public int getEmptyLayoutId() {
        return emptyLayoutId != 0 ? emptyLayoutId : R.layout.ease_layout_default_no_conversation_data;
    }

    /**
     * set empty layout
     * @param layoutId
     */
    public void setEmptyLayoutId(int layoutId) {
        this.emptyLayoutId = layoutId;
        notifyDataSetChanged();
    }

}

