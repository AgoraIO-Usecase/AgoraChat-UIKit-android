package io.agora.chat.uikit.chathistory.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowImage;


/**
 * image for row
 */
public class EaseChatRowHistoryImage extends EaseChatRowImage {

    public EaseChatRowHistoryImage(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowHistoryImage(Context context, ChatMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.ease_row_history_picture, this);
    }

    @Override
    public void setOtherTimestamp(ChatMessage preMessage) {
        timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
        timeStampView.setVisibility(View.VISIBLE);
    }
}
