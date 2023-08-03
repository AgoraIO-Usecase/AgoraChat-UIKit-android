package io.agora.chat.uikit.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.chathistory.EaseChatHistoryFragment;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.databinding.EaseActivityFragmentBinding;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class EaseChatHistoryActivity extends EaseBaseActivity {
    private EaseActivityFragmentBinding binding;
    private ChatMessage combineMessage;

    public static void actionStart(Context context, ChatMessage combineMessage) {
        Intent intent = new Intent(context, EaseChatHistoryActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE, combineMessage);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        binding = EaseActivityFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext= this;
        initIntent(getIntent());
        initView();
    }

    public void initIntent(Intent intent) {
        combineMessage = intent.getParcelableExtra(EaseConstant.EXTRA_CHAT_COMBINE_MESSAGE);
    }

    public void initView() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("chat_history");
        if(fragment == null) {
            EaseChatHistoryFragment.Builder builder = new EaseChatHistoryFragment.Builder(combineMessage)
                    .useHeader(true)
                    .enableHeaderPressBack(true)
                    .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                        @Override
                        public void onBackPress(View view) {
                            onBackPressed();
                        }
                    })
                    .setEmptyLayout(R.layout.ease_layout_no_data_show_nothing);
            fragment = builder.build();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment, "chat_history").commit();
    }
}
