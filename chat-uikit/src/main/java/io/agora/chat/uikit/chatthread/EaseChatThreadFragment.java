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
import io.agora.chat.ImageMessageBody;
import io.agora.chat.MessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.interfaces.OnRecallMessageResultListener;
import io.agora.chat.uikit.chatthread.interfaces.EaseChatThreadParentMsgViewProvider;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.EaseMessageListener;
import io.agora.chat.uikit.interfaces.OnJoinChatThreadResultListener;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;
import io.agora.chat.uikit.chatthread.adapter.EaseChatThreadHeaderAdapter;
import io.agora.chat.uikit.chatthread.interfaces.OnChatThreadRoleResultCallback;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadPresenter;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadPresenterImpl;
import io.agora.chat.uikit.chatthread.presenter.IChatThreadView;

public class EaseChatThreadFragment extends EaseChatFragment implements IChatThreadView, OnRecallMessageResultListener {
    protected String parentMsgId;
    protected ChatThread mThread;
    protected String parentId;
    protected EaseChatThreadRole threadRole = EaseChatThreadRole.UNKNOWN;

    protected EaseChatThreadPresenter mPresenter;
    private EaseChatThreadHeaderAdapter headerAdapter;
    private List<ChatMessage> data = new ArrayList<>();
    private OnJoinChatThreadResultListener joinThreadResultListener;
    private OnChatThreadRoleResultCallback resultCallback;
    private MessageListener messageListener;
    private boolean hideHeader;
    private EaseChatThreadParentMsgViewProvider parentMsgViewProvider;

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
            parentId = bundle.getString(Constant.KEY_PARENT_ID);
            hideHeader = bundle.getBoolean(Constant.KEY_HIDE_HEADER, false);
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
        if(hideHeader) {
            return;
        }
        headerAdapter = new EaseChatThreadHeaderAdapter(parentMsgViewProvider);
        chatLayout.getChatMessageListLayout().addHeaderAdapter(headerAdapter);
    }

    private void setThreadInfo(ChatThread thread) {
        if(thread == null) {
            return;
        }
        if(titleBar != null) {
            titleBar.setTitle(thread.getChatThreadName());
        }
        if(headerAdapter != null) {
            headerAdapter.setThreadInfo(thread);
        }
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, ChatMessage message) {
        super.onPreMenu(helper, message);
        // Chat Thread is load from server, not need to delete from local
        helper.findItemVisible(R.id.action_chat_delete, false);
        // Chat Thread can not reply again
        helper.findItemVisible(R.id.action_chat_reply, false);
        if(!message.isChatThreadMessage() || message.direct() == ChatMessage.Direct.RECEIVE) {
            helper.findItemVisible(R.id.action_chat_recall, false);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        messageListener = new MessageListener();
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
        chatLayout.setOnRecallMessageResultListener(this);
    }

    @Override
    public void onChatThreadEvent(int event, String target, List<String> usernames) {
        super.onChatThreadEvent(event, target, usernames);
        if((event == THREAD_DESTROY || event == THREAD_LEAVE) && TextUtils.equals(target, conversationId)) {
            mContext.finish();
        }
    }

    @Override
    public void recallSuccess(ChatMessage originalMessage, ChatMessage notification) {
        if(chatLayout != null) {
            chatLayout.getChatMessageListLayout().removeMessage(originalMessage);
        }
    }

    @Override
    public void recallFail(int code, String errorMsg) {

    }

    private class MessageListener extends EaseMessageListener {

        @Override
        public void onMessageReceived(List<ChatMessage> messages) {
            for (ChatMessage message:messages) {
                // Determine if there is new group information, and display a red dot if there is new group information
                if (message.getChatType() == ChatMessage.ChatType.GroupChat || message.getChatType() == ChatMessage.ChatType.ChatRoom) {
                    if(TextUtils.equals(parentId, message.conversationId())) {
                        runOnUiThread(()-> {
                            if(titleBar != null) {
                                titleBar.setUnreadIconVisible(true);
                            }
                        });
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
        if(TextUtils.equals(event.getChatThread().getChatThreadId(), conversationId)) {
            runOnUiThread(()->{
                chatLayout.getChatMessageListLayout().refreshMessages();
                if(headerAdapter != null) {
                    headerAdapter.updateThreadName(event.getChatThread().getChatThreadName());
                }
                if(mContext != null && !mContext.isFinishing() && titleBar != null) {
                    titleBar.setTitle(event.getChatThread().getChatThreadName());
                }
            });
        }
    }

    @Override
    public void onChatThreadDestroyed(ChatThreadEvent event) {
        exitThreadChat(event.getChatThread().getChatThreadId());
    }

    @Override
    public void onChatThreadUserRemoved(ChatThreadEvent event) {
        exitThreadChat(event.getChatThread().getChatThreadId());
    }

    private void exitThreadChat(String threadId) {
        if(TextUtils.equals(threadId, conversationId)) {
            mContext.finish();
        }
    }

    @Override
    public void initData() {
        initChatLayout();
        setThreadInfo(mThread);
        if(headerAdapter != null) {
            headerAdapter.setData(data);
        }
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
        runOnUiThread(()-> {
            loadData();
            isMessageInit = true;
        });
    }

    @Override
    public void OnJoinThreadFail(int error, String errorMsg) {
        if(error == Error.USER_ALREADY_EXIST) {
            // If has joined the chat thread, make the role to member
            if(threadRole == EaseChatThreadRole.UNKNOWN) {
                threadRole = EaseChatThreadRole.MEMBER;
            }
            mPresenter.getThreadInfo(conversationId);
            runOnUiThread(()-> {
                loadData();
                isMessageInit = true;
            });
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

    private void setParentMsgViewProvider(EaseChatThreadParentMsgViewProvider parentMsgViewProvider) {
        this.parentMsgViewProvider = parentMsgViewProvider;
    }

    private void setThreadPresenter(EaseChatThreadPresenter presenter) {
        this.mPresenter = presenter;
    }

    private void setOnJoinThreadResultListener(OnJoinChatThreadResultListener listener) {
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
            if(TextUtils.equals(thread.getOwner(), ChatClient.getInstance().getCurrentUser())) {
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

    public static class Builder extends EaseChatFragment.Builder {
        private EaseChatThreadPresenter presenter;
        private OnJoinChatThreadResultListener listener;
        private OnChatThreadRoleResultCallback resultCallback;
        private EaseChatThreadParentMsgViewProvider parentMsgViewProvider;

        /**
         * Constructor
         * @param parentMsgId    Usually is the group message ID
         * @param conversationId Agora Chat ID
         */
        public Builder(String parentMsgId, String conversationId, String parentId) {
            super(conversationId, EaseChatType.GROUP_CHAT);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
            this.bundle.putString(Constant.KEY_PARENT_ID, parentId);
            this.bundle.putBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, true);
        }

        /**
         * Constructor
         * @param parentMsgId       Usually is the group ID
         * @param conversationId    Agora Chat ID
         * @param historyMsgId
         */
        public Builder(String parentMsgId, String conversationId, String parentId, String historyMsgId) {
            super(conversationId, EaseChatType.GROUP_CHAT, historyMsgId);
            this.bundle.putString(Constant.KEY_PARENT_MESSAGE_ID, parentMsgId);
            this.bundle.putString(Constant.KEY_PARENT_ID, parentId);
            this.bundle.putBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, true);
        }

        /**
         * Set header adapter hidden.
         * @param hideHeader
         * @return
         */
        public Builder hideHeader(boolean hideHeader) {
            this.bundle.putBoolean(Constant.KEY_HIDE_HEADER, hideHeader);
            return this;
        }

        /**
         * Set thread parent message view provider
         * @param provider
         * @return
         */
        public Builder setThreadParentMsgViewProvider(EaseChatThreadParentMsgViewProvider provider) {
            this.parentMsgViewProvider = provider;
            return this;
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
        public Builder setOnJoinThreadResultListener(OnJoinChatThreadResultListener listener) {
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
                ((EaseChatThreadFragment)this.customFragment).setParentMsgViewProvider(this.parentMsgViewProvider);
                ((EaseChatThreadFragment)this.customFragment).setThreadPresenter(this.presenter);
                ((EaseChatThreadFragment)this.customFragment).setOnJoinThreadResultListener(this.listener);
                ((EaseChatThreadFragment)this.customFragment).setOnThreadRoleResultCallback(this.resultCallback);
            }

            return super.build();
        }
    }

    private static final class Constant {
        public static final String KEY_PARENT_MESSAGE_ID = "key_parent_message_id";
        public static final String KEY_PARENT_ID = "key_parent_id";
        public static final String KEY_THREAD_MESSAGE_FLAG = "key_thread_message_flag";
        public static final String KEY_HIDE_HEADER = "key_hide_header";
    }

}
