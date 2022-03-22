package io.agora.chat.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import io.agora.chat.uikit.databinding.EaseActivityThreadCreateBinding;
import io.agora.chat.uikit.thread.EaseThreadCreateFragment;
import io.agora.chat.uikit.thread.interfaces.EaseThreadParentMsgViewProvider;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class EaseThreadCreateActivity extends EaseBaseActivity {
    public EaseActivityThreadCreateBinding binding;
    public String parentId;
    public String messageId;

    public static void actionStart(Context context, String parentId, String messageId) {
        Intent intent = new Intent(context, EaseThreadCreateActivity.class);
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
            fragment = new EaseThreadCreateFragment.Builder(parentId, messageId)
                    .useHeader(true)
                    .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                        @Override
                        public void onBackPress(View view) {
                            onBackPressed();
                        }
                    })
                    .setThreadParentMsgViewProvider(new EaseThreadParentMsgViewProvider() {
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
                    })
                    .build();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "create_thread").commit();
    }
}
