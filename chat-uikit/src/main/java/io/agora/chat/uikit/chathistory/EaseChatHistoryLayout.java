package io.agora.chat.uikit.chathistory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.chathistory.presenter.EaseChatHistoryPresenter;
import io.agora.chat.uikit.chathistory.presenter.EaseChatHistoryPresenterImpl;
import io.agora.chat.uikit.chathistory.presenter.IChatHistoryLayoutView;
import io.agora.chat.uikit.chathistory.presenter.IHandleChatHistory;

public class EaseChatHistoryLayout extends RelativeLayout implements IChatHistoryLayoutView, IHandleChatHistory {
    private EaseChatMessageListLayout messageListLayout;
    private EaseChatHistoryPresenter mPresenter;
    private Context mContext;
    private EaseMessageAdapter messageAdapter;

    public EaseChatHistoryLayout(Context context) {
        this(context, null);
    }

    public EaseChatHistoryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatHistoryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat_history, this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        messageListLayout = findViewById(R.id.layout_chat_message);
        messageListLayout.canUseDefaultRefresh(false);
    }

    private void initListener() {

    }

    private void initData() {
        mPresenter = new EaseChatHistoryPresenterImpl();
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(mPresenter);
        }
        mPresenter.attachView(this);
        messageAdapter = new EaseChatHistoryAdapter();
        messageListLayout.setMessageAdapter(messageAdapter);
    }

    public void loadData(ChatMessage message) {
        if(message == null) {
            finishCurrentChat();
            return;
        }
        if(message.getType() != ChatMessage.Type.COMBINE) {
            finishCurrentChat();
            return;
        }
        mPresenter.downloadCombineMessage(message);
    }

    protected void finishCurrentChat() {

    }

    @Override
    public void downloadCombinedMessagesSuccess(List<ChatMessage> messageList) {
        messageListLayout.setData(messageList);
    }

    @Override
    public void downloadCombinedMessagesFailed(int error, String errorMsg) {

    }

    @Override
    public void downloadThumbnailSuccess(ChatMessage message, int position) {
        messageListLayout.refreshMessage(message);
    }

    @Override
    public void downloadThumbnailFailed(ChatMessage message, int position, int error, String errorMsg) {

    }

    @Override
    public void downloadVoiceSuccess(ChatMessage message, int position) {
        messageListLayout.refreshMessage(message);
    }

    @Override
    public void downloadVoiceFailed(ChatMessage message, int position, int error, String errorMsg) {

    }

    @Override
    public void refreshAll() {
        messageListLayout.refreshMessages();
    }

    @Override
    public void refreshItem(ChatMessage message, int position) {
        messageListLayout.refreshMessage(message);
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public void setPresenter(EaseChatHistoryPresenter presenter) {
        this.mPresenter = presenter;
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(mPresenter);
        }
        mPresenter.attachView(this);
    }

    @Override
    public void setMessageAdapter(EaseMessageAdapter adapter) {
        if(adapter != null) {
            this.messageAdapter = adapter;
            messageListLayout.setMessageAdapter(messageAdapter);
        }
    }

    @Override
    public EaseChatMessageListLayout getChatMessageListLayout() {
        return messageListLayout;
    }
}
