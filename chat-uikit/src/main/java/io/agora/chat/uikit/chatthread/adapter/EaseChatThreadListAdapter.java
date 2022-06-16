package io.agora.chat.uikit.chatthread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.databinding.EaseItemRowThreadListBinding;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseChatThreadListAdapter extends EaseBaseRecyclerViewAdapter<ChatThread> {
    private Map<String, ChatMessage> messageMap = new HashMap<>();
    @Override
    public ViewHolder<ChatThread> getViewHolder(ViewGroup parent, int viewType) {
        EaseItemRowThreadListBinding binding = EaseItemRowThreadListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ThreadListViewHolder(binding);
    }
    
    private class ThreadListViewHolder extends ViewHolder<ChatThread> {
        private EaseItemRowThreadListBinding binding;

        public ThreadListViewHolder(@NonNull EaseItemRowThreadListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            EaseUserUtils.setUserAvatarStyle(binding.avatar);
        }

        @Override
        public void setData(ChatThread item, int position) {
            String threadId = item.getChatThreadId();
            binding.name.setText(item.getChatThreadName());
            ChatMessage message;
            // Prioritize the use of data obtained from the server
            if(messageMap != null &&
                    messageMap.containsKey(threadId) &&
                    messageMap.get(threadId) != null) {
                message = messageMap.get(threadId);
            }else {
                Conversation conversation = ChatClient.getInstance().chatManager().getConversation(threadId,
                        Conversation.ConversationType.GroupChat,
                        true, true);
                message = conversation.getLastMessage();
            }

            if(message != null && message.isChatThreadMessage()) {
                binding.groupUser.setVisibility(View.VISIBLE);
                binding.tvNoMsg.setVisibility(View.GONE);
                EaseUser userInfo = EaseUserUtils.getUserInfo(message.getFrom());
                String username;
                if(userInfo == null) {
                    username = message.getFrom();
                }else {
                    username = userInfo.getNickname();
                }
                binding.username.setText(username);
                EaseUserUtils.setUserAvatar(mContext, message.getFrom(), binding.avatar);
                binding.message.setText(EaseSmileUtils.getSmiledText(mContext, EaseUtils.getMessageDigest(message, mContext)));
                binding.time.setText(EaseDateUtils.getTimestampString(mContext, new Date(message.getMsgTime())));
            }else {
                binding.groupUser.setVisibility(View.GONE);
                binding.tvNoMsg.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * Set thread latest messages
     * @param messageMap
     */
    public void setLatestMessages(Map<String, ChatMessage> messageMap) {
        if(messageMap != null && !messageMap.isEmpty()) {
            this.messageMap.putAll(messageMap);
        }
        notifyDataSetChanged();
    }

    /**
     * Get thread latest message map
     * @return
     */
    public Map<String, ChatMessage> getLatestMessages() {
        return this.messageMap;
    }
}
