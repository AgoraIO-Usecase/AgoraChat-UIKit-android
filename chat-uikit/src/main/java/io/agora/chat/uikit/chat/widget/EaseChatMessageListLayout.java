package io.agora.chat.uikit.chat.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.interfaces.IChatMessageItemSet;
import io.agora.chat.uikit.chat.interfaces.IChatMessageListLayout;
import io.agora.chat.uikit.chat.interfaces.IRecyclerViewHandle;
import io.agora.chat.uikit.chat.model.EaseChatItemStyleHelper;
import io.agora.chat.uikit.chat.presenter.EaseChatMessagePresenter;
import io.agora.chat.uikit.chat.presenter.EaseChatMessagePresenterImpl;
import io.agora.chat.uikit.chat.presenter.IChatMessageListView;
import io.agora.chat.uikit.interfaces.MessageListItemClickListener;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.manager.EaseThreadManager;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;


public class EaseChatMessageListLayout extends RelativeLayout implements IChatMessageListView, IRecyclerViewHandle
                                                                        , IChatMessageItemSet, IChatMessageListLayout {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String TAG = EaseChatMessageListLayout.class.getSimpleName();
    private EaseChatMessagePresenter presenter;
    private EaseMessageAdapter messageAdapter;
    private ConcatAdapter baseAdapter;
    /**
     * There are currently three ways to load data, regular mode (loaded from local)
     * , roaming mode, and query history message mode (searching through the database)
     */
    private LoadDataType loadDataType;
    /**
     * Message id, this parameter is generally used when searching for historical messages
     */
    private String msgId;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private RecyclerView rvList;
    private SwipeRefreshLayout srlRefresh;
    private LinearLayoutManager layoutManager;
    private Conversation conversation;
    /**
     * Conversation type, including single chat, group chat and chat room
     */
    private Conversation.ConversationType conType;
    /**
     * The other chat id
     */
    private String username;
    private boolean canUseRefresh = true;
    private LoadMoreStatus loadMoreStatus;
    private OnMessageTouchListener messageTouchListener;
    private OnChatErrorListener errorListener;
    /**
     * The height of the last control
     */
    private int recyclerViewLastHeight;
    private MessageListItemClickListener messageListItemClickListener;
    private EaseChatItemStyleHelper chatSetHelper;
    private String messageCursor;
    /**
     * When is thread conversation, whether thread message list has reached the latest message
     */
    private boolean isReachedLatestThreadMessage = false;

    public EaseChatMessageListLayout(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatMessageListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatMessageListLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        EaseChatItemStyleHelper.getInstance().clear(context);
        chatSetHelper = EaseChatItemStyleHelper.getInstance();
        chatSetHelper.setCurrentContext(context);
        presenter = new EaseChatMessagePresenterImpl();
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
        initAttrs(context, attrs);
        initViews();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageListLayout);
            float textSize = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_size
                    , 0);
            chatSetHelper.setTextSize(context, (int) textSize);
            int textColorRes = a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color, -1);
            int textColor;
            if(textColorRes != -1) {
                textColor = ContextCompat.getColor(context, textColorRes);
            }else {
                textColor = a.getColor(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color, 0);
            }
            chatSetHelper.setTextColor(context, textColor);

            float itemMinHeight = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_min_height, 0);
            chatSetHelper.setItemMinHeight(context, (int) itemMinHeight);

            float timeTextSize = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_size, 0);
            chatSetHelper.setTimeTextSize(context, (int) timeTextSize);
            int timeTextColorRes = a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color, -1);
            int timeTextColor;
            if(timeTextColorRes != -1) {
                timeTextColor = ContextCompat.getColor(context, textColorRes);
            }else {
                timeTextColor = a.getColor(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color, 0);
            }
            chatSetHelper.setTimeTextColor(context, timeTextColor);
            chatSetHelper.setTimeBgDrawable(context, a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_background));

            Drawable avatarDefaultDrawable = a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_default_src);
            //float avatarSize = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_size, 0);
            int shapeType = a.getInteger(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_shape_type, 0);
            //float avatarRadius = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_radius, 0);
            //float borderWidth = a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_border_width, 0);
            //int borderColorRes = a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_border_color, -1);
//            int borderColor;
//            if(borderColorRes != -1) {
//                borderColor = ContextCompat.getColor(context, borderColorRes);
//            }else {
//                borderColor = a.getColor(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_border_color, Color.TRANSPARENT);
//            }
            chatSetHelper.setAvatarDefaultSrc(context, avatarDefaultDrawable);
//            chatSetHelper.setAvatarSize(avatarSize);
            chatSetHelper.setShapeType(context, shapeType);
//            chatSetHelper.setAvatarRadius(avatarRadius);
//            chatSetHelper.setBorderWidth(borderWidth);
//            chatSetHelper.setBorderColor(borderColor);

            chatSetHelper.setReceiverBgDrawable(context, a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_receiver_background));
            chatSetHelper.setSenderBgDrawable(context, a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_sender_background));

            //chatSetHelper.setShowAvatar(a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_avatar, true));
            chatSetHelper.setShowNickname(context, a.getBoolean(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_nickname, false));

            chatSetHelper.setItemShowType(context, a.getInteger(R.styleable.EaseChatMessageListLayout_ease_chat_item_show_type, 0));

            a.recycle();
        }
    }

    private void initViews() {
        presenter.attachView(this);

        rvList = findViewById(R.id.message_list);
        srlRefresh = findViewById(R.id.srl_refresh);

        srlRefresh.setEnabled(canUseRefresh);

        layoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(layoutManager);

        baseAdapter = new ConcatAdapter();
        messageAdapter = new EaseMessageAdapter();
        baseAdapter.addAdapter(messageAdapter);
        rvList.setAdapter(baseAdapter);

        initListener();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getContext() != null && getContext() instanceof AppCompatActivity) {
            ((AppCompatActivity) getContext()).getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                EMLog.e(TAG, "event: "+event.name());
                if(event == Lifecycle.Event.ON_PAUSE && ((AppCompatActivity) getContext()).isFinishing()) {
                    if(conversation != null) {
                        conversation.markAllMessagesAsRead();
                    }
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EaseChatItemStyleHelper.getInstance().clear(getContext());
    }

    public void init(LoadDataType loadDataType, String username, EaseChatType chatType) {
        this.username = username;
        this.loadDataType = loadDataType;
        this.conType = EaseUtils.getConversationType(chatType);
        conversation = ChatClient.getInstance().chatManager().getConversation(username, conType, true, this.loadDataType == LoadDataType.THREAD);
        presenter.setupWithConversation(conversation);
        // If it is thread conversation, should not use refresh animator
        if(this.loadDataType == LoadDataType.THREAD) {
            srlRefresh.setEnabled(false);
        }
    }

    public void init(String username, EaseChatType chatType) {
        init(LoadDataType.LOCAL, username, chatType);
    }

    public void loadDefaultData() {
        loadData(pageSize, null);
    }

    public void loadData(String msgId) {
        loadData(pageSize, msgId);
    }

    public void loadData(int pageSize, String msgId) {
        this.pageSize = pageSize;
        this.msgId = msgId;
        checkConType();
    }

    private void checkConType() {
        if(isChatRoomCon()) {
            presenter.joinChatRoom(username);
        }else {
            loadData();
        }
    }

    private void loadData() {
        if(!isSingleChat()) {
            chatSetHelper.setShowNickname(getContext(), true);
        }
        conversation.markAllMessagesAsRead();
        if(loadDataType == LoadDataType.ROAM) {
            presenter.loadServerMessages(pageSize);
        }else if(loadDataType == LoadDataType.HISTORY) {
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, Conversation.SearchDirection.DOWN);
        }else if(loadDataType == LoadDataType.THREAD) {
            presenter.loadServerMessages(pageSize, Conversation.SearchDirection.DOWN);
        }else {
            presenter.loadLocalMessages(pageSize);
        }
    }

    public void setMessageAdapter(EaseMessageAdapter adapter) {
        if(this.messageAdapter != null && baseAdapter.getAdapters().contains(this.messageAdapter)) {
            int index = baseAdapter.getAdapters().indexOf(this.messageAdapter);
            baseAdapter.removeAdapter(this.messageAdapter);
            baseAdapter.addAdapter(index, adapter);
        }else {
            baseAdapter.addAdapter(adapter);
        }
        this.messageAdapter = adapter;
        setAdapterListener();
    }

    public void loadMorePreviousData() {
        String msgId = getListFirstMessageId();
        if(loadDataType == LoadDataType.ROAM) {
            presenter.loadMoreServerMessages(msgId, pageSize);
        }else if(loadDataType == LoadDataType.HISTORY) {
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, Conversation.SearchDirection.UP);
        }else {
            presenter.loadMoreLocalMessages(msgId, pageSize);
        }
    }

    public void onRefreshData() {
        if(loadDataType != LoadDataType.THREAD) {
            loadMorePreviousData();
        }
    }

    public void loadMoreHistoryData() {
        String msgId = getListLastMessageId();
        if(loadDataType == LoadDataType.HISTORY) {
            loadMoreStatus = LoadMoreStatus.HAS_MORE;
            presenter.loadMoreLocalHistoryMessages(msgId, pageSize, Conversation.SearchDirection.DOWN);
        }
    }

    public void loadMoreData() {
        if(loadDataType == LoadDataType.HISTORY) {
            loadMoreHistoryData();
        }else if(loadDataType == LoadDataType.THREAD) {
            loadMoreThreadMessages();
        }
    }

    public void loadMoreThreadMessages() {
        presenter.loadMoreServerMessages(messageCursor, pageSize, Conversation.SearchDirection.DOWN);
    }

    private String getListFirstMessageId() {
        ChatMessage message = null;
        try {
            message = messageAdapter.getData().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message == null ? null : message.getMsgId();
    }

    private String getListLastMessageId() {
        ChatMessage message = null;
        try {
            message = messageAdapter.getData().get(messageAdapter.getData().size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message == null ? null : message.getMsgId();
    }

    public boolean isChatRoomCon() {
        return conType == Conversation.ConversationType.ChatRoom && loadDataType != LoadDataType.THREAD;
    }

    public boolean isGroupChat() {
        return conType == Conversation.ConversationType.GroupChat && loadDataType != LoadDataType.THREAD;
    }

    private boolean isSingleChat() {
        return conType == Conversation.ConversationType.Chat;
    }

    private void initListener() {
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!rvList.canScrollVertically(1)) {
                        if(messageTouchListener != null) {
                            messageTouchListener.onReachBottom();
                        }
                    }
                   if(loadMoreStatus == LoadMoreStatus.HAS_MORE
                           && layoutManager.findLastVisibleItemPosition() != 0
                           && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() -1) {
                       loadMoreData();
                   }
                }else {
                    //if recyclerView not idle should hide keyboard
                    if(messageTouchListener != null) {
                        messageTouchListener.onViewDragging();
                    }
                }
            }
        });

        rvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = rvList.getHeight();
                if(recyclerViewLastHeight == 0) {
                    recyclerViewLastHeight = height;
                }
                if(recyclerViewLastHeight != height) {
                    if(messageAdapter.getData() != null && !messageAdapter.getData().isEmpty()) {
                        post(()-> smoothSeekToPosition(messageAdapter.getData().size() - 1));
                    }
                }
                recyclerViewLastHeight = height;
            }
        });

        setAdapterListener();
    }

    private void setAdapterListener() {
        messageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(messageTouchListener != null) {
                    messageTouchListener.onTouchItemOutside(view, position);
                }
            }
        });
        messageAdapter.setListItemClickListener(new MessageListItemClickListener() {
            @Override
            public boolean onBubbleClick(ChatMessage message) {
                if(messageListItemClickListener != null) {
                    return messageListItemClickListener.onBubbleClick(message);
                }
                return false;
            }

            @Override
            public boolean onResendClick(ChatMessage message) {
                if(messageListItemClickListener != null) {
                    return messageListItemClickListener.onResendClick(message);
                }
                return false;
            }

            @Override
            public boolean onBubbleLongClick(View v, ChatMessage message) {
                if(messageListItemClickListener != null) {
                    return messageListItemClickListener.onBubbleLongClick(v, message);
                }
                return false;
            }

            @Override
            public void onUserAvatarClick(String username) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onUserAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onUserAvatarLongClick(username);
                }
            }

            @Override
            public boolean onThreadClick(String messageId, String threadId) {
                if(messageListItemClickListener != null) {
                    return messageListItemClickListener.onThreadClick(messageId, threadId);
                }
                return false;
            }

            @Override
            public boolean onThreadLongClick(View v, String messageId, String threadId) {
                if(messageListItemClickListener != null) {
                    return messageListItemClickListener.onThreadLongClick(v, messageId, threadId);
                }
                return false;
            }

            @Override
            public void onMessageCreate(ChatMessage message) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onMessageCreate(message);
                }
            }

            @Override
            public void onMessageSuccess(ChatMessage message) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onMessageSuccess(message);
                }
            }

            @Override
            public void onMessageError(ChatMessage message, int code, String error) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onMessageError(message, code, error);
                }
            }

            @Override
            public void onMessageInProgress(ChatMessage message, int progress) {
                if(messageListItemClickListener != null) {
                    messageListItemClickListener.onMessageInProgress(message, progress);
                }
            }

            @Override
            public void onRemoveReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener.onRemoveReaction(message, reactionEntity);
                }
            }

            @Override
            public void onAddReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener.onAddReaction(message, reactionEntity);
                }
            }
        });
    }

    private void finishRefresh() {
        if(presenter.isActive()) {
            runOnUi(() -> {
                if(srlRefresh != null) {
                    srlRefresh.setRefreshing(false);
                }
            });
        }
    }

    private void notifyDataSetChanged() {
        messageAdapter.notifyDataSetChanged();
    }

    public void setData(List<ChatMessage> data) {
        messageAdapter.setData(data);
    }

    public void addData(List<ChatMessage> data) {
        messageAdapter.addData(data);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public Conversation getCurrentConversation() {
        return conversation;
    }

    @Override
    public void joinChatRoomSuccess(ChatRoom value) {
        loadData();
    }

    @Override
    public void joinChatRoomFail(int error, String errorMsg) {
        if(presenter.isActive()) {
            runOnUi(() -> {
                if(errorListener != null) {
                    errorListener.onChatError(error, errorMsg);
                }
            });
        }
    }

    @Override
    public void loadMsgFail(int error, String message) {
        finishRefresh();
        if(errorListener != null) {
            errorListener.onChatError(error, message);
        }
    }

    @Override
    public void loadLocalMsgSuccess(List<ChatMessage> data) {
        refreshToLatest();
    }

    @Override
    public void loadNoLocalMsg() {

    }

    @Override
    public void loadMoreLocalMsgSuccess(List<ChatMessage> data) {
        finishRefresh();
        presenter.refreshCurrentConversation();
        post(()->smoothSeekToPosition(data.size() - 1));
    }

    @Override
    public void loadNoMoreLocalMsg() {
        finishRefresh();
    }

    @Override
    public void loadMoreLocalHistoryMsgSuccess(List<ChatMessage> data, Conversation.SearchDirection direction) {
        if(direction == Conversation.SearchDirection.UP) {
            finishRefresh();
            messageAdapter.addData(0, data);
        }else {
            messageAdapter.addData(data);
            if(data.size() >= pageSize) {
                loadMoreStatus = LoadMoreStatus.HAS_MORE;
            }else {
                loadMoreStatus = LoadMoreStatus.NO_MORE_DATA;
            }
        }
    }

    @Override
    public void loadNoMoreLocalHistoryMsg() {
        finishRefresh();
    }

    @Override
    public void loadServerMsgSuccess(List<ChatMessage> data, String cursor) {
        messageCursor = cursor;
        if(loadDataType == LoadDataType.THREAD) {
            if(data.size() >= pageSize || !TextUtils.isEmpty(cursor)) {
                loadMoreStatus = LoadMoreStatus.HAS_MORE;
            }else {
                loadMoreStatus = LoadMoreStatus.NO_MORE_DATA;
            }
            presenter.refreshCurrentConversation();
        }else {
            presenter.refreshToLatest();
        }
    }

    @Override
    public void loadMoreServerMsgSuccess(List<ChatMessage> data, String cursor) {
        messageCursor = cursor;
        finishRefresh();
        presenter.refreshCurrentConversation();
        if(loadDataType == LoadDataType.THREAD) {
            if(data.size() >= pageSize || !TextUtils.isEmpty(cursor)) {
                loadMoreStatus = LoadMoreStatus.HAS_MORE;
            }else {
                loadMoreStatus = LoadMoreStatus.NO_MORE_DATA;
            }
            //post(()-> smoothSeekToPosition(messageAdapter.getData().size() - data.size()));
        }else {
            post(()-> smoothSeekToPosition(data.size() - 1));
        }
    }

    @Override
    public void refreshCurrentConSuccess(List<ChatMessage> data, boolean toLatest) {
        messageAdapter.setData(data);
        if(toLatest) {
            seekToPosition(data.size() - 1);
        }
    }

    @Override
    public void insertMessageToLast(ChatMessage message) {
        messageAdapter.addData(message);
        seekToPosition(messageAdapter.getData().size() - 1);
    }

    @Override
    public void reachedLatestThreadMessage() {
        this.isReachedLatestThreadMessage = true;
    }

    public boolean isReachedLatestThreadMessage() {
        return isReachedLatestThreadMessage;
    }

    @Override
    public void canUseDefaultRefresh(boolean canUseRefresh) {
        this.canUseRefresh = canUseRefresh;
        srlRefresh.setEnabled(canUseRefresh);
    }

    @Override
    public void refreshMessages() {
        try {
            presenter.refreshCurrentConversation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshToLatest() {
        presenter.refreshToLatest();
    }

    @Override
    public void refreshMessage(ChatMessage message) {
        int position = messageAdapter.getData().lastIndexOf(message);
        if(position != -1) {
            runOnUi(()-> messageAdapter.notifyItemChanged(position));
        }
    }

    @Override
    public void refreshMessage(String messageId) {
        if(TextUtils.isEmpty(messageId)) {
            return;
        }
        ChatMessage message = ChatClient.getInstance().chatManager().getMessage(messageId);
        if(message != null) {
            int position = messageAdapter.getData().lastIndexOf(message);
            if(position != -1) {
                runOnUi(()-> messageAdapter.notifyItemChanged(position));
            }
        }
    }

    @Override
    public void removeMessage(ChatMessage message) {
        if(message == null || messageAdapter.getData() == null) {
            return;
        }
        conversation.removeMessage(message.getMsgId());
        runOnUi(()-> {
            if(presenter.isActive()) {
                List<ChatMessage> messages = messageAdapter.getData();
                int position = messages.lastIndexOf(message);
                if(position != -1) {
                    messages.remove(position);
                    messageAdapter.notifyItemRemoved(position);
                    messageAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public void moveToPosition(int position) {
        seekToPosition(position);
    }

    @Override
    public void showNickname(boolean showNickname) {
        chatSetHelper.setShowNickname(getContext(), showNickname);
        notifyDataSetChanged();
    }

    @Override
    public void setItemSenderBackground(Drawable bgDrawable) {
        chatSetHelper.setSenderBgDrawable(getContext(), bgDrawable);
        notifyDataSetChanged();
    }

    @Override
    public void setItemReceiverBackground(Drawable bgDrawable) {
        chatSetHelper.setReceiverBgDrawable(getContext(), bgDrawable);
        notifyDataSetChanged();
    }

    @Override
    public void setItemTextSize(int textSize) {
        chatSetHelper.setTextSize(getContext(), textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setItemTextColor(int textColor) {
        chatSetHelper.setTextColor(getContext(), textColor);
        notifyDataSetChanged();
    }

//    @Override
//    public void setItemMinHeight(int height) {
//        chatSetHelper.setItemMinHeight(height);
//        notifyDataSetChanged();
//    }

    @Override
    public void setTimeTextSize(int textSize) {
        chatSetHelper.setTimeTextSize(getContext(), textSize);
        notifyDataSetChanged();
    }

    @Override
    public void setTimeTextColor(int textColor) {
        chatSetHelper.setTimeTextColor(getContext(), textColor);
        notifyDataSetChanged();
    }

    @Override
    public void setTimeBackground(Drawable bgDrawable) {
        chatSetHelper.setTimeBgDrawable(getContext(), bgDrawable);
        notifyDataSetChanged();
    }

    @Override
    public void setItemShowType(ShowType type) {
        if(!isSingleChat()) {
            chatSetHelper.setItemShowType(getContext(), type.ordinal());
            notifyDataSetChanged();
        }
    }

    @Override
    public void hideChatReceiveAvatar(boolean hide) {
        chatSetHelper.setHideReceiveAvatar(getContext(), hide);
        notifyDataSetChanged();
    }

    @Override
    public void hideChatSendAvatar(boolean hide) {
        chatSetHelper.setHideSendAvatar(getContext(), hide);
        notifyDataSetChanged();
    }

    @Override
    public void setAvatarDefaultSrc(Drawable src) {
        chatSetHelper.setAvatarDefaultSrc(getContext(), src);
        notifyDataSetChanged();
    }

//    @Override
//    public void setAvatarSize(float avatarSize) {
//        chatSetHelper.setAvatarSize(avatarSize);
//        notifyDataSetChanged();
//    }

    @Override
    public void setAvatarShapeType(int shapeType) {
        chatSetHelper.setShapeType(getContext(), shapeType);
        notifyDataSetChanged();
    }

//    @Override
//    public void setAvatarRadius(int radius) {
//        chatSetHelper.setAvatarRadius(radius);
//        notifyDataSetChanged();
//    }

//    @Override
//    public void setAvatarBorderWidth(int borderWidth) {
//        chatSetHelper.setBorderWidth(borderWidth);
//        notifyDataSetChanged();
//    }

//    @Override
//    public void setAvatarBorderColor(int borderColor) {
//        chatSetHelper.setBorderColor(borderColor);
//        notifyDataSetChanged();
//    }

    @Override
    public void addHeaderAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.addAdapter(0, adapter);
    }

    @Override
    public void addFooterAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.addAdapter(adapter);
    }

    @Override
    public void removeAdapter(RecyclerView.Adapter adapter) {
        baseAdapter.removeAdapter(adapter);
    }

    @Override
    public void addRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvList.addItemDecoration(decor);
    }

    @Override
    public void removeRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        rvList.removeItemDecoration(decor);
    }

    /**
     * Determine if there is a new message
     * The judgment basis is: whether the timestamp of the latest piece of data in the database is greater than the timestamp of the latest piece of data on the page
     * @return
     */
    public boolean haveNewMessages() {
        if(messageAdapter == null || messageAdapter.getData() == null || messageAdapter.getData().isEmpty()
                || conversation == null || conversation.getLastMessage() == null) {
            return false;
        }
        return conversation.getLastMessage().getMsgTime() > messageAdapter.getData().get(messageAdapter.getData().size() - 1).getMsgTime();
    }

    private void seekToPosition(int position) {
        if(presenter.isDestroy() || rvList == null) {
            return;
        }
        if(position < 0) {
            position = 0;
        }
        RecyclerView.LayoutManager manager = rvList.getLayoutManager();
        if(manager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) manager).scrollToPositionWithOffset(position, 0);
        }
    }

    private void smoothSeekToPosition(int position) {
        if(presenter.isDestroy() || rvList == null) {
            return;
        }
        if(position < 0) {
            position = 0;
        }
        RecyclerView.LayoutManager manager = rvList.getLayoutManager();
        if(manager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) manager).scrollToPositionWithOffset(position, 0);
            //setMoveAnimation(manager, position);
        }
    }

    private void setMoveAnimation(RecyclerView.LayoutManager manager, int position) {
        int prePosition;
        if(position > 0) {
            prePosition = position - 1;
        }else {
            prePosition = position;
        }
        View view = manager.findViewByPosition(0);
        int height;
        if(view != null) {
            height = view.getHeight();
        }else {
            height = 200;
        }
        ValueAnimator animator = ValueAnimator.ofInt(-height, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ((LinearLayoutManager)manager).scrollToPositionWithOffset(prePosition, value);
            }
        });
        animator.setDuration(800);
        animator.start();
    }

    @Override
    public void setPresenter(EaseChatMessagePresenter presenter) {
        this.presenter = presenter;
        if(getContext() instanceof AppCompatActivity) {
            ((AppCompatActivity) getContext()).getLifecycle().addObserver(presenter);
        }
        this.presenter.attachView(this);
        this.presenter.setupWithConversation(conversation);
    }

    @Override
    public EaseMessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOnMessageTouchListener(OnMessageTouchListener listener) {
        this.messageTouchListener = listener;
    }

    @Override
    public void setOnChatErrorListener(OnChatErrorListener listener) {
        this.errorListener = listener;
    }

    @Override
    public void setMessageListItemClickListener(MessageListItemClickListener listener) {
        this.messageListItemClickListener = listener;
    }

    public static boolean isVisibleBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
            return true;
        } else {
            return false;
        }
    }

    public void runOnUi(Runnable runnable) {
        EaseThreadManager.getInstance().runOnMainThread(runnable);
    }

    /**
     * Set current conversation's send message
     * @param message
     */
    public void setSendOrReceiveMessage(ChatMessage message) {
        if(presenter != null) {
            presenter.setSendOrReceiveMessage(message);
        }
    }

    public interface OnMessageTouchListener {
        /**
         * touch event
         * @param v
         * @param position
         */
        void onTouchItemOutside(View v, int position);

        /**
         * The control is being dragged
         */
        void onViewDragging();

        /**
         * RecyclerView scroll to bottom
         */
        void onReachBottom();
    }

    public interface OnChatErrorListener {
        /**
         * Wrong message in chat
         * @param code
         * @param errorMsg
         */
        void onChatError(int code, String errorMsg);
    }

    /**
     * Three data loading modes, local is to load from the local database,
     * Roam is to enable message roaming, and History is to search for local messages
     */
    public enum LoadDataType {
        /**
         * Get message from local DB
         */
        LOCAL,
        /**
         * Get message from chat server
         */
        ROAM,
        /**
         * Get thread message from server and show the oldest message first
         */
        THREAD,
        /**
         * Get historical messages through messageId, which can be loaded up or down for messages except thread messages
         */
        HISTORY
    }

    public enum LoadMoreStatus {
        IS_LOADING, HAS_MORE, NO_MORE_DATA
    }

    /**
     * Message list show type
     */
    public enum ShowType {
        // Receiver is on the left, sender is on the right
        LEFT_RIGHT,
        // Receiver and sender are on the left
        ALL_LEFT/*, RIGHT*/
    }
}

