package io.agora.chat.uikit.thread;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.agora.ThreadChangeListener;
import io.agora.ThreadNotifyListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.Group;
import io.agora.chat.ThreadEvent;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.EaseMessageListener;
import io.agora.chat.uikit.thread.adapter.EaseThreadChatHeaderAdapter;
import io.agora.chat.uikit.thread.interfaces.OnThreadRoleResultCallback;
import io.agora.chat.uikit.thread.presenter.EaseThreadChatPresenter;
import io.agora.chat.uikit.thread.presenter.EaseThreadChatPresenterImpl;
import io.agora.chat.uikit.thread.presenter.IThreadChatView;

public class EaseThreadChatFragment extends EaseChatFragment implements IThreadChatView {
    protected String parentMsgId;
    protected ChatThread mThread;
    protected String parentId;
    protected EaseThreadRole threadRole = EaseThreadRole.UNKNOWN;

    protected EaseThreadChatPresenter mPresenter;
    private EaseThreadChatHeaderAdapter headerAdapter;
    private List<ChatMessage> data = new ArrayList<>();
    private OnJoinThreadResultListener joinThreadResultListener;
    private OnThreadRoleResultCallback resultCallback;
    private MessageListener messageListener;
    private MyThreadChangeListener threadChangeListener;

    @Override
    public void initView() {
        super.initView();
        if(mPresenter == null) {
            mPresenter = new EaseThreadChatPresenterImpl();
        }
        mPresenter.attachView(this);
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(mPresenter);
        }

        Bundle bundle = getArguments();
        if(bundle != null) {
            parentMsgId = bundle.getString(Constant.KEY_PARENT_MESSAGE_ID);
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
        if(titleBar != null) {
            titleBar.setTitle(thread.getThreadName());
        }
        headerAdapter.setThreadInfo(thread);
    }

    @Override
    public void initListener() {
        super.initListener();
        messageListener = new MessageListener();
        threadChangeListener = new MyThreadChangeListener();
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
        ChatClient.getInstance().threadManager().addThreadChangeListener(threadChangeListener);
    }

    private class MessageListener extends EaseMessageListener {

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
        public void onMessageRecalled(List<ChatMessage> messages) {

        }

        @Override
        public void onMessageChanged(ChatMessage message, Object change) {

        }
    }

    private class MyThreadChangeListener implements ThreadChangeListener {

        @Override
        public void onThreadNameUpdated(String parentId, String threadId, String operator, String newThreadName) {
            if(TextUtils.equals(threadId, conversationId)) {
                runOnUiThread(()->chatLayout.getChatMessageListLayout().refreshMessages());
            }
        }

        @Override
        public void onThreadDestroyed(String parentId, String threadId, String threadName) {
            exitThreadChat(threadId);
        }

        @Override
        public void onMemberJoined(String parentId, String threadId, String threadName, String username) {

        }

        @Override
        public void onMemberExited(String parentId, String threadId, String threadName, String username) {

        }

        @Override
        public void onUserRemoved(String parentId, String threadId, String threadName) {
            exitThreadChat(threadId);
        }
    }

    private void exitThreadChat(String threadId) {
        if(TextUtils.equals(threadId, conversationId)) {
            mContext.finish();
        }
    }

    @Override
    public void initData() {
        if(mThread == null) {
            mPresenter.getThreadInfo(conversationId);
        }
        setThreadInfo(mThread);
        headerAdapter.setData(data);
        joinThread();
        setGroupInfo();
    }

    @Override
    public void onThreadNotify(ThreadEvent event) {
        super.onThreadNotify(event);
        if(event != null && TextUtils.equals(event.getThreadId(), conversationId)) {
            if(event.getType() == ThreadEvent.TYPE.DELETE) {
                mContext.finish();
            }else if(event.getType() == ThreadEvent.TYPE.UPDATE) {
                runOnUiThread(()-> {
                    headerAdapter.updateThreadName(event.getThreadName());
                    if(mContext != null && !mContext.isFinishing() && titleBar != null) {
                        titleBar.setTitle(event.getThreadName());
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(messageListener != null) {
            ChatClient.getInstance().chatManager().removeMessageListener(messageListener);
        }
        if(threadChangeListener != null) {
            ChatClient.getInstance().threadManager().removeThreadChangeListener(threadChangeListener);
        }
    }

    private void setGroupInfo() {
        if(TextUtils.isEmpty(parentId)) {
            return;
        }
        mPresenter.getGroupInfo(parentId);
    }

    private void joinThread() {
        mPresenter.joinThread(conversationId);
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public void onGetThreadInfoSuccess(ChatThread thread) {
        mThread = thread;
        setThreadInfo(thread);
        getThreadRole(mThread);
    }

    @Override
    public void onGetThreadInfoFail(int error, String errorMsg) {

    }

    @Override
    public void OnJoinThreadSuccess() {
        if(joinThreadResultListener != null) {
            joinThreadResultListener.joinSuccess(conversationId);
        }
        if(threadRole != EaseThreadRole.GROUP_ADMIN && threadRole != EaseThreadRole.CREATOR) {
            threadRole = EaseThreadRole.MEMBER;
            if(resultCallback != null) {
                resultCallback.onThreadRole(threadRole);
            }
        }
        runOnUiThread(super::initData);
    }

    @Override
    public void OnJoinThreadFail(int error, String errorMsg) {
        if(joinThreadResultListener != null) {
            joinThreadResultListener.joinFailed(error, errorMsg);
        }
    }

    @Override
    public void onGetGroupInfoSuccess(Group group) {
        titleBar.setSubTitle(getString(R.string.ease_thread_list_sub_title, group.getGroupName()));
        if(isGroupAdmin(group)) {
            threadRole = EaseThreadRole.GROUP_ADMIN;
            if(resultCallback != null) {
                resultCallback.onThreadRole(threadRole);
            }
        }
    }

    @Override
    public void onGetGroupInfoFail(int error, String errorMsg) {

    }

    private void setThreadPresenter(EaseThreadChatPresenter presenter) {
        this.mPresenter = presenter;
    }

    private void setOnJoinThreadResultListener(OnJoinThreadResultListener listener) {
        this.joinThreadResultListener = listener;
    }

    private void setOnThreadRoleResultCallback(OnThreadRoleResultCallback callback) {
        this.resultCallback = callback;
    }

    private EaseThreadRole getThreadRole(ChatThread thread) {
        if(threadRole == EaseThreadRole.GROUP_ADMIN) {
            return threadRole;
        }
        if(thread != null) {
            if(TextUtils.equals(thread.getOwner(), ChatClient.getInstance().getCurrentUser())) {
                threadRole = EaseThreadRole.CREATOR;
            }
        }
        if(resultCallback != null) {
            resultCallback.onThreadRole(threadRole);
        }
        return threadRole;
    }

    /**
     * Judge whether current user is group admin
     * @param group
     * @return
     */
    public boolean isGroupAdmin(Group group) {
        if(group == null) {
            return false;
        }
        return TextUtils.equals(group.getOwner(), ChatClient.getInstance().getCurrentUser()) ||
                (group.getAdminList() != null &&
                        group.getAdminList().contains(ChatClient.getInstance().getCurrentUser()));
    }

    public interface OnJoinThreadResultListener {
        /**
         * Join thread success
         * @param threadId
         */
        void joinSuccess(String threadId);

        /**
         * Join thread failed
         * @param errorCode
         * @param message
         */
        void joinFailed(int errorCode, String message);
    }

    public static class Builder extends EaseChatFragment.Builder {
        private EaseThreadChatPresenter presenter;
        private OnJoinThreadResultListener listener;
        private OnThreadRoleResultCallback resultCallback;

        /**
         * Constructor
         * @param parentMsgId    Usually is the group ID
         * @param conversationId Agora Chat ID
         */
        public Builder(String parentMsgId, String conversationId) {
            super(conversationId, EaseConstant.CHATTYPE_GROUP);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
            this.bundle.putBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, true);
        }

        /**
         * Constructor
         * @param parentMsgId       Usually is the group ID
         * @param conversationId    Agora Chat ID
         * @param historyMsgId
         */
        public Builder(String parentMsgId, String conversationId, String historyMsgId) {
            super(conversationId, EaseConstant.CHATTYPE_GROUP, historyMsgId);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
            this.bundle.putBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, true);
        }

        /**
         * Set custom thread presenter if you want to add your logic
         * @param presenter
         * @return
         */
        public Builder setThreadPresenter(EaseThreadChatPresenter presenter) {
            this.presenter = presenter;
            return this;
        }

        /**
         * Set join thread listener
         * @param listener
         * @return
         */
        public Builder setOnJoinThreadResultListener(OnJoinThreadResultListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Set thread role callback
         * @param callback
         * @return
         */
        public Builder setOnThreadRoleResultCallback(OnThreadRoleResultCallback callback) {
            this.resultCallback = callback;
            return this;
        }

        @Override
        public EaseChatFragment build() {
            if(this.customFragment == null) {
                this.customFragment = new EaseThreadChatFragment();
            }
            setThreadMessage(true);
            if(this.customFragment instanceof EaseThreadChatFragment) {
                ((EaseThreadChatFragment)this.customFragment).setThreadPresenter(this.presenter);
                ((EaseThreadChatFragment)this.customFragment).setOnJoinThreadResultListener(this.listener);
                ((EaseThreadChatFragment)this.customFragment).setOnThreadRoleResultCallback(this.resultCallback);
            }

            return super.build();
        }
    }

    private static final class Constant {
        public static final String KEY_PARENT_MESSAGE_ID = "key_parent_message_id";
        public static final String KEY_THREAD_MESSAGE_FLAG = "key_thread_message_flag";
    }

}
