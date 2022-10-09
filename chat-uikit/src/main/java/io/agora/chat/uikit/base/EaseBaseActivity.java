/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.agora.chat.uikit.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.StatusBarCompat;
import io.agora.chat.uikit.widget.dialog.EaseProgressDialog;


public class EaseBaseActivity extends AppCompatActivity {

    protected InputMethodManager inputMethodManager;
    private EaseProgressDialog dialog;
    //Dialog generation time, used to determine the display time of the dialog
    private long dialogCreateTime;
    // Used for the dialog delay to disappear
    private Handler handler = new Handler();
    protected Activity mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // should be in launcher activity, but all app use this can avoid the problem
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
        mContext = this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setActivityTheme();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setActivityTheme();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setActivityTheme();
    }

    public void setActivityTheme() {
        setFitSystemForTheme(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
//        EaseUI.getInstance().getNotifier().reset();
    }
    
    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * back
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * Common settings for activity
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, "#feffffff", true);
    }

    /**
     * Can set the status bar's style and change the background color
     * @param fitSystemForTheme
     * @param colorId Color resource
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, @ColorRes int colorId, boolean isDark) {
        setFitSystem(fitSystemForTheme);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, colorId));
        StatusBarCompat.setLightStatusBar(this, isDark);
    }

    /**
     * Can set the status bar's style and change the background color
     * @param fitSystemForTheme
     * @param color Color string
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, String color, boolean isDark) {
        setFitSystem(fitSystemForTheme);
        StatusBarCompat.compat(this, Color.parseColor(color));
        StatusBarCompat.setLightStatusBar(this, isDark);
    }

    /**
     * Set status bar's style
     * @param fitSystemForTheme
     */
    private void setFitSystem(boolean fitSystemForTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if(fitSystemForTheme) {
            ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    public void showLoading() {
        showLoading(getString(R.string.ease_loading));
    }

    public void showLoading(String message) {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if(this.isFinishing()) {
            return;
        }
        dialogCreateTime = System.currentTimeMillis();
        dialog = new EaseProgressDialog.Builder(this)
                .setLoadingMessage(message)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void dismissLoading() {
        if(dialog != null && dialog.isShowing()) {
            if(System.currentTimeMillis() - dialogCreateTime < 500 && !isFinishing()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mContext != null && !mContext.isFinishing() && dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                }, 1000);
            }else {
                dialog.dismiss();
                dialog = null;
            }

        }
    }
}
