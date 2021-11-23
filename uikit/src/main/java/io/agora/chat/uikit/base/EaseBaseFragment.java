package io.agora.chat.uikit.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
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

    /**
     * hide keyboard
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager == null) {
                    return;
                }
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
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
