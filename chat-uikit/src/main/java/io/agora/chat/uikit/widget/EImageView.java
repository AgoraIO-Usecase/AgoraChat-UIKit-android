package io.agora.chat.uikit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * To resolve the exception of “trying to use a recycled bitmap android.graphics.Bitmap@2d46e6b”
 */
public class EImageView extends ImageView {
    public EImageView(Context context) {
        super(context);
    }

    public EImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

