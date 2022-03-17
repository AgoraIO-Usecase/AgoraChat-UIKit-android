package io.agora.chat.uikit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.superrtc.livepusher.PermissionsManager;

import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.chat.interfaces.OnChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.OnChatInputChangeListener;
import io.agora.chat.uikit.chat.interfaces.OnChatRecordTouchListener;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.databinding.EaseActivityThreadChatBinding;
import io.agora.chat.uikit.thread.EaseThreadChatFragment;
import io.agora.util.EMLog;

public class EaseThreadChatActivity extends EaseBaseActivity {
    private String parentMsgId;
    private String conversationId;
    private int chatType;
    private ChatThread thread;
    private EaseBaseActivity mContext;

    public static void actionStart(Context context, String parentMsgId, String conversationId, int chatType) {
        Intent intent = new Intent(context, EaseThreadChatActivity.class);
        intent.putExtra("parentMsgId", parentMsgId);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("chatType", chatType);
        context.startActivity(intent);
    }
    
    public static void actionStart(Context context, String parentMsgId, String conversationId, int chatType, ChatThread thread) {
        Intent intent = new Intent(context, EaseThreadChatActivity.class);
        intent.putExtra("parentMsgId", parentMsgId);
        intent.putExtra("conversationId", conversationId);
        intent.putExtra("chatType", chatType);
        intent.putExtra("thread", thread);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EaseActivityThreadChatBinding inflate = EaseActivityThreadChatBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        mContext= this;
        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    public void initIntent(Intent intent) {
        parentMsgId = intent.getStringExtra("parentMsgId");
        conversationId = intent.getStringExtra("conversationId");
        chatType = intent.getIntExtra("chatType", EaseConstant.CHATTYPE_GROUP);
        thread = intent.getParcelableExtra("thread");
    }

    public void initView() {

    }

    public void initListener() {

    }

    public void initData() {
        EaseChatFragment.Builder builder = new EaseThreadChatFragment.Builder(parentMsgId, conversationId)
                .setThreadInfo(thread)
                .useHeader(false)
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
                        } else if (itemId == R.id.extend_item_picture || itemId == R.id.extend_item_file || itemId == R.id.extend_item_video) {
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
        EaseChatFragment fragment = builder.build();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "thread_chat").commit();

    }

    public void setChildFragmentBuilder(EaseChatFragment.Builder builder) {
        
    }
}
