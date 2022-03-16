package io.agora.chat.uikit.thread;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import io.agora.MessageListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.thread.presenter.EaseThreadChatPresenter;
import io.agora.chat.uikit.thread.presenter.EaseThreadChatPresenterImpl;
import io.agora.chat.uikit.thread.presenter.EaseThreadCreatePresenterImpl;
import io.agora.chat.uikit.thread.presenter.IThreadChatView;

public class EaseThreadChatFragment extends EaseChatFragment implements IThreadChatView {
    protected String parentMsgId;
    protected ChatThread mThread;
    protected String parentId;

    protected EaseThreadChatPresenter mPresenter;
    private EaseThreadChatHeaderAdapter headerAdapter;
    private List<ChatMessage> data = new ArrayList<>();

    @Override
    public void initView() {
        super.initView();
        if(mPresenter == null) {
            mPresenter = new EaseThreadChatPresenterImpl();
        }
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(mPresenter);
        }

        Bundle bundle = getArguments();
        if(bundle != null) {
            parentMsgId = bundle.getString(Constant.KEY_PARENT_MESSAGE_ID);
            mThread = bundle.getParcelable(Constant.KEY_THREAD_BEAN);
        }
        if(mThread == null && parentMsgId != null) {
            ChatMessage message = ChatClient.getInstance().chatManager().getMessage(parentMsgId);
            if(message != null && (message.getChatType() == ChatMessage.ChatType.GroupChat || message.getChatType() == ChatMessage.ChatType.ChatRoom)) {
                parentId = message.getTo();
                data.clear();
                data.add(message);
            }
        }
        addHeaderViewToList();
    }

    private void addHeaderViewToList() {
        headerAdapter = new EaseThreadChatHeaderAdapter();
        chatLayout.getChatMessageListLayout().addHeaderAdapter(headerAdapter);
    }

    private void setThreadInfo(ChatThread thread) {
        if(thread == null) {
            return;
        }
        headerAdapter.setThreadInfo(thread);
    }

    @Override
    public void initListener() {
        super.initListener();
        ChatClient.getInstance().chatManager().addMessageListener(new MessageListener() {
            @Override
            public void onMessageReceived(List<ChatMessage> messages) {
                for (ChatMessage message:messages) {
                    // Determine if there is new group information, and display a red dot if there is new group information
                    if (message.getChatType() == ChatMessage.ChatType.GroupChat || message.getChatType() == ChatMessage.ChatType.ChatRoom) {
                        if(TextUtils.equals(parentId, message.getTo())) {
                            titleBar.setUnreadIconVisible(true);
                        }
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<ChatMessage> messages) {

            }

            @Override
            public void onMessageRead(List<ChatMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<ChatMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<ChatMessage> messages) {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        if(mThread == null) {
            mPresenter.getThreadInfo(conversationId);
        }
        setThreadInfo(mThread);
        headerAdapter.setData(data);
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public void onGetThreadInfoSuccess(ChatThread thread) {
        mThread = thread;
        setThreadInfo(thread);
    }

    @Override
    public void onGetThreadInfoFail(int error, String errorMsg) {

    }

    private void setThreadPresenter(EaseThreadChatPresenter presenter) {
        this.mPresenter = presenter;
    }

    public static class Builder extends EaseChatFragment.Builder {
        private EaseThreadChatPresenter presenter;

        public Builder(String parentMsgId, String conversationId, int chatType) {
            super(conversationId, chatType);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
        }

        public Builder(String parentMsgId, String conversationId, int chatType, String historyMsgId) {
            super(conversationId, chatType, historyMsgId);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
        }
        
        public Builder setThread(ChatThread thread) {
            this.bundle.putParcelable(Constant.KEY_THREAD_BEAN, thread);
            return this;
        }

        public Builder setThreadPresenter(EaseThreadChatPresenter presenter) {
            this.presenter = presenter;
            return this;
        }

        @Override
        public EaseChatFragment build() {
            EaseChatFragment fragment = super.build();
            if(fragment instanceof EaseThreadChatFragment) {
                ((EaseThreadChatFragment)fragment).setThreadPresenter(this.presenter);
            }
            return fragment;
        }
    }

    private static final class Constant {
        public static final String KEY_PARENT_MESSAGE_ID = "key_parent_message_id";
        public static final String KEY_THREAD_BEAN = "key_thread_bean";
    }

}
