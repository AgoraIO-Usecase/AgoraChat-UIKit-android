package io.agora.chat.uikit.lives;

import android.text.TextUtils;

public enum EaseLiveMessageType {
    /**
     * 礼物消息
     */
    CHATROOM_GIFT("chatroom_gift"),

    /**
     * 点赞
     */
    CHATROOM_PRAISE("chatroom_praise"),

    /**
     * 弹幕
     */
    CHATROOM_BARRAGE("chatroom_barrage");

    private String name;

    private EaseLiveMessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EaseLiveMessageType fromName(String name) {
        for (EaseLiveMessageType type : EaseLiveMessageType.values()) {
            if (TextUtils.equals(type.getName(), name)) {
                return type;
            }
        }
        return null;
    }

}
