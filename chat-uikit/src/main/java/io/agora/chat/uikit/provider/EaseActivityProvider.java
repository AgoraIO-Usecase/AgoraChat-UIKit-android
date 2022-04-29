package io.agora.chat.uikit.provider;


public interface EaseActivityProvider {
    /**
     * Provide a activity from app by activityName of UIKit
     * @param activityName
     * @return
     */
    Class getActivity(String activityName);
}
