package io.agora.chat.uikit.simple;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.agora.CallBack;
import io.agora.chat.ChatClient;
import io.agora.chat.uikit.simple.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignInBinding binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ChatClient.getInstance().getOptions().getAutoLogin() && ChatClient.getInstance().isLoggedInBefore()) {
            MainActivity.actionStart(SignInActivity.this, "");
            finish();
        }else {
            binding.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = binding.etUsername.getText().toString().trim();
                    String trim = binding.etPassword.getText().toString().trim();
                    String toChatName = binding.etToChatUsername.getText().toString().trim();
                    ChatClient.getInstance().login(username, trim, new CallBack() {
                        @Override
                        public void onSuccess() {
                            MainActivity.actionStart(SignInActivity.this, toChatName);
                            finish();
                        }

                        @Override
                        public void onError(int i, String s) {
                            runOnUiThread(()->Toast.makeText(SignInActivity.this, "error: "+i+" error message: "+s, Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            });
        }

    }
}
