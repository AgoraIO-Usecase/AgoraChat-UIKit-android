package io.agora.chat.uikit.chathistory.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.widget.chatrow.EaseChatRowCombine;


public class EaseChatRowHistoryCombine extends EaseChatRowCombine {

    public EaseChatRowHistoryCombine(Context context, boolean isSender) {
		super(context, isSender);
	}

    public EaseChatRowHistoryCombine(Context context, ChatMessage message, int position, Object adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(R.layout.ease_row_history_combine, this);
	}

	@Override
	public void setOtherTimestamp(ChatMessage preMessage) {
		timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
		timeStampView.setVisibility(View.VISIBLE);
	}
}
