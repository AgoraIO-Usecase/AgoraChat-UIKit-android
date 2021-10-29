package io.agora.chat.uikit.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatInputChangeListener;
import io.agora.chat.uikit.chat.interfaces.OnChatItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatLayoutListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSendCallBack;
import io.agora.chat.uikit.chat.interfaces.OnOtherTypingListener;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.OnMenuChangeListener;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.util.EMLog;
import io.agora.util.PathUtil;
import io.agora.util.VersionUtils;

public class EaseChatFragment extends EaseBaseFragment implements OnChatLayoutListener, OnMenuChangeListener, OnAddMsgAttrsBeforeSendEvent, OnChatRecordTouchListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final String TAG = EaseChatFragment.class.getSimpleName();
    public EaseChatLayout chatLayout;
    public EaseTitleBar titleBar;
    public String conversationId;
    public int chatType;
    public String historyMsgId;
    public boolean isRoam;
    public boolean isMessageInit;
    private OnChatLayoutListener listener;

    protected File cameraFile;
    private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
    private OnChatInputChangeListener chatInputChangeListener;
    private OnChatItemClickListener chatItemClickListener;
    private OnMessageSendCallBack messageSendCallBack;
    private OnOtherTypingListener otherTypingListener;
    private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
    private OnChatRecordTouchListener recordTouchListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return inflater.inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.ease_fragment_chat_list;
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
            conversationId = bundle.getString(EaseConstant.EXTRA_CONVERSATION_ID);
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            historyMsgId = bundle.getString(EaseConstant.HISTORY_MSG_ID);
            isRoam = bundle.getBoolean(EaseConstant.EXTRA_IS_ROAM, false);
        }
    }

    public void initView() {
        titleBar = findViewById(R.id.title_bar);
        chatLayout = findViewById(R.id.layout_chat);
        chatLayout.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL);

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
                chatLayout.getChatMessageListLayout().setItemSenderBackground(ContextCompat.getDrawable(mContext, leftBubbleBg));
            }
            int rightBubbleBg = bundle.getInt(Constant.KEY_MSG_RIGHT_BUBBLE, -1);
            if(rightBubbleBg != -1) {
                chatLayout.getChatMessageListLayout().setItemReceiverBackground(ContextCompat.getDrawable(mContext, leftBubbleBg));
            }
            boolean showNickname = bundle.getBoolean(Constant.KEY_SHOW_NICKNAME, false);
            chatLayout.getChatMessageListLayout().showNickname(showNickname);
            String messageListShowType = bundle.getString(Constant.KEY_MESSAGE_LIST_SHOW_TYPE, "");
            if(!TextUtils.isEmpty(messageListShowType)) {
                EaseChatMessageListLayout.ShowType showType = EaseChatMessageListLayout.ShowType.valueOf(messageListShowType);
                if(showType != null) {
                    chatLayout.getChatMessageListLayout().setItemShowType(showType);
                }
            }
            // TODO: 2021/10/29 需要添加逻辑
            boolean hideLeftAvatar = bundle.getBoolean(Constant.KEY_HIDE_LEFT_AVATAR, true);
            boolean hideRightAvatar = bundle.getBoolean(Constant.KEY_HIDE_RIGHT_AVATAR, true);
            int chatBg = bundle.getInt(Constant.KEY_CHAT_BACKGROUND, -1);
            if(chatBg != -1) {
                chatLayout.getChatMessageListLayout().setBackgroundResource(chatBg);
            }
            String chatMenuStyle = bundle.getString(Constant.KEY_CHAT_MENU_STYLE, "");
            if(!TextUtils.isEmpty(chatMenuStyle)) {
                EaseInputMenuStyle menuStyle = EaseInputMenuStyle.valueOf(chatMenuStyle);
                if(menuStyle != null) {
                    chatLayout.getChatInputMenu().getPrimaryMenu().setMenuShowType(menuStyle);
                }
            }
            int inputBg = bundle.getInt(Constant.KEY_CHAT_MENU_INPUT_BG, -1);
            if(inputBg != -1) {
                chatLayout.getChatInputMenu().getPrimaryMenu().setMenuBackground(ContextCompat.getDrawable(mContext, inputBg));
            }
            String inputHint = bundle.getString(Constant.KEY_CHAT_MENU_INPUT_HINT, "");
            if(!TextUtils.isEmpty(inputHint)) {
                chatLayout.getChatInputMenu().getPrimaryMenu().getEditText().setHint(inputHint);
            }
        }
    }

    public void initListener() {
        chatLayout.setOnChatLayoutListener(listener != null ? listener : this);
        chatLayout.setOnPopupWindowItemClickListener(this);
        chatLayout.setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent != null ? sendMsgEvent : this);
        chatLayout.setOnChatRecordTouchListener(recordTouchListener != null ? recordTouchListener : this);
    }

    public void initData() {
        if(!TextUtils.isEmpty(historyMsgId)) {
            chatLayout.init(EaseChatMessageListLayout.LoadDataType.HISTORY, conversationId, chatType);
            chatLayout.loadData(historyMsgId);
        }else {
            if(isRoam) {
                chatLayout.init(EaseChatMessageListLayout.LoadDataType.ROAM, conversationId, chatType);
            }else {
                chatLayout.init(conversationId, chatType);
            }
            chatLayout.loadDefaultData();
        }
        isMessageInit = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshMessages();
        }
    }

    private void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    private void setOnChatExtendMenuItemClickListener(OnChatExtendMenuItemClickListener listener) {
        this.extendMenuItemClickListener = listener;
    }

    private void setOnChatInputChangeListener(OnChatInputChangeListener listener) {
        this.chatInputChangeListener = listener;
    }

    private void setOnChatItemClickListener(OnChatItemClickListener listener) {
        this.chatItemClickListener = listener;
    }

    private void setOnMessageSendCallBack(OnMessageSendCallBack callBack) {
        this.messageSendCallBack = callBack;
    }

    private void setOnOtherTypingListener(OnOtherTypingListener listener) {
        this.otherTypingListener = listener;
    }

    private void setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
        this.sendMsgEvent = sendMsgEvent;
    }

    private void setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
        this.recordTouchListener = recordTouchListener;
    }

    @Override
    public boolean onBubbleClick(ChatMessage message) {
        if(chatItemClickListener != null) {
            return chatItemClickListener.onBubbleClick(message);
        }
        return false;
    }

    @Override
    public boolean onBubbleLongClick(View v, ChatMessage message) {
        if(chatItemClickListener != null) {
            return chatItemClickListener.onBubbleLongClick(v, message);
        }
        return false;
    }

    @Override
    public void onUserAvatarClick(String username) {
        if(chatItemClickListener != null) {
            chatItemClickListener.onUserAvatarClick(username);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        if(chatItemClickListener != null) {
            chatItemClickListener.onUserAvatarLongClick(username);
        }
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        if(extendMenuItemClickListener != null && extendMenuItemClickListener.onChatExtendMenuItemClick(view, itemId)) {
            return;
        }
        if(itemId == R.id.extend_item_take_picture) {
            selectPicFromCamera();
        }else if(itemId == R.id.extend_item_picture) {
            selectPicFromLocal();
        }else if(itemId == R.id.extend_item_video) {
            selectVideoFromLocal();
        }else if(itemId == R.id.extend_item_file) {
            selectFileFromLocal();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(chatInputChangeListener != null) {
            chatInputChangeListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void onChatSuccess(ChatMessage message) {
        // you can do something after sending a successful message
        if(messageSendCallBack != null) {
            messageSendCallBack.onChatSuccess(message);
        }
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if(messageSendCallBack != null) {
            messageSendCallBack.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onOtherTyping(String action) {
        if(otherTypingListener != null) {
            otherTypingListener.onOtherTyping(action);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            chatLayout.getChatInputMenu().hideExtendContainer();
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data);
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data);
            }else if(requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            }
        }
    }

    /**
     * select picture from camera
     */
    protected void selectPicFromCamera() {
        if(!checkSdCardExist()) {
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), ChatClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);
    }

    /**
     * select local video
     */
    protected void selectVideoFromLocal() {

    }

    /**
     * select local file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent();
        if(VersionUtils.isTargetQ(getActivity())) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 相机返回处理结果
     * @param data
     */
    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            chatLayout.sendImageMessage(Uri.parse(cameraFile.getAbsolutePath()));
        }
    }

    /**
     * 选择本地图片处理结果
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendImageMessage(Uri.parse(filePath));
                }else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    chatLayout.sendImageMessage(selectedImage);
                }
            }
        }
    }

    protected void onActivityResultForDingMsg(@Nullable Intent data) {
        if(data != null) {
            String msgContent = data.getStringExtra("msg");
            EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
            // Send the ding-type msg.
            ChatMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent);
            chatLayout.sendMessage(dingMsg);
        }
    }

    /**
     * 本地文件选择结果处理
     * @param data
     */
    protected void onActivityResultForLocalFiles(@Nullable Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, uri);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendFileMessage(Uri.parse(filePath));
                }else {
                    EaseFileUtils.saveUriPermission(mContext, uri, data);
                    chatLayout.sendFileMessage(uri);
                }
            }
        }
    }

    /**
     * 检查sd卡是否挂载
     * @return
     */
    protected boolean checkSdCardExist() {
        return EaseUtils.isSdcardExist();
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, ChatMessage message) {

    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, ChatMessage message) {
        return false;
    }

    @Override
    public void addMsgAttrsBeforeSend(ChatMessage message) {

    }

    /**
     * Set whether can touch voice button
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onRecordTouch(View v, MotionEvent event) {
        return true;
    }

    public static class Builder {
        private final Bundle bundle;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private EaseMessageAdapter adapter;
        private OnChatLayoutListener listener;
        private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
        private OnChatInputChangeListener chatInputChangeListener;
        private OnChatItemClickListener chatItemClickListener;
        private OnMessageSendCallBack messageSendCallBack;
        private OnOtherTypingListener otherTypingListener;
        private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
        private OnChatRecordTouchListener recordTouchListener;

        public Builder(String conversationId, int chatType) {
            this.bundle = new Bundle();
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        }

        public Builder(String conversationId, int chatType, String historyMsgId) {
            this.bundle = new Bundle();
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
            bundle.putString(EaseConstant.HISTORY_MSG_ID, historyMsgId);
        }

        public Builder setUseHeader(boolean useTitle) {
            this.bundle.putBoolean(Constant.KEY_USE_TITLE, useTitle);
            return this;
        }

        public Builder setHeaderTitle(String title) {
            this.bundle.putString(Constant.KEY_SET_TITLE, title);
            return this;
        }

        public Builder setHeaderEnableBack(boolean canBack) {
            this.bundle.putBoolean(Constant.KEY_ENABLE_BACK, canBack);
            return this;
        }

        public Builder setUseRoamMessage(boolean useRoamMessage) {
            this.bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, useRoamMessage);
            return this;
        }

        public Builder setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
            this.backPressListener = listener;
            return this;
        }

        public Builder setOnChatLayoutListener(OnChatLayoutListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setOnChatExtendMenuItemClickListener(OnChatExtendMenuItemClickListener listener) {
            this.extendMenuItemClickListener = listener;
            return this;
        }

        public Builder setOnChatInputChangeListener(OnChatInputChangeListener listener) {
            this.chatInputChangeListener = listener;
            return this;
        }

        public Builder setOnChatItemClickListener(OnChatItemClickListener listener) {
            this.chatItemClickListener = listener;
            return this;
        }

        public Builder setOnMessageSendCallBack(OnMessageSendCallBack callBack) {
            this.messageSendCallBack = callBack;
            return this;
        }

        public Builder setOnOtherTypingListener(OnOtherTypingListener listener) {
            this.otherTypingListener = listener;
            return this;
        }

        public Builder setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
            this.sendMsgEvent = sendMsgEvent;
            return this;
        }

        public Builder setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
            this.recordTouchListener = recordTouchListener;
            return this;
        }

        public Builder setMsgTimeTextColor(@ColorInt int color) {
            this.bundle.putInt(Constant.KEY_MSG_TIME_COLOR, color);
            return this;
        }

        public Builder setMsgTimeTextSize(int size) {
            this.bundle.putInt(Constant.KEY_MSG_TIME_SIZE, size);
            return this;
        }

        public Builder setChatReceiveBubbleBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_MSG_LEFT_BUBBLE, bgDrawable);
            return this;
        }

        public Builder setChatSendBubbleBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_MSG_RIGHT_BUBBLE, bgDrawable);
            return this;
        }

        public Builder showChatNickname(boolean showNickname) {
            this.bundle.putBoolean(Constant.KEY_SHOW_NICKNAME, showNickname);
            return this;
        }

        public Builder setMessageListStyle(EaseChatMessageListLayout.ShowType showType) {
            this.bundle.putString(Constant.KEY_MESSAGE_LIST_SHOW_TYPE, showType.name());
            return this;
        }

        public Builder hideChatLeftAvatar(boolean hide) {
            this.bundle.putBoolean(Constant.KEY_HIDE_LEFT_AVATAR, hide);
            return this;
        }

        public Builder hideChatRightAvatar(boolean hide) {
            this.bundle.putBoolean(Constant.KEY_HIDE_RIGHT_AVATAR, hide);
            return this;
        }

        public Builder setChatBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_CHAT_BACKGROUND, bgDrawable);
            return this;
        }

        public Builder setChatMenuStyle(EaseInputMenuStyle style) {
            this.bundle.putString(Constant.KEY_CHAT_MENU_STYLE, style.name());
            return this;
        }

        public Builder setChatMenuInputBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_CHAT_MENU_INPUT_BG, bgDrawable);
            return this;
        }

        public Builder setChatMenuInputHint(String inputHint) {
            this.bundle.putString(Constant.KEY_CHAT_MENU_INPUT_HINT, inputHint);
            return this;
        }

        public Builder setEmptyLayout(@LayoutRes int emptyLayout) {
            this.bundle.putInt(Constant.KEY_EMPTY_LAYOUT, emptyLayout);
            return this;
        }

        public Builder setCustomAdapter(EaseMessageAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public EaseChatFragment build() {
            EaseChatFragment fragment = new EaseChatFragment();
            fragment.setArguments(this.bundle);
            return fragment;
        }
    }

    private static class Constant {
        public static final String KEY_USE_TITLE = "key_use_title";
        public static final String KEY_SET_TITLE = "key_set_title";
        public static final String KEY_EMPTY_LAYOUT = "key_empty_layout";
        public static final String KEY_ENABLE_BACK = "key_enable_back";
        public static final String KEY_MSG_TIME_COLOR = "key_msg_time_color";
        public static final String KEY_MSG_TIME_SIZE = "key_msg_time_size";
        public static final String KEY_MSG_LEFT_BUBBLE = "key_msg_left_bubble";
        public static final String KEY_MSG_RIGHT_BUBBLE = "key_msg_right_bubble";
        public static final String KEY_SHOW_NICKNAME = "key_show_nickname";
        public static final String KEY_MESSAGE_LIST_SHOW_TYPE = "key_message_list_show_type";
        public static final String KEY_HIDE_LEFT_AVATAR = "key_hide_left_avatar";
        public static final String KEY_HIDE_RIGHT_AVATAR = "key_hide_right_avatar";
        public static final String KEY_CHAT_BACKGROUND = "key_chat_background";
        public static final String KEY_CHAT_MENU_STYLE = "key_chat_menu_style";
        public static final String KEY_CHAT_MENU_INPUT_BG = "key_chat_menu_input_bg";
        public static final String KEY_CHAT_MENU_INPUT_HINT = "key_chat_menu_input_hint";
    }
}

