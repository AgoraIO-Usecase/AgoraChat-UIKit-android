package io.agora.chat.uikit.models;

import java.util.LinkedHashMap;

import io.agora.chat.uikit.R;

public class EaseMessageMenuData {

    private static final int[] REACTION_ICONS = new int[]{
            R.drawable.emoji_1,
            R.drawable.emoji_2,
            R.drawable.emoji_3,
            R.drawable.emoji_4,
            R.drawable.emoji_5,
            R.drawable.emoji_6,
            R.drawable.emoji_7,
            R.drawable.emoji_8,
            R.drawable.emoji_9,
            R.drawable.emoji_10,
            R.drawable.emoji_11,
            R.drawable.emoji_12,
            R.drawable.emoji_13,
            R.drawable.emoji_14,
            R.drawable.emoji_15,
            R.drawable.emoji_16,
            R.drawable.emoji_17,
            R.drawable.emoji_18,
            R.drawable.emoji_19,
            R.drawable.emoji_20,
            R.drawable.emoji_21,
            R.drawable.emoji_22,
            R.drawable.emoji_23,
            R.drawable.emoji_24,
            R.drawable.emoji_25,
            R.drawable.emoji_26,
            R.drawable.emoji_27,
            R.drawable.emoji_28,
            R.drawable.emoji_29,
            R.drawable.emoji_30,
            R.drawable.emoji_31,
            R.drawable.emoji_32,
            R.drawable.emoji_33,
            R.drawable.emoji_34,
            R.drawable.emoji_35,
            R.drawable.emoji_36,
            R.drawable.emoji_37,
            R.drawable.emoji_38,
            R.drawable.emoji_39,
            R.drawable.emoji_40,
            R.drawable.emoji_41,
            R.drawable.emoji_42,
            R.drawable.emoji_43,
            R.drawable.emoji_44,
            R.drawable.emoji_45,
            R.drawable.emoji_46,
            R.drawable.emoji_47,
            R.drawable.emoji_48,
            R.drawable.emoji_49,
    };
    public static String[] REACTION_FREQUENTLY_ICONS_IDS = new String[]{
            "emoji_40",
            "emoji_43",
            "emoji_37",
            "emoji_36",
            "emoji_15",
            "emoji_10",
    };

    public static final int[] MENU_ITEM_IDS = {R.id.action_chat_reply, R.id.action_chat_copy, R.id.action_chat_delete, R.id.action_chat_recall};
    public static final int[] MENU_TITLES = {R.string.ease_action_reply, R.string.ease_action_copy, R.string.ease_action_delete, R.string.ease_action_unsent};
    public static final int[] MENU_ICONS = {R.drawable.ease_chat_item_menu_reply, R.drawable.ease_chat_item_menu_copy, R.drawable.ease_chat_item_menu_delete,
            R.drawable.ease_chat_item_menu_unsent};


    public static final String EMOTICON_MORE_IDENTITY_CODE = "emoji_more";

    private static final EaseEmojicon REACTION_MORE = createMoreEmoticon();

    private static EaseEmojicon createMoreEmoticon() {
        EaseEmojicon data = new EaseEmojicon();
        data.setIdentityCode(EMOTICON_MORE_IDENTITY_CODE);
        data.setIcon(R.drawable.ee_reaction_more);
        return data;
    }

    public static EaseEmojicon getReactionMore() {
        return REACTION_MORE;
    }

    private static final LinkedHashMap<String, EaseEmojicon> REACTION_DATA_MAP = createReactionDataMap();

    private static LinkedHashMap<String, EaseEmojicon> createReactionDataMap() {
        LinkedHashMap<String, EaseEmojicon> emojiconsMap = new LinkedHashMap<>(REACTION_ICONS.length);
        EaseEmojicon emojicon;
        String id;
        for (int i = 0; i < REACTION_ICONS.length; i++) {
            emojicon = new EaseEmojicon(REACTION_ICONS[i], "", EaseEmojicon.Type.NORMAL);
            id = "emoji_" + (i + 1);
            emojicon.setIdentityCode(id);
            emojiconsMap.put(id, emojicon);
        }
        return emojiconsMap;
    }

    public static LinkedHashMap<String, EaseEmojicon> getReactionDataMap() {
        return REACTION_DATA_MAP;
    }


}
