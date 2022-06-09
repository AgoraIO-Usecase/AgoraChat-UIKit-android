package io.agora.chat.uikit.chatthread.adapter;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.chatthread.interfaces.EaseChatThreadParentMsgViewProvider;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.chatthread.widget.EaseChatThreadParentMsgView;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseChatThreadHeaderAdapter extends EaseBaseRecyclerViewAdapter<ChatMessage> {
    private ChatThread thread;
    private String threadName;
    private EaseChatThreadParentMsgViewProvider parentMsgViewProvider;

    public EaseChatThreadHeaderAdapter(EaseChatThreadParentMsgViewProvider parentMsgViewProvider) {
        this.parentMsgViewProvider = parentMsgViewProvider;
    }

    public void setThreadInfo(ChatThread thread) {
        this.thread = thread;
        notifyDataSetChanged();
    }

    public void updateThreadName(String threadName) {
        this.threadName = threadName;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder<ChatMessage> getEmptyViewHolder(ViewGroup parent) {
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.ease_item_thread_chat_header_no_parent_message, parent, false);
        return new ViewHolder<ChatMessage>(emptyView) {
            private TextView tvNoMsg;
            private TextView tvThreadName;
            private TextView tvCreateOwner;

            @Override
            public void initView(View itemView) {
                super.initView(itemView);
                tvNoMsg = findViewById(R.id.tv_no_msg);
                tvThreadName = findViewById(R.id.tv_thread_name);
                tvCreateOwner = findViewById(R.id.tv_create_owner);
            }

            @Override
            public void setEmptyData() {
                if(thread != null) {
                    tvThreadName.setText(thread.getChatThreadName());
                    if(!TextUtils.isEmpty(thread.getOwner())) {
                        EaseUser userInfo = EaseUserUtils.getUserInfo(thread.getOwner());
                        String nickname = userInfo != null ? userInfo.getNickname() : thread.getOwner();
                        String content = mContext.getString(R.string.ease_thread_started_by_user, nickname);
                        SpannableStringBuilder builder = new SpannableStringBuilder(content);
                        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.black)),
                                content.length() - nickname.length(),
                                content.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvCreateOwner.setText(builder);
                    }
                }
            }

            @Override
            public void setData(ChatMessage item, int position) {

            }
        };
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_thread_chat_header, parent, false);
        return new HeaderViewHolder(view);
    }
    
    private class HeaderViewHolder extends ViewHolder<ChatMessage> {
        private TextView tvThreadName;
        private TextView tvCreateOwner;
        private FrameLayout threadParentMsg;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThreadName = findViewById(R.id.tv_thread_name);
            tvCreateOwner = findViewById(R.id.tv_create_owner);
            threadParentMsg = findViewById(R.id.thread_parent_msg);
        }

        @Override
        public void setData(ChatMessage item, int position) {
            if(thread != null) {
                tvThreadName.setText(thread.getChatThreadName());
                if(!TextUtils.isEmpty(thread.getOwner())) {
                    EaseUser userInfo = EaseUserUtils.getUserInfo(thread.getOwner());
                    String nickname = userInfo != null ? userInfo.getNickname() : thread.getOwner();
                    String content = mContext.getString(R.string.ease_thread_started_by_user, nickname);
                    SpannableStringBuilder builder = new SpannableStringBuilder(content);
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.black)),
                            content.length() - nickname.length(),
                                content.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvCreateOwner.setText(builder);
                }
            }
            if(!TextUtils.isEmpty(threadName)) {
                tvThreadName.setText(threadName);
            }
            if(parentMsgViewProvider != null && parentMsgViewProvider.parentMsgView(item) != null) {
                View view = parentMsgViewProvider.parentMsgView(item);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(layoutParams);
                threadParentMsg.removeAllViews();
                threadParentMsg.addView(view);
            }else {
                EaseChatThreadParentMsgView view = new EaseChatThreadParentMsgView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(layoutParams);
                threadParentMsg.removeAllViews();
                threadParentMsg.addView(view);
                view.setMessage(item);
            }
        }
    }
}
