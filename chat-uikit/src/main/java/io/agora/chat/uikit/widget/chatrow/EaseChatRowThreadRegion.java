package io.agora.chat.uikit.widget.chatrow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.provider.EaseUserProfileProvider;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseImageView;

public class EaseChatRowThreadRegion extends FrameLayout implements View.OnClickListener {
    private static final String TAG = EaseChatRowThreadRegion.class.getSimpleName();
    private ImageView ivThreadIcon;
    private TextView tvThreadName;
    private TextView tvThreadMsgCount;
    private ImageView ivRightIcon;
    private EaseImageView ivUserIcon;
    private TextView tvMsgUser;
    private TextView tvMsgTime;
    private TextView tvMsgContent;
    private Group group_msg;
    private TextView tv_no_msg;
    private ChatThread info;

    private OnClickListener listener;

    public EaseChatRowThreadRegion(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatRowThreadRegion(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatRowThreadRegion(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat_thread_region, this);
        ivThreadIcon = findViewById(R.id.iv_thread_icon);
        tvThreadName = findViewById(R.id.tv_thread_name);
        tvThreadMsgCount = findViewById(R.id.tv_thread_msg_count);
        ivRightIcon = findViewById(R.id.iv_thread_right_icon);
        ivUserIcon = findViewById(R.id.iv_user_icon);
        tvMsgUser = findViewById(R.id.tv_msg_username);
        tvMsgTime = findViewById(R.id.tv_msg_time);
        tvMsgContent = findViewById(R.id.tv_msg_content);
        group_msg = findViewById(R.id.group_msg);
        tv_no_msg = findViewById(R.id.tv_no_msg);
        initAttr(context, attrs);
        EaseUserUtils.setUserAvatarStyle(ivUserIcon);
        if(info != null) {
            setThreadRegion(info);
        }
        ivRightIcon.setOnClickListener(this);
        tvThreadMsgCount.setOnClickListener(this);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatRowThreadRegion);
            Drawable threadLabel = ta.getDrawable(R.styleable.EaseChatRowThreadRegion_thread_region_label);
            if(threadLabel != null) {
                ivThreadIcon.setImageDrawable(threadLabel);
            }

            int threadTitle = ta.getResourceId(R.styleable.EaseChatRowThreadRegion_thread_region_title, -1);
            if (threadTitle != -1) {
                tvThreadName.setText(threadTitle);
            } else {
                String title = ta.getString(R.styleable.EaseChatRowThreadRegion_thread_region_title);
                tvThreadName.setText(title);
            }

            int threadMsgCount = ta.getResourceId(R.styleable.EaseChatRowThreadRegion_thread_region_msg_count, -1);
            if (threadMsgCount != -1) {
                tvThreadMsgCount.setText(threadMsgCount);
            } else {
                String msgCount = ta.getString(R.styleable.EaseChatRowThreadRegion_thread_region_msg_count);
                tvThreadMsgCount.setText(msgCount);
            }

            Drawable rightIcon = ta.getDrawable(R.styleable.EaseChatRowThreadRegion_thread_region_right_icon);
            if(rightIcon != null) {
                ivRightIcon.setImageDrawable(rightIcon);
            }

            Drawable userSrc = ta.getDrawable(R.styleable.EaseChatRowThreadRegion_thread_region_user_src);
            if(userSrc != null) {
                ivUserIcon.setImageDrawable(userSrc);
            }

            int threadContentRes = ta.getResourceId(R.styleable.EaseChatRowThreadRegion_thread_region_content, -1);
            if (threadContentRes != -1) {
                tvMsgUser.setText(threadContentRes);
            } else {
                String threadContent = ta.getString(R.styleable.EaseChatRowThreadRegion_thread_region_content);
                tvMsgUser.setText(threadContent);
            }

            int threadTimeRes = ta.getResourceId(R.styleable.EaseChatRowThreadRegion_thread_region_time, -1);
            if (threadTimeRes != -1) {
                tvMsgTime.setText(threadTimeRes);
            } else {
                String threadContent = ta.getString(R.styleable.EaseChatRowThreadRegion_thread_region_time);
                tvMsgTime.setText(threadContent);
            }
        }
    }

    public void setThreadInfo(ChatThread info) {
        this.info = info;
        setThreadRegion(info);
        requestLayout();
    }

    private void setThreadRegion(ChatThread info) {
        if(info == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        String threadName = info.getChatThreadName();
        if(!TextUtils.isEmpty(threadName)) {
            tvThreadName.setText(threadName);
        }
        String num = EaseUtils.handleBigNum(info.getMessageCount());
        tvThreadMsgCount.setText(num);

        ChatMessage msgOverview = info.getLastMessage();
        if(msgOverview == null) {
            group_msg.setVisibility(GONE);
            tv_no_msg.setVisibility(VISIBLE);
            return;
        }
        group_msg.setVisibility(VISIBLE);
        tv_no_msg.setVisibility(GONE);
        String nickname = msgOverview.getFrom();
        EaseUserProfileProvider userProvider = EaseUIKit.getInstance().getUserProvider();
        if(userProvider != null && !TextUtils.isEmpty(nickname)) {
            EaseUserUtils.setUserAvatar(getContext(), msgOverview.getFrom(), null, null, ivUserIcon);
            EaseUser user = userProvider.getUser(msgOverview.getFrom());
            if(user != null) {
                nickname = user.getNickname();
            }
        }

        tvMsgUser.setText(nickname);
        Spannable span = EaseSmileUtils.getSmiledText(getContext(), EaseUtils.getMessageDigest(msgOverview, getContext()));
        tvMsgContent.setText(span, TextView.BufferType.SPANNABLE);

        tvMsgTime.setText(EaseDateUtils.getTimestampSimpleString(getContext(), msgOverview.getMsgTime()));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_thread_right_icon || v.getId() == R.id.tv_thread_msg_count) {
            if(listener != null) {
                listener.onClick(v);
            }
        }
    }

    /**
     * Set go label click listener
     * @param listener
     */
    public void setOnGoClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
