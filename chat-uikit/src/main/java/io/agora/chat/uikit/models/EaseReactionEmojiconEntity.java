package io.agora.chat.uikit.models;

import androidx.annotation.NonNull;

import java.util.List;

public class EaseReactionEmojiconEntity {
    private EaseEmojicon emojicon;
    private int count;
    private List<String> userList;
    private boolean isAddedBySelf;

    public EaseEmojicon getEmojicon() {
        return emojicon;
    }

    public void setEmojicon(EaseEmojicon emojicon) {
        this.emojicon = emojicon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public boolean isAddedBySelf() {
        return isAddedBySelf;
    }

    public void setAddedBySelf(boolean addedBySelf) {
        isAddedBySelf = addedBySelf;
    }

    @Override
    public String toString() {
        return "EaseReactionEmojiconEntity{" +
                "emojicon=" + emojicon +
                ", count=" + count +
                ", userList=" + userList +
                ", isAddedBySelf=" + isAddedBySelf +
                '}';
    }
}
