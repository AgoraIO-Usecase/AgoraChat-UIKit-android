package io.agora.chat.uikit.chatthread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.activities.EaseImageGridActivity;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.chat.interfaces.ChatInputMenuListener;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnMessageSendCallBack;
import io.agora.chat.uikit.chat.widget.EaseChatExtendMenuDialog;
import io.agora.chat.uikit.databinding.EaseFragmentCreateThreadBinding;
import io.agora.chat.uikit.interfaces.EaseMessageListener;
import io.agora.chat.uikit.manager.EaseActivityProviderHelper;
import io.agora.chat.uikit.manager.EaseDingMessageHelper;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.chatthread.interfaces.EaseChatThreadParentMsgViewProvider;
import io.agora.chat.uikit.chatthread.interfaces.OnChatThreadCreatedResultListener;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadCreatePresenter;
import io.agora.chat.uikit.chatthread.presenter.EaseChatThreadCreatePresenterImpl;
import io.agora.chat.uikit.chatthread.presenter.IChatThreadCreateView;
import io.agora.chat.uikit.chatthread.widget.EaseChatThreadParentMsgView;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.chat.uikit.widget.dialog.EaseAlertDialog;
import io.agora.util.EMLog;
import io.agora.util.FileHelper;
import io.agora.util.PathUtil;
import io.agora.util.VersionUtils;

public class EaseChatThreadCreateFragment extends EaseBaseFragment implements ChatInputMenuListener, IChatThreadCreateView {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;

    private static final String TAG = EaseChatThreadCreateFragment.class.getSimpleName();
    private EaseFragmentCreateThreadBinding binding;
    private EaseTitleBar.OnBackPressListener backPressListener;
    private EaseChatThreadParentMsgViewProvider parentMsgViewProvider;
    private String messageId;
    private String parentId;
    private EaseChatThreadCreatePresenter presenter;

    private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
    private OnMessageItemClickListener chatItemClickListener;
    private OnMessageSendCallBack messageSendCallBack;
    private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
    private OnChatRecordTouchListener recordTouchListener;
    private File cameraFile;
    private boolean sendOriginalImage;
    private ChatThread chatThread;
    private OnChatThreadCreatedResultListener resultListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        binding = EaseFragmentCreateThreadBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
            messageId = bundle.getString(Constant.KEY_MESSAGE_ID);
            parentId = bundle.getString(Constant.KEY_PARENT_ID);
        }
    }

    public void initView() {
        if(presenter == null) {
            presenter = new EaseChatThreadCreatePresenterImpl();
        }
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(presenter);
        }
        presenter.attachView(this);
        presenter.setupWithToUser(parentId, messageId, binding.etInputName);
        Bundle bundle = getArguments();
        if(bundle != null) {
            sendOriginalImage = bundle.getBoolean(Constant.KEY_SEND_ORIGINAL_IMAGE_MESSAGE, false);
            boolean useHeader = bundle.getBoolean(Constant.KEY_USE_TITLE, false);
            binding.titleBar.setVisibility(useHeader ? View.VISIBLE : View.GONE);
            if(useHeader) {
                String title = bundle.getString(Constant.KEY_SET_TITLE, "");
                if(!TextUtils.isEmpty(title)) {
                    binding.titleBar.setTitle(title);
                }

                boolean canBack = bundle.getBoolean(Constant.KEY_ENABLE_BACK, false);
                binding.titleBar.setDisplayHomeAsUpEnabled(canBack);

                binding.titleBar.setOnBackPressListener(backPressListener != null ? backPressListener : new EaseTitleBar.OnBackPressListener() {
                    @Override
                    public void onBackPress(View view) {
                        mContext.onBackPressed();
                    }
                });
            }
            String threadMention = bundle.getString(Constant.KEY_THREAD_MENTION, "");
            if(!TextUtils.isEmpty(threadMention)) {
                binding.tvThreadMentions.setText(threadMention);
            }
            String inputHint = bundle.getString(Constant.KEY_THREAD_INPUT_HINT, "");
            if(!TextUtils.isEmpty(inputHint)) {
                binding.etInputName.setHint(inputHint);
            }else {
                binding.etInputName.requestFocus();
                binding.etInputName.setFocusableInTouchMode(true);
                InputMethodManager inputManager = (InputMethodManager)binding.etInputName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(binding.etInputName, 0);
            }
            if(this.parentMsgViewProvider != null) {
                View parentMsgView = this.parentMsgViewProvider.parentMsgView(ChatClient.getInstance().chatManager().getMessage(messageId));
                if(parentMsgView != null) {
                    binding.threadParentMsg.removeAllViews();
                    binding.threadParentMsg.addView(parentMsgView);
                }else {
                    addDefaultParentMsgView();
                }
            }else {
                addDefaultParentMsgView();
            }
        }
        setCustomExtendMenu();
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
        binding.layoutMenu.setCustomExtendMenu(dialog);
    }

    private void addDefaultParentMsgView() {
        EaseChatThreadParentMsgView view = new EaseChatThreadParentMsgView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        binding.threadParentMsg.removeAllViews();
        binding.threadParentMsg.addView(view);

        view.setOnMessageItemClickListener(chatItemClickListener);
        view.setBottomDividerVisible(false);

        view.setMessage(ChatClient.getInstance().chatManager().getMessage(messageId));
    }


    public void initListener() {
        binding.layoutMenu.setChatInputMenuListener(this);
        ChatClient.getInstance().chatManager().addMessageListener(new EaseMessageListener() {
            @Override
            public void onMessageReceived(List<ChatMessage> messages) {
                for (ChatMessage message : messages) {
                    if(message.getChatType() == ChatMessage.ChatType.GroupChat) {
                        if(mContext != null) {
                            mContext.runOnUiThread(()-> binding.titleBar.setUnreadIconVisible(true));
                        }
                    }
                }
            }

            @Override
            public void onMessageRecalled(List<ChatMessage> messages) {

            }

            @Override
            public void onMessageChanged(ChatMessage message, Object change) {

            }
        });
    }

    public void initData() {

    }

    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {

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
        return binding.voiceRecorder.onPressToSpeakBtnTouch(v, event, (this::sendVoiceMessage));
    }

    private void sendVoiceMessage(String filePath, int length) {
        presenter.sendVoiceMessage(Uri.parse(filePath), length);
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
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

    private void setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
        this.backPressListener = listener;
    }

    private void setParentMsgViewProvider(EaseChatThreadParentMsgViewProvider provider) {
        this.parentMsgViewProvider = provider;
    }

    private void setPresenter(EaseChatThreadCreatePresenter presenter) {
        this.presenter = presenter;
    }

    private void setOnChatExtendMenuItemClickListener(OnChatExtendMenuItemClickListener listener) {
        this.extendMenuItemClickListener = listener;
    }

    private void setOnMessageItemClickListener(OnMessageItemClickListener listener) {
        this.chatItemClickListener = listener;
    }

    private void setOnMessageSendCallBack(OnMessageSendCallBack callBack) {
        this.messageSendCallBack = callBack;
    }

    private void setOnAddMsgAttrsBeforeSendEvent(OnAddMsgAttrsBeforeSendEvent sendMsgEvent) {
        this.sendMsgEvent = sendMsgEvent;
    }

    private void setOnChatRecordTouchListener(OnChatRecordTouchListener recordTouchListener) {
        this.recordTouchListener = recordTouchListener;
    }

    private void setOnThreadCreatedResultListener(OnChatThreadCreatedResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public void sendMessageFail(String message) {
        if(messageSendCallBack != null) {
            messageSendCallBack.onError(Error.GENERAL_ERROR, message);
        }
    }

    @Override
    public void addMsgAttrBeforeSend(ChatMessage message) {
        if(sendMsgEvent != null) {
            sendMsgEvent.addMsgAttrsBeforeSend(message);
        }
    }

    @Override
    public void onPresenterMessageSuccess(ChatMessage message) {
        if(messageSendCallBack != null) {
            messageSendCallBack.onSuccess(message);
        }
        if(resultListener == null  || !resultListener.onThreadCreatedSuccess(messageId, message.conversationId())) {
            startToChatThreadActivity(message);
        }
    }

    @Override
    public void onPresenterMessageError(ChatMessage message, int code, String error) {
        if(messageSendCallBack != null) {
            messageSendCallBack.onError(code, error);
        }
        if(resultListener != null ) {
            resultListener.onThreadCreatedFail(code, error);
        }else {
            startToChatThreadActivity(message);
        }
    }

    @Override
    public void onPresenterMessageInProgress(ChatMessage message, int progress) {

    }

    @Override
    public void sendMessageFinish(ChatMessage message) {

    }

    @Override
    public void onCreateThreadSuccess(ChatThread thread, ChatMessage message) {
        chatThread = thread;
        presenter.sendMessage(message);
    }

    @Override
    public void onCreateThreadFail(int errorCode, String message) {
        if(resultListener != null ) {
            resultListener.onThreadCreatedFail(errorCode, message);
        }
    }

    public void startToChatThreadActivity(ChatMessage message){
        EaseActivityProviderHelper.startToChatThreadActivity(mContext, message.conversationId(), messageId, parentId);
        mContext.finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            binding.layoutMenu.hideExtendContainer();
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data);
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data);
            }else if(requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            } else if (REQUEST_CODE_SELECT_VIDEO == requestCode) {
                onActivityResultForLocalVideos(data);
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
                presenter.sendVideoMessage(Uri.parse(videoPath), duration);
            } else {
                Uri videoUri = FileHelper.getInstance().formatInUri(uriString);
                presenter.sendVideoMessage(videoUri, duration);
            }
        }
    }

    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            presenter.sendImageMessage(Uri.parse(cameraFile.getAbsolutePath()), sendOriginalImage);
        }
    }

    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    presenter.sendImageMessage(Uri.parse(filePath), sendOriginalImage);
                }else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    presenter.sendImageMessage(selectedImage, sendOriginalImage);
                }
            }
        }
    }

    protected void onActivityResultForDingMsg(@Nullable Intent data) {
        if(data != null) {
            String msgContent = data.getStringExtra("msg");
            EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
            // Send the ding-type msg.
            if(chatThread != null) {
                String conversationId = chatThread.getChatThreadId();
                ChatMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent);
                presenter.sendGroupDingMessage(dingMsg);
            }
        }
    }

    protected void onActivityResultForLocalFiles(@Nullable Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, uri);
                if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    presenter.sendFileMessage(Uri.parse(filePath));
                }else {
                    EaseFileUtils.saveUriPermission(mContext, uri, data);
                    presenter.sendFileMessage(uri);
                }
            }
        }
    }

    protected boolean checkSdCardExist() {
        return EaseUtils.isSdcardExist();
    }

    public static class Builder {
        private final Bundle bundle;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private EaseChatThreadParentMsgViewProvider parentMsgViewProvider;
        private EaseChatThreadCreateFragment customFragment;
        private EaseChatThreadCreatePresenter presenter;
        private OnChatExtendMenuItemClickListener extendMenuItemClickListener;
        private OnMessageItemClickListener messageItemClickListener;
        private OnMessageSendCallBack messageSendCallBack;
        private OnAddMsgAttrsBeforeSendEvent sendMsgEvent;
        private OnChatRecordTouchListener recordTouchListener;
        private OnChatThreadCreatedResultListener resultListener;

        /**
         * Constructor
         * @param parentId Usually is group id.
         * @param messageId Usually is group message id.
         */
        public Builder(String parentId, String messageId) {
            this.bundle = new Bundle();
            bundle.putString(Constant.KEY_PARENT_ID, parentId);
            bundle.putString(Constant.KEY_MESSAGE_ID, messageId);
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
         * Set thread mention
         * @param threadMention
         * @return
         */
        public Builder setThreadMention(String threadMention) {
            this.bundle.putString(Constant.KEY_THREAD_MENTION, threadMention);
            return this;
        }

        /**
         * Set thread input hint
         * @param threadInputHint
         * @return
         */
        public Builder setThreadInputHint(String threadInputHint) {
            this.bundle.putString(Constant.KEY_THREAD_INPUT_HINT, threadInputHint);
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
         * Set thread parent message view provider
         * @param provider
         * @return
         */
        public Builder setThreadParentMsgViewProvider(EaseChatThreadParentMsgViewProvider provider) {
            this.parentMsgViewProvider = provider;
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

        public Builder setOnThreadCreatedResultListener(OnChatThreadCreatedResultListener listener) {
            this.resultListener = listener;
            return this;
        }

        /**
         * Set custom fragment which should extends EaseMessageFragment
         * @param fragment
         * @param <T>
         * @return
         */
        public <T extends EaseChatThreadCreateFragment> Builder setCustomFragment(T fragment) {
            this.customFragment = fragment;
            return this;
        }

        /**
         * Set custom presenter if you want to add your logic
         * @param presenter
         * @param <T>
         * @return
         */
        public <T extends EaseChatThreadCreatePresenter> Builder setCustomPresenter(EaseChatThreadCreatePresenter presenter) {
            this.presenter = presenter;
            return this;
        }

        public EaseChatThreadCreateFragment build() {
            EaseChatThreadCreateFragment fragment = this.customFragment != null ? this.customFragment : new EaseChatThreadCreateFragment();
            fragment.setArguments(this.bundle);
            fragment.setHeaderBackPressListener(this.backPressListener);
            fragment.setParentMsgViewProvider(this.parentMsgViewProvider);
            fragment.setPresenter(this.presenter);
            fragment.setOnChatExtendMenuItemClickListener(this.extendMenuItemClickListener);
            fragment.setOnMessageItemClickListener(this.messageItemClickListener);
            fragment.setOnMessageSendCallBack(this.messageSendCallBack);
            fragment.setOnAddMsgAttrsBeforeSendEvent(this.sendMsgEvent);
            fragment.setOnChatRecordTouchListener(this.recordTouchListener);
            fragment.setOnThreadCreatedResultListener(this.resultListener);
            return fragment;
        }
    }

    private static class Constant {
        public static final String KEY_PARENT_ID = "key_parent_id";
        public static final String KEY_MESSAGE_ID = "key_message_id";
        public static final String KEY_USE_TITLE = "key_use_title";
        public static final String KEY_SET_TITLE = "key_set_title";
        public static final String KEY_ENABLE_BACK = "key_enable_back";
        public static final String KEY_THREAD_MENTION = "key_thread_mention";
        public static final String KEY_THREAD_INPUT_HINT = "key_thread_input_hint";
        public static final String KEY_SEND_ORIGINAL_IMAGE_MESSAGE = "key_send_original_image_message";
    }
}
