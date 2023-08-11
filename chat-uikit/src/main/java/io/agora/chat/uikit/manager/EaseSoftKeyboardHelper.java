package io.agora.chat.uikit.manager;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class EaseSoftKeyboardHelper {
    /**
     * show soft keyboard.
     * @param view
     */
    public static void showKeyboard(final View view) {
        if (view == null) return;
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    /**
     * hide soft keyboard.
     * @param view
     */
    public static void hideKeyboard(final View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * toggle soft keyboard.
     * @param context
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }
}
