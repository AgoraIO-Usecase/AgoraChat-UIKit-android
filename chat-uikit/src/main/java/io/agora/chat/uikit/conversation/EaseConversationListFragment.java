package io.agora.chat.uikit.conversation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.MessageReactionChange;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.conversation.adapter.EaseConversationListAdapter;
import io.agora.chat.uikit.conversation.interfaces.OnConItemClickListener;
import io.agora.chat.uikit.conversation.interfaces.OnConversationChangeListener;
import io.agora.chat.uikit.conversation.interfaces.OnConversationLoadListener;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.interfaces.EaseChatRoomListener;
import io.agora.chat.uikit.interfaces.EaseGroupListener;
import io.agora.chat.uikit.interfaces.EaseMessageListener;
import io.agora.chat.uikit.interfaces.EaseMultiDeviceListener;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.menu.EasePopupMenuHelper;
import io.agora.chat.uikit.menu.OnPopupMenuItemClickListener;
import io.agora.chat.uikit.menu.OnPopupMenuPreShowListener;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.util.EMLog;

public class EaseConversationListFragment extends EaseBaseFragment implements OnItemClickListener, OnPopupMenuItemClickListener
        , OnPopupMenuPreShowListener, SwipeRefreshLayout.OnRefreshListener, OnConversationLoadListener {
    private static final String TAG = EaseConversationListFragment.class.getSimpleName();
    public LinearLayout llRoot;
    public EaseTitleBar titleBar;
    public EaseConversationListLayout conversationListLayout;
    public SwipeRefreshLayout srlRefresh;
    private EaseConversationListAdapter adapter;
    private OnConItemClickListener<EaseConversationInfo> itemClickListener;
    private EaseTitleBar.OnBackPressListener backPressListener;
    private OnConversationChangeListener conversationListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
        initData();
    }

    public int getLayoutId() {
        return R.layout.ease_fragment_conversations;
    }

    public void initView(Bundle savedInstanceState) {
        llRoot = findViewById(R.id.ll_root);
        titleBar = findViewById(R.id.title_chats);
        srlRefresh = findViewById(R.id.srl_refresh);
        conversationListLayout = findViewById(R.id.list_conversation);
        if(this.adapter != null) {
            conversationListLayout.setListAdapter(this.adapter);
        }
        conversationListLayout.init();

        // Set custom settings from fragment
        Bundle bundle = getArguments();
        if(bundle != null) {
            boolean useHeader = bundle.getBoolean(Constant.KEY_USE_TITLE, false);
            titleBar.setVisibility(useHeader ? View.VISIBLE : View.GONE);

            String title = bundle.getString(Constant.KEY_SET_TITLE, "");
            if(!TextUtils.isEmpty(title)) {
                titleBar.setTitle(title);
            }

            boolean canBack = bundle.getBoolean(Constant.KEY_ENABLE_BACK, false);
            titleBar.setDisplayHomeAsUpEnabled(canBack);

            titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
                @Override
                public void onBackPress(View view) {
                    mContext.onBackPressed();
                }
            });

            boolean hideUnread = bundle.getBoolean(Constant.KEY_HIDE_UNREAD, false);
            conversationListLayout.hideUnreadDot(hideUnread);

            String position = bundle.getString(Constant.KEY_UNREAD_POSITION, EaseConversationSetStyle.UnreadDotPosition.RIGHT.name());
            conversationListLayout.showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition.valueOf(position));

            String style = bundle.getString(Constant.KEY_UNREAD_STYLE, EaseConversationSetStyle.UnreadStyle.NUM.name());
            conversationListLayout.setUnreadStyle(EaseConversationSetStyle.UnreadStyle.valueOf(style));

            int emptyLayout = bundle.getInt(Constant.KEY_EMPTY_LAYOUT, -1);
            if(emptyLayout != -1) {
                conversationListLayout.getListAdapter().setEmptyView(emptyLayout);
            }
        }

    }

    public void initListener() {
        conversationListLayout.setOnItemClickListener(this);
        conversationListLayout.setOnPopupMenuItemClickListener(this);
        conversationListLayout.setOnPopupMenuPreShowListener(this);
        conversationListLayout.setOnConversationLoadListener(this);
        conversationListLayout.setOnConversationChangeListener(conversationListener);
        srlRefresh.setOnRefreshListener(this);
        titleBar.setOnBackPressListener(this.backPressListener);
        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
        ChatClient.getInstance().groupManager().addGroupChangeListener(groupListener);
        ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
        ChatClient.getInstance().addMultiDeviceListener(multiDeviceListener);
    }

    public void initData() {
        conversationListLayout.loadDefaultData();
    }

    private void setCustomAdapter(EaseConversationListAdapter adapter) {
        this.adapter = adapter;
    }

    private void setItemClickListener(OnConItemClickListener<EaseConversationInfo> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void setOnBackPressListener(EaseTitleBar.OnBackPressListener backPressListener) {
        this.backPressListener = backPressListener;
    }

    private void setConversationChangeListener(OnConversationChangeListener listener) {
        this.conversationListener = listener;
    }

    /**
     * Conversation item click event
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        if(this.itemClickListener != null) {
            EaseConversationInfo info = null;
            if(conversationListLayout != null) {
                info = conversationListLayout.getListAdapter().getItem(position);
            }
            this.itemClickListener.onItemClick(view, info, position);
        }
    }

    /**
     * Session long press menu item click event
     * @param item
     * @param position
     */
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EMLog.i(TAG, "click menu position = "+position);
        return false;
    }

    /**
     * To listen to events before the long-press menu is displayed in the conversation
     * , you can add items to PopupMenu {@link EaseConversationListLayout#addItemMenu(int, int, int, String)},
     * Hide or show items {@link EaseConversationListLayout#findItemVisible(int, boolean)}
     * @param menuHelper
     * @param position
     */
    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {

    }

    @Override
    public void onRefresh() {
        conversationListLayout.loadDefaultData();
    }

    @Override
    public void loadDataFinish(List<EaseConversationInfo> data) {
        finishRefresh();
    }

    @Override
    public void loadDataFail(String message) {
        finishRefresh();
    }

    /**
     * Finish refresh
     */
    public void finishRefresh() {
        if(!mContext.isFinishing() && srlRefresh != null) {
            runOnUiThread(()->srlRefresh.setRefreshing(false));
        }
    }

    public void refreshList() {
        if(conversationListLayout != null) {
            conversationListLayout.refreshData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatClient.getInstance().chatManager().removeMessageListener(messageListener);
        ChatClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        ChatClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        ChatClient.getInstance().removeMultiDeviceListener(multiDeviceListener);
    }

    private EaseGroupListener groupListener = new EaseGroupListener() {
        @Override
        public void onUserRemoved(String groupId, String groupName) {
            refreshList();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            refreshList();
        }
    };

    private EaseMessageListener messageListener = new EaseMessageListener() {
        @Override
        public void onMessageReceived(List<ChatMessage> messages) {
            refreshList();
        }

        @Override
        public void onMessageRecalled(List<ChatMessage> messages) {
            refreshList();
        }

        @Override
        public void onMessageChanged(ChatMessage message, Object change) {
            refreshList();
        }

        @Override
        public void onReactionChanged(List<MessageReactionChange> list) {
        }
    };

    private EaseChatRoomListener chatRoomListener = new EaseChatRoomListener() {
        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            refreshList();
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            refreshList();
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {

        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {

        }
    };

    private EaseMultiDeviceListener multiDeviceListener = new EaseMultiDeviceListener() {
        @Override
        public void onChatThreadEvent(int event, String target, List<String> usernames) {

        }

        @Override
        protected void onContactAllow(String target, String ext) {
            refreshList();
        }

        @Override
        protected void onContactBan(String target, String ext) {
            refreshList();
        }

        @Override
        protected void onContactAccept(String target, String ext) {
            refreshList();
        }

        @Override
        protected void onContactRemove(String target, String ext) {
            refreshList();
        }

        @Override
        protected void onGroupLeave(String target, List<String> usernames) {
            refreshList();
        }

        @Override
        protected void onGroupDestroy(String target, List<String> usernames) {
            refreshList();
        }
    };

    public static class Builder {
        private final Bundle bundle;
        private EaseConversationListFragment customFragment;
        private OnConItemClickListener<EaseConversationInfo> itemClickListener;
        private EaseConversationListAdapter adapter;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private OnConversationChangeListener conversationChangeListener;

        public Builder() {
            this.bundle = new Bundle();
        }

        /**
         * Set custom fragment which should extends EaseConversationListFragment
         * @param fragment
         * @param <T>
         * @return
         */
        public <T extends EaseConversationListFragment> EaseConversationListFragment.Builder setCustomFragment(T fragment) {
            this.customFragment = fragment;
            return this;
        }

        /**
         * Whether to use default titleBar which is {@link EaseTitleBar}
         * @param useTitle
         * @return
         */
        public Builder useHeader(boolean useTitle) {
            this.bundle.putBoolean(Constant.KEY_USE_TITLE, useTitle);
            return this;
        }

        /**
         * Set titleBar's title
         * @param title
         * @return
         */
        public Builder setHeaderTitle(String title) {
            this.bundle.putString(Constant.KEY_SET_TITLE, title);
            return this;
        }

        /**
         * Whether show back icon in titleBar
         * @param canBack
         * @return
         */
        public Builder enableHeaderPressBack(boolean canBack) {
            this.bundle.putBoolean(Constant.KEY_ENABLE_BACK, canBack);
            return this;
        }

        /**
         * If you have set {@link EaseConversationListFragment.Builder#enableHeaderPressBack(boolean)}, you can set the listener
         * @param listener
         * @return
         */
        public Builder setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
            this.backPressListener = listener;
            return this;
        }

        /**
         * Set conversation item click listener
         * @param itemClickListener
         * @return
         */
        public Builder setItemClickListener(OnConItemClickListener<EaseConversationInfo> itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        /**
         * Set conversation change listener, such as conversation was been removed
         * @param listener
         * @return
         */
        public Builder setConversationChangeListener(OnConversationChangeListener listener) {
            this.conversationChangeListener = listener;
            return this;
        }

        /**
         * Whether to show unread icon
         * @param hideUnread
         * @return
         */
        public Builder hideUnread(boolean hideUnread) {
            this.bundle.putBoolean(Constant.KEY_HIDE_UNREAD, hideUnread);
            return this;
        }

        /**
         * Set unread icon position
         * @param position
         * @return
         */
        public Builder setUnreadPosition(EaseConversationSetStyle.UnreadDotPosition position) {
            this.bundle.putString(Constant.KEY_UNREAD_POSITION, position.name());
            return this;
        }

        /**
         * Set unread icon's show style
         * @param style
         * @return
         */
        public Builder setUnreadStyle(EaseConversationSetStyle.UnreadStyle style) {
            this.bundle.putString(Constant.KEY_UNREAD_STYLE, style.name());
            return this;
        }

        /**
         * Set chat list's empty layout if you want replace the default
         * @param emptyLayout
         * @return
         */
        public Builder setEmptyLayout(@LayoutRes int emptyLayout) {
            this.bundle.putInt(Constant.KEY_EMPTY_LAYOUT, emptyLayout);
            return this;
        }

        /**
         * Set custom adapter which should extends EaseConversationListAdapter
         * @param adapter
         * @return
         */
        public Builder setCustomAdapter(EaseConversationListAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public EaseConversationListFragment build() {
            EaseConversationListFragment fragment = this.customFragment != null ? this.customFragment : new EaseConversationListFragment();
            fragment.setArguments(this.bundle);
            fragment.setCustomAdapter(this.adapter);
            fragment.setItemClickListener(this.itemClickListener);
            fragment.setOnBackPressListener(this.backPressListener);
            fragment.setConversationChangeListener(this.conversationChangeListener);
            return fragment;
        }
    }

    private static class Constant {
        public static final String KEY_USE_TITLE = "key_use_title";
        public static final String KEY_SET_TITLE = "key_set_title";
        public static final String KEY_HIDE_UNREAD = "key_hide_unread";
        public static final String KEY_UNREAD_POSITION = "key_unread_position";
        public static final String KEY_UNREAD_STYLE = "key_unread_style";
        public static final String KEY_EMPTY_LAYOUT = "key_empty_layout";
        public static final String KEY_ENABLE_BACK = "key_enable_back";
    }
}

