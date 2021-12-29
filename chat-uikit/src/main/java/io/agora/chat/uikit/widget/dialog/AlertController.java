package io.agora.chat.uikit.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.collection.ArrayMap;


class AlertController {


    private final Context mContext;
    private final Dialog dialog;
    private final Window window;
    private DialogViewHelper viewHelper;

    public AlertController(Context context, Dialog dialog, Window window) {
        this.mContext = context;
        this.dialog = dialog;
        this.window = window;

    }

    public Context getmContext() {
        return mContext;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Window getWindow() {
        return window;
    }

    public <T extends View> T getViewById(int viewId) {
        return viewHelper.getViewById(viewId);
    }

    static class AlertParams {

        public final Context mContext;
        public final int mThemeResId;
        // Out of the area whether to click cancel
        public boolean mCancelable = true;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        //The container is used to hold the text of the Settings
        public SparseArray<CharSequence> texts;
        public ArrayMap<Integer, Integer> imageViews;
        public SparseArray<View.OnClickListener> listeners;
        public int contentViewId;
        public View contentView;
        public EaseAlertDialog customDialog;
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mAnimation = 0;
        public int mGravity = Gravity.CENTER;


        public AlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
            texts = new SparseArray();
            listeners = new SparseArray();
            imageViews = new ArrayMap<>();
        }

        public void apply(AlertController mAlert) {
            DialogViewHelper viewHelper = null;
            if (contentView != null) {
                viewHelper = new DialogViewHelper(mContext, contentView);
            }
            if (contentViewId != 0) {
                viewHelper = new DialogViewHelper(mContext, contentViewId);
            }
            if(customDialog != null) {
                viewHelper = new DialogViewHelper(mContext);
            }
            if (viewHelper == null) {
                throw new IllegalArgumentException("Not set layout");
            }
            if(customDialog == null) {
                mAlert.getDialog().setContentView(viewHelper.getContentView());
            }
            // set view helper
            mAlert.setViewHelper(viewHelper);
            // set text
            int textsSize = texts.size();
            for (int i = 0; i < textsSize; i++) {
                mAlert.setText(texts.keyAt(i), texts.valueAt(i));
            }
            // set image
            int imageViewsCount = imageViews.size();
            for (int i = 0; i < imageViewsCount; i++) {
                mAlert.setImageView(imageViews.keyAt(i), imageViews.valueAt(i));
            }
            // set click listener
            int listenerSize = listeners.size();
            for (int i = 0; i < listenerSize; i++) {
                mAlert.setOnClickListener(listeners.keyAt(i), listeners.valueAt(i));
            }
            // set layout params
            Window window = mAlert.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            // set width and height
            if (mWidth != 0) {
                params.width = mWidth;
                params.height = mHeight;
            }
            window.setAttributes(params);
            // set gravity
            window.setGravity(mGravity);
            // set pop_up animation
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation);
            }

        }
    }

    private void setImageView(int viewId, int  resId) {
        viewHelper.setImageView(viewId, resId);
    }

    void setOnClickListener(int viewId, View.OnClickListener onClickListener) {
        viewHelper.setOnClickListener(viewId, onClickListener);
    }

    private void setViewHelper(DialogViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    void setText(int viewId, CharSequence text) {
        viewHelper.setText(viewId, text);
    }
}
