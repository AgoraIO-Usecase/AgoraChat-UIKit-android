package io.agora.chat.uikit.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.agora.chat.uikit.chat.EaseChatFragment;
import io.agora.chat.uikit.menu.EaseChatType;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class ChatActivity extends AppCompatActivity {

    public static void actionStart(Context context, String conversationID, EaseChatType chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("conversationID", conversationID);
        intent.putExtra("chatType", chatType.getChatType());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String conversationID = getIntent().getStringExtra("conversationID");
        int chatType = getIntent().getIntExtra("chatType", EaseChatType.SINGLE_CHAT.getChatType());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment,
                        new EaseChatFragment.Builder(conversationID, EaseChatType.from(chatType))
                                .useHeader(true)
                                .setHeaderTitle(conversationID)
                                .enableHeaderPressBack(true)
                                .setHeaderBackPressListener(new EaseTitleBar.OnBackPressListener() {
                                    @Override
                                    public void onBackPress(View view) {
                                        onBackPressed();
                                    }
                                })
                                .build())
                .commit();
    }
}
