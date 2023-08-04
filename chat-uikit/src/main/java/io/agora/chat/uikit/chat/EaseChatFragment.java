package io.agora.chat.uikit.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.agora.ChatThreadChangeListener;
import io.agora.MultiDeviceListener;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThreadEvent;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.NormalFileMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.activities.EaseImageGridActivity;
import io.agora.chat.uikit.activities.EaseShowBigImageActivity;
import io.agora.chat.uikit.activities.EaseShowNormalFileActivity;
import io.agora.chat.uikit.activities.EaseShowVideoActivity;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.chat.adapter.EaseMessageAdapter;
import io.agora.chat.uikit.chat.interfaces.ChatQuoteMessageProvider;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatInputChangeListener;
import io.agora.chat.uikit.chat.interfaces.OnChatLayoutFinishInflateListener;
import io.agora.chat.uikit.chat.interfaces.OnChatLayoutListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSelectResultListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSendCallBack;
import io.agora.chat.uikit.chat.interfaces.OnPeerTypingListener;
import io.agora.chat.uikit.chat.interfaces.OnReactionMessageListener;
import io.agora.chat.uikit.chat.model.EaseInputMenuStyle;
import io.agora.chat.uikit.chat.widget.EaseChatExtendMenuDialog;
import io.agora.chat.uikit.chat.widget.EaseChatExtendQuoteView;
import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;
import io.agora.chat.uikit.chat.widget.EaseChatMultiSelectView;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.OnMenuChangeListener;
import io.agora.chat.uikit.interfaces.OnQuoteViewClickListener;
import io.agora.chat.uikit.manager.EaseChatInterfaceManager;
import io.agora.chat.uikit.manager.EaseChatMessageMultiSelectHelper;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;
import io.agora.chat.uikit.models.EaseMessageMenuData;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.chat.uikit.widget.dialog.EaseAlertDialog;
import io.agora.util.EMLog;
import io.agora.util.FileHelper;
import io.agora.util.ImageUtils;
import io.agora.util.PathUtil;
import io.agora.util.VersionUtils;

public class EaseChatFragment extends EaseBaseFragment implements OnChatLayoutListener, OnMenuChangeListener,
        OnAddMsgAttrsBeforeSendEvent, OnChatRecordTouchListener, OnReactionMessageListener,
        MultiDeviceListener, ChatThreadChangeListener, OnQuoteViewClickListener, ChatQuoteMessageProvider {
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
    public EaseChatType chatType;
    public String historyMsgId;
    public boolean isFromServer;
    public boolean isMessageInit;
    public boolean isThread;

    protected File cameraFile;
    private EaseTitleBar.OnBackPressListener backPressListener;
    private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
    private OnChatInputChangeListener chatInputChangeListener;
    private OnMessageItemClickListener chatItemClickListener;
    private OnMessageSendCallBack messageSendCallBack;
    private OnPeerTypingListener otherTypingListener;
    private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
    private OnChatRecordTouchListener recordTouchListener;
    private OnChatLayoutFinishInflateListener finishInflateListener;
    private OnReactionMessageListener reactionMessageListener;
    private OnMessageSelectResultListener messageSelectResultListener;
    private EaseMessageAdapter messageAdapter;
    private boolean sendOriginalImage;
    /**
     * The inner label is used to mark whether a reference operation is in progress.
     */
    private boolean isQuoting = false;
    private JSONObject quoteObject = null;

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
            chatType = EaseChatType.from(bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseChatType.SINGLE_CHAT.getChatType()));
            historyMsgId = bundle.getString(EaseConstant.HISTORY_MSG_ID);
            isFromServer = bundle.getBoolean(EaseConstant.EXTRA_IS_FROM_SERVER, false);
            isThread = bundle.getBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, false);
        }
    }

    public void initView() {
        titleBar = findViewById(R.id.title_bar);
        chatLayout = findViewById(R.id.layout_chat);
        if(this.messageAdapter != null) {
            chatLayout.getChatMessageListLayout().setMessageAdapter(this.messageAdapter);
        }
        chatLayout.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.LEFT_RIGHT);

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
            boolean showNickname = bundle.getBoolean(Constant.KEY_SHOW_NICKNAME, false);
            chatLayout.getChatMessageListLayout().showNickname(showNickname);
            String messageListShowType = bundle.getString(Constant.KEY_MESSAGE_LIST_SHOW_STYLE, "");
            if(!TextUtils.isEmpty(messageListShowType)) {
                EaseChatMessageListLayout.ShowType showType = EaseChatMessageListLayout.ShowType.valueOf(messageListShowType);
                if(showType != null) {
                    chatLayout.getChatMessageListLayout().setItemShowType(showType);
                }
            }
            boolean hideReceiveAvatar = bundle.getBoolean(Constant.KEY_HIDE_RECEIVE_AVATAR, false);
            chatLayout.getChatMessageListLayout().hideChatReceiveAvatar(hideReceiveAvatar);
            boolean hideSendAvatar = bundle.getBoolean(Constant.KEY_HIDE_SEND_AVATAR, false);
            chatLayout.getChatMessageListLayout().hideChatSendAvatar(hideSendAvatar);
            boolean turnOnTypingMonitor = bundle.getBoolean(Constant.KEY_TURN_ON_TYPING_MONITOR, false);
            chatLayout.turnOnTypingMonitor(turnOnTypingMonitor);
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
            sendOriginalImage = bundle.getBoolean(Constant.KEY_SEND_ORIGINAL_IMAGE_MESSAGE, false);
            int emptyLayout = bundle.getInt(Constant.KEY_EMPTY_LAYOUT, -1);
            if(emptyLayout != -1) {
                chatLayout.getChatMessageListLayout().getMessageAdapter().setEmptyView(emptyLayout);
            }
        }
        setCustomExtendMenu();
        setCustomTopExtendMenu();
        AddCustomLongClickMenu();
        // Provide views after finishing inflate
        if(finishInflateListener != null) {
            finishInflateListener.onTitleBarFinishInflate(titleBar);
            finishInflateListener.onChatListFinishInflate(chatLayout);
        }
    }

    public void initListener() {
        chatLayout.setOnChatLayoutListener(this);
        chatLayout.setOnPopupWindowItemClickListener(this);
        chatLayout.setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent != null ? sendMsgEvent : this);
        chatLayout.setOnChatRecordTouchListener(recordTouchListener != null ? recordTouchListener : this);
        chatLayout.setOnReactionListener(reactionMessageListener != null ? reactionMessageListener : this);
        ChatClient.getInstance().addMultiDeviceListener(this);
        ChatClient.getInstance().chatThreadManager().addChatThreadChangeListener(this);
        EaseChatInterfaceManager.getInstance().setInterface(mContext, OnQuoteViewClickListener.class.getSimpleName(), this);
        EaseChatInterfaceManager.getInstance().setInterface(mContext, ChatQuoteMessageProvider.class.getSimpleName(), this);
    }

    public void initData() {
        initChatLayout();
        loadData();
        isMessageInit = true;
    }

    public void initChatLayout() {
        if(!TextUtils.isEmpty(historyMsgId)) {
            chatLayout.init(EaseChatMessageListLayout.LoadDataType.HISTORY, conversationId, chatType);
        }else {
            if(isThread) {
                chatLayout.init(EaseChatMessageListLayout.LoadDataType.THREAD, conversationId, chatType);
            }else {
                if(isFromServer) {
                    chatLayout.init(EaseChatMessageListLayout.LoadDataType.ROAM, conversationId, chatType);
                }else {
                    chatLayout.init(conversationId, chatType);
                }
            }
        }
    }

    public void loadData() {
        if(!TextUtils.isEmpty(historyMsgId)) {
            chatLayout.loadData(historyMsgId);
        }else {
            chatLayout.loadDefaultData();
        }
    }

    /**
     * Set custom extend menu
     */
    public void setCustomExtendMenu() {
        EaseChatExtendMenuDialog chatMenuDialog = new EaseChatExtendMenuDialog(mContext);
        chatMenuDialog.init();
        EaseChatExtendMenuDialog dialog = new EaseAlertDialog.Builder<EaseChatExtendMenuDialog>(mContext)
                .setCustomDialog(chatMenuDialog)
                .setFullWidth()
                .setGravity(Gravity.BOTTOM)
                .setFromBottomAnimation()
                .create();
        chatLayout.getChatInputMenu().setCustomExtendMenu(dialog);
    }

    /**
     * Set custom top extend menu
     */
    public void setCustomTopExtendMenu() {
        IChatTopExtendMenu chatTopExtendMenu = chatLayout.getChatInputMenu().getChatTopExtendMenu();
        if(chatTopExtendMenu instanceof EaseChatExtendQuoteView) {
            chatLayout.getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE);
            return;
        }
        EaseChatExtendQuoteView quoteView = new EaseChatExtendQuoteView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        quoteView.setLayoutParams(params);
        quoteView.showTopExtendMenu(false);
        chatLayout.getChatInputMenu().setCustomTopExtendMenu(quoteView);
        chatLayout.getChatInputMenu().showTopExtendMenu(true);
        chatLayout.getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE);
    }

    /**
     * Show multi select view on EaseChatInputMenu.
     */
    public void showMultiSelectView() {
        EaseChatMultiSelectView multiSelectView = new EaseChatMultiSelectView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        multiSelectView.setLayoutParams(params);
        multiSelectView.setOnDismissListener(view -> chatLayout.getChatInputMenu().getPrimaryMenu().setVisible(View.VISIBLE));
        multiSelectView.setOnSelectClickListener(new EaseChatMultiSelectView.OnSelectClickListener() {
            @Override
            public void onMultiDeleteClick(List<String> deleteMsgIdList) {
                if(messageSelectResultListener != null && messageSelectResultListener.onMessageDelete(deleteMsgIdList)) {
                    return;
                }
                chatLayout.deleteMessages(deleteMsgIdList);
            }

            @Override
            public void onMultiReplyClick(List<String> replyMsgIdList) {
                if(messageSelectResultListener != null && messageSelectResultListener.onMessageReply(replyMsgIdList)) {
                    return;
                }
                EaseChatMessageMultiSelectHelper.getCombineMessageSummary(replyMsgIdList);
            }
        });
        multiSelectView.setupWithAdapter(messageAdapter);
        chatLayout.getChatInputMenu().setCustomTopExtendMenu(multiSelectView);
        chatLayout.getChatInputMenu().showTopExtendMenu(true);
        chatLayout.getChatInputMenu().hideInputMenu();
    }

    private void AddCustomLongClickMenu() {
        MenuItemBean itemBean = new MenuItemBean(0, R.id.action_chat_quote, EaseMessageMenuData.MENU_ITEM_IDS.length * 10, mContext.getString(R.string.ease_action_quote));
        itemBean.setResourceId(R.drawable.ease_chat_item_menu_forward);
        chatLayout.addItemMenu(itemBean);

        itemBean = new MenuItemBean(0, R.id.action_chat_select, chatLayout.getMenuHelper().getLength() * 10, mContext.getString(R.string.ease_action_select));
        itemBean.setResourceId(R.drawable.ease_chat_item_menu_select);
        chatLayout.addItemMenu(itemBean);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isMessageInit && TextUtils.isEmpty(historyMsgId)) {
            chatLayout.getChatMessageListLayout().refreshMessages();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatClient.getInstance().chatThreadManager().removeChatThreadChangeListener(this);
        ChatClient.getInstance().removeMultiDeviceListener(this);
        EaseChatInterfaceManager.getInstance().removeInterface(mContext, OnQuoteViewClickListener.class.getSimpleName());
        EaseChatInterfaceManager.getInstance().removeInterface(mContext, ChatQuoteMessageProvider.class.getSimpleName());
    }

    @Override
    public void onQuoteViewClick(ChatMessage message) {
        if(message == null) {
            EMLog.d(TAG,"onQuoteViewClick: message is null.");
            return;
        }
        if(message.getType() == ChatMessage.Type.IMAGE || message.getType() == ChatMessage.Type.VIDEO || message.getType() == ChatMessage.Type.FILE) {
            showQuoteByType(message);
            return;
        }
        chatLayout.getChatMessageListLayout().moveToTarget(message);
    }

    public void showQuoteByType(ChatMessage message){
        ChatMessage.Type type = message.getType();
        switch (type){
            case IMAGE:
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                Intent imageIntent = new Intent(getContext(), EaseShowBigImageActivity.class);
                Uri imgUri = imgBody.getLocalUri();
                EaseFileUtils.takePersistableUriPermission(getContext(), imgUri);
                if(EaseFileUtils.isFileExistByUri(getContext(), imgUri)) {
                    imageIntent.putExtra("uri", imgUri);
                } else{
                    String msgId = message.getMsgId();
                    imageIntent.putExtra("messageId", msgId);
                    imageIntent.putExtra("filename", imgBody.getFileName());
                }
                if (getContext() != null){
                    getContext().startActivity(imageIntent);
                }
                break;
            case VIDEO:
                Intent videoIntent = new Intent(getContext(), EaseShowVideoActivity.class);
                videoIntent.putExtra("msg", message);
                if (getContext() != null){
                    getContext().startActivity(videoIntent);
                }
                break;
            case FILE:
                NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
                Uri filePath = fileMessageBody.getLocalUri();
                EaseFileUtils.takePersistableUriPermission(getContext(), filePath);
                if(EaseFileUtils.isFileExistByUri(getContext(), filePath)){
                    EaseCompat.openFile(getContext(), filePath);
                } else {
                    if (getContext() != null){
                        getContext().startActivity(new Intent(getContext(), EaseShowNormalFileActivity.class).putExtra("msg", message));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onQuoteViewClickError(int code, String message) {

    }

    private void setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
        this.backPressListener = listener;
    }

    private void setOnChatExtendMenuItemClickListener(OnChatExtendMenuItemClickListener listener) {
        this.extendMenuItemClickListener = listener;
    }

    private void setOnChatInputChangeListener(OnChatInputChangeListener listener) {
        this.chatInputChangeListener = listener;
    }

    private void setOnMessageItemClickListener(OnMessageItemClickListener listener) {
        this.chatItemClickListener = listener;
    }

    private void setOnMessageSendCallBack(OnMessageSendCallBack callBack) {
        this.messageSendCallBack = callBack;
    }

    private void setOnPeerTypingListener(OnPeerTypingListener listener) {
        this.otherTypingListener = listener;
    }

    private void setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
        this.sendMsgEvent = sendMsgEvent;
    }

    private void setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
        this.recordTouchListener = recordTouchListener;
    }

    private void setOnReactionMessageListener(OnReactionMessageListener reactionMessageListener) {
        this.reactionMessageListener = reactionMessageListener;
    }

    private void setOnChatLayoutFinishInflateListener(OnChatLayoutFinishInflateListener inflateListener) {
        this.finishInflateListener = inflateListener;
    }

    private void setOnMessageSelectResultListener(OnMessageSelectResultListener messageSelectResultListener) {
        this.messageSelectResultListener = messageSelectResultListener;
    }

    private void setCustomAdapter(EaseMessageAdapter adapter) {
        this.messageAdapter = adapter;
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
    public boolean onThreadClick(String messageId, String threadId) {
        if(chatItemClickListener != null) {
            return chatItemClickListener.onThreadClick(messageId, threadId);
        }
        return false;
    }

    @Override
    public boolean onThreadLongClick(View v, String messageId, String threadId) {
        if(chatItemClickListener != null) {
            return chatItemClickListener.onThreadLongClick(v, messageId, threadId);
        }
        return false;
    }

    @Override
    public boolean onChatExtendMenuItemClick(View view, int itemId) {
        if(extendMenuItemClickListener != null && extendMenuItemClickListener.onChatExtendMenuItemClick(view, itemId)) {
            return true;
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
        return true;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(chatInputChangeListener != null) {
            chatInputChangeListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void onSuccess(ChatMessage message) {
        if (isQuoting && message.getType() == ChatMessage.Type.TXT){
            isQuoting = false;
            chatLayout.getChatInputMenu().getChatTopExtendMenu().showTopExtendMenu(false);
        }
        // you can do something after sending a successful message
        if(messageSendCallBack != null) {
            messageSendCallBack.onSuccess(message);
        }
    }

    @Override
    public void onError(int code, String errorMsg) {
        if (isQuoting){
            isQuoting = false;
            chatLayout.getChatInputMenu().getChatTopExtendMenu().showTopExtendMenu(false);
        }
        if(messageSendCallBack != null) {
            messageSendCallBack.onError(code, errorMsg);
        }
    }

    @Override
    public void onPeerTyping(String action) {
        if(otherTypingListener != null) {
            otherTypingListener.onPeerTyping(action);
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
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            } else if (REQUEST_CODE_SELECT_VIDEO == requestCode) {
                onActivityResultForLocalVideos(data);
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
        Intent intent = new Intent(getActivity(), EaseImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
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

    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            Uri uri = Uri.parse(cameraFile.getAbsolutePath());
            //检查图片是否被旋转并调整回来
            if(sendOriginalImage) {
                uri= ImageUtils.checkDegreeAndRestoreImage(mContext, uri);
            }
            chatLayout.sendImageMessage(uri, sendOriginalImage);
        }
    }

    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendImageMessage(Uri.parse(filePath), sendOriginalImage);
                }else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    chatLayout.sendImageMessage(selectedImage, sendOriginalImage);
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

    protected void onActivityResultForLocalVideos(@Nullable Intent data) {
        if (data != null) {
            int duration = data.getIntExtra("dur", 0);
            if(duration == -1) {
                duration = 0;
            }
            duration = (int) Math.round(duration * 1.0 / 1000);
            String videoPath = data.getStringExtra("path");
            String uriString = data.getStringExtra("uri");
            if (!TextUtils.isEmpty(videoPath)) {
                chatLayout.sendVideoMessage(Uri.parse(videoPath), duration);
            } else {
                Uri videoUri = FileHelper.getInstance().formatInUri(uriString);
                chatLayout.sendVideoMessage(videoUri, duration);
            }
        }
    }

    protected boolean checkSdCardExist() {
        return EaseUtils.isSdcardExist();
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, ChatMessage message) {
        boolean isThreadNotify = message.getBooleanAttribute(EaseConstant.EASE_THREAD_NOTIFICATION_TYPE, false);
        if(isThreadNotify) {
            helper.findItemVisible(R.id.action_chat_copy, false);
            helper.findItemVisible(R.id.action_chat_thread, false);
            helper.findItemVisible(R.id.action_chat_recall, false);
            helper.findItemVisible(R.id.action_chat_delete, true);
            helper.showHeaderView(false);
        }
        helper.findItem(R.id.action_chat_recall).setTitleColor(ContextCompat.getColor(mContext, R.color.ease_message_unsend_menu_txt));
        helper.findItemVisible(R.id.action_chat_quote, message.status() == ChatMessage.Status.SUCCESS);
    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, ChatMessage message) {
        if(item.getItemId() == R.id.action_chat_quote) {
            setCustomTopExtendMenu();
            onQuoteMenuItemClick(message);
            return true;
        }else if(item.getItemId() == R.id.action_chat_select) {
            showMultiSelectDialog(message);
            return true;
        }
        return false;
    }

    protected void showMultiSelectDialog(ChatMessage message) {
        showMultiSelectView();
    }

    @Override
    public void addMsgAttrsBeforeSend(ChatMessage message) {
        if(message != null && message.getType() == ChatMessage.Type.TXT && isQuoting) {
            message.setAttribute(EaseConstant.QUOTE_MSG_QUOTE, quoteObject);
        }
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

    @Override
    public void addReactionMessageSuccess(ChatMessage message) {

    }

    @Override
    public void addReactionMessageFail(ChatMessage message, int code, String error) {

    }

    @Override
    public void removeReactionMessageSuccess(ChatMessage message) {

    }

    @Override
    public void removeReactionMessageFail(ChatMessage message, int code, String error) {

    }

    @Override
    public void onContactEvent(int event, String target, String ext) {

    }

    @Override
    public void onGroupEvent(int event, String target, List<String> usernames) {
        if(event == GROUP_DESTROY || event == GROUP_LEAVE) {
            if(TextUtils.equals(target, conversationId)) {
                mContext.finish();
            }
        }
    }

    @Override
    public void onChatThreadEvent(int event, String target, List<String> usernames) {

    }

    @Override
    public void onChatThreadCreated(ChatThreadEvent event) {
        if(isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshToLatest();
        }
    }

    @Override
    public void onChatThreadUpdated(ChatThreadEvent event) {
        if(isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshMessage(event.getChatThread().getMessageId());
        }
    }

    @Override
    public void onChatThreadDestroyed(ChatThreadEvent event) {
        if(isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshMessages();
        }
    }

    @Override
    public void onChatThreadUserRemoved(ChatThreadEvent event) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mContext != null && mContext.isFinishing()) {
            ChatClient.getInstance().chatManager().removeMessageListener(chatLayout);
        }
    }

    public void onQuoteMenuItemClick(ChatMessage message){
        isQuoting = true;
        quoteObject = new JSONObject();
        try {
            if (message.getBody() != null){
                quoteObject.put(EaseConstant.QUOTE_MSG_ID,message.getMsgId());
                if (message.getType() == ChatMessage.Type.TXT && !TextUtils.isEmpty(((TextMessageBody)message.getBody()).getMessage())){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,((TextMessageBody)message.getBody()).getMessage());
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"txt");
                }else if (message.getType() == ChatMessage.Type.IMAGE){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_picture));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"img");
                }else if (message.getType() == ChatMessage.Type.VIDEO){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_video));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"video");
                }else if (message.getType() == ChatMessage.Type.LOCATION){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_location));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"location");
                }else if (message.getType() == ChatMessage.Type.VOICE){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_voice));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"audio");
                }else if (message.getType() == ChatMessage.Type.FILE){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_file));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"file");
                }else if (message.getType() == ChatMessage.Type.CUSTOM){
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,getResources().getString(R.string.ease_custom));
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE,"custom");
                }else {
                    quoteObject.put(EaseConstant.QUOTE_MSG_PREVIEW,"["+message.getType().name().toLowerCase()+"]");
                    quoteObject.put(EaseConstant.QUOTE_MSG_TYPE, message.getType().name().toLowerCase());
                }
                quoteObject.put(EaseConstant.QUOTE_MSG_SENDER, message.getFrom());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((EaseChatExtendQuoteView)(chatLayout.getChatInputMenu().getChatTopExtendMenu())).startQuote(message);
        chatLayout.getChatInputMenu().getPrimaryMenu().showTextStatus();
    }

    @Override
    public SpannableString provideQuoteContent(ChatMessage quoteMessage, ChatMessage.Type quoteMsgType, String quoteSender, String quoteContent) {
        return null;
    }

    public static class Builder {
        protected final Bundle bundle;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private EaseMessageAdapter adapter;
        private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
        private OnChatInputChangeListener chatInputChangeListener;
        private OnMessageItemClickListener messageItemClickListener;
        private OnMessageSendCallBack messageSendCallBack;
        private OnPeerTypingListener peerTypingListener;
        private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
        private OnChatRecordTouchListener recordTouchListener;
        private OnReactionMessageListener reactionMessageListener;
        private OnChatLayoutFinishInflateListener finishInflateListener;
        protected EaseChatFragment customFragment;
        private OnMessageSelectResultListener messageSelectResultListener;

        /**
         * Constructor
         * @param conversationId Agora Chat ID
         * @param chatType       See {@link EaseChatType}
         */
        public Builder(String conversationId, EaseChatType chatType) {
            this.bundle = new Bundle();
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType.getChatType());
        }

        /**
         * Constructor
         * @param conversationId Agora Chat ID
         * @param chatType       See {@link EaseChatType}
         * @param historyMsgId   Message ID
         */
        public Builder(String conversationId, EaseChatType chatType, String historyMsgId) {
            this.bundle = new Bundle();
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType.getChatType());
            bundle.putString(EaseConstant.HISTORY_MSG_ID, historyMsgId);
        }

        /**
         * Set history message id.
         * @param historyMessageId
         * @return
         */
        public Builder setHistoryMessageId(String historyMessageId) {
            bundle.putString(EaseConstant.HISTORY_MSG_ID, historyMessageId);
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
         * Set Whether to get history message from server or local database
         * @param isFromServer
         * @return
         */
        public Builder getHistoryMessageFromServerOrLocal(boolean isFromServer) {
            this.bundle.putBoolean(EaseConstant.EXTRA_IS_FROM_SERVER, isFromServer);
            return this;
        }

        /**
         * Set chat extension menu item click listener
         * @param listener
         * @return
         */
        public Builder setOnChatExtendMenuItemClickListener(OnChatExtendMenuItemClickListener listener) {
            this.extendMenuItemClickListener = listener;
            return this;
        }

        /**
         * Set chat menu's text change listener
         * @param listener
         * @return
         */
        public Builder setOnChatInputChangeListener(OnChatInputChangeListener listener) {
            this.chatInputChangeListener = listener;
            return this;
        }

        /**
         * Set message item click listener, include bubble click, bubble long click, avatar click
         * and avatar long click
         * @param listener
         * @return
         */
        public Builder setOnMessageItemClickListener(OnMessageItemClickListener listener) {
            this.messageItemClickListener = listener;
            return this;
        }

        /**
         * Set message's callback after which is sent
         * @param callBack
         * @return
         */
        public Builder setOnMessageSendCallBack(OnMessageSendCallBack callBack) {
            this.messageSendCallBack = callBack;
            return this;
        }

        /**
         * Turn on other peer's typing monitor, only for single chat
         * @param turnOn
         * @return
         */
        public Builder turnOnTypingMonitor(boolean turnOn) {
            this.bundle.putBoolean(Constant.KEY_TURN_ON_TYPING_MONITOR, turnOn);
            return this;
        }

        /**
         * Set peer's typing listener, only for single chat. You need call {@link Builder#turnOnTypingMonitor(boolean)} first.
         * @param listener
         * @return
         */
        public Builder setOnPeerTypingListener(OnPeerTypingListener listener) {
            this.peerTypingListener = listener;
            return this;
        }

        /**
         * Set the event you can add message's attrs before send message
         * @param sendMsgEvent
         * @return
         */
        public Builder setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
            this.sendMsgEvent = sendMsgEvent;
            return this;
        }

        /**
         * Set touch event listener during recording
         * @param recordTouchListener
         * @return
         */
        public Builder setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
            this.recordTouchListener = recordTouchListener;
            return this;
        }

        /**
         * Set reaction listener
         * @param reactionMessageListener
         * @return
         */
        public Builder setOnReactionMessageListener(OnReactionMessageListener reactionMessageListener) {
            this.reactionMessageListener = reactionMessageListener;
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
         * Whether to show nickname in message item
         * @param showNickname
         * @return
         */
        public Builder showNickname(boolean showNickname) {
            this.bundle.putBoolean(Constant.KEY_SHOW_NICKNAME, showNickname);
            return this;
        }

        /**
         * Set message list show style, including left_right and all_left style
         * @param showType
         * @return
         */
        public Builder setMessageListShowStyle(EaseChatMessageListLayout.ShowType showType) {
            this.bundle.putString(Constant.KEY_MESSAGE_LIST_SHOW_STYLE, showType.name());
            return this;
        }

        /**
         * Set layout inflated listener
         * @param finishInflateListener
         * @return
         */
        public Builder setOnChatLayoutFinishInflateListener(OnChatLayoutFinishInflateListener finishInflateListener) {
            this.finishInflateListener = finishInflateListener;
            return this;
        }

        /**
         * Set message selection result listener.
         * @param listener
         * @return
         */
        public Builder setOnMessageSelectResultListener(OnMessageSelectResultListener listener) {
            this.messageSelectResultListener = listener;
            return this;
        }

        /**
         * Whether to hide receiver's avatar
         * @param hide
         * @return
         */
        public Builder hideReceiverAvatar(boolean hide) {
            this.bundle.putBoolean(Constant.KEY_HIDE_RECEIVE_AVATAR, hide);
            return this;
        }

        /**
         * Whether to hide sender's avatar
         * @param hide
         * @return
         */
        public Builder hideSenderAvatar(boolean hide) {
            this.bundle.putBoolean(Constant.KEY_HIDE_SEND_AVATAR, hide);
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
         * Set chat input menu style, including voice input, text input,
         * emoji input and extended function input
         * @param style
         * @return
         */
        public Builder setChatInputMenuStyle(EaseInputMenuStyle style) {
            this.bundle.putString(Constant.KEY_CHAT_MENU_STYLE, style.name());
            return this;
        }

        /**
         * Set chat input menu background
         * @param bgDrawable
         * @return
         */
        public Builder setChatInputMenuBackground(@DrawableRes int bgDrawable) {
            this.bundle.putInt(Constant.KEY_CHAT_MENU_INPUT_BG, bgDrawable);
            return this;
        }

        /**
         * Set chat input menu's hint text
         * @param inputHint
         * @return
         */
        public Builder setChatInputMenuHint(String inputHint) {
            this.bundle.putString(Constant.KEY_CHAT_MENU_INPUT_HINT, inputHint);
            return this;
        }

        /**
         * Set whether to use original file to send image message
         * @param sendOriginalImage
         * @return
         */
        public Builder sendMessageByOriginalImage(boolean sendOriginalImage) {
            this.bundle.putBoolean(Constant.KEY_SEND_ORIGINAL_IMAGE_MESSAGE, sendOriginalImage);
            return this;
        }

        /**
         * Set whether to use original file to send image message
         * @param isThread
         * @return
         */
        public Builder setThreadMessage(boolean isThread) {
            this.bundle.putBoolean(Constant.KEY_THREAD_MESSAGE_FLAG, isThread);
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
        public <T extends EaseChatFragment> Builder setCustomFragment(T fragment) {
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

        public EaseChatFragment build() {
            EaseChatFragment fragment = this.customFragment != null ? this.customFragment : new EaseChatFragment();
            fragment.setArguments(this.bundle);
            fragment.setHeaderBackPressListener(this.backPressListener);
            fragment.setOnChatExtendMenuItemClickListener(this.extendMenuItemClickListener);
            fragment.setOnChatInputChangeListener(this.chatInputChangeListener);
            fragment.setOnMessageItemClickListener(this.messageItemClickListener);
            fragment.setOnMessageSendCallBack(this.messageSendCallBack);
            fragment.setOnPeerTypingListener(this.peerTypingListener);
            fragment.setOnAddMsgAttrsBeforeSendEvent(this.sendMsgEvent);
            fragment.setOnChatRecordTouchListener(this.recordTouchListener);
            fragment.setOnChatLayoutFinishInflateListener(this.finishInflateListener);
            fragment.setCustomAdapter(this.adapter);
            fragment.setOnReactionMessageListener(this.reactionMessageListener);
            fragment.setOnMessageSelectResultListener(messageSelectResultListener);
            return fragment;
        }
    }

    private static class Constant {
        public static final String KEY_USE_TITLE = "key_use_title";
        public static final String KEY_SET_TITLE = "key_set_title";
        public static final String KEY_SET_SUB_TITLE = "key_set_sub_title";
        public static final String KEY_EMPTY_LAYOUT = "key_empty_layout";
        public static final String KEY_ENABLE_BACK = "key_enable_back";
        public static final String KEY_MSG_TIME_COLOR = "key_msg_time_color";
        public static final String KEY_MSG_TIME_SIZE = "key_msg_time_size";
        public static final String KEY_MSG_LEFT_BUBBLE = "key_msg_left_bubble";
        public static final String KEY_MSG_RIGHT_BUBBLE = "key_msg_right_bubble";
        public static final String KEY_SHOW_NICKNAME = "key_show_nickname";
        public static final String KEY_MESSAGE_LIST_SHOW_STYLE = "key_message_list_show_type";
        public static final String KEY_HIDE_RECEIVE_AVATAR = "key_hide_left_avatar";
        public static final String KEY_HIDE_SEND_AVATAR = "key_hide_right_avatar";
        public static final String KEY_CHAT_BACKGROUND = "key_chat_background";
        public static final String KEY_CHAT_MENU_STYLE = "key_chat_menu_style";
        public static final String KEY_CHAT_MENU_INPUT_BG = "key_chat_menu_input_bg";
        public static final String KEY_CHAT_MENU_INPUT_HINT = "key_chat_menu_input_hint";
        public static final String KEY_TURN_ON_TYPING_MONITOR = "key_turn_on_typing_monitor";
        public static final String KEY_SEND_ORIGINAL_IMAGE_MESSAGE = "key_send_original_image_message";
        public static final String KEY_THREAD_MESSAGE_FLAG = "key_thread_message_flag";
    }
}

