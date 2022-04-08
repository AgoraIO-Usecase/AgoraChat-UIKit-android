package io.agora.chat.uikit.activities;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseActivity;
import io.agora.chat.uikit.widget.video.EaseImageGridFragment;


public class EaseImageGridActivity extends EaseBaseActivity {
    private final static String TAG = EaseImageGridActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.ease_activity_image_grid);
        setFitSystemForTheme(false, R.color.transparent, false);

        initData();
    }


    protected void initData() {
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fl_fragment, new EaseImageGridFragment(), TAG);
            ft.commit();
        }
    }
}
