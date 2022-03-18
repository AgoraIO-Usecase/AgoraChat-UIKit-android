package io.agora.chat.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import io.agora.chat.ChatThread;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.databinding.EaseActivityThreadListBinding;
import io.agora.chat.uikit.thread.interfaces.OnItemThreadClickListener;
import io.agora.chat.uikit.thread.EaseThreadListFragment;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class EaseThreadListActivity extends EaseBaseActivity {
    private EaseActivityThreadListBinding binding;
    private String parentId;

    public static void actionStart(Context context, String parentId) {
        Intent intent = new Intent(context, EaseThreadListActivity.class);
        intent.putExtra("parentId", parentId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        binding = EaseActivityThreadListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initIntent(getIntent());
        initView();
        initListener();
        initData();
    }

    public void initIntent(Intent intent) {
        parentId = intent.getStringExtra("parentId");
    }

    public void initView() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("thread_list");
        if(fragment == null) {
            fragment = new EaseThreadListFragment.Builder(parentId)
                    .useHeader(true)
                    .enableHeaderPressBack(true)
                    .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                        @Override
                        public void onBackPress(View view) {
                            onBackPressed();
                        }
                    })
                    .setLoadResultListener(new EaseThreadListFragment.OnLoadResultListener() {
                        @Override
                        public void onLoadFinish() {

                        }

                        @Override
                        public void onLoadFail(int errorCode, String message) {

                        }
                    })
                    .setOnItemClickListener(new OnItemThreadClickListener() {
                        @Override
                        public void onItemClick(View view, ChatThread thread, String messageId) {
                            EaseThreadChatActivity.actionStart(EaseThreadListActivity.this, messageId, thread.getThreadId(), thread);
                        }
                    })
                    .build();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "thread_list").commit();
    }

    public void initListener() {

    }

    public void initData() {

    }
}
