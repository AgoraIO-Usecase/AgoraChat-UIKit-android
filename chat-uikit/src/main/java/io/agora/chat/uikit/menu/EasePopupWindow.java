package io.agora.chat.uikit.menu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class EasePopupWindow extends PopupWindow {

    private Context mContext;
    private float mShowAlpha = 0.88f;
    private Drawable mBackgroundDrawable;
    private boolean mCloseChangeBg;

    public EasePopupWindow(Context context) {
        this.mContext = context;
        initBasePopupWindow();
    }

    /**
     * The second parameter is to change the background color
     * @param context
     * @param closeChangeBg
     */
    public EasePopupWindow(Context context, boolean closeChangeBg){
        this.mContext = context;
        this.mCloseChangeBg = closeChangeBg;
        initBasePopupWindow();
    }

    /**
     * Set background transparency
     * @param alpha
     */
    public void setBackgroundAlpha(float alpha){
        this.mShowAlpha = alpha;
    }

    @Override
    public void setOutsideTouchable(boolean touchable) {
        super.setOutsideTouchable(touchable);
        if(touchable) {
            if(mBackgroundDrawable == null) {
                mBackgroundDrawable = new ColorDrawable(0x00000000);
            }
            super.setBackgroundDrawable(mBackgroundDrawable);
        } else {
            super.setBackgroundDrawable(null);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
        setOutsideTouchable(isOutsideTouchable());
    }

    private void initBasePopupWindow() {
        setAnimationStyle(android.R.style.Animation_Dialog);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    @Override
    public void setContentView(View contentView) {
        if(contentView != null) {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            addKeyListener(contentView);
        }
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        showAnimator().start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissAnimator().start();
    }

    /**
     * Window display, window background transparency gradient animation
     * */
    private ValueAnimator showAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mShowAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                if(!mCloseChangeBg) {
                    setWindowBackgroundAlpha(alpha);
                }
            }
        });
        animator.setDuration(360);
        return animator;
    }

    /**
     * The window is hidden, and the window background transparency gradient animation
     * */
    private ValueAnimator dismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(mShowAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                if(!mCloseChangeBg) {
                    setWindowBackgroundAlpha(alpha);
                }
            }
        });
        animator.setDuration(320);
        return animator;
    }

    /**
     * Add outside click event to the form
     * */
    private void addKeyListener(View contentView) {
        if(contentView != null) {
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dismiss();
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private void setWindowBackgroundAlpha(float alpha) {
        Window window = ((Activity)getContext()).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    /**
     * Popup window item click listener
     */
    public interface OnPopupWindowItemClickListener {
        
        /**
         * Menu item click
         * @param item
         * @return
         */
        boolean onMenuItemClick(MenuItemBean item);

        /**
         * Reaction item click
         * @param item
         * @param isAdd
         */
        default void onReactionItemClick(ReactionItemBean item, boolean isAdd) {}
    }

    public interface OnPopupWindowDismissListener {
        void onDismiss(PopupWindow menu);
    }

    public enum Style {
        /**
         * PopupWindow attaches a view
         */
        ATTACH_ITEM_VIEW,
        /**
         * PopupWindow show the bottom of screen
         */
        BOTTOM_SCREEN,
        /**
         * PopupWindow show the center of screen
         */
        CENTER_SCREEN
    }

}