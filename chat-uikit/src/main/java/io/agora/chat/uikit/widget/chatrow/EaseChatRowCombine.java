package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.CombineMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.widget.EaseChatQuoteView;
import io.agora.util.EMLog;


public class EaseChatRowCombine extends EaseChatRow {
	private TextView contentView;
    private TextView tvChatSummary;

    public EaseChatRowCombine(Context context, boolean isSender) {
		super(context, isSender);
	}

    public EaseChatRowCombine(Context context, ChatMessage message, int position, Object adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(!showSenderType ? R.layout.ease_row_received_combine
                : R.layout.ease_row_sent_combine, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
        tvChatSummary = findViewById(R.id.tv_chat_summary);
	}

    @Override
    public void onSetUpView() {
        CombineMessageBody combineBody = (CombineMessageBody) message.getBody();
        if(combineBody == null) {
            return;
        }
        String title = combineBody.getTitle();
        String summary = combineBody.getSummary();
        contentView.setText(title);
        if(!TextUtils.isEmpty(summary)) {
            tvChatSummary.setText(summary);
            tvChatSummary.setVisibility(VISIBLE);
        }else {
            tvChatSummary.setVisibility(GONE);
        }
    }

    @Override
    protected void onMessageCreate() {
        setStatus(View.VISIBLE, View.GONE);
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        setStatus(View.GONE, View.GONE);
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        setStatus(View.GONE, View.VISIBLE);
    }

    @Override
    protected void onMessageInProgress() {
        setStatus(View.VISIBLE, View.GONE);
    }

    /**
     * set progress and status view visible or gone
     * @param progressVisible
     * @param statusVisible
     */
    private void setStatus(int progressVisible, int statusVisible) {
        if(progressBar != null) {
            progressBar.setVisibility(progressVisible);
        }
        if(statusView != null) {
            statusView.setVisibility(statusVisible);
        }
    }

}
