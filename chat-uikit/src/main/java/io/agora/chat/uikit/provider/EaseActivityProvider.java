package io.agora.chat.uikit.provider;

import android.app.Activity;

public interface EaseActivityProvider {
    /**
     * Provide a activity from app by activityName of UIKit
     * @param activityName
     * @return
     */
    Class getActivity(String activityName);
}
