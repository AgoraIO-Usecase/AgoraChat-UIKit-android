package io.agora.chat.uikit.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import io.agora.chat.uikit.R;


/**
 * Sample:
 * val dialog = EaseAlertDialog.Builder(this)
 *                 .setContentView(R.layout.dialog_reset_pwd)
 *                 .setText(R.id.tv_message, msg)
 *                 .setLayoutParams(UIUtils.dp2px(this, 256), ViewGroup.LayoutParams.WRAP_CONTENT)
 *                 .show()
 *         dialog.setOnClickListener(R.id.btn_input_again, object : View.OnClickListener {
 *             override fun onClick(v: View?) {
 *                 dialog.dismiss()
 *             }
 *         })
 */
public class EaseAlertDialog extends Dialog {
    //Controller
    final AlertController mAlert;

    public EaseAlertDialog(@NonNull Context context) {
        this(context, 0);

    }

    public EaseAlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mAlert = new AlertController(getContext(), this, getWindow());
    }

    public void setText(int viewId, String text) {
        mAlert.setText(viewId, text);
    }

    public void setOnClickListener(int viewId, View.OnClickListener onClickListener) {
        mAlert.setOnClickListener(viewId, onClickListener);
    }

    public <T extends View> T getViewById(int viewId) {
        return mAlert.getViewById(viewId);
    }

    /**
     * builder
     */
    public static class Builder<T extends EaseAlertDialog> {
        private final AlertController.AlertParams P;
        private T dialog;
        private T customDialog;

        public Builder(Context context) {
            this(context, R.style.dialog);
        }

        public Builder(Context context, int themeResId) {
            P = new AlertController.AlertParams(
                    context, themeResId);
        }

        /**
         * Set the layout as a resource ID
         *
         * @param contentViewId
         * @return
         */
        public Builder<T> setContentView(@LayoutRes int contentViewId) {
            P.contentViewId = contentViewId;
            return this;
        }

        /**
         * Set the layout as a View
         *
         * @param contentView
         * @return
         */
        public Builder<T> setContentView(View contentView) {
            P.contentView = contentView;
            return this;
        }

        /**
         * Sets the text content based on the control ID
         *
         * @param viewId
         * @param text
         * @return
         */
        public Builder<T> setText(@IdRes int viewId, String text) {
            P.texts.put(viewId, text);
            return this;
        }

        public Builder<T> setText(int viewId, CharSequence text) {
            P.texts.put(viewId, text);
            return this;
        }
        /**
         * Set the image based on the control ID
         *
         * @param viewId
         * @param imageId
         * @return
         */
        public Builder<T> setImageview(int viewId, int imageId) {
            P.imageViews.put(viewId, imageId);
            return this;
        }

        /**
         * Set the listener based on the control ID
         *
         * @param viewId
         * @param listener
         * @return
         */
        public Builder<T> setOnClickListener(int viewId, View.OnClickListener listener) {
            P.listeners.put(viewId, listener);
            return this;
        }

        public Builder<T> setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        public Builder<T> setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }
        public Builder<T> setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder<T> setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }
        
        public Builder<T> setCustomDialog(T dialog) {
            this.customDialog = dialog;
            P.customDialog = dialog;
            return this;
        }

        public T create() {
            // We can't use Dialog's 3-arg constructor with the createThemeContextWrapper param,
            // so we always have to re-set the theme
            final T dialog = customDialog != null ? customDialog : (T) new EaseAlertDialog(P.mContext, P.mThemeResId);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        public T show() {
            dialog = create();
            dialog.show();
            return dialog;
        }

        public void dismiss() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }

        /**
         * Set up the full width
         *
         * @return
         */
        public Builder<T> setFullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * Set the bottom pop-up animation
         *
         * @return
         */
        public Builder<T> setFromBottomAnimation() {
            P.mAnimation = R.style.dialog_from_bottom_anim;
            return this;
        }

        /**
         * Set gravity
         *
         * @param gravity
         * @return
         */
        public Builder<T> setGravity(int gravity) {
            P.mGravity = gravity;
            return this;
        }

        /**
         * Set animation
         *
         * @param animation
         * @return
         */
        public Builder<T> setAnimation(int animation) {
            P.mAnimation = animation;
            return this;
        }

        /**
         * Set LayoutParams
         *
         * @param width
         * @param height
         * @return
         */
        public Builder<T> setLayoutParams(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }
    }

}
