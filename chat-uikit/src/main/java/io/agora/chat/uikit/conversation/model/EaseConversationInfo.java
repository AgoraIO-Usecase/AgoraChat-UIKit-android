package io.agora.chat.uikit.conversation.model;

import java.io.Serializable;

public class EaseConversationInfo implements Serializable, Comparable<EaseConversationInfo> {
    // Default is conversation, maybe other entity
    private Object info;
    // Whether the item is selected
    private boolean isSelected;
    private long timestamp;
    // Whether bo be make top
    private boolean isTop;
    // Whether is group
    private boolean isGroup;
    // Whether to enable offline push
    private boolean isMute;

    private OnSelectListener listener;

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        if(listener != null) {
            listener.onSelect(selected);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public int compareTo(EaseConversationInfo o) {
        return timestamp > o.timestamp ? -1 : 1;
    }

    public interface OnSelectListener {
        void onSelect(boolean isSelected);
    }
}

