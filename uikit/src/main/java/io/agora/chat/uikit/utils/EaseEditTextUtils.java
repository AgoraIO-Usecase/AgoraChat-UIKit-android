package io.agora.chat.uikit.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import io.agora.chat.uikit.R;
import io.agora.util.EMLog;


public class EaseEditTextUtils {

    public static void changePwdDrawableRight(EditText editText, Drawable eyeOpen , Drawable eyeClose, Drawable left, Drawable top, Drawable bottom) {
        //Identify whether the password can be seen
        final boolean[] canBeSeen = {false};
        editText.setOnTouchListener((v, event) -> {

            Drawable drawable = editText.getCompoundDrawables()[2];
            if (drawable == null)
                return false;
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > editText.getWidth()
                    - editText.getPaddingRight()
                    - drawable.getIntrinsicWidth())
            {

                if (canBeSeen[0])
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(left, top, eyeOpen, bottom);
                    canBeSeen[0] = false;
                } else
                {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

                    editText.setCompoundDrawablesWithIntrinsicBounds(left, top, eyeClose, bottom);
                    canBeSeen[0] = true;
                }
                editText.setSelection(editText.getText().toString().length());

                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();

                return true;
            }
            return false;
        });

    }

    public static void showRightDrawable(EditText editText, Drawable right) {
        String content = editText.getText().toString().trim();
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, TextUtils.isEmpty(content) ? null : right, null);
    }

    public static void clearEditTextListener(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            Drawable drawable = editText.getCompoundDrawables()[2];
            if (drawable == null)
                return false;
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > editText.getWidth()
                    - editText.getPaddingRight()
                    - drawable.getIntrinsicWidth()) {
                editText.setText("");
                return true;
            }
            return false;
        });
    }

    /**
     * Single line, determine the different positions of the ellipsis according to the keyword
     * @param textView
     * @param str
     * @param keyword
     * @param width
     * @return
     */
    public static String ellipsizeString(TextView textView, String str, String keyword, int width) {
        if(TextUtils.isEmpty(keyword)) {
            return str;
        }
        Paint paint = textView.getPaint();
        if(paint.measureText(str) < width) {
            return str;
        }
        int count = paint.breakText(str, 0, str.length(), true, width, null);
        int index = str.indexOf(keyword);
        //If the keyword is on the first line, an ellipsis is displayed at the end
        if(index + keyword.length() < count) {
            return str;
        }
        //If the keyword is at the end, an ellipsis is displayed at the beginning
        if(str.length() - index <= count - 3) {
            String end = str.substring(str.length() - count);
            end = "..." + end.substring(3);
            return end;
        }
        //If it is in the middle, display ellipsis at the beginning and end
        int subCount = (count - keyword.length()) / 2;
        String middle = str.substring(index - subCount, index + keyword.length() + subCount);
        middle = "..." + middle.substring(3);
        middle = middle.substring(0, middle.length() - 3) + "...";
        return middle;
    }

    public static SpannableStringBuilder highLightKeyword(Context context, String str, String keyword) {
        if(TextUtils.isEmpty(str) || TextUtils.isEmpty(keyword) || !str.contains(keyword)) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.ease_color_brand)), str.indexOf(keyword), str.indexOf(keyword) + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * Set the maximum number of displayed lines and keep the text type at the end
     * @param textView
     * @param str
     * @param num
     * @param width
     * @return
     */
    public static String ellipsizeMiddleString(TextView textView, String str, int num, int width) {
        textView.setMaxLines(num);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        TextPaint paint = textView.getPaint();
        if(TextUtils.isEmpty(str) || width <= 0 || paint.measureText(str) < width) {
            return str;
        }
        int startIndex = 0;
        int maxNum = 0;
        for(int i = 0; i < num; i++) {
            if(startIndex < str.length()) {
                maxNum += paint.breakText(str, startIndex, str.length(), true, width, null);
                startIndex = maxNum - 1;
            }
        }
        if(str.length() < maxNum) {
            return str;
        }
        int maxCount = 0;
        try {
            maxCount = textView.getLayout().getLineEnd(num - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
        if(str.length() < maxCount) {
            return str;
        }
        if(str.contains(".")) {
            int lastIndex = str.lastIndexOf(".");
            String suffix = "..." + str.substring(lastIndex - 5);
            float requestWidth = paint.measureText(suffix);
            String reverse = new StringBuilder(str.substring(0, maxCount)).reverse().toString();
            int takeUpCount = paint.breakText(reverse, 0, reverse.length(), true, requestWidth, null);
            takeUpCount = getTakeUpCount(paint, reverse, takeUpCount, requestWidth);
            str = str.substring(0, maxCount - takeUpCount) + suffix;
            EMLog.i("EaseEditTextUtils", "last str = "+str);
        }
        return str;
    }

    /**
     * @param paint
     * @param reverse
     * @param takeUpCount
     * @param requestWidth
     * @return
     */
    private static int getTakeUpCount(Paint paint, String reverse, int takeUpCount, float requestWidth) {
        float measureWidth = paint.measureText(reverse.substring(0, takeUpCount));
        if(measureWidth <= requestWidth) {
            return getTakeUpCount(paint, reverse, takeUpCount + 1, requestWidth);
        }
        return takeUpCount + 1;
    }
}
