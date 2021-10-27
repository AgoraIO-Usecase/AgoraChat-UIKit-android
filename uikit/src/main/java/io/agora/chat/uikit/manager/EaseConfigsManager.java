package io.agora.chat.uikit.manager;

import android.content.Context;

import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;


public class EaseConfigsManager {
    private static final String TAG = EaseConfigsManager.class.getSimpleName();

    /**
     * 是否使用发送channel_ack消息功能，此功能启动旨在减少发送read_ack消息，默认为开启
     * @return
     */
    public static boolean enableSendChannelAck() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_enable_send_channel_ack);
        }
        return false;
    }

    /**
     * Whether to display system notifications, such as notification of adding a contact.
     * @return
     */
    public static boolean isShowSysNotificationForConversation() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_conversation_show_system_notification);
        }
        return false;
    }

    private static boolean checkIfUIKitInit() {
        Context context = EaseUIKit.getInstance().getContext();
        if(context == null) {
            EMLog.e(TAG, "You should initialize the UIKit first!");
            return false;
        }
        return true;
    }
}

