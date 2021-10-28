package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import io.agora.chat.ChatMessage;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.utils.EaseSmileUtils;


public class EaseChatRowUnknown extends EaseChatRow {
	private TextView contentView;

    public EaseChatRowUnknown(Context context, boolean isSender) {
		super(context, isSender);
	}

    public EaseChatRowUnknown(Context context, ChatMessage message, int position, Object adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(!showSenderType ? R.layout.ease_row_received_message
                : R.layout.ease_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

    @Override
    public void onSetUpView() {
        contentView.setText(getContext().getString(R.string.ease_chat_unknown_type));
    }
}
