package io.agora.chat.uikit.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.agora.chat.uikit.manager.EaseSoftKeyboardHelper;
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
        EaseSoftKeyboardHelper.showKeyboard(view);
    }

    public  void hideKeyboard(final View view) {
        EaseSoftKeyboardHelper.hideKeyboard(view);
    }

    public void toggleKeyboard(Context context) {
        EaseSoftKeyboardHelper.toggleKeyboard(context);
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
