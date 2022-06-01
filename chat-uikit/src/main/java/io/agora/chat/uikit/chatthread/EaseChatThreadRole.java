package io.agora.chat.uikit.chatthread;

public enum EaseChatThreadRole {
    /**
     * thread member
     */
    MEMBER,
    /**
     * thread creator
     */
    CREATOR,
    /**
     * group admin
     */
    GROUP_ADMIN,
    /**
     * Unknown identity
     */
    UNKNOWN;

    public static EaseChatThreadRole getThreadRole(int role) {
        EaseChatThreadRole threadRole = UNKNOWN;
        switch (role) {
            case 0 :
                threadRole = MEMBER;
                break;
            case 1 :
                threadRole = CREATOR;
                break;
            case 2 :
                threadRole = GROUP_ADMIN;
                break;
        }
        return threadRole;
    }
}
