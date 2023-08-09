package io.agora.chat.uikit.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.agora.chat.uikit.manager.EaseThreadManager;


public class EaseBaseFragment extends Fragment {
    public Activity mContext;
    public boolean onClickBackPress;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    /**
     * Get the current view control by id,
     * which needs to be called in the life cycle after onViewCreated()
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return requireView().findViewById(id);
    }

    /**
     * back
     */
    public void onBackPress() {
        onClickBackPress = true;
        mContext.onBackPressed();
    }

    public  void showKeyboard(final View view) {
        if (view == null) return;
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    public  void hideKeyboard(final View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    /**
     * Determine whether the current activity is available
     * @return
     */
    public boolean isActivityDisable() {
        return mContext == null || mContext.isFinishing();
    }


    /**
     * Switch to UI thread
     * @param runnable
     */
    public void runOnUiThread(Runnable runnable) {
        EaseThreadManager.getInstance().runOnMainThread(runnable);
    }

}
