package io.agora.chat.uikit.activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.agora.CallBack;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.utils.EaseCompat;


public class EaseShowNormalFileActivity extends EaseBaseActivity {
    private static final String TAG = EaseShowNormalFileActivity.class.getSimpleName();
	private ProgressBar progressBar;

    @Override
    public void setActivityTheme() {
        super.setActivityTheme();
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ease_activity_show_file);
        setFitSystemForTheme(true, R.color.transparent, true);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		final ChatMessage message = getIntent().getParcelableExtra("msg");
        if (!(message.getBody() instanceof FileMessageBody)) {
            Toast.makeText(EaseShowNormalFileActivity.this, "Unsupported message body", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        message.setMessageStatusCallback(new CallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EaseCompat.openFile(EaseShowNormalFileActivity.this,
                                ((FileMessageBody) message.getBody()).getLocalUri());
                        finish();
                    }
                });

            }

            @Override
            public void onError(final int code, final String error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EaseCompat.deleteFile(EaseShowNormalFileActivity.this, ((FileMessageBody) message.getBody()).getLocalUri());
                        String str4 = getResources().getString(R.string.ease_failed_to_download_file);
                        if (code == Error.FILE_NOT_FOUND) {
                            str4 = getResources().getString(R.string.ease_file_expired);
                        }
                        Toast.makeText(getApplicationContext(), str4+message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        ChatClient.getInstance().chatManager().downloadAttachment(message);
	}
}
