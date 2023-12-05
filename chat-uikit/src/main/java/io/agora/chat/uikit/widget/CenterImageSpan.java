package io.agora.chat.uikit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenterImageSpan extends ImageSpan {

   private Boolean isSmallImage = false;


   public CenterImageSpan(@NonNull Context context, int resourceId) {
      this(context, resourceId,2);
   }

   public CenterImageSpan(@NonNull Context context, int resourceId, int verticalAlignment) {
      super(context, resourceId, verticalAlignment);

   }

   @Override
   public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
      Drawable d = getDrawable();
      Rect rect = d.getBounds();
      if (fm != null){
         Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
         int fontH = fmPaint.descent - fmPaint.ascent;
         int imageH = rect.bottom - rect.top;

         if (imageH > fontH) {
            isSmallImage = false;
            fm.ascent = fmPaint.ascent - (imageH - fontH) / 2;
            fm.top = fmPaint.ascent - (imageH - fontH) / 2;
            fm.bottom = fmPaint.descent + (imageH - fontH) / 2;
            fm.descent = fmPaint.descent + (imageH - fontH) / 2;
         }else {
            isSmallImage = true;
            fm.ascent = -rect.bottom;
            fm.descent = 0;
            fm.top = fm.ascent;
            fm.bottom = 0;
         }
      }
      return rect.right;
   }

   @Override
   public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
      Drawable b = getDrawable();
      canvas.save();

      int transY = bottom - b.getBounds().bottom;
      if (isSmallImage) {
         transY -= ((bottom - top) / 2 - b.getBounds().height() / 2);
      }
      canvas.translate(x, (float) transY);
      b.draw(canvas);
      canvas.restore();
   }

}
