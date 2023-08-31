package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.Date;

import io.agora.CallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseAdapter;
import io.agora.chat.uikit.chat.model.EaseChatItemStyleHelper;
import io.agora.chat.uikit.chat.model.EaseChatSetStyle;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.chat.widget.EaseChatReactionView;
import io.agora.chat.uikit.interfaces.MessageResultCallback;
import io.agora.chat.uikit.interfaces.OnMessageListItemClickListener;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.manager.EaseChatMessageMultiSelectHelper;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;
import io.agora.chat.uikit.options.EaseAvatarOptions;
import io.agora.chat.uikit.options.EaseReactionOptions;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseImageView;
import io.agora.util.EMLog;

/**
 * base chat row view
 */
public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    /**
     * ListView's adapter or RecyclerView's adapter
     */
    protected Object adapter;
    protected ChatMessage message;
    /**
     * message's position in list
     */
    protected int position;

    /**
     * timestamp
     */
    protected TextView timeStampView;
    /**
     * avatar
     */
    protected ImageView userAvatarView;
    /**
     * bubble
     */
    protected View bubbleLayout;
    /**
     * nickname
     */
    protected TextView usernickView;
    /**
     * percent
     */
    protected TextView percentageView;
    /**
     * progress
     */
    protected ProgressBar progressBar;
    /**
     * status
     */
    protected ImageView statusView;
    /**
     * if asked
     */
    protected TextView ackedView;
    /**
     * if delivered
     */
    protected TextView deliveredView;
    /**
     * if edited
     */
    protected TextView editView;
    /**
     * Multi selects.
     */
    protected RadioButton selectRadio;
    /**
     * if is sender
     */
    protected boolean isSender;
    /**
     * normal along with {@link #isSender}
     */
    protected boolean showSenderType;
    /**
     * chat message callback
     */
    protected EaseChatCallback chatCallback;
    /**
     * switch to main thread
     */
    private Handler mainThreadHandler;

    protected OnMessageListItemClickListener itemClickListener;
    private EaseChatRowActionCallback itemActionCallback;
    private EaseChatRowThreadRegion threadRegion;
    protected EaseChatReactionView reactionContentView;
    protected MessageResultCallback messageResultCallback;

    public EaseChatRow(Context context, boolean isSender) {
        super(context);
        this.context = context;
        this.isSender = isSender;
        this.inflater = LayoutInflater.from(context);

        initView();
    }

    public EaseChatRow(Context context, ChatMessage message, int position, Object adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.isSender = message.direct() == ChatMessage.Direct.SEND;
        this.position = position;
        this.adapter = adapter;
        this.inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    private void initView() {
        showSenderType = isSender;
        EaseChatItemStyleHelper helper = getItemStyleHelper();
        if(helper != null && helper.getStyle(getContext()) != null) {
            if(helper.getStyle(getContext()).getItemShowType() == 1) {
                showSenderType = false;
            }else if(helper.getStyle(getContext()).getItemShowType() == 2) {
                showSenderType = true;
            }
        }
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);
        reactionContentView = findViewById(R.id.tv_subReactionContent);
        threadRegion = (EaseChatRowThreadRegion) findViewById(R.id.thread_region);
        selectRadio = (RadioButton) findViewById(R.id.rb_select);
        editView = (TextView) findViewById(R.id.tv_edit);

        setLayoutStyle();

        mainThreadHandler = new Handler(Looper.getMainLooper());
        onFindViewById();
    }

    protected void setLayoutStyle() {
        EaseChatItemStyleHelper helper = getItemStyleHelper();
        if(helper != null) {
            EaseChatSetStyle itemStyle = helper.getStyle(getContext());
            if(bubbleLayout != null) {
                try {
                    if (isSender()) {
                        Drawable senderBgDrawable = itemStyle.getSenderBgDrawable();
                        if(senderBgDrawable != null) {
                            bubbleLayout.setBackground(senderBgDrawable.getConstantState().newDrawable());
                        }
                    } else {
                        Drawable receiverBgDrawable = itemStyle.getReceiverBgDrawable();
                        if (receiverBgDrawable != null) {
                            bubbleLayout.setBackground(receiverBgDrawable.getConstantState().newDrawable());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(timeStampView != null) {
                if(itemStyle.getTimeBgDrawable() != null) {
                    timeStampView.setBackground(itemStyle.getTimeBgDrawable().getConstantState().newDrawable());
                }
                if(itemStyle.getTimeTextSize() != 0) {
                    timeStampView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTimeTextSize());
                }
                if(itemStyle.getTimeTextColor() != 0) {
                    timeStampView.setTextColor(itemStyle.getTimeTextColor());
                }
            }
            TextView content = findViewById(R.id.tv_chatcontent);
            if(content != null) {
                if(itemStyle.getTextSize() != 0) {
                    content.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTextSize());
                }
                if(itemStyle.getTextColor() != 0) {
                    content.setTextColor(itemStyle.getTextColor());
                }
            }
            if(editView!=null) {
                editView.setVisibility(GONE);
            }
        }
    }

    /**
     * update view
     * @param msg
     */
    public void updateView(final ChatMessage msg) {
        if(chatCallback == null) {
            chatCallback = new EaseChatCallback();
        }
        msg.setMessageStatusCallback(chatCallback);
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                onViewUpdate(msg);
            }
        });
    }

    /**
     * set property according message and position
     * the method should be called by child
     *
     * @param message
     * @param position
     */
    public void setUpView(ChatMessage message, int position) {
        this.message = message;
        this.position = position;

        setUpBaseView();
        onSetUpView();
        onSetUpReactionView();
        //setLayoutStyle();
        setClickListener();
    }

    /**
     * set timestamp, avatar, nickname and so on
     */
    private void setUpBaseView() {
        setTimestamp();
        setItemStyle();
        setAvatarAndNick();
        updateSenderMessageViewStatus();
        setThreadRegion();

        if(selectRadio != null) {
            selectRadio.setVisibility(EaseChatMessageMultiSelectHelper.getInstance().isMultiStyle(getContext()) ? VISIBLE : GONE);
            selectRadio.setChecked(EaseChatMessageMultiSelectHelper.getInstance().isContainsMessage(getContext(), message));
        }

        if( editView != null) {
            if (message.getBody().operationCount() > 0) {
                editView.setVisibility(VISIBLE);
            } else {
                editView.setVisibility(GONE);
            }
        }
    }

    /**
     * Update sender message view's status
     */
    public void updateSenderMessageViewStatus() {
        if(message == null || !isSender()) {
            return;
        }
        if(deliveredView != null) {
            deliveredView.setVisibility(View.INVISIBLE);
            if(message.status() == ChatMessage.Status.SUCCESS) {
                deliveredView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ease_msg_status_sent), null);
                deliveredView.setVisibility(VISIBLE);
            }
            if (ChatClient.getInstance().getOptions().getRequireDeliveryAck() && message.isDelivered()) {
                deliveredView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.ease_msg_status_received), null);
                deliveredView.setVisibility(View.VISIBLE);
            }
        }

        if(ackedView != null && ChatClient.getInstance().getOptions().getRequireAck()) {
            ackedView.setVisibility(View.INVISIBLE);
            if(message.isAcked()) {
                if(deliveredView != null) {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
                ackedView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setThreadRegion() {
        if(threadRegion == null) {
            return;
        }
        if(shouldShowThreadRegion()) {
            threadRegion.setVisibility(VISIBLE);
            threadRegion.setThreadInfo(message.getChatThread());
        }else {
            threadRegion.setVisibility(GONE);
        }
    }

    /**
     * If need to show thread region
     * @return
     */
    public boolean shouldShowThreadRegion() {
        return message != null && message.getChatThread() != null;
    }

    /**
     * set item's style by easeMessageListItemStyle
     */
    private void setItemStyle() {
        EaseChatItemStyleHelper helper = getItemStyleHelper();
        if(helper != null) {
            EaseChatSetStyle itemStyle = helper.getStyle(getContext());
            if(userAvatarView != null) {
                setAvatarOptions(itemStyle);
            }
            if(usernickView != null && itemStyle != null) {
                //If displayed on the same side, the nickname needs to be displayed
                if(itemStyle.getItemShowType() == 1 || itemStyle.getItemShowType() == 2) {
                    usernickView.setVisibility(VISIBLE);
                }else {
                    //If it is not on the same side, the nickname is displayed based on the judgment
                    usernickView.setVisibility((itemStyle.isShowNickname() && message.direct() == ChatMessage.Direct.RECEIVE) ? VISIBLE : GONE);
                }
            }
            if(bubbleLayout != null) {
                if(message.getType() == ChatMessage.Type.TXT) {
                    if(itemStyle != null && itemStyle.getItemMinHeight() != 0) {
                        bubbleLayout.setMinimumHeight(itemStyle.getItemMinHeight());
                    }
                }
            }
        }
    }

    private EaseChatItemStyleHelper getItemStyleHelper() {
        return EaseChatItemStyleHelper.getInstance();
    }

    /**
     * set avatar options
     * @param itemStyle
     */
    protected void setAvatarOptions(EaseChatSetStyle itemStyle) {
        if(itemStyle == null) {
            return;
        }
        if (itemStyle.isShowAvatar()) {
            userAvatarView.setVisibility(View.VISIBLE);
            if(userAvatarView instanceof EaseImageView) {
                EaseImageView avatarView = (EaseImageView) userAvatarView;
                if(itemStyle.getAvatarDefaultSrc() != null) {
                    avatarView.setImageDrawable(itemStyle.getAvatarDefaultSrc());
                }
                avatarView.setShapeType(itemStyle.getShapeType());
                if(itemStyle.getAvatarSize() != 0) {
                    ViewGroup.LayoutParams params = avatarView.getLayoutParams();
                    params.width = (int) itemStyle.getAvatarSize();
                    params.height = (int) itemStyle.getAvatarSize();
                }
                if(itemStyle.getBorderWidth() != 0) {
                    avatarView.setBorderWidth((int) itemStyle.getBorderWidth());
                }
                if(itemStyle.getBorderColor() != 0) {
                    avatarView.setBorderColor(itemStyle.getBorderColor());
                }
                if(itemStyle.getAvatarRadius() != 0) {
                    avatarView.setRadius((int) itemStyle.getAvatarRadius());
                }
            }
            EaseAvatarOptions avatarOptions = provideAvatarOptions();
            if(avatarOptions != null && userAvatarView instanceof EaseImageView){
                EaseImageView avatarView = ((EaseImageView)userAvatarView);
                if(avatarOptions.getAvatarShape() != 0)
                    avatarView.setShapeType(avatarOptions.getAvatarShape());
                if(avatarOptions.getAvatarBorderWidth() != 0)
                    avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
                if(avatarOptions.getAvatarBorderColor() != 0)
                    avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
                if(avatarOptions.getAvatarRadius() != 0)
                    avatarView.setRadius(avatarOptions.getAvatarRadius());
            }
        } else {
            userAvatarView.setVisibility(View.GONE);
        }
        if(itemStyle.isHideReceiveAvatar() && message.direct() == ChatMessage.Direct.RECEIVE) {
            userAvatarView.setVisibility(GONE);
        }
        if(itemStyle.isHideSendAvatar() && message.direct() == ChatMessage.Direct.SEND) {
            userAvatarView.setVisibility(GONE);
        }
    }

    /**
     * Set chat item include image which contains thread region
     * @param imageView
     */
    public void setImageIncludeThread(ImageView imageView) {
        if(shouldShowThreadRegion()) {
            if(isSender()) {
                Drawable senderBgDrawable = EaseChatItemStyleHelper.getSenderBgDrawable(getContext());
                if(senderBgDrawable != null) {
                    bubbleLayout.setBackground(senderBgDrawable.getConstantState().newDrawable());
                }else {
                    bubbleLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ease_send_message_et_bg));
                }
            }else {
                Drawable receiverBgDrawable = EaseChatItemStyleHelper.getReceiverBgDrawable(getContext());
                if(receiverBgDrawable != null) {
                    bubbleLayout.setBackground(receiverBgDrawable.getConstantState().newDrawable());
                }else {
                    bubbleLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ease_chat_bubble_receive_bg));
                }
            }
            int[] marginArray = getContext().getResources().getIntArray(R.array.ease_chat_image_margin_include_thread);
            if(marginArray != null && marginArray.length == 4) {
                int leftMargin = Math.max(marginArray[0], 0);
                int topMargin = Math.max(marginArray[1], 0);
                int rightMargin = Math.max(marginArray[2], 0);
                int bottomMargin = Math.max(marginArray[3], 0);
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                if(layoutParams instanceof ConstraintLayout.LayoutParams) {
                    ((ConstraintLayout.LayoutParams) layoutParams).leftMargin = (int) EaseUtils.dip2px(getContext(),
                            leftMargin);
                    ((ConstraintLayout.LayoutParams) layoutParams).topMargin = (int) EaseUtils.dip2px(getContext(),
                            topMargin);
                    ((ConstraintLayout.LayoutParams) layoutParams).rightMargin = (int) EaseUtils.dip2px(getContext(),
                            rightMargin);
                    ((ConstraintLayout.LayoutParams) layoutParams).bottomMargin = (int) EaseUtils.dip2px(getContext(),
                            bottomMargin);
                }
            }
        }
    }
    /**
     *
     * @return
     */
    protected EaseAvatarOptions provideAvatarOptions() {
        return EaseUIKit.getInstance().getAvatarOptions();
    }

    /**
     * Whether is the sender
     * @return
     */
    public boolean isSender() {
        return isSender;
    }

    /**
     * set avatar and nickname
     */
    protected void setAvatarAndNick() {
        if(userAvatarView == null || message == null) {
            return;
        }
        if (isSender()) {
            EaseUserUtils.setUserAvatar(context, ChatClient.getInstance().getCurrentUser(), userAvatarView);
            if(EaseChatItemStyleHelper.getInstance().getStyle(getContext()).getItemShowType() != EaseChatMessageListLayout.ShowType.LEFT_RIGHT.ordinal()) {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
        } else {
            if (message.getChatType() == ChatMessage.ChatType.GroupChat){
                EaseUserUtils.setUserAvatar(context, message.conversationId(), message.getFrom(), userAvatarView);
                EaseUserUtils.setUserNick(message.conversationId(),message.getFrom(), usernickView);
            }else {
                EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
        }
    }

    /**
     * set timestamp
     */
    protected void setTimestamp() {
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if(timestamp == null || message == null || adapter == null) {
            return;
        }
        if (position == 0) {
            timestamp.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // show time stamp if interval with last message is > 30 seconds
            ChatMessage prevMessage = null;
            if(adapter instanceof BaseAdapter) {
                prevMessage = (ChatMessage) ((BaseAdapter)adapter).getItem(position - 1);
            }
            if(adapter instanceof EaseBaseAdapter) {
                prevMessage = (ChatMessage) ((EaseBaseAdapter)adapter).getItem(position - 1);
            }

            setOtherTimestamp(prevMessage);
        }
    }

    public void setTimestamp(ChatMessage preMessage) {
        if (position == 0) {
            timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
            timeStampView.setVisibility(View.VISIBLE);
        } else {
            setOtherTimestamp(preMessage);
        }
    }

    public void setOtherTimestamp(ChatMessage preMessage) {
        if (preMessage != null && EaseDateUtils.isCloseEnough(message.getMsgTime(), preMessage.getMsgTime())) {
            timeStampView.setVisibility(View.GONE);
        } else {
            timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
            timeStampView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return EaseChatMessageMultiSelectHelper.getInstance().isMultiStyle(getContext());
    }

    /**
     * set click listener
     */
    private void setClickListener() {
        chatCallback = new EaseChatCallback();
        if(bubbleLayout != null){
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)){
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        return itemClickListener.onBubbleLongClick(v, message);
                    }
                    return false;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onResendClick(message)){
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onResendClick(message);
                    }
                }
            });
        }

        if(userAvatarView != null){
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == ChatMessage.Direct.SEND) {
                            itemClickListener.onUserAvatarClick(ChatClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(itemClickListener != null){
                        if (message.direct() == ChatMessage.Direct.SEND) {
                            itemClickListener.onUserAvatarLongClick(ChatClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        if(threadRegion != null) {
            threadRegion.setOnClickListener(v -> {
                ChatThread info = message.getChatThread();
                if(info != null && !TextUtils.isEmpty(info.getChatThreadId())) {
                    if (itemClickListener != null && itemClickListener.onThreadClick(message.getMsgId(), info.getChatThreadId())){
                        return;
                    }
                    EaseActivityProviderHelper.startToChatThreadActivity(context, info.getChatThreadId(), message.getMsgId(), info.getParentId());
                }else {
                    EMLog.e(TAG, "message's thread info is null");
                }
            });
            threadRegion.setOnLongClickListener(v -> {
                ChatThread info = message.getChatThread();
                if(info != null && !TextUtils.isEmpty(info.getChatThreadId())) {
                    if (itemClickListener != null) {
                        return itemClickListener.onThreadLongClick(v, message.getMsgId(), info.getChatThreadId());
                    }
                }else {
                    EMLog.e(TAG, "message's thread info is null");
                }
                return false;
            });
        }
        if(selectRadio != null && EaseChatMessageMultiSelectHelper.getInstance().isMultiStyle(getContext())) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = selectRadio.isChecked();
                    selectRadio.setChecked(!checked);
                    if(!checked) {
                        EaseChatMessageMultiSelectHelper.getInstance().addChatMessage(getContext(), message);
                    }else {
                        EaseChatMessageMultiSelectHelper.getInstance().removeChatMessage(getContext(), message);
                    }
                }
            });
        }
    }

    /**
     * refresh view when message status change
     */
    protected void onViewUpdate(ChatMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
            default:
                EMLog.i(TAG, "default");
                break;
        }
    }

    protected void onSetUpReactionView() {
        if (null == message || null == reactionContentView) {
            EMLog.e(TAG, "view is null, don't setup reaction view");
            return;
        }

        EaseReactionOptions reactionOptions = EaseUIKit.getInstance().getReactionOptions();
        if (null == reactionOptions || !reactionOptions.isOpen()) {
            EMLog.i(TAG, "reaction option don't show reaction view");
            reactionContentView.setVisibility(GONE);
            return;
        }

        reactionContentView.updateMessageInfo(message);
        reactionContentView.setOnReactionItemListener(new EaseChatReactionView.OnReactionItemListener() {
            @Override
            public void removeReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (itemClickListener != null) {
                    itemClickListener.onRemoveReaction(message, reactionEntity);
                }
            }

            @Override
            public void addReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (itemClickListener != null) {
                    itemClickListener.onAddReaction(message, reactionEntity);
                }
            }
        });
    }

    private class EaseChatCallback implements CallBack {

        @Override
        public void onSuccess() {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageSuccess();
                    if(messageResultCallback != null) {
                        messageResultCallback.onMessageSuccess(message);
                    }
                }
            });
        }

        @Override
        public void onError(int code, String error) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageError();
                    postMessageError(message, code, error);
                }
            });
        }

        @Override
        public void onProgress(int progress, String status) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageInProgress();
                    if(messageResultCallback != null) {
                        messageResultCallback.onMessageInProgress(message, progress);
                    }
                }
            });
        }
    }

    public void postMessageError(ChatMessage message, int code, String error) {
        if(messageResultCallback != null) {
            messageResultCallback.onMessageError(message, code, error);
        }
    }

    /**
     * message create status
     */
    protected void onMessageCreate() {
        EMLog.i(TAG, "onMessageCreate");
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    /**
     * message success status
     */
    protected void onMessageSuccess() {
        EMLog.i(TAG, "onMessageSuccess");
        updateSenderMessageViewStatus();
        showSuccessStatus();
    }

    /**
     * message fail status
     */
    protected void onMessageError() {
        if(ackedView != null) {
            ackedView.setVisibility(INVISIBLE);
        }
        if(deliveredView != null) {
            deliveredView.setVisibility(INVISIBLE);
        }

        if(editView!=null) {
            editView.setVisibility(GONE);
        }
        EMLog.e(TAG, "onMessageError");
        showErrorStatus();
    }

    /**
     * message in progress status
     */
    protected void onMessageInProgress() {
        EMLog.i(TAG, "onMessageInProgress");
        showInProgressStatus();
    }

    /**
     * Show progress bar.
     */
    protected void showInProgressStatus() {
        if(progressBar != null && progressBar.getVisibility() != VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (percentageView != null) {
            percentageView.setVisibility(View.VISIBLE);
            percentageView.setText(message.progress() + "%");
        }
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error status.
     */
    protected void showErrorStatus() {
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.VISIBLE);
    }

    /**
     * Show success status.
     */
    protected void showSuccessStatus() {
        if(progressBar != null) {
            progressBar.setVisibility(INVISIBLE);
        }
        if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    /**
     * Set message item click listeners.
     * @param listener
     */
    public void setOnMessageListItemClickListener(OnMessageListItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * Set message result callback.
     * @param callback
     */
    public void setMessageResultCallback(MessageResultCallback callback) {
        messageResultCallback = callback;
    }

    /**
     * Set message action callback.
     * @param callback
     */
    public void setChatRowActionCallback(EaseChatRowActionCallback callback) {
        itemActionCallback = callback;
    }

    /**
     * inflate view, child should implement it
     */
    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * setup view
     *
     */
    protected abstract void onSetUpView();

    /**
     * row action call back
     */
    public interface EaseChatRowActionCallback {
        /**
         * click resend action
         * @param message
         */
        void onResendClick(ChatMessage message);

        /**
         * click bubble layout
         * @param message
         */
        void onBubbleClick(ChatMessage message);

        /**
         * when view detach from window
         */
        void onDetachedFromWindow();
    }
}
