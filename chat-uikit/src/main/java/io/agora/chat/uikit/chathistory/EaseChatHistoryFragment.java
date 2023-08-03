package io.agora.chat.uikit.chathistory;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.CombineMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.interfaces.ChatQuoteMessageProvider;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.manager.EaseChatInterfaceManager;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class EaseChatHistoryFragment extends EaseBaseFragment implements ChatQuoteMessageProvider {
    public EaseChatHistoryLayout chatLayout;
    public EaseTitleBar titleBar;
    private EaseMessageAdapter messageAdapter;
    private ChatMessage combineMessage;
    private EaseTitleBar.OnBackPressListener backPressListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return inflater.inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.ease_fragment_chat_history;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();
    }

    public void initArguments() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            combineMessage = (ChatMessage) bundle.getParcelable(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE);
        }
    }

    public void initView() {
        titleBar = findViewById(R.id.title_bar);
        chatLayout = findViewById(R.id.layout_chat);
        if(this.messageAdapter != null) {
            chatLayout.getChatMessageListLayout().setMessageAdapter(this.messageAdapter);
        }

        Bundle bundle = getArguments();
        if(bundle != null) {
            boolean useHeader = bundle.getBoolean(Constant.KEY_USE_TITLE, false);
            titleBar.setVisibility(useHeader ? View.VISIBLE : View.GONE);
            if(useHeader) {
                String title = bundle.getString(Constant.KEY_SET_TITLE, "");
                if(!TextUtils.isEmpty(title)) {
                    titleBar.setTitle(title);
                }

                String subTitle = bundle.getString(Constant.KEY_SET_SUB_TITLE, "");
                if(!TextUtils.isEmpty(subTitle)) {
                    titleBar.setSubTitle(subTitle);
                    titleBar.getSubTitle().setVisibility(View.VISIBLE);
                }

                boolean canBack = bundle.getBoolean(Constant.KEY_ENABLE_BACK, false);
                titleBar.setDisplayHomeAsUpEnabled(canBack);

                titleBar.setOnBackPressListener(backPressListener != null ? backPressListener : new EaseTitleBar.OnBackPressListener() {
                    @Override
                    public void onBackPress(View view) {
                        mContext.onBackPressed();
                    }
                });
            }

            int timeColor = bundle.getInt(Constant.KEY_MSG_TIME_COLOR, -1);
            if(timeColor != -1) {
                chatLayout.getChatMessageListLayout().setTimeTextColor(timeColor);
            }
            int timeTextSize = bundle.getInt(Constant.KEY_MSG_TIME_SIZE, -1);
            if(timeTextSize != -1) {
                chatLayout.getChatMessageListLayout().setTimeTextSize(timeTextSize);
            }
            int leftBubbleBg = bundle.getInt(Constant.KEY_MSG_LEFT_BUBBLE, -1);
            if(leftBubbleBg != -1) {
                chatLayout.getChatMessageListLayout().setItemReceiverBackground(ContextCompat.getDrawable(mContext, leftBubbleBg));
            }
            int rightBubbleBg = bundle.getInt(Constant.KEY_MSG_RIGHT_BUBBLE, -1);
            if(rightBubbleBg != -1) {
                chatLayout.getChatMessageListLayout().setItemSenderBackground(ContextCompat.getDrawable(mContext, rightBubbleBg));
            }
            int chatBg = bundle.getInt(Constant.KEY_CHAT_BACKGROUND, -1);
            if(chatBg != -1) {
                chatLayout.getChatMessageListLayout().setBackgroundResource(chatBg);
            }
            int emptyLayout = bundle.getInt(Constant.KEY_EMPTY_LAYOUT, -1);
            if(emptyLayout != -1) {
                chatLayout.getChatMessageListLayout().getMessageAdapter().setEmptyView(emptyLayout);
            }
        }
    }

    public void initListener() {
        EaseChatInterfaceManager.getInstance().setInterface(mContext, ChatQuoteMessageProvider.class.getSimpleName(), this);
    }

    public void initData() {
        if(combineMessage != null) {
            CombineMessageBody combineBody = (CombineMessageBody) combineMessage.getBody();
            if(combineBody != null) {
                titleBar.setTitle(combineBody.getTitle());
            }
        }
        chatLayout.loadData(combineMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EaseChatInterfaceManager.getInstance().removeInterface(mContext, ChatQuoteMessageProvider.class.getSimpleName());
    }

    private void setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
        this.backPressListener = listener;
    }

    private void setCustomAdapter(EaseMessageAdapter adapter) {
        this.messageAdapter = adapter;
    }

    @Override
    public SpannableString provideQuoteContent(ChatMessage quoteMessage, ChatMessage.Type quoteMsgType, String quoteSender, String quoteContent) {
        return null;
    }

    public static class Builder {
        protected final Bundle bundle;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private EaseMessageAdapter adapter;
        protected EaseChatHistoryFragment customFragment;

        /**
         * Constructor
         */
        public Builder() {
            this.bundle = new Bundle();
        }

        /**
         * Constructor
         * @param message Agora Chat combine message
         */
        public Builder(ChatMessage message) {
            this.bundle = new Bundle();
            bundle.putParcelable(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE, message);
        }

        /**
         * Set the param of combine message.
         * @param message
         * @return
         */
        public Builder setCombineMessage(ChatMessage message) {
            this.bundle.putParcelable(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE, message);
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
         * Set titleBar's sub title
         * @param subTitle
         * @return
         */
        public Builder setHeaderSubTitle(String subTitle) {
            this.bundle.putString(Constant.KEY_SET_SUB_TITLE, subTitle);
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
         * If you have set {@link Builder#enableHeaderPressBack(boolean)}, you can set the listener
         * @param listener
         * @return
         */
        public Builder setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
            this.backPressListener = listener;
            return this;
        }

        /**
         * Set the text color of message item time
         * @param color
         * @return
         */
        public Builder setMsgTimeTextColor(@ColorInt int color) {
            this.bundle.putInt(Constant.KEY_MSG_TIME_COLOR, color);
            return this;
        }

        /**
         * Set the text size of message item time, unit is px
         * @param size
         * @return
         */
        public Builder setMsgTimeTextSize(int size) {
            this.bundle.putInt(Constant.KEY_MSG_TIME_SIZE, size);
            return this;
        }

        /**
         * Set the bubble background of the received message
         * @param bgDrawable
         * @return
         */
        public Builder setReceivedMsgBubbleBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_MSG_LEFT_BUBBLE, bgDrawable);
            return this;
        }

        /**
         * Set the bubble background of the sent message
         * @param bgDrawable
         * @return
         */
        public Builder setSentBubbleBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_MSG_RIGHT_BUBBLE, bgDrawable);
            return this;
        }

        /**
         * Set the background of the chat list region
         * @param bgDrawable
         * @return
         */
        public Builder setChatBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_CHAT_BACKGROUND, bgDrawable);
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
         * Set custom fragment which should extends EaseMessageFragment
         * @param fragment
         * @param <T>
         * @return
         */
        public <T extends EaseChatHistoryFragment> Builder setCustomFragment(T fragment) {
            this.customFragment = fragment;
            return this;
        }

        /**
         * Set custom adapter which should extends EaseMessageAdapter
         * @param adapter
         * @return
         */
        public Builder setCustomAdapter(EaseMessageAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public EaseChatHistoryFragment build() {
            EaseChatHistoryFragment fragment = this.customFragment != null ? this.customFragment : new EaseChatHistoryFragment();
            fragment.setArguments(this.bundle);
            fragment.setHeaderBackPressListener(this.backPressListener);
            fragment.setCustomAdapter(this.adapter);
            return fragment;
        }
    }

    private static class Constant {
        static final String KEY_USE_TITLE = "key_use_title";
        static final String KEY_SET_TITLE = "key_set_title";
        static final String KEY_SET_SUB_TITLE = "key_set_sub_title";
        static final String KEY_EMPTY_LAYOUT = "key_empty_layout";
        static final String KEY_ENABLE_BACK = "key_enable_back";
        static final String KEY_MSG_TIME_COLOR = "key_msg_time_color";
        static final String KEY_MSG_TIME_SIZE = "key_msg_time_size";
        static final String KEY_MSG_LEFT_BUBBLE = "key_msg_left_bubble";
        static final String KEY_MSG_RIGHT_BUBBLE = "key_msg_right_bubble";
        static final String KEY_CHAT_BACKGROUND = "key_chat_background";
    }
}
