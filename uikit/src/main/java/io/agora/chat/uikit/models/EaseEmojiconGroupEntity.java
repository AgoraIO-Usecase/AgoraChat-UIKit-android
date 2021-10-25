package io.agora.chat.uikit.models;


import java.util.List;

import io.agora.chat.uikit.models.EaseEmojicon.Type;

/**
 * An entity of group Emojicon
 */
public class EaseEmojiconGroupEntity {
    /**
     * Emojicon data
     */
    private List<EaseEmojicon> emojiconList;
    /**
     * Group icon
     */
    private int icon;
    /**
     * Group name
     */
    private String name;
    /**
     * Emojicon type
     */
    private Type type;
    
    public EaseEmojiconGroupEntity(){}
    
    public EaseEmojiconGroupEntity(int icon, List<EaseEmojicon> emojiconList){
        this.icon = icon;
        this.emojiconList = emojiconList;
        type = Type.NORMAL;
    }
    
    public EaseEmojiconGroupEntity(int icon, List<EaseEmojicon> emojiconList, Type type){
        this.icon = icon;
        this.emojiconList = emojiconList;
        this.type = type;
    }
    
    public List<EaseEmojicon> getEmojiconList() {
        return emojiconList;
    }
    public void setEmojiconList(List<EaseEmojicon> emojiconList) {
        this.emojiconList = emojiconList;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    
}
