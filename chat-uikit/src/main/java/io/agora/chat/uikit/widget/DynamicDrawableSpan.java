package io.agora.chat.uikit.widget;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import javax.security.auth.login.LoginException;


public class DynamicDrawableSpan extends ReplacementSpan {
    private static final String TAG = DynamicDrawableSpan.class.getSimpleName();

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    /**
     * A constant indicating that this span should be vertically centered between
     * the top and the lowest descender.
     */
    public static final int ALIGN_CENTER = 2;

    /**
     * A constant indicating that this span should be vertically centered between
     * the top and the lowest descender.
     */
    public static final int ALIGN_TOP = 3;

    protected final int mVerticalAlignment;

    private WeakReference<Drawable> mDrawableRef;
    private Drawable mDrawable;

    /**
     * Creates a {@link DynamicDrawableSpan}. The default vertical alignment is
     * {@link #ALIGN_BOTTOM}
     */
    public DynamicDrawableSpan() {
        mVerticalAlignment = ALIGN_BOTTOM;
    }

    /**
     * Creates a {@link DynamicDrawableSpan}. The default vertical alignment is
     * {@link #ALIGN_BOTTOM}
     */
    public DynamicDrawableSpan(final Drawable drawable) {
        mVerticalAlignment = ALIGN_BOTTOM;
        mDrawable = drawable;
        if(mDrawable != null) {
            mDrawableRef = new WeakReference<>(drawable);
        }
    }

    /**
     * Creates a {@link DynamicDrawableSpan} based on a vertical alignment.\
     *
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM}, {@link #ALIGN_BASELINE} or
     *                          {@link #ALIGN_CENTER}
     */
    protected DynamicDrawableSpan(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    /**
     * Creates a {@link DynamicDrawableSpan} based on a vertical alignment.\
     *
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM}, {@link #ALIGN_BASELINE} or
     *                          {@link #ALIGN_CENTER}
     */
    protected DynamicDrawableSpan(final Drawable drawable, int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
        mDrawable = drawable;
        if(mDrawable != null) {
            mDrawableRef = new WeakReference<>(drawable);
        }
    }

    /**
     * Returns the vertical alignment of this span, one of {@link #ALIGN_BOTTOM},
     * {@link #ALIGN_BASELINE} or {@link #ALIGN_CENTER}.
     */
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    /**
     * Your subclass must implement this method to provide the bitmap
     * to be drawn.  The dimensions of the bitmap must be the same
     * from each call to the next.
     */
    public Drawable getDrawable() {
       return mDrawable;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       @IntRange(from = 0) int start, @IntRange(from = 0) int end,
                       @Nullable Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        if(mVerticalAlignment == ALIGN_TOP) {
            if (fm != null) {
                fm.descent = fm.descent + rect.bottom - (fm.bottom - fm.top);
                fm.bottom = fm.descent;
            }
        }else if(mVerticalAlignment == ALIGN_CENTER) {
            if(isDrawableHigher(fm)) {
                if (fm != null) {
                    int dValue = (rect.height() - (fm.bottom - fm.top)) / 2;
                    fm.descent = fm.descent + dValue;
                    fm.bottom = fm.descent;

                    fm.ascent = fm.ascent - dValue;
                    fm.top = fm.ascent;
                }
            }
        }else {
            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
        }

        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {

        Drawable b = getCachedDrawable();
        canvas.save();
        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = 0;
        } else if (mVerticalAlignment == ALIGN_TOP) {
            transY = 0;
        }
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null) {
            d = wr.get();
        }

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private boolean isDrawableHigher(@Nullable Paint.FontMetricsInt fm) {
        if(fm == null) {
            return true;
        }
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        int drawableHeight = rect.bottom - rect.top;
        int textHeight = fm.bottom - fm.top;
        return drawableHeight - textHeight > 0;
    }
}