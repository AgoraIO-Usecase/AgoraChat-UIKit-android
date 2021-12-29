package io.agora.chat.uikit.chat.viewholder;

public enum EaseChatType {
    VIEW_TYPE_MESSAGE_TXT_ME(0),
    VIEW_TYPE_MESSAGE_TXT_OTHER(1),
    VIEW_TYPE_MESSAGE_IMAGE_ME(2),
    VIEW_TYPE_MESSAGE_IMAGE_OTHER(3),
    VIEW_TYPE_MESSAGE_VIDEO_ME(4),
    VIEW_TYPE_MESSAGE_VIDEO_OTHER(5),
    VIEW_TYPE_MESSAGE_LOCATION_ME(6),
    VIEW_TYPE_MESSAGE_LOCATION_OTHER(7),
    VIEW_TYPE_MESSAGE_VOICE_ME(8),
    VIEW_TYPE_MESSAGE_VOICE_OTHER(9),
    VIEW_TYPE_MESSAGE_FILE_ME(10),
    VIEW_TYPE_MESSAGE_FILE_OTHER(11),
    VIEW_TYPE_MESSAGE_CMD_ME(12),
    VIEW_TYPE_MESSAGE_CMD_OTHER(13),
    VIEW_TYPE_MESSAGE_CUSTOM_ME(14),
    VIEW_TYPE_MESSAGE_CUSTOM_OTHER(15),
    VIEW_TYPE_MESSAGE_UNKNOWN_ME(16),
    VIEW_TYPE_MESSAGE_UNKNOWN_OTHER(17);

    private int value;

    private EaseChatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EaseChatType from(int value) {
        EaseChatType[] types = values();
        int length = types.length;

        for(int i = 0; i < length; i++) {
            EaseChatType type = types[i];
            if (type.value == value) {
                return type;
            }
        }

        return VIEW_TYPE_MESSAGE_UNKNOWN_OTHER;
    }
}
