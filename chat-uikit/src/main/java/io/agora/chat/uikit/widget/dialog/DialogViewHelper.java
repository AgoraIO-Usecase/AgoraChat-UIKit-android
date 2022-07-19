package io.agora.chat.uikit.widget.dialog;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;


class DialogViewHelper {
    private final Context mContext;
    private final View mContentView;
    private SparseArray<WeakReference<View>> views = new SparseArray<>();//Use WeakReference to prevent memory leaks


    public DialogViewHelper(Context mContext) {
        this.mContext = mContext;
        this.mContentView = null;
    }

    public DialogViewHelper(Context mContext, View contentView) {
        this.mContext = mContext;
        this.mContentView = contentView;
    }

    public DialogViewHelper(Context mContext, int contentViewId) {
        this.mContext = mContext;
        this.mContentView = LayoutInflater.from(mContext).inflate(contentViewId, null);
    }

    /**
     * Set click listener
     * @param viewId
     * @param onClickListener
     */
    public void setOnClickListener(int viewId, View.OnClickListener onClickListener) {
        View view = getViewById(viewId);
        if(view != null) {
            view.setOnClickListener(onClickListener);
        }
    }

    <T extends View> T getViewById(int viewId) {
        View view = null;
        WeakReference<View> weakReference = views.get(viewId);
        if (weakReference != null) {
            view = weakReference.get();
        } else {
            if(mContentView != null) {
                view = mContentView.findViewById(viewId);
                views.put(viewId, new WeakReference<View>(view));
            }
        }
        return (T) view;

    }

    /**
     * Set text
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        TextView view = getViewById(viewId);
        if (view != null) {
            view.setText(text);
            //Make hyperlink clickable
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * @return the dialog's view
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * Set Image View by resource ID
     * @param viewId
     * @param resId
     */
    public void setImageView(int viewId, int resId) {
        ImageView imageView = getViewById(viewId);
        if(imageView!=null) {
            imageView.setImageResource(resId);
        }
    }
}
