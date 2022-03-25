package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.CustomMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;

public class EaseChatRowCustom extends EaseChatRow {

	private TextView contentView;

    public EaseChatRowCustom(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowCustom(Context context, ChatMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(!showSenderType ?
				R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

    @Override
    public void onSetUpView() {
        CustomMessageBody txtBody = (CustomMessageBody) message.getBody();
        String msg = context.getString(R.string.ease_custom_message, txtBody.event());
        contentView.setText(msg);
    }

    public void onAckUserUpdate(final int count) {
        if (ackedView != null && isSender()) {
            ackedView.post(new Runnable() {
                @Override
                public void run() {
                    ackedView.setVisibility(VISIBLE);
                    ackedView.setText(String.format(getContext().getString(R.string.ease_group_ack_read_count), count));
                }
            });
        }
    }

    @Override
    protected void onMessageCreate() {
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }

        // Show "1 Read" if this msg is a ding-type msg.
        if (isSender() && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.ease_group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelper.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    onAckUserUpdate(list.size());
                }
            };
}
