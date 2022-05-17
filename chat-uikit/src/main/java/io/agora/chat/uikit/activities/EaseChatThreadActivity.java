package io.agora.chat.uikit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.superrtc.livepusher.PermissionsManager;

import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatInputChangeListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.databinding.EaseActivityThreadChatBinding;
import io.agora.chat.uikit.chatthread.EaseChatThreadFragment;
import io.agora.chat.uikit.chatthread.EaseChatThreadRole;
import io.agora.chat.uikit.chatthread.interfaces.OnChatThreadRoleResultCallback;
import io.agora.chat.uikit.interfaces.OnJoinChatThreadResultListener;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.util.EMLog;

public class EaseChatThreadActivity extends EaseBaseActivity {
    protected String parentMsgId;
    protected String conversationId;
    protected ChatThread thread;
    protected EaseBaseActivity mContext;
    protected EaseActivityThreadChatBinding binding;
    protected EaseChatThreadRole threadRole = EaseChatThreadRole.UNKNOWN;
    // Usually is group id
    private String parentId;

    public static void actionStart(Context context, String conversationId, String parentMsgId) {
        Intent intent = new Intent(context, EaseChatThreadActivity.class);
        intent.putExtra("parentMsgId", parentMsgId);
        intent.putExtra("conversationId", conversationId);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String conversationId, String parentMsgId, String parentId) {
        Intent intent = new Intent(context, EaseChatThreadActivity.class);
        intent.putExtra("parentMsgId", parentMsgId);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("parentId", parentId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        binding = EaseActivityThreadChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext= this;
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    public void initIntent(Intent intent) {
        parentMsgId = intent.getStringExtra("parentMsgId");
        conversationId = intent.getStringExtra("conversationId");
        parentId = intent.getStringExtra("parentId");
    }

    public void initView() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("thread_chat");
        if(fragment == null) {
            EaseChatFragment.Builder builder = new EaseChatThreadFragment.Builder(parentMsgId, conversationId, parentId)
                    .setOnJoinThreadResultListener(new OnJoinChatThreadResultListener() {
                        @Override
                        public void joinSuccess(String threadId) {
                            joinChatThreadSuccess(threadId);
                        }

                        @Override
                        public void joinFailed(int errorCode, String message) {
                            joinChatThreadFailed(errorCode, message);
                        }
                    })
                    .setOnThreadRoleResultCallback(new OnChatThreadRoleResultCallback() {
                        @Override
                        public void onThreadRole(EaseChatThreadRole role) {
                            threadRole = role;
                        }
                    })
                    .useHeader(true)
                    .enableHeaderPressBack(true)
                    .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                        @Override
                        public void onBackPress(View view) {
                            onBackPressed();
                        }
                    })
                    .setEmptyLayout(R.layout.ease_layout_no_data_show_nothing)
                    .setOnChatExtendMenuItemClickListener(new OnChatExtendMenuItemClickListener() {
                        @Override
                        public boolean onChatExtendMenuItemClick(View view, int itemId) {
                            EMLog.e("TAG", "onChatExtendMenuItemClick");
                            if (itemId == R.id.extend_item_take_picture) {
                                // check if has permissions
                                if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.CAMERA)) {
                                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                            , new String[]{Manifest.permission.CAMERA}, null);
                                    return true;
                                }
                                if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, null);
                                    return true;
                                }
                                return false;
                            } else if (itemId == R.id.extend_item_picture || itemId == R.id.extend_item_file) {
                                if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, null);
                                    return true;
                                }
                                return false;
                            } else if (itemId == R.id.extend_item_video) {
                                if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.CAMERA)) {
                                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                            , new String[]{Manifest.permission.CAMERA}, null);
                                    return true;
                                }
                                if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, null);
                                    return true;
                                }
                                return false;
                            }
                            return false;
                        }
                    })
                    .setOnChatInputChangeListener(new OnChatInputChangeListener() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            EMLog.e("TAG", "onTextChanged: s: " + s.toString());
                        }
                    })
                    .setOnChatRecordTouchListener(new OnChatRecordTouchListener() {
                        @Override
                        public boolean onRecordTouch(View v, MotionEvent event) {
                            // Check if has record audio permission
                            if (!PermissionsManager.getInstance().hasPermission(mContext, Manifest.permission.RECORD_AUDIO)) {
                                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mContext
                                        , new String[]{Manifest.permission.RECORD_AUDIO}, null);
                                return true;
                            }
                            return false;
                        }
                    })
                    .hideSenderAvatar(true);
            setChildFragmentBuilder(builder);
            fragment = builder.build();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "thread_chat").commit();
    }

    protected void joinChatThreadSuccess(String threadId) {

    }

    protected void joinChatThreadFailed(int errorCode, String message) {
        finish();
    }

    public void initListener() {

    }

    public void initData() {

    }

    public void setChildFragmentBuilder(EaseChatFragment.Builder builder) {

    }
}
