package io.agora.chat.uikit.thread;

public enum EaseThreadRole {
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

    public static EaseThreadRole getThreadRole(int role) {
        EaseThreadRole threadRole = UNKNOWN;
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
