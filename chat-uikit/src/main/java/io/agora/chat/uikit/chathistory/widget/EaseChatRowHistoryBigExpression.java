package io.agora.chat.uikit.chathistory.widget;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowBigExpression;

/**
 * big emoji icons
 *
 */
public class EaseChatRowHistoryBigExpression extends EaseChatRowBigExpression {

    public EaseChatRowHistoryBigExpression(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowHistoryBigExpression(Context context, ChatMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.ease_row_history_bigexpression, this);
    }

    @Override
    public void setOtherTimestamp(ChatMessage preMessage) {
        timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
        timeStampView.setVisibility(View.VISIBLE);
    }
}
