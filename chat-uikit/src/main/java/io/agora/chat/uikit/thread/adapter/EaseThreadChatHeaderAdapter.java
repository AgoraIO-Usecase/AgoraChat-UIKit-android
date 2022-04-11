package io.agora.chat.uikit.thread.adapter;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.thread.widget.EaseThreadParentMsgView;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseThreadChatHeaderAdapter extends EaseBaseRecyclerViewAdapter<ChatMessage> {
    private ChatThread thread;
    private String threadName;

    public EaseThreadChatHeaderAdapter() {

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
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_thread_chat_header, parent, false);
        return new HeaderViewHolder(view);
    }
    
    private class HeaderViewHolder extends ViewHolder<ChatMessage> {
        private TextView tvThreadName;
        private TextView tvCreateOwner;
        private EaseThreadParentMsgView threadParentMsg;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThreadName = findViewById(R.id.tv_thread_name);
            tvCreateOwner = findViewById(R.id.tv_create_owner);
            threadParentMsg = findViewById(R.id.thread_parent_msg);
        }

        @Override
        public void setData(ChatMessage item, int position) {
            if(thread != null) {
                tvThreadName.setText(thread.getThreadName());
                if(!TextUtils.isEmpty(thread.getCreator())) {
                    EaseUser userInfo = EaseUserUtils.getUserInfo(thread.getCreator());
                    String nickname = userInfo != null ? userInfo.getNickname() : thread.getCreator();
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
            threadParentMsg.setMessage(item);
        }
    }
}
