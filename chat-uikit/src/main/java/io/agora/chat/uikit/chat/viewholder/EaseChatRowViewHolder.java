package io.agora.chat.uikit.chat.viewholder;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.widget.chatrow.EaseChatRow;


public class EaseChatRowViewHolder extends EaseMessageAdapter.ViewHolder<ChatMessage> implements EaseChatRow.EaseChatRowActionCallback {
    private static final String TAG = EaseChatRowViewHolder.class.getSimpleName();
    private Context context;
    private EaseChatRow chatRow;
    private ChatMessage message;

    public EaseChatRowViewHolder(@NonNull View itemView) {
        super(itemView);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        this.context = itemView.getContext();
    }

    @Override
    public void initView(View itemView) {
        this.chatRow = (EaseChatRow) itemView;
    }

    @Override
    public void setData(ChatMessage item, int position) {
        message = item;
        chatRow.setUpView(item, position);
        chatRow.setChatRowActionCallback(this);
        handleMessage();
    }

    @Override
    public void setDataList(List<ChatMessage> data, int position) {
        super.setDataList(data, position);
        chatRow.setTimestamp(position == 0 ? null : data.get(position - 1));
    }

    @Override
    public void onResendClick(ChatMessage message) {

    }

    @Override
    public void onBubbleClick(ChatMessage message) {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    private void handleMessage() {
        if (message.direct() == ChatMessage.Direct.SEND) {
            handleSendMessage(message);
        } else if (message.direct() == ChatMessage.Direct.RECEIVE) {
            handleReceiveMessage(message);
        }
    }

    /**
     * send message
     * @param message
     */
    protected void handleSendMessage(final ChatMessage message) {
        // Update the view according to the message current status.
        getChatRow().updateView(message);
    }

    /**
     * receive message
     * @param message
     */
    protected void handleReceiveMessage(ChatMessage message) {

    }

    public Context getContext() {
        return context;
    }

    public EaseChatRow getChatRow() {
        return chatRow;
    }
}
