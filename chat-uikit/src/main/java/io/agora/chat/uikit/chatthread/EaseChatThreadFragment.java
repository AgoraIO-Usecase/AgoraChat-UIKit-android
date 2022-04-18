package io.agora.chat.uikit.chatthread;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.ChatThreadEvent;
import io.agora.chat.Group;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.EaseMessageListener;
import io.agora.chat.uikit.menu.MenuItemBean;
import io.agora.chat.uikit.chatthread.adapter.EaseChatThreadHeaderAdapter;
import io.agora.chat.uikit.chatthread.interfaces.OnChatThreadRoleResultCallback;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadPresenter;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadPresenterImpl;
import io.agora.chat.uikit.chatthread.presenter.IChatThreadView;

public class EaseChatThreadFragment extends EaseChatFragment implements IChatThreadView {
    protected String parentMsgId;
    protected ChatThread mThread;
    protected String parentId;
    protected EaseChatThreadRole threadRole = EaseChatThreadRole.UNKNOWN;

    protected EaseChatThreadPresenter mPresenter;
    private EaseChatThreadHeaderAdapter headerAdapter;
    private List<ChatMessage> data = new ArrayList<>();
    private OnJoinThreadResultListener joinThreadResultListener;
    private OnChatThreadRoleResultCallback resultCallback;
    private MessageListener messageListener;

    @Override
    public void initView() {
        super.initView();
        if(mPresenter == null) {
            mPresenter = new EaseChatThreadPresenterImpl();
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
        setThreadMenu();
    }

    private void setThreadMenu() {
        chatLayout.findItemVisible(R.id.action_chat_delete, false);
        chatLayout.addItemMenu(0, R.id.action_chat_unsent, 100, getString(R.string.ease_action_unsent));
    }

    private void addHeaderViewToList() {
        headerAdapter = new EaseChatThreadHeaderAdapter();
        chatLayout.getChatMessageListLayout().addHeaderAdapter(headerAdapter);
    }

    private void setThreadInfo(ChatThread thread) {
        if(thread == null) {
            return;
        }
        if(titleBar != null) {
            titleBar.setTitle(thread.getChatThreadName());
        }
        headerAdapter.setThreadInfo(thread);
    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, ChatMessage message) {
        if(item.getItemId() == R.id.action_chat_unsent) {
            chatLayout.recallMessage(message);
            return true;
        }
        return super.onMenuItemClick(item, message);
    }

    @Override
    public void initListener() {
        super.initListener();
        messageListener = new MessageListener();
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    public void onThreadEvent(int event, String target, List<String> usernames) {
        super.onThreadEvent(event, target, usernames);
        if((event == THREAD_DESTROY || event == THREAD_LEAVE) && TextUtils.equals(target, conversationId)) {
            mContext.finish();
        }
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

    @Override
    public void onChatThreadUpdated(ChatThreadEvent event) {
        if(TextUtils.equals(event.getChatThreadId(), conversationId)) {
            runOnUiThread(()->{
                chatLayout.getChatMessageListLayout().refreshMessages();
                headerAdapter.updateThreadName(event.getChatThreadName());
                if(mContext != null && !mContext.isFinishing() && titleBar != null) {
                    titleBar.setTitle(event.getChatThreadName());
                }
            });
        }
    }

    @Override
    public void onChatThreadDestroyed(ChatThreadEvent event) {
        exitThreadChat(event.getChatThreadId());
    }

    @Override
    public void onChatThreadUserRemoved(ChatThreadEvent event) {
        exitThreadChat(event.getChatThreadId());
    }

    private void exitThreadChat(String threadId) {
        if(TextUtils.equals(threadId, conversationId)) {
            mContext.finish();
        }
    }

    @Override
    public void initData() {
        setThreadInfo(mThread);
        headerAdapter.setData(data);
        joinThread();
        setGroupInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(messageListener != null) {
            ChatClient.getInstance().chatManager().removeMessageListener(messageListener);
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
    public void OnJoinThreadSuccess(ChatThread thread) {
        if(joinThreadResultListener != null) {
            joinThreadResultListener.joinSuccess(conversationId);
        }
        mThread = thread;
        setThreadInfo(thread);
        getThreadRole(mThread);
        if(threadRole != EaseChatThreadRole.GROUP_ADMIN && threadRole != EaseChatThreadRole.CREATOR) {
            threadRole = EaseChatThreadRole.MEMBER;
            if(resultCallback != null) {
                resultCallback.onThreadRole(threadRole);
            }
        }
        runOnUiThread(super::initData);
    }

    @Override
    public void OnJoinThreadFail(int error, String errorMsg) {
        if(error == Error.USER_ALREADY_EXIST) {
            mPresenter.getThreadInfo(conversationId);
        }else {
            if(joinThreadResultListener != null) {
                joinThreadResultListener.joinFailed(error, errorMsg);
            }
        }
    }

    @Override
    public void onGetGroupInfoSuccess(Group group) {
        titleBar.setSubTitle(getString(R.string.ease_thread_list_sub_title, group.getGroupName()));
        if(isGroupAdmin(group)) {
            threadRole = EaseChatThreadRole.GROUP_ADMIN;
            if(resultCallback != null) {
                resultCallback.onThreadRole(threadRole);
            }
        }
    }

    @Override
    public void onGetGroupInfoFail(int error, String errorMsg) {

    }

    private void setThreadPresenter(EaseChatThreadPresenter presenter) {
        this.mPresenter = presenter;
    }

    private void setOnJoinThreadResultListener(OnJoinThreadResultListener listener) {
        this.joinThreadResultListener = listener;
    }

    private void setOnThreadRoleResultCallback(OnChatThreadRoleResultCallback callback) {
        this.resultCallback = callback;
    }

    private EaseChatThreadRole getThreadRole(ChatThread thread) {
        if(threadRole == EaseChatThreadRole.GROUP_ADMIN) {
            return threadRole;
        }
        if(thread != null) {
            if(TextUtils.equals(thread.getCreator(), ChatClient.getInstance().getCurrentUser())) {
                threadRole = EaseChatThreadRole.CREATOR;
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
        private EaseChatThreadPresenter presenter;
        private OnJoinThreadResultListener listener;
        private OnChatThreadRoleResultCallback resultCallback;

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
        public Builder setThreadPresenter(EaseChatThreadPresenter presenter) {
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
        public Builder setOnThreadRoleResultCallback(OnChatThreadRoleResultCallback callback) {
            this.resultCallback = callback;
            return this;
        }

        @Override
        public EaseChatFragment build() {
            if(this.customFragment == null) {
                this.customFragment = new EaseChatThreadFragment();
            }
            setThreadMessage(true);
            if(this.customFragment instanceof EaseChatThreadFragment) {
                ((EaseChatThreadFragment)this.customFragment).setThreadPresenter(this.presenter);
                ((EaseChatThreadFragment)this.customFragment).setOnJoinThreadResultListener(this.listener);
                ((EaseChatThreadFragment)this.customFragment).setOnThreadRoleResultCallback(this.resultCallback);
            }

            return super.build();
        }
    }

    private static final class Constant {
        public static final String KEY_PARENT_MESSAGE_ID = "key_parent_message_id";
        public static final String KEY_THREAD_MESSAGE_FLAG = "key_thread_message_flag";
    }

}
