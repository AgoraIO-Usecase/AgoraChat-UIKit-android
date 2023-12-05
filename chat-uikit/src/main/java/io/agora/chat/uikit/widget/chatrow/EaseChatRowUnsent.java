package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.CustomMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;

public class EaseChatRowUnsent extends EaseChatRow {

	private TextView contentView;

    public EaseChatRowUnsent(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowUnsent(Context context, ChatMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(R.layout.ease_row_unsent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.text_content);
	}

    @Override
    public void onSetUpView() {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        String msg = txtBody.getMessage();
        contentView.setText(msg);
    }
}
