package io.agora.chat.uikit.menu;

import androidx.annotation.NonNull;

public class ReactionItemBean {
    private String identityCode;
    private int icon;
    private String emojiText;

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getEmojiText() {
        return emojiText;
    }

    public void setEmojiText(String emojiText) {
        this.emojiText = emojiText;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReactionItemBean{" +
                "identityCode='" + identityCode + '\'' +
                ", icon=" + icon +
                ", emojiText='" + emojiText + '\'' +
                '}';
    }
}

