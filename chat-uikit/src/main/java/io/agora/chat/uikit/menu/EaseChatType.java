package io.agora.chat.uikit.menu;

public enum EaseChatType {
    UNKNOWN(0),
    SINGLE_CHAT(1),
    GROUP_CHAT(2),
    CHATROOM(3);

    private int chatType;

    private EaseChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getChatType() {
        return chatType;
    }

    public static EaseChatType from(int chatType) {
        for (EaseChatType type: EaseChatType.values()) {
            if(type.chatType == chatType) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
