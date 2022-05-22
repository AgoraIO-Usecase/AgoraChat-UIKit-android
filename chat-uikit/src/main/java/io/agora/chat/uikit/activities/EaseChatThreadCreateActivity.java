package io.agora.chat.uikit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.superrtc.livepusher.PermissionsManager;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.databinding.EaseActivityThreadCreateBinding;
import io.agora.chat.uikit.chatthread.EaseChatThreadCreateFragment;
import io.agora.chat.uikit.chatthread.interfaces.EaseChatThreadParentMsgViewProvider;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.util.EMLog;

public class EaseChatThreadCreateActivity extends EaseBaseActivity {
    public EaseActivityThreadCreateBinding binding;
    public String parentId;
    public String messageId;

    public static void actionStart(Context context, String parentId, String messageId) {
        Intent intent = new Intent(context, EaseChatThreadCreateActivity.class);
        intent.putExtra("parentId", parentId);
        intent.putExtra("messageId", messageId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = EaseActivityThreadCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    public void initIntent(Intent intent) {
        parentId = intent.getStringExtra("parentId");
        messageId = intent.getStringExtra("messageId");
    }

    public void initView() {

    }

    public void initListener() {

    }

    public void initData() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("create_thread");
        if(fragment == null) {
            EaseChatThreadCreateFragment.Builder builder = new EaseChatThreadCreateFragment.Builder(parentId, messageId)
                    .useHeader(true)
                    .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                        @Override
                        public void onBackPress(View view) {
                            onBackPressed();
                        }
                    })
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
                    .setThreadParentMsgViewProvider(new EaseChatThreadParentMsgViewProvider() {
                        @Override
                        public View parentMsgView(ChatMessage message) {
                            // Add your parent view
                            return null;
                        }
                    })
                    .setOnAddMsgAttrsBeforeSendEvent(new OnAddMsgAttrsBeforeSendEvent() {
                        @Override
                        public void addMsgAttrsBeforeSend(ChatMessage message) {

                        }
                    });
            setChildFragmentBuilder(builder);
            fragment = builder.build();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "create_thread").commit();
    }

    public void setChildFragmentBuilder(EaseChatThreadCreateFragment.Builder builder) {

    }
}
