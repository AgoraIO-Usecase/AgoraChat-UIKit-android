package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.widget.EaseChatQuoteView;
import io.agora.util.EMLog;


public class EaseChatRowText extends EaseChatRow {
    protected TextView contentView;
    private EaseChatQuoteView quoteView;

    public EaseChatRowText(Context context, boolean isSender) {
		super(context, isSender);
	}

    public EaseChatRowText(Context context, ChatMessage message, int position, Object adapter) {
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
        quoteView = (EaseChatQuoteView)findViewById(R.id.chat_quote_view);
        resetBackground();
	}

    @Override
    public void onSetUpView() {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        if(txtBody != null){
            Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
            // 设置内容
            contentView.setText(span, BufferType.SPANNABLE);
            contentView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    contentView.setTag(R.id.action_chat_long_click,true);
                    if (itemClickListener != null) {
                        return itemClickListener.onBubbleLongClick(v, message);
                    }
                    return false;
                }
            });
            replaceSpan();
        }
        resetBackground();
        onSetUpQuoteView(message);
    }

    /**
     * Resolve long press event conflict with Relink
     * Refer to：https://www.jianshu.com/p/d3bef8449960
     */
    private void replaceSpan() {
        Spannable spannable = (Spannable) contentView.getText();
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int i = 0; i < spans.length; i++) {
            String url = spans[i].getURL();
            int index = spannable.toString().indexOf(url);
            int end = index + url.length();
            if (index == -1) {
                if (url.contains("http://")) {
                    url = url.replace("http://", "");
                } else if (url.contains("https://")) {
                    url = url.replace("https://", "");
                } else if (url.contains("rtsp://")) {
                    url = url.replace("rtsp://", "");
                }
                index = spannable.toString().indexOf(url);
                end = index + url.length();
            }
            if (index != -1) {
                spannable.removeSpan(spans[i]);
                spannable.setSpan(new AutolinkSpan(spans[i].getURL()), index
                        , end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    @Override
    protected void onMessageSuccess() {
        super.onMessageSuccess();

        // Show "1 Read" if this msg is a ding-type msg.
        if (isSender() && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.ease_group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    public void onSetUpQuoteView(ChatMessage message) {
        if(quoteView == null) {
            EMLog.e(TAG, "view is null, don't setup quote view");
            return;
        }
        quoteView.setVisibility(GONE);
        boolean isUpdated = quoteView.updateMessageInfo(message);
        if(isUpdated) {
            updateBackground();
        }
    }

    public void resetBackground() {
        bubbleLayout.setBackground(ContextCompat.getDrawable(context, isSender()
                ? R.drawable.ease_chat_bubble_send_bg : R.drawable.ease_chat_bubble_receive_bg));
    }

    public void updateBackground() {
        bubbleLayout.setBackground(ContextCompat.getDrawable(context, isSender() ? R.drawable.ease_chat_bubble_send_bg_has_top
                : R.drawable.ease_chat_bubble_receive_bg_has_top));
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener = list -> onAckUserUpdate(list.size());

    public void onAckUserUpdate(final int count) {
        if(ackedView == null) {
            return;
        }
        ackedView.post(()->{
            if (isSender()) {
                ackedView.setVisibility(VISIBLE);
                ackedView.setText(String.format(getContext().getString(R.string.ease_group_ack_read_count), count));
            }
        });
    }
}
