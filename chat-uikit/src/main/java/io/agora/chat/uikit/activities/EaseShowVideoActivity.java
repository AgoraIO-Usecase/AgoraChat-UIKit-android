package io.agora.chat.uikit.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.File;

import io.agora.CallBack;
import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.util.EMLog;

/**
 * show the video
 * 
 */
public class EaseShowVideoActivity extends EaseBaseActivity {
	private static final String TAG = "ShowVideoActivity";
	
	private RelativeLayout loadingLayout;
	private ProgressBar progressBar;
	private Uri localFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ease_showvideo_activity);
		loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		final ChatMessage message = getIntent().getParcelableExtra("msg");
		if (!(message.getBody() instanceof VideoMessageBody)) {
			Toast.makeText(EaseShowVideoActivity.this, "Unsupported message body", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		VideoMessageBody messageBody = (VideoMessageBody)message.getBody();

		localFilePath = messageBody.getLocalUri();
		EMLog.d(TAG, "localFilePath = "+localFilePath);
		EMLog.d(TAG, "local filename = "+messageBody.getFileName());

		//Check Uri read permissions
		EaseFileUtils.takePersistableUriPermission(this, localFilePath);

		if(EaseFileUtils.isFileExistByUri(this, localFilePath)) {
		    showLocalVideo(localFilePath);
		} else {
			EMLog.d(TAG, "download remote video file");
			downloadVideo(message);
		}
	}

	@Override
	public void setActivityTheme() {

	}

	private void showLocalVideo(Uri videoUri) {
		EaseShowLocalVideoActivity.actionStart(this, videoUri.toString());
		finish();
	}

	/**
	 * download video file
	 */
	private void downloadVideo(final ChatMessage message) {
		loadingLayout.setVisibility(View.VISIBLE);
		message.setMessageStatusCallback(new CallBack() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loadingLayout.setVisibility(View.GONE);
						progressBar.setProgress(0);
						showLocalVideo(((VideoMessageBody)message.getBody()).getLocalUri());
					}
				});
			}

			@Override
			public void onProgress(final int progress,String status) {
				Log.d("ease", "video progress:" + progress);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressBar.setProgress(progress);
					}
				});

			}

			@Override
			public void onError(final int error, String msg) {
				EMLog.e("###", "offline file transfer error:" + msg);
				Uri localFilePath = ((VideoMessageBody) message.getBody()).getLocalUri();
				String filePath = EaseFileUtils.getFilePath(EaseShowVideoActivity.this, localFilePath);
				if(TextUtils.isEmpty(filePath)) {
				    EaseShowVideoActivity.this.getContentResolver().delete(localFilePath, null, null);
				}else {
					File file = new File(filePath);
					if (file.exists()) {
						file.delete();
					}
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (error == Error.FILE_NOT_FOUND) {
							Toast.makeText(getApplicationContext(), R.string.ease_video_expired, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		ChatClient.getInstance().chatManager().downloadAttachment(message);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
 

}
