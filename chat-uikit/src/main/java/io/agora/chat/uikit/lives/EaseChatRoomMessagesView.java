package io.agora.chat.uikit.lives;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatRoom;
import io.agora.chat.Conversation;
import io.agora.chat.CustomMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseImageView;


public class EaseChatRoomMessagesView extends RelativeLayout {
    private Conversation mConversation;
    private Context mContext;

    private RecyclerView mMessageListView;
    private MessageListAdapter mAdapter;
    private EditText mMessageInputEt;
    private ConstraintLayout mMessageInputLayout;
    private TextView mMessageInputTip;
    private RelativeLayout mViewLayout;
    private View mBottomView;
    private TextView mUnreadMessageView;

    private MessageViewListener mMessageViewListener;
    private boolean mMessageStopRefresh;
    private int mNicknameMaxEms;
    private int mNicknameEllipsize;
    private ChatRoom mChatRoom;
    private List<ChatMessage> mChatMessageList;

    public EaseChatRoomMessagesView(Context context) {
        super(context);
        init(context, null);
    }

    public EaseChatRoomMessagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatRoomMessagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.ease_live_chat_room_messages, this);
        mMessageListView = findViewById(R.id.room_message_list);
        mMessageInputEt = findViewById(R.id.message_input_et);
        mMessageInputLayout = findViewById(R.id.message_input_layout);
        mBottomView = findViewById(R.id.bottom_view);
        mViewLayout = findViewById(R.id.view_layout);
        mMessageInputTip = findViewById(R.id.message_input_tip);
        mUnreadMessageView = findViewById(R.id.unread_message_view);

        float inputEditMarginBottom = 0;
        float inputEditMarginEnd = 0;
        float messageListMarginEnd = 0;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatRoomMessagesView);
            inputEditMarginBottom = ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_input_edit_margin_bottom, -1);
            inputEditMarginEnd = ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_input_edit_margin_end, -1);
            messageListMarginEnd = ta.getDimension(R.styleable.EaseChatRoomMessagesView_ease_live_message_list_margin_end, -1);
            mNicknameMaxEms = ta.getInteger(R.styleable.EaseChatRoomMessagesView_ease_live_message_nick_name_max_ems, -1);
            mNicknameEllipsize = ta.getInteger(R.styleable.EaseChatRoomMessagesView_ease_live_message_nick_name_ellipsize, -1);
            ta.recycle();
        }

        RelativeLayout.LayoutParams listParams = (RelativeLayout.LayoutParams) mMessageListView.getLayoutParams();
        listParams.setMarginEnd((int) messageListMarginEnd);
        mMessageListView.setLayoutParams(listParams);
        mUnreadMessageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageStopRefresh = false;
                refreshSelectLast();
            }
        });

        RelativeLayout.LayoutParams tipParams = (RelativeLayout.LayoutParams) mMessageInputTip.getLayoutParams();
        tipParams.setMarginEnd((int) inputEditMarginEnd);
        mMessageInputTip.setLayoutParams(tipParams);
        mMessageInputTip.setText(mContext.getResources().getString(R.string.ease_live_message_input_tip));
        mMessageInputTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputState(true);
            }
        });

        mMessageInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (!TextUtils.isEmpty(mMessageInputEt.getText().toString())) {
                        if (null != mMessageViewListener) {
                            mMessageViewListener.onMessageSend(mMessageInputEt.getText().toString(), false);
                        }
                        mMessageInputEt.setText("");
                    }
                    return true;
                }
                return false;
            }
        });


        int navigationBarHeight = getNavigationBarHeight(mContext);
        if (0 == navigationBarHeight) {
            navigationBarHeight = getNavBarHeight(mContext);
        }
        navigationBarHeight += (int) inputEditMarginBottom;
        RelativeLayout.LayoutParams bottomViewParams = (RelativeLayout.LayoutParams) mBottomView.getLayoutParams();
        bottomViewParams.height = navigationBarHeight;
        mBottomView.setLayoutParams(bottomViewParams);
    }

    public void setShow(boolean show) {
        if (show) {
            mViewLayout.setVisibility(View.VISIBLE);
            refreshSelectLast();
        } else {
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(false);
            }
            mViewLayout.setVisibility(View.GONE);
        }
    }

    public boolean isShowing() {
        return mViewLayout.getVisibility() == VISIBLE;
    }

    public EditText getInputView() {
        return mMessageInputEt;
    }

    public void init(String chatroomId) {
        mChatRoom = ChatClient.getInstance().chatroomManager().getChatRoom(chatroomId);
        mConversation = ChatClient.getInstance().chatManager().getConversation(chatroomId, Conversation.ConversationType.ChatRoom, true);
        mAdapter = new MessageListAdapter();
        mAdapter.hideEmptyView(true);
        mMessageListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageListView.setAdapter(mAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(0, (int) EaseUtils.dip2px(getContext(), 4));
        itemDecoration.setDrawable(drawable);
        mMessageListView.addItemDecoration(itemDecoration);

        mMessageListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setInputState(false);
                return false;
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mMessageStopRefresh = true;
                if (null != mMessageViewListener) {
                    mMessageViewListener.onItemClickListener(mAdapter.getItem(position));
                }
            }
        });
    }

    public void setInputState(boolean state) {
        if (state) {
            mMessageInputTip.setVisibility(INVISIBLE);
            mMessageInputLayout.setVisibility(VISIBLE);
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(true);
            }
            mMessageInputEt.setFocusable(true);
            mMessageInputEt.setFocusableInTouchMode(true);
            mMessageInputEt.requestFocus();
            showInputMethod();
        } else {
            hideInputMethod();
            mMessageInputTip.setVisibility(VISIBLE);
            mMessageInputLayout.setVisibility(INVISIBLE);
            if (null != mMessageViewListener) {
                mMessageViewListener.onHiderBottomBar(false);
            }
            mMessageInputEt.setFocusable(false);
            mMessageInputEt.setFocusableInTouchMode(false);
            mMessageInputEt.clearFocus();
        }
    }

    public void setMessageViewListener(MessageViewListener messageViewListener) {
        this.mMessageViewListener = messageViewListener;
    }

    public void refresh() {
        if (mMessageStopRefresh) {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    if (mConversation.getUnreadMsgCount() > 0) {
                        mUnreadMessageView.setVisibility(VISIBLE);
                        mUnreadMessageView.setText(mContext.getString(R.string.ease_live_unread_message_tip, mConversation.getUnreadMsgCount()));
                    }
                }
            });
        } else {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    mUnreadMessageView.setVisibility(INVISIBLE);
                    updateData();
                }
            });

        }
    }

    public void refreshSelectLast() {
        if (mMessageStopRefresh) {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    if (mConversation.getUnreadMsgCount() > 0) {
                        mUnreadMessageView.setVisibility(VISIBLE);
                        mUnreadMessageView.setText(mContext.getString(R.string.ease_live_unread_message_tip, mConversation.getUnreadMsgCount()));
                    }
                }
            });
        } else {
            mUnreadMessageView.post(new Runnable() {
                @Override
                public void run() {
                    mUnreadMessageView.setVisibility(INVISIBLE);
                    if (mAdapter != null) {
                        updateData();
                        if (mAdapter.getItemCount() > 1) {
                            mMessageListView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                        }
                    }
                }
            });
        }
    }

    public int getNavigationBarHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            return dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void showInputMethod() {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideInputMethod() {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mMessageInputEt.getWindowToken(), 0);
    }

    private void updateData() {
        if (null == mAdapter) {
            return;
        }
        ChatMessage[] messages = mConversation.getAllMessages().toArray(new ChatMessage[0]);
        mConversation.markAllMessagesAsRead();
        if (null == mChatMessageList) {
            mChatMessageList = new ArrayList<>(messages.length);
        } else {
            mChatMessageList.clear();
        }
        for (ChatMessage message : messages) {
            if (message.getBody() instanceof TextMessageBody) {
                mChatMessageList.add(message);
            }
        }
        mAdapter.setData(mChatMessageList);
    }

    private class MessageListAdapter extends EaseBaseRecyclerViewAdapter<ChatMessage> {

        public MessageListAdapter() {
        }

        @Override
        public MessageViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ease_live_room_msgs_item, parent, false);
            return new MessageViewHolder(view, mContext);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

/*    private class MessageViewHolder extends EaseChatRowViewHolder {
        EaseImageView avatar;
        TextView joinNickname;
        Group joinGroup;

        TextView txtMessageNickname;
        TextView txtMessageNicknameRole;
        TextView txtMessageContent;
        Group txtMessageGroup;

        public MessageViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
            super(itemView, itemClickListener);
            avatar = itemView.findViewById(R.id.iv_avatar);
            joinNickname = itemView.findViewById(R.id.joined_nickname);
            joinGroup = itemView.findViewById(R.id.join_group);

            txtMessageNickname = itemView.findViewById(R.id.txt_message_nickname);
            txtMessageNicknameRole = itemView.findViewById(R.id.txt_message_nickname_role);
            txtMessageContent = itemView.findViewById(R.id.txt_message_content);
            txtMessageGroup = itemView.findViewById(R.id.txt_message_group);

            if (-1 != mNicknameMaxEms) {
                joinNickname.setMaxEms(mNicknameMaxEms);
                txtMessageNickname.setMaxEms(mNicknameMaxEms);
            }

            if (-1 != mNicknameEllipsize) {
                TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                switch (mNicknameEllipsize) {
                    case 0:
                        truncateAt = TextUtils.TruncateAt.START;
                        break;
                    case 1:
                        truncateAt = TextUtils.TruncateAt.MIDDLE;
                        break;
                    case 2:
                        truncateAt = TextUtils.TruncateAt.END;
                        break;
                    case 3:
                        truncateAt = TextUtils.TruncateAt.MARQUEE;
                        break;
                }
                joinNickname.setEllipsize(truncateAt);
                txtMessageNickname.setEllipsize(truncateAt);
            }
        }
    }*/

    private class MessageViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<ChatMessage> {
        private Context context;
        private EaseImageView avatar;
        private TextView joinNickname;
        private Group joinGroup;

        private TextView txtMessageNickname;
        private TextView txtMessageNicknameRole;
        private TextView txtMessageContent;
        private Group txtMessageGroup;

        public MessageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
        }

        @Override
        public void initView(View itemView) {
            avatar = findViewById(R.id.iv_avatar);
            joinNickname = findViewById(R.id.joined_nickname);
            joinGroup = findViewById(R.id.join_group);

            txtMessageNickname = findViewById(R.id.txt_message_nickname);
            txtMessageNicknameRole = findViewById(R.id.txt_message_nickname_role);
            txtMessageContent = findViewById(R.id.txt_message_content);
            txtMessageGroup = findViewById(R.id.txt_message_group);

            if (-1 != mNicknameMaxEms) {
                joinNickname.setMaxEms(mNicknameMaxEms);
                txtMessageNickname.setMaxEms(mNicknameMaxEms);
            }

            if (-1 != mNicknameEllipsize) {
                TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                switch (mNicknameEllipsize) {
                    case 0:
                        truncateAt = TextUtils.TruncateAt.START;
                        break;
                    case 1:
                        truncateAt = TextUtils.TruncateAt.MIDDLE;
                        break;
                    case 2:
                        truncateAt = TextUtils.TruncateAt.END;
                        break;
                    case 3:
                        truncateAt = TextUtils.TruncateAt.MARQUEE;
                        break;
                }
                joinNickname.setEllipsize(truncateAt);
                txtMessageNickname.setEllipsize(truncateAt);
            }
        }

        @Override
        public void setData(ChatMessage message, int position) {
            joinGroup.setVisibility(GONE);
            txtMessageGroup.setVisibility(GONE);

            String from = message.getFrom();
            if (message.getBody() instanceof TextMessageBody) {
                boolean memberAdd = false;
                Map<String, Object> ext = message.ext();
                if (ext.containsKey(EaseLiveMessageConstant.LIVE_MESSAGE_KEY_MEMBER_ADD)) {
                    memberAdd = (boolean) ext.get(EaseLiveMessageConstant.LIVE_MESSAGE_KEY_MEMBER_ADD);
                }
                String content = ((TextMessageBody) message.getBody()).getMessage();
                if (memberAdd) {
                    showMemberAddMsg(from);
                } else {
                    showText(from, content);
                }
            } else if (message.getBody() instanceof CustomMessageBody) {
                //TODO handle custom message
            }
        }

        private void showMemberAddMsg(final String id) {
            txtMessageGroup.setVisibility(GONE);
            joinGroup.setVisibility(VISIBLE);
            EaseUserUtils.setUserNick(id, joinNickname);
            EaseUserUtils.setUserAvatar(context, id, avatar);
        }

        private void showText(String id, String content) {
            joinGroup.setVisibility(GONE);
            txtMessageGroup.setVisibility(VISIBLE);
            txtMessageContent.setText(content);
            if (null == mChatRoom) {
                txtMessageNicknameRole.setVisibility(View.GONE);
                return;
            }

            if (mChatRoom.getOwner().equals(id)) {
                txtMessageNicknameRole.setVisibility(View.VISIBLE);
                txtMessageNicknameRole.setText(context.getResources().getString(R.string.ease_live_role_type_streamer));
                txtMessageNicknameRole.setBackgroundResource(R.drawable.ease_live_streamer_bg);
            } else if (mChatRoom.getAdminList().contains(id)) {
                txtMessageNicknameRole.setVisibility(View.VISIBLE);
                txtMessageNicknameRole.setText(context.getResources().getString(R.string.ease_live_role_type_moderator));
                txtMessageNicknameRole.setBackgroundResource(R.drawable.ease_live_moderator_bg);
            } else {
                txtMessageNicknameRole.setVisibility(View.GONE);
            }

            EaseUserUtils.setUserNick(id, txtMessageNickname);
            EaseUserUtils.setUserAvatar(context, id, avatar);
        }
    }

    public interface MessageViewListener {
        void onMessageSend(String content, boolean isBarrageMsg);

        void onItemClickListener(ChatMessage message);

        void onHiderBottomBar(boolean hide);
    }

}
