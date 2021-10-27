package io.agora.chat.uikit.conversation.viewholder;

public enum EaseConversationType {
    VIEW_TYPE_CONVERSATION(0),
    VIEW_TYPE_CONVERSATION_NOTIFICATION(1),
    VIEW_TYPE_CONVERSATION_UNKNOWN(2);

    private int value;

    private EaseConversationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EaseConversationType from(int value) {
        EaseConversationType[] types = values();
        int length = types.length;

        for(int i = 0; i < length; i++) {
            EaseConversationType type = types[i];
            if (type.value == value) {
                return type;
            }
        }

        return VIEW_TYPE_CONVERSATION;
    }
}
