package io.agora.chat.uikit.models;


import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseSmileUtils;

public class EaseDefaultEmojiconDatas {
    
    private static String[] emojis = new String[]{
        EaseSmileUtils.ee_1,
        EaseSmileUtils.ee_2,
        EaseSmileUtils.ee_3,
        EaseSmileUtils.ee_4,
        EaseSmileUtils.ee_5,
        EaseSmileUtils.ee_6,
        EaseSmileUtils.ee_7,
        EaseSmileUtils.ee_8,
        EaseSmileUtils.ee_9,
        EaseSmileUtils.ee_10,
        EaseSmileUtils.ee_11,
        EaseSmileUtils.ee_12,
        EaseSmileUtils.ee_13,
        EaseSmileUtils.ee_14,
        EaseSmileUtils.ee_15,
        EaseSmileUtils.ee_16,
        EaseSmileUtils.ee_17,
        EaseSmileUtils.ee_18,
        EaseSmileUtils.ee_19,
        EaseSmileUtils.ee_20,
        EaseSmileUtils.ee_21,
        EaseSmileUtils.ee_22,
        EaseSmileUtils.ee_23,
        EaseSmileUtils.ee_24,
        EaseSmileUtils.ee_25,
        EaseSmileUtils.ee_26,
        EaseSmileUtils.ee_27,
        EaseSmileUtils.ee_28,
        EaseSmileUtils.ee_29,
        EaseSmileUtils.ee_30,
        EaseSmileUtils.ee_31,
        EaseSmileUtils.ee_32,
        EaseSmileUtils.ee_33,
        EaseSmileUtils.ee_34,
        EaseSmileUtils.ee_35,
        EaseSmileUtils.ee_36,
        EaseSmileUtils.ee_37,
        EaseSmileUtils.ee_38,
        EaseSmileUtils.ee_39,
        EaseSmileUtils.ee_40, 
        EaseSmileUtils.ee_41,
        EaseSmileUtils.ee_42,
        EaseSmileUtils.ee_43,
        EaseSmileUtils.ee_44,
        EaseSmileUtils.ee_45,
        EaseSmileUtils.ee_46,
        EaseSmileUtils.ee_47,
        EaseSmileUtils.ee_48,
        EaseSmileUtils.ee_49,
       
    };
    
    private static int[] icons = new int[]{
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
    
    
    private static final EaseEmojicon[] DATA = createData();
    
    private static EaseEmojicon[] createData(){
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], emojis[i], EaseEmojicon.Type.NORMAL);
        }
        return datas;
    }
    
    public static EaseEmojicon[] getData(){
        return DATA;
    }
}
