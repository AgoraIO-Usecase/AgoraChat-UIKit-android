package io.agora.chat.uikit.manager;

import android.content.Context;

import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;


public class EaseConfigsManager {
    private static final String TAG = EaseConfigsManager.class.getSimpleName();

    /**
     * Whether to use the sending channel_ack message function, this function is activated to reduce the sending of read_ack messages,
     * and it is enabled by default
     * @return
     */
    public static boolean enableSendChannelAck() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_enable_send_channel_ack);
        }
        return false;
    }

    /**
     * Get whether to show send button in input menu
     * @return
     */
    public static boolean isShowInputMenuSendButton() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_input_show_send_button);
        }
        return false;
    }

    /**
     * Get whether to enable reply messages.
     * @return
     */
    public static boolean enableReplyMessage() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_enable_message_reply);
        }
        return false;
    }

    /**
     * Get whether to enable send combine messages.
     * @return
     */
    public static boolean enableSendCombineMessage() {
        if(checkIfUIKitInit()) {
            return EaseUtils.getBooleanResource(EaseUIKit.getInstance().getContext(), R.bool.ease_enable_message_combine);
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

