package io.agora.chat.uikit.chat;

import static io.agora.chat.uikit.utils.EaseUtils.canEdit;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.List;

import io.agora.ConversationListener;
import io.agora.MessageListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatManager;
import io.agora.chat.ChatMessage;
import io.agora.chat.CmdMessageBody;
import io.agora.chat.Conversation;
import io.agora.chat.MessageBody;
import io.agora.chat.MessageReactionChange;
import io.agora.chat.TextMessageBody;
import io.agora.chat.adapter.EMAChatRoomManagerListener;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.ChatInputMenuListener;
import io.agora.chat.uikit.chat.interfaces.IChatLayout;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.chat.interfaces.OnChatLayoutListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSelectResultListener;
import io.agora.chat.uikit.chat.interfaces.OnModifyMessageListener;
import io.agora.chat.uikit.chat.interfaces.OnReactionMessageListener;
import io.agora.chat.uikit.chat.interfaces.OnRecallMessageResultListener;
import io.agora.chat.uikit.chat.presenter.EaseHandleMessagePresenter;
import io.agora.chat.uikit.chat.presenter.EaseHandleMessagePresenterImpl;
import io.agora.chat.uikit.chat.presenter.IHandleMessageView;
import io.agora.chat.uikit.chat.widget.EaseChatExtendQuoteView;
import io.agora.chat.uikit.chat.widget.EaseChatInputMenu;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.chat.widget.EaseChatMultiSelectView;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.EaseChatRoomListener;
import io.agora.chat.uikit.interfaces.EaseGroupListener;
import io.agora.chat.uikit.interfaces.IPopupWindow;
import io.agora.chat.uikit.interfaces.MessageResultCallback;
import io.agora.chat.uikit.interfaces.OnMessageListItemClickListener;
import io.agora.chat.uikit.interfaces.OnMenuChangeListener;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.manager.EaseAtMessageHelper;
import io.agora.chat.uikit.manager.EaseChatMessageMultiSelectHelper;
import io.agora.chat.uikit.manager.EaseConfigsManager;
import io.agora.chat.uikit.manager.EaseSoftKeyboardHelper;
import io.agora.chat.uikit.manager.EaseThreadManager;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.menu.EasePopupWindow;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.EaseReactionMenuHelper;
import io.agora.chat.uikit.menu.MenuItemBean;
import io.agora.chat.uikit.menu.ReactionItemBean;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseDialog;
import io.agora.chat.uikit.widget.EaseVoiceRecorderView;
import io.agora.chat.uikit.widget.dialog.EaseAlertDialog;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;

public class EaseChatLayout extends RelativeLayout implements IChatLayout, IHandleMessageView, IPopupWindow
        , ChatInputMenuListener, MessageListener, EaseChatMessageListLayout.OnMessageTouchListener
        , OnMessageListItemClickListener, EaseChatMessageListLayout.OnChatErrorListener, ConversationListener, MessageResultCallback {
    private static final String TAG = EaseChatLayout.class.getSimpleName();
    private static final int MSG_TYPING_HEARTBEAT = 0;
    private static final int MSG_TYPING_END = 1;
    private static final int MSG_OTHER_TYPING_END = 2;

    public static final String ACTION_TYPING_BEGIN = "TypingBegin";
    public static final String ACTION_TYPING_END = "TypingEnd";
    protected static final int TYPING_SHOW_TIME = 10000;
    protected static final int OTHER_TYPING_SHOW_TIME = 5000;

    public static final String AT_PREFIX = "@";
    public static final String AT_SUFFIX = " ";

    private EaseChatMessageListLayout messageListLayout;
    private EaseChatInputMenu inputMenu;
    private EaseVoiceRecorderView voiceRecorder;
    /**
     * The switch of the "inputting" function.
     * When it is turned on, the device will continue to send cmd type messages
     * to notify the other party of "inputting" when sending messages
     */
    private boolean turnOnTyping;
    /**
     * The handler used to process whether the user is typing
     */
    private Handler typingHandler;
    /**
     * The conversation id, which may be the ring letter id of the other party, or the group id or chat room id
     */
    private String conversationId;
    /**
     * Chat type, see {@link EaseChatType}
     */
    private EaseChatType chatType;

    /**
     * Label of the loading data type.
     */
    private EaseChatMessageListLayout.LoadDataType loadDataType = EaseChatMessageListLayout.LoadDataType.LOCAL;
    /**
     * Used to monitor changes in messages
     */
    private OnChatLayoutListener listener;
    /**
     * Used to monitor touch events for sending voice
     */
    private OnChatRecordTouchListener recordTouchListener;
    private EaseHandleMessagePresenter presenter;
    /**
     * Whether to show the default menu
     */
    private boolean showDefaultMenu = true;
    /**
     * Long press entry menu help category
     */
    private EasePopupWindowHelper menuHelper;
    private ClipboardManager clipboard;
    private OnMenuChangeListener menuChangeListener;

    private OnReactionMessageListener reactionMessageListener;
    /**
     * Withdraw monitoring
     */
    private OnRecallMessageResultListener recallMessageListener;
    private ChatRoomListener chatRoomListener;
    private GroupListener groupListener;
    /**
     * Add a message attribute event before sending a message
     */
    private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
    /**
     * Whether it is the first time to send, the default is true
     */
    private boolean isNotFirstSend;
    private Drawable preBackground;
    // To flag whether has get the background drawable of input menu
    private boolean hasGetInputBgFlag;
    /**
     * Message's header view, default is Reaction view
     */
    private View mMenuHeaderView;
    /**
     * Whether to show reaction view
     */
    private boolean mIsShowReactionView = true;
    /**
     * The inner label is used to mark whether a reference operation is in progress.
     */
    private boolean isQuoting = false;
    private JSONObject quoteObject = null;
    /**
     * listener for modify message
     */
    private OnModifyMessageListener modifyMessageListener;
    /**
     * listener for select message.
     */
    private OnMessageSelectResultListener selectClickListener;

    public EaseChatLayout(Context context) {
        this(context, null);
    }

    public EaseChatLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        presenter = new EaseHandleMessagePresenterImpl();
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat, this);
        initView();
        initListener();
    }

    private void initView() {
        messageListLayout = findViewById(R.id.layout_chat_message);
        inputMenu = findViewById(R.id.layout_menu);
        voiceRecorder = findViewById(R.id.voice_recorder);

        presenter.attachView(this);

        menuHelper = new EasePopupWindowHelper();
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    private void initListener() {
        messageListLayout.setOnMessageTouchListener(this);
        messageListLayout.setOnMessageListItemClickListener(this);
        messageListLayout.setMessageResultCallback(this);
        messageListLayout.setOnChatErrorListener(this);
        inputMenu.setChatInputMenuListener(this);
        getChatManager().addMessageListener(this);
        getChatManager().addConversationListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getChatManager().removeMessageListener(this);
        getChatManager().removeConversationListener(this);
        if(chatRoomListener != null) {
            ChatClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
        }
        if(groupListener != null) {
            ChatClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }
        if(isChatRoomCon()) {
            ChatClient.getInstance().chatroomManager().leaveChatRoom(conversationId);
        }
        if(isGroupCon()) {
            EaseAtMessageHelper.get().removeAtMeGroup(conversationId);
            EaseAtMessageHelper.get().cleanToAtUserList();
        }
        if(typingHandler != null) {
            typingHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * initialization
     * @param username chat id
     * @param chatType Chat type, single chat, group chat or chat room
     */
    public void init(String username, EaseChatType chatType) {
        init(EaseChatMessageListLayout.LoadDataType.LOCAL, username, chatType);
    }

    /**
     * initialization
     * @param loadDataType Load data mode
     * @param conversationId      The conversation id, which may be the ring letter id of the other party,
     *                            or the group id or chat room id
     * @param chatType Chat type, single chat, group chat or chat room
     */
    public void init(EaseChatMessageListLayout.LoadDataType loadDataType, String conversationId, EaseChatType chatType) {
        this.conversationId = conversationId;
        this.chatType = chatType;
        this.loadDataType = loadDataType;
        messageListLayout.init(loadDataType, this.conversationId, chatType);
        presenter.setupWithToUser(chatType, this.conversationId, loadDataType == EaseChatMessageListLayout.LoadDataType.THREAD);
        if(loadDataType != EaseChatMessageListLayout.LoadDataType.THREAD) {
            if(isChatRoomCon()) {
                chatRoomListener = new ChatRoomListener();
                ChatClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
            }else if(isGroupCon()) {
                EaseAtMessageHelper.get().removeAtMeGroup(conversationId);
                groupListener = new GroupListener();
                ChatClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            }
        }
        initTypingHandler();
    }

    /**
     * Initialize historical message search mode
     * @param toChatUsername
     * @param chatType
     */
    public void initHistoryModel(String toChatUsername, EaseChatType chatType) {
        init(EaseChatMessageListLayout.LoadDataType.HISTORY, toChatUsername, chatType);
    }

    public void loadDefaultData() {
        sendChannelAck();
        messageListLayout.loadDefaultData();
    }

    public void loadData(String msgId, int pageSize) {
        sendChannelAck();
        messageListLayout.loadData(pageSize, msgId);
    }

    public void loadData(String msgId) {
        sendChannelAck();
        messageListLayout.loadData(msgId);
    }

    private void initTypingHandler() {
        typingHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_TYPING_HEARTBEAT :
                        setTypingBeginMsg(this);
                        break;
                    case MSG_TYPING_END :
                        setTypingEndMsg(this);
                        break;
                    case MSG_OTHER_TYPING_END:
                        setOtherTypingEnd(this);
                        break;
                }
            }
        };
        if(!turnOnTyping) {
            if(typingHandler != null) {
                typingHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    /**
     * Send channel ack message
     * (1) If it is a 1v1 session, the other party will receive a channel ack callback, the callback method is {@link ConversationListener#onConversationRead(String, String)}
     * The SDK will set the isAcked of the message sent for this session to true.
     * (2) If it is a multi-terminal device, the other end will receive a channel ack callback, and the SDK will set the session as read.
     * (3) Not send channel ack when the conversation is thread
     */
    private void sendChannelAck() {
        if(EaseConfigsManager.enableSendChannelAck()) {
            Conversation conversation = ChatClient.getInstance().chatManager().getConversation(conversationId);
            // Not send channel ack when the conversation is thread
            if(conversation == null || conversation.getUnreadMsgCount() <= 0 || conversation.isChatThread()) {
                return;
            }
            try {
                ChatClient.getInstance().chatManager().ackConversationRead(conversationId);
            } catch (ChatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The other party's input status is aborted
     * @param handler
     */
    private void setOtherTypingEnd(Handler handler) {
//        if(!turnOnTyping) {
//            return;
//        }
        // Only support single-chat type conversation.
        if (chatType != EaseChatType.SINGLE_CHAT)
            return;
        handler.removeMessages(MSG_OTHER_TYPING_END);
        if(listener != null) {
            listener.onPeerTyping(ACTION_TYPING_END);
        }
    }

    /**
     * Processing "input in progress" begins
     * @param handler
     */
    private void setTypingBeginMsg(Handler handler) {
        if (!turnOnTyping) return;
        // Only support single-chat type conversation.
        if (chatType != EaseChatType.SINGLE_CHAT)
            return;
        // Send TYPING-BEGIN cmd msg
        presenter.sendCmdMessage(ACTION_TYPING_BEGIN);
        handler.sendEmptyMessageDelayed(MSG_TYPING_HEARTBEAT, TYPING_SHOW_TIME);
    }

    /**
     * End of processing "input in progress"
     * @param handler
     */
    private void setTypingEndMsg(Handler handler) {
        if (!turnOnTyping) return;

        // Only support single-chat type conversation.
        if (chatType != EaseChatType.SINGLE_CHAT)
            return;

        isNotFirstSend = false;
        handler.removeMessages(MSG_TYPING_HEARTBEAT);
        handler.removeMessages(MSG_TYPING_END);
        // Send TYPING-END cmd msg
        //presenter.sendCmdMessage(ACTION_TYPING_END);
    }

    /**
     * Determine whether it is a chat room
     * @return
     */
    public boolean isChatRoomCon() {
        return EaseUtils.getConversationType(chatType) == Conversation.ConversationType.ChatRoom;
    }

    /**
     * Determine whether it is a group chat
     * @return
     */
    public boolean isGroupCon() {
        return EaseUtils.getConversationType(chatType) == Conversation.ConversationType.GroupChat;
    }

    @Override
    public void setPresenter(EaseHandleMessagePresenter presenter) {
        this.presenter = presenter;
        if(context() instanceof AppCompatActivity) {
            ((AppCompatActivity) context()).getLifecycle().addObserver(presenter);
        }
        presenter.attachView(this);
    }

    @Override
    public EaseChatMessageListLayout getChatMessageListLayout() {
        return messageListLayout;
    }

    @Override
    public EaseChatInputMenu getChatInputMenu() {
        return inputMenu;
    }

    @Override
    public String getInputContent() {
        return inputMenu.getPrimaryMenu().getEditText().getText().toString().trim();
    }

    @Override
    public void turnOnTypingMonitor(boolean turnOn) {
        this.turnOnTyping = turnOn;
        if(!turnOn) {
            isNotFirstSend = false;
        }
    }

    @Override
    public void sendTextMessage(String content) {
        presenter.sendTextMessage(content);
    }

    @Override
    public void sendTextMessage(String content, boolean isNeedGroupAck) {
        presenter.sendTextMessage(content, isNeedGroupAck);
    }

    @Override
    public void sendAtMessage(String content) {
        presenter.sendAtMessage(content);
    }

    @Override
    public void sendBigExpressionMessage(String name, String identityCode) {
        presenter.sendBigExpressionMessage(name, identityCode);
    }

    @Override
    public void sendVoiceMessage(String filePath, int length) {
        sendVoiceMessage(Uri.parse(filePath), length);
    }

    @Override
    public void sendVoiceMessage(Uri filePath, int length) {
        presenter.sendVoiceMessage(filePath, length);
    }

    @Override
    public void sendImageMessage(Uri imageUri) {
        presenter.sendImageMessage(imageUri);
    }

    @Override
    public void sendImageMessage(Uri imageUri, boolean sendOriginalImage) {
        presenter.sendImageMessage(imageUri, sendOriginalImage);
    }

    @Override
    public void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        presenter.sendLocationMessage(latitude, longitude, locationAddress);
    }

    @Override
    public void sendVideoMessage(Uri videoUri, int videoLength) {
        presenter.sendVideoMessage(videoUri, videoLength);
    }

    @Override
    public void sendCombineMessage(ChatMessage message) {
        presenter.sendCombineMessage(message);
    }

    @Override
    public void sendFileMessage(Uri fileUri) {
        presenter.sendFileMessage(fileUri);
    }

    @Override
    public void sendMessage(ChatMessage message) {
        presenter.sendMessage(message);
    }

    @Override
    public void resendMessage(ChatMessage message) {
        EMLog.i(TAG, "resendMessage");
        presenter.resendMessage(message);
    }

    @Override
    public void deleteMessage(ChatMessage message) {
        presenter.deleteMessage(message);
    }

    @Override
    public void deleteMessages(List<String> messages) {
        presenter.deleteMessages(messages);
    }

    @Override
    public void recallMessage(ChatMessage message) {
        presenter.recallMessage(message);
    }

    @Override
    public void modifyMessage(String messageId, MessageBody messageBodyModified) {
        presenter.modifyMessage(messageId,messageBodyModified);
    }

    @Override
    public void addMessageAttributes(ChatMessage message) {
        presenter.addMessageAttributes(message);
    }

    @Override
    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
        this.recordTouchListener = recordTouchListener;
    }

    @Override
    public void setOnRecallMessageResultListener(OnRecallMessageResultListener listener) {
        this.recallMessageListener = listener;
    }

    @Override
    public void setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
        this.sendMsgEvent = sendMsgEvent;
    }

    /**
     * Sending logic: If you are typing, send a cmd message for the first time, and then send it every 10s;
     * If you stop sending more than 10 seconds later, the status needs to be reset.
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {
        if(listener != null) {
            listener.onTextChanged(s, start, before, count);
        }
        if(turnOnTyping) {
            if(typingHandler != null) {
                if(!isNotFirstSend) {
                    isNotFirstSend = true;
                    typingHandler.sendEmptyMessage(MSG_TYPING_HEARTBEAT);
                }
                typingHandler.removeMessages(MSG_TYPING_END);
                typingHandler.sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
            }
        }
    }

    @Override
    public void onSendMessage(String content) {
        presenter.sendTextMessage(content);
    }

    @Override
    public void onExpressionClicked(Object emojicon) {
        if(emojicon instanceof EaseEmojicon) {
            presenter.sendBigExpressionMessage(((EaseEmojicon) emojicon).getName(), ((EaseEmojicon) emojicon).getIdentityCode());
        }
    }

    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        if(recordTouchListener != null) {
            boolean onRecordTouch = recordTouchListener.onRecordTouch(v, event);
            if (onRecordTouch) {
                return true;
            }
        }
        return voiceRecorder.onPressToSpeakBtnTouch(v, event, (this::sendVoiceMessage));
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        if(listener != null) {
            listener.onChatExtendMenuItemClick(view, itemId);
        }
    }

    private ChatManager getChatManager() {
        return ChatClient.getInstance().chatManager();
    }

    @Override
    public void onMessageReceived(List<ChatMessage> messages) {
        boolean refresh = false;
        for (ChatMessage message : messages) {
            String username = null;
            sendGroupReadAck(message);
            sendReadAck(message);
            // group message
            if (message.getChatType() == ChatMessage.ChatType.GroupChat || message.getChatType() == ChatMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }
            // if the message is for current conversation
            if (username.equals(conversationId) || message.getTo().equals(conversationId) || message.conversationId().equals(conversationId)) {
                refresh = true;
            }

            if (EaseAtMessageHelper.get().isAtMeMsg(message) && message.conversationId().equals(conversationId)){
                EaseAtMessageHelper.get().removeAtMeGroup(conversationId);
            }
        }
        if(refresh && getChatMessageListLayout() != null && !messages.isEmpty()) {
            getChatMessageListLayout().setSendOrReceiveMessage(messages.get(0));
            getChatMessageListLayout().refreshToLatest();
        }
    }

    public void sendReadAck(ChatMessage message) {
        if(EaseConfigsManager.enableSendChannelAck()) {
            //It is a received message, a read ack message has not been sent and it is a single chat
            if(message.direct() == ChatMessage.Direct.RECEIVE
                    && !message.isAcked()
                    && message.getChatType() == ChatMessage.ChatType.Chat) {
                ChatMessage.Type type = message.getType();
                //Video, voice and files need to be clicked before sending
                if(type == ChatMessage.Type.VIDEO || type == ChatMessage.Type.VOICE || type == ChatMessage.Type.FILE) {
                    return;
                }
                // If not the same conversation, do not send read ack.
                if(!TextUtils.equals(message.conversationId(), conversationId)) {
                    return;
                }
                try {
                    ChatClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (ChatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send group read receipt
     * @param message
     */
    private void sendGroupReadAck(ChatMessage message) {
        if(message.isNeedGroupAck() && message.isUnread() && TextUtils.equals(message.conversationId(), conversationId)) {
            try {
                ChatClient.getInstance().chatManager().ackGroupMessageRead(message.getTo(), message.getMsgId(), "");
            } catch (ChatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The processing logic of the input action is received:
     * If a message that is being input is received, the timing starts, and if no new message is received within 5s, the input state ends
     * @param messages
     */
    @Override
    public void onCmdMessageReceived(List<ChatMessage> messages) {
        if(!turnOnTyping) {
            return;
        }
        for (final ChatMessage msg : messages) {
            final CmdMessageBody body = (CmdMessageBody) msg.getBody();
            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly());
            EaseThreadManager.getInstance().runOnMainThread(() -> {
                if(TextUtils.equals(msg.getFrom(), conversationId)) {
                    if(listener != null) {
                        listener.onPeerTyping(body.action());
                    }
                    if(typingHandler != null) {
                        typingHandler.removeMessages(MSG_OTHER_TYPING_END);
                        typingHandler.sendEmptyMessageDelayed(MSG_OTHER_TYPING_END, OTHER_TYPING_SHOW_TIME);
                    }
                }
            });
        }
    }

    @Override
    public void onMessageRead(List<ChatMessage> messages) {
        refreshMessages(messages);
    }

    @Override
    public void onMessageDelivered(List<ChatMessage> messages) {
        refreshMessages(messages);
    }

    @Override
    public void onMessageRecalled(List<ChatMessage> messages) {
        boolean isRefresh = false;
        if(messages != null && messages.size() > 0) {
            for(ChatMessage message : messages) {
                if(TextUtils.equals(message.conversationId(), conversationId)) {
                    isRefresh = true;
                }
            }
        }
        if(getChatMessageListLayout() != null && isRefresh) {
            getChatMessageListLayout().refreshMessages();
        }
    }

    @Override
    public void onMessageChanged(ChatMessage message, Object change) {
        refreshMessage(message);
    }

    private void refreshMessage(ChatMessage message) {
        if(getChatMessageListLayout() != null) {
            getChatMessageListLayout().refreshMessage(message);
        }
    }

    private void refreshMessages(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            refreshMessage(msg);
        }
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void createThumbFileFail(String message) {
        if(listener != null) {
            listener.onError(-1, message);
        }
    }

    @Override
    public void addMsgAttrBeforeSend(ChatMessage message) {
        if (message != null && message.getType() == ChatMessage.Type.TXT && isQuoting) {
            message.setAttribute(EaseConstant.QUOTE_MSG_QUOTE, quoteObject);
        }
        if(sendMsgEvent != null) {
            sendMsgEvent.addMsgAttrsBeforeSend(message);
        }
    }

    @Override
    public void sendMessageFail(String message) {
        if(listener != null) {
            listener.onError(-1, message);
        }
    }

    @Override
    public void sendMessageFinish(ChatMessage message) {
        if (isQuoting && message.getType() == ChatMessage.Type.TXT) {
            isQuoting = false;
            if(getChatInputMenu().getChatTopExtendMenu() instanceof EaseChatExtendQuoteView) {
                getChatInputMenu().getChatTopExtendMenu().showTopExtendMenu(false);
            }
        }
        if(getChatMessageListLayout() != null) {
            getChatMessageListLayout().setSendOrReceiveMessage(message);
            getChatMessageListLayout().refreshToLatest();
        }
    }

    @Override
    public void deleteLocalMessageSuccess(ChatMessage message) {
        messageListLayout.removeMessage(message);
    }

    @Override
    public void deleteLocalMessagesSuccess() {
        messageListLayout.refreshMessages();
    }

    @Override
    public void recallMessageFinish(ChatMessage originalMessage, ChatMessage notification) {
        if(recallMessageListener != null) {
            recallMessageListener.recallSuccess(originalMessage, notification);
        }
        messageListLayout.refreshMessages();
    }

    @Override
    public void recallMessageFail(int code, String message) {
        if(recallMessageListener != null) {
            recallMessageListener.recallFail(code, message);
        }
        if(listener != null) {
            listener.onError(code, message);
        }
    }

    @Override
    public void onPresenterMessageSuccess(ChatMessage message) {
        EMLog.i(TAG, "send message onPresenterMessageSuccess");
        if(message.isChatThreadMessage() && messageListLayout != null && messageListLayout.isReachedLatestThreadMessage()) {
            message.setAttribute(EaseConstant.FLAG_REACH_LATEST_THREAD_MESSAGE, true);
        }
        if(listener != null) {
            listener.onSuccess(message);
        }
    }

    @Override
    public void onPresenterMessageError(ChatMessage message, int code, String error) {
        EMLog.i(TAG, "send message onPresenterMessageError code: "+code + " error: "+error);
        refreshMessage(message);
        if(listener != null) {
            listener.onError(code, error);
        }
    }

    @Override
    public void onPresenterMessageInProgress(ChatMessage message, int progress) {
        EMLog.i(TAG, "send message onPresenterMessageInProgress");
    }

    @Override
    public void onTouchItemOutside(View v, int position) {
        inputMenu.hideSoftKeyboard();
        inputMenu.showExtendMenu(false);
    }

    @Override
    public void onViewDragging() {
        if(!hasGetInputBgFlag) {
            preBackground = inputMenu.getBackground();
            hasGetInputBgFlag = true;
        }
        inputMenu.setBackgroundResource(R.drawable.ease_chat_input_bg);
        inputMenu.hideSoftKeyboard();
        inputMenu.showExtendMenu(false);
    }

    @Override
    public void onReachBottom() {
        inputMenu.setBackground(preBackground);
    }

    @Override
    public boolean onBubbleClick(ChatMessage message) {
        if(listener != null) {
            return listener.onBubbleClick(message);
        }
        return false;
    }

    @Override
    public boolean onResendClick(ChatMessage message) {
        EMLog.i(TAG, "onResendClick");
        new EaseDialog(getContext(), R.string.ease_resend, R.string.ease_confirm_resend, null, new EaseDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (!confirmed) {
                    return;
                }
                resendMessage(message);
            }
        }, true).show();
        return true;
    }

    @Override
    public boolean onBubbleLongClick(View v, ChatMessage message) {
        if(showDefaultMenu) {
            inputMenu.hideSoftKeyboard();
            showDefaultMenu(v, message);
            if(listener != null) {
                return listener.onBubbleLongClick(v, message);
            }
            return true;
        }
        if(listener != null) {
            return listener.onBubbleLongClick(v, message);
        }
        return false;
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(listener != null) {
            listener.onUserAvatarClick(username);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        EMLog.i(TAG, "onUserAvatarLongClick");
        inputAtUsername(username, true);
        if(listener != null) {
            listener.onUserAvatarLongClick(username);
        }
    }

    @Override
    public boolean onThreadClick(String messageId, String threadId) {
        if(listener != null) {
            return listener.onThreadClick(messageId, threadId);
        }
        return false;
    }

    @Override
    public boolean onThreadLongClick(View v, String messageId, String threadId) {
        if(listener != null) {
            return listener.onThreadLongClick(v, messageId, threadId);
        }
        return false;
    }

    @Override
    public void onMessageSuccess(ChatMessage message) {
        EMLog.i(TAG, "message onMessageSuccess");
        if(message.isChatThreadMessage() && messageListLayout != null && messageListLayout.isReachedLatestThreadMessage()) {
            message.setAttribute(EaseConstant.FLAG_REACH_LATEST_THREAD_MESSAGE, true);
        }
        if(listener != null) {
            listener.onSuccess(message);
        }
    }

    @Override
    public void onMessageError(ChatMessage message, int code, String error) {
        if(listener != null) {
            listener.onError(code, error);
        }
    }

    @Override
    public void onMessageInProgress(ChatMessage message, int progress) {
        EMLog.i(TAG, "message in progress: "+progress);
    }

    @Override
    public void onRemoveReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
        presenter.removeReaction(message, reactionEntity.getEmojicon().getIdentityCode());
    }

    @Override
    public void onAddReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
        presenter.addReaction(message, reactionEntity.getEmojicon().getIdentityCode());
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if(listener != null) {
            listener.onError(code, errorMsg);
        }
    }

    @Override
    public void showItemDefaultMenu(boolean showDefault) {
        this.showDefaultMenu = showDefault;
    }

    @Override
    public void clearMenu() {
        menuHelper.clear();
    }

    @Override
    public void addItemMenu(MenuItemBean item) {
        menuHelper.addItemMenu(item);
    }

    @Override
    public void addItemMenu(int groupId, int itemId, int order, String title) {
        menuHelper.addItemMenu(groupId, itemId, order, title);
    }

    @Override
    public MenuItemBean findItem(int id) {
        return menuHelper.findItem(id);
    }

    @Override
    public void findItemVisible(int id, boolean visible) {
        menuHelper.findItemVisible(id, visible);
    }

//    @Override
//    public void setMenuStyle(EasePopupWindow.Style style) {
//        menuHelper.setMenuStyle(style);
//    }

    @Override
    public void setItemMenuIconVisible(boolean visible) {
        menuHelper.setItemMenuIconVisible(visible);
    }

    @Override
    public EasePopupWindowHelper getMenuHelper() {
        return menuHelper;
    }

    @Override
    public void setOnPopupWindowItemClickListener(OnMenuChangeListener listener) {
        this.menuChangeListener = listener;
    }

    @Override
    public void addHeaderView(View view) {
        this.mMenuHeaderView = view;
    }

    @Override
    public void hideReactionView(boolean hide) {
        this.mIsShowReactionView = !hide;
    }

    /**
     * input @
     * only for group chat
     * @param username
     */
    public void inputAtUsername(String username, boolean autoAddAtSymbol){
        if(ChatClient.getInstance().getCurrentUser().equals(username) ||
                !messageListLayout.isGroupChat()){
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null){
            username = user.getNickname();
        }
        EditText editText = inputMenu.getPrimaryMenu().getEditText();
        if(autoAddAtSymbol)
            insertText(editText, AT_PREFIX + username + AT_SUFFIX);
        else
            insertText(editText, username + AT_SUFFIX);
    }

    /**
     * insert text to EditText
     * @param edit
     * @param text
     */
    private void insertText(EditText edit, String text) {
        if(edit.isFocused()) {
            edit.getText().insert(edit.getSelectionStart(), text);
        }else {
            edit.getText().insert(edit.getText().length() - 1, text);
        }
    }

    private void showDefaultMenu(View v, ChatMessage message) {
        menuHelper.initMenu(getContext());
        menuHelper.addHeaderView(checkHeaderViewForDefaultMenu(message));
        menuHelper.setDefaultMenus();
        menuHelper.setOutsideTouchable(true);
        setMenuByMsgType(message);
        if(menuChangeListener != null) {
            menuChangeListener.onPreMenu(menuHelper, message);
        }
        menuHelper.setOnPopupMenuItemClickListener(new EasePopupWindow.OnPopupWindowItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItemBean item) {
                if(menuChangeListener != null && menuChangeListener.onMenuItemClick(item, message)) {
                    return true;
                }
                if(showDefaultMenu) {
                    int itemId = item.getItemId();
                    if(itemId == R.id.action_chat_copy) {
                        clipboard.setPrimaryClip(ClipData.newPlainText(null,
                                ((TextMessageBody) message.getBody()).getMessage()));
                        EMLog.i(TAG, "copy success");
                    }else if(itemId == R.id.action_chat_delete) {
                        deleteMessage(message);
                        EMLog.i(TAG,"currentMsgId = "+message.getMsgId() + " timestamp = "+message.getMsgTime());
                    }else if(itemId == R.id.action_chat_recall) {
                        recallMessage(message);
                    }else if(itemId == R.id.action_chat_thread) {
                        skipToCreateThread(message);
                    }else if(itemId == R.id.action_chat_reply) {
                        showChatExtendQuoteView();
                        if(getChatInputMenu().getChatTopExtendMenu() instanceof EaseChatExtendQuoteView) {
                            ((EaseChatExtendQuoteView) (getChatInputMenu().getChatTopExtendMenu())).startQuote(message);
                            getChatInputMenu().getPrimaryMenu().showTextStatus();
                            presenter.createReplyMessageExt(message);
                        }
                    }else if(itemId == R.id.action_chat_select) {
                        showMultiSelectView(message);
                    }else if(itemId == R.id.action_chat_edit) {
                        showEditMessageDialog(message);
                    }
                    return true;
                }
                return false;
            }
        });
        menuHelper.setOnPopupMenuDismissListener(new EasePopupWindow.OnPopupWindowDismissListener() {
            @Override
            public void onDismiss(PopupWindow menu) {
                if(menuChangeListener != null) {
                    menuChangeListener.onDismiss(menu);
                }
            }
        });
        menuHelper.show(this, v);
    }

    private View checkHeaderViewForDefaultMenu(ChatMessage message) {
        if(mMenuHeaderView != null) {
            return mMenuHeaderView;
        }
        if(!mIsShowReactionView) {
            return null;
        }
        // Use reaction view
        EaseReactionMenuHelper helper = new EaseReactionMenuHelper();
        helper.init(getContext(), menuHelper);
        helper.setMessageReactions(message.getMessageReaction());
        helper.show();
        helper.setReactionItemClickListener(new EasePopupWindow.OnPopupWindowItemClickListener() {
            @Override
            public void onReactionItemClick(ReactionItemBean item, boolean isAdd) {
                if (isAdd) {
                    presenter.addReaction(message, item.getIdentityCode());
                } else {
                    presenter.removeReaction(message, item.getIdentityCode());
                }
            }

            @Override
            public boolean onMenuItemClick(MenuItemBean item) {
                return false;
            }
        });
        return helper.getView();
    }

    private void skipToCreateThread(ChatMessage message) {
        EaseActivityProviderHelper.startToCreateChatThreadActivity(context(), conversationId, message.getMsgId());
    }

    private void setMenuByMsgType(ChatMessage message) {
        ChatMessage.Type type = message.getType();
        menuHelper.setAllItemsVisible(false);
        menuHelper.findItemVisible(R.id.action_chat_delete, true);
        menuHelper.findItem(R.id.action_chat_delete).setTitle(getContext().getString(R.string.ease_action_delete));
        if(message.status() == ChatMessage.Status.SUCCESS && message.direct() == ChatMessage.Direct.SEND) {
            menuHelper.findItemVisible(R.id.action_chat_recall, true);
        }
        switch (type) {
            case TXT:
                menuHelper.findItemVisible(R.id.action_chat_copy, true);
                menuHelper.findItemVisible(R.id.action_chat_recall, true);
                break;
            case LOCATION:
            case FILE:
            case IMAGE:
                menuHelper.findItemVisible(R.id.action_chat_recall, true);
                break;
            case VOICE:
                menuHelper.findItem(R.id.action_chat_delete).setTitle(getContext().getString(R.string.ease_delete_voice));
                menuHelper.findItemVisible(R.id.action_chat_recall, true);
                break;
            case VIDEO:
                menuHelper.findItem(R.id.action_chat_delete).setTitle(getContext().getString(R.string.ease_delete_video));
                menuHelper.findItemVisible(R.id.action_chat_recall, true);
                break;
        }
        if(message.getChatType() == ChatMessage.ChatType.GroupChat && message.getChatThread() == null) {
            menuHelper.findItemVisible(R.id.action_chat_thread, true);
        }

        if(message.direct() == ChatMessage.Direct.RECEIVE ){
            menuHelper.findItemVisible(R.id.action_chat_recall, false);
        }
        if(message.status() != ChatMessage.Status.SUCCESS) {
            menuHelper.findItemVisible(R.id.action_chat_recall, false);
            menuHelper.findItemVisible(R.id.action_chat_thread, false);
            menuHelper.showHeaderView(false);
        }
        menuHelper.findItemVisible(R.id.action_chat_reply, message.status() == ChatMessage.Status.SUCCESS && EaseConfigsManager.enableReplyMessage());
        menuHelper.findItemVisible(R.id.action_chat_select, message.status() == ChatMessage.Status.SUCCESS && EaseConfigsManager.enableSendCombineMessage());
        menuHelper.findItemVisible(R.id.action_chat_edit, canEdit(message));
    }

    /**
     * Set custom top extend menu
     */
    public void showChatExtendQuoteView() {
        if(!EaseConfigsManager.enableReplyMessage()) {
            return;
        }
        IChatTopExtendMenu chatTopExtendMenu = getChatInputMenu().getChatTopExtendMenu();
        if (chatTopExtendMenu instanceof EaseChatExtendQuoteView) {
            getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE);
            return;
        }
        EaseChatExtendQuoteView quoteView = new EaseChatExtendQuoteView(context());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        quoteView.setLayoutParams(params);
        quoteView.showTopExtendMenu(false);
        getChatInputMenu().setCustomTopExtendMenu(quoteView);
        getChatInputMenu().showTopExtendMenu(true);
        getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE);
    }

    /**
     * Show multi select view on EaseChatInputMenu.
     */
    public void showMultiSelectView(ChatMessage message) {
        if(!EaseConfigsManager.enableSendCombineMessage()) {
            return;
        }
        EaseChatMultiSelectView multiSelectView = new EaseChatMultiSelectView(context());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        multiSelectView.setLayoutParams(params);
        multiSelectView.setOnDismissListener(view -> {
            getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE);
            messageListLayout.isNeedScrollToBottomWhenViewChange(true);
        });
        multiSelectView.setOnSelectClickListener(new EaseChatMultiSelectView.OnSelectClickListener() {
            @Override
            public void onMultiSelectClick(EaseChatMultiSelectView.MultiSelectType type, List<String> msgIdList) {
                if(type == EaseChatMultiSelectView.MultiSelectType.DELETE) {
                    if(selectClickListener != null && selectClickListener.onSelectResult(multiSelectView, OnMessageSelectResultListener.SelectType.DELETE, msgIdList)) {
                        return;
                    }
                    deleteMessages(msgIdList);
                }else if(type == EaseChatMultiSelectView.MultiSelectType.FORWARD) {
                    if(selectClickListener != null && selectClickListener.onSelectResult(multiSelectView, OnMessageSelectResultListener.SelectType.FORWARD, msgIdList)) {
                        return;
                    }
                    presenter.sendCombineMessage(context().getString(R.string.ease_combine_default)
                            , EaseChatMessageMultiSelectHelper.getCombineMessageSummary(msgIdList)
                            , context().getString(R.string.ease_combine_compatible_default), msgIdList);
                }
            }
        });
        multiSelectView.setupWithAdapter(messageListLayout.getMessageAdapter());
        multiSelectView.setSelectMessage(message);
        getChatInputMenu().setCustomTopExtendMenu(multiSelectView);
        if(loadDataType == EaseChatMessageListLayout.LoadDataType.THREAD) {
            multiSelectView.findViewById(R.id.iv_multi_select_delete).setVisibility(INVISIBLE);
        }
        getChatInputMenu().showTopExtendMenu(true);
        getChatInputMenu().hideInputMenu();
        messageListLayout.isNeedScrollToBottomWhenViewChange(false);
    }

    private void showEditMessageDialog(ChatMessage message) {
        EaseAlertDialog dialog = new EaseAlertDialog.Builder<>(context())
                .setGravity(Gravity.BOTTOM)
                .setContentView(R.layout.ease_dialog_message_edit)
                .setFullWidth()
                .setCancelable(true)
                .show();
        EditText editText = dialog.getViewById(R.id.edt_msg_edit);
        TextView tvDone = dialog.getViewById(R.id.tv_done);
        String content = ((TextMessageBody) message.getBody()).getMessage();
        dialog.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setOnClickListener(R.id.tv_done, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                String newContent = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(newContent)) {
                    TextMessageBody textMessageBody = new TextMessageBody(newContent);
                    textMessageBody.setTargetLanguages(((TextMessageBody) message.getBody()).getTargetLanguages());
                    modifyMessage(message.getMsgId() , textMessageBody);
                }

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    if (editText != null) {
                        editText.setHint(content);
                    }
                    if (tvDone != null) {
                        tvDone.setEnabled(false);
                    }
                } else {
                    editText.setSelection(s.length());
                    if (tvDone != null) {
                        tvDone.setEnabled(true);
                    }
                }
            }
        });
        editText.setText(content);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EaseSoftKeyboardHelper.showKeyboard(editText);
            }
        }, 200);

    }

    @Override
    public void onConversationUpdate() {
    }

    @Override
    public void onConversationRead(String s, String s1) {
        messageListLayout.refreshMessages();
    }

    @Override
    public void onReactionChanged(List<MessageReactionChange> list) {
        EMLog.i(TAG, "onReactionChanged");
        for (MessageReactionChange reactionChange : list) {
            if (conversationId.equals(reactionChange.getConversionID())) {
                EaseThreadManager.getInstance().runOnMainThread(()-> refreshMessage(ChatClient.getInstance().chatManager().getMessage(reactionChange.getMessageId())));
            }
        }
    }

    private class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(String roomId, String roomName) {
            finishCurrent();
        }

        @Override
        public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {
            if(!TextUtils.equals(roomId, conversationId)) {
                return;
            }
            if(reason == EMAChatRoomManagerListener.BE_KICKED) {
                finishCurrent();
            }
        }

        @Override
        public void onMemberJoined(String roomId, String participant) {

        }

        @Override
        public void onMemberExited(String roomId, String roomName, String participant) {

        }
    }

    /**
     * group listener
     */
    private class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            finishCurrent();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            finishCurrent();
        }
    }

    /**
     * finish current activity
     */
    private void finishCurrent() {
        if(getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void setOnReactionListener(OnReactionMessageListener reactionListener) {
        this.reactionMessageListener = reactionListener;
    }

    @Override
    public void setOnSelectClickListener(OnMessageSelectResultListener listener) {
        this.selectClickListener = listener;
    }

    @Override
    public void addReactionMessageSuccess(ChatMessage message) {
        EMLog.e(TAG, "addReactionMessageSuccess");
        refreshMessage(message);
        if (null != reactionMessageListener) {
            reactionMessageListener.addReactionMessageSuccess(message);
        }
    }

    @Override
    public void addReactionMessageFail(ChatMessage message, int code, String error) {
        EMLog.e(TAG, "addReactionMessageFail,code = " + code + ",error=" + error);
        if (null != reactionMessageListener) {
            reactionMessageListener.addReactionMessageFail(message, code, error);
        }
    }

    @Override
    public void removeReactionMessageSuccess(ChatMessage message) {
        EMLog.e(TAG, "removeReactionMessageSuccess");
        refreshMessage(message);
        if (null != reactionMessageListener) {
            reactionMessageListener.removeReactionMessageSuccess(message);
        }
    }

    @Override
    public void removeReactionMessageFail(ChatMessage message, int code, String error) {
        EMLog.e(TAG, "removeReactionMessageFail");
        if (null != reactionMessageListener) {
            reactionMessageListener.removeReactionMessageFail(message, code, error);
        }
    }

    @Override
    public void onModifyMessageSuccess(ChatMessage messageModified) {
        refreshMessage(messageModified);
        if (modifyMessageListener != null) {
            modifyMessageListener.onModifyMessageSuccess(messageModified);
        }
    }

    @Override
    public void onModifyMessageFailure(String messageId, int code, String error) {
        EMLog.i(TAG, "onModifyMessageFailure:" + code + ":" + error);
        if (modifyMessageListener != null) {
            modifyMessageListener.onModifyMessageFailure(messageId,code,error);
        }
    }

    @Override
    public void createReplyMessageExtSuccess(JSONObject extObject) {
        isQuoting = true;
        quoteObject = extObject;
    }

    @Override
    public void createReplyMessageExtFail(int code, String error) {
        if(listener != null) {
            listener.onError(code, error);
        }
    }

    @Override
    public void setOnEditMessageListener(OnModifyMessageListener listener) {
        this.modifyMessageListener = listener;
    }

}

