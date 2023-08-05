package io.agora.chat.uikit.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.manager.EaseChatMessageMultiSelectHelper;

public class EaseChatMultiSelectView extends FrameLayout implements IChatTopExtendMenu {
    private ImageView ivMultiSelectDelete;
    private ImageView ivMultiSelectForward;
    private OnDismissListener dismissListener;
    private OnSelectClickListener clickListener;
    private EaseMessageAdapter messageAdapter;

    public EaseChatMultiSelectView(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatMultiSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatMultiSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_chat_messages_multi_select, this);
        initView();
        initListener();
    }

    private void initView() {
        ivMultiSelectDelete = findViewById(R.id.iv_multi_select_delete);
        ivMultiSelectForward = findViewById(R.id.iv_multi_select_forward);
    }

    private void initListener() {
        ivMultiSelectDelete.setOnClickListener(v -> {
            List<String> sortedMessages = EaseChatMessageMultiSelectHelper.getInstance().getSortedMessages(getContext());
            if(clickListener != null) {
                clickListener.onMultiDeleteClick(sortedMessages);
            }
        });
        ivMultiSelectForward.setOnClickListener(v -> {
            List<String> sortedMessages = EaseChatMessageMultiSelectHelper.getInstance().getSortedMessages(getContext());
            if(clickListener != null) {
                clickListener.onMultiReplyClick(sortedMessages);
            }
        });
    }

    public void dismissSelectView(View v) {
        cancelSelectModel();
        setDismissListener(v);
    }

    public void cancelSelectModel() {
        showTopExtendMenu(false);
        EaseChatMessageMultiSelectHelper.getInstance().setMultiStyle(getContext(), false);
        notifyAdapter();
    }

    public void dismiss() {
        cancelSelectModel();
        setDismissListener(null);
    }

    public void setupWithAdapter(EaseMessageAdapter adapter) {
        this.messageAdapter = adapter;
    }

    private void setDismissListener(View view) {
        if(dismissListener != null) {
            dismissListener.onDismiss(view);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EaseChatMessageMultiSelectHelper.getInstance().init(getContext());
        EaseChatMessageMultiSelectHelper.getInstance().setMultiStyle(getContext(), true);
        notifyAdapter();
    }

    private void notifyAdapter() {
        if(messageAdapter != null) {
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        messageAdapter = null;
        EaseChatMessageMultiSelectHelper.getInstance().clear(getContext());
    }

    @Override
    public void showTopExtendMenu(boolean isShow) {
        this.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setOnDismissListener(OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setOnSelectClickListener(OnSelectClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnDismissListener {
        void onDismiss(View view);
    }

    public interface OnSelectClickListener {
        /**
         * Click the delete event.
         * @param deleteMsgIdList
         * @return
         */
        void onMultiDeleteClick(List<String> deleteMsgIdList);

        /**
         * Click the reply event.
         * @param replyMsgIdList
         * @return
         */
        void onMultiReplyClick(List<String> replyMsgIdList);
    }
}
