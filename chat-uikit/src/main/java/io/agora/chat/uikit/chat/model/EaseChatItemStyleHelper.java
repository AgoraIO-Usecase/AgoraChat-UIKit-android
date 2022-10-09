package io.agora.chat.uikit.chat.model;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chat.uikit.chat.widget.EaseChatMessageListLayout;

public class EaseChatItemStyleHelper {
    private static EaseChatItemStyleHelper instance;
    private Map<Context, EaseChatSetStyle> mMap;

    private EaseChatItemStyleHelper(){
        mMap = new HashMap<>();
    }

    public static EaseChatItemStyleHelper getInstance() {
        if(instance == null) {
            synchronized (EaseChatItemStyleHelper.class) {
                if(instance == null) {
                    instance = new EaseChatItemStyleHelper();
                }
            }
        }
        return instance;
    }

    public void setCurrentContext(Context context) {
            EaseChatSetStyle style = new EaseChatSetStyle();
            style.setShowAvatar(true);
            style.setShowNickname(false);
            mMap.put(context, style);
    }

    public EaseChatSetStyle getStyle(Context context) {
        if(!mMap.containsKey(context)) {
            return null;
        }
        return mMap.get(context);
    }

    public void clear(Context context) {
        if(!mMap.containsKey(context)) {
            return;
        }
        mMap.remove(context);
        if(mMap.size() == 0) {
            instance = null;
        }
    }

    public void setAvatarSize(Context context, float avatarSize) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setAvatarSize(avatarSize);
            }
        }
    }

    public void setShapeType(Context context, int shapeType) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setShapeType(shapeType);
            }
        }
    }

    public void setAvatarRadius(Context context, float avatarRadius) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setAvatarRadius(avatarRadius);
            }
        }
    }

    public void setBorderWidth(Context context, float borderWidth) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setBorderWidth(borderWidth);
            }
        }
    }

    public void setBorderColor(Context context, int borderColor) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setBorderColor(borderColor);
            }
        }
    }

    public void setItemHeight(Context context, float itemHeight) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setItemHeight(itemHeight);
            }
        }
    }

    public void setBgDrawable(Context context, Drawable bgDrawable) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setBgDrawable(bgDrawable);
            }
        }
    }

    public void setTextSize(Context context, int textSize) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setTextSize(textSize);
            }
        }
    }

    public void setTextColor(Context context, int textColor) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setTextColor(textColor);
            }
        }
    }

    public void setItemMinHeight(Context context, int itemMinHeight) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setItemMinHeight(itemMinHeight);
            }
        }
    }

    public void setTimeTextSize(Context context, int timeTextSize) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setTimeTextSize(timeTextSize);
            }
        }
    }

    public void setTimeTextColor(Context context, int timeTextColor) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setTimeTextColor(timeTextColor);
            }
        }
    }

    public void setTimeBgDrawable(Context context, Drawable timeBgDrawable) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setTimeBgDrawable(timeBgDrawable);
            }
        }
    }

    public void setAvatarDefaultSrc(Context context, Drawable avatarDefaultSrc) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setAvatarDefaultSrc(avatarDefaultSrc);
            }
        }
    }

    public void setShowNickname(Context context, boolean showNickname) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setShowNickname(showNickname);
            }
        }
    }

    public void setShowAvatar(Context context, boolean showAvatar) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setShowAvatar(showAvatar);
            }
        }
    }

    public void setReceiverBgDrawable(Context context, Drawable receiverBgDrawable) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setReceiverBgDrawable(receiverBgDrawable);
            }
        }
    }

    public void setSenderBgDrawable(Context context, Drawable senderBgDrawable) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setSenderBgDrawable(senderBgDrawable);
            }
        }
    }

    public void setItemShowType(Context context, int itemShowType) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setItemShowType(itemShowType);
            }
        }
    }
    public void setHideReceiveAvatar(Context context, boolean hideReceiveAvatar) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setHideReceiveAvatar(hideReceiveAvatar);
            }
        }
    }

    public void setHideSendAvatar(Context context, boolean hideSendAvatar) {
        if(context != null && mMap.containsKey(context)) {
            EaseChatSetStyle style = mMap.get(context);
            if(style != null) {
                style.setHideSendAvatar(hideSendAvatar);
            }
        }
    }
    
    public static Drawable getSenderBgDrawable(Context context) {
        if(instance == null || instance.mMap == null ||
                !instance.mMap.containsKey(context) || instance.mMap.get(context) == null) {
            return null;
        }
        return instance.mMap.get(context).getSenderBgDrawable();
    }
    
    public static Drawable getReceiverBgDrawable(Context context) {
        if(instance == null || instance.mMap == null ||
                !instance.mMap.containsKey(context) || instance.mMap.get(context) == null) {
            return null;
        }
        return instance.mMap.get(context).getReceiverBgDrawable();
    }
    
    public static Drawable getTimeBgDrawable(Context context) {
        if(instance == null || instance.mMap == null ||
                !instance.mMap.containsKey(context) || instance.mMap.get(context) == null) {
            return null;
        }
        return instance.mMap.get(context).getTimeBgDrawable();
    }
    
    public static Drawable getAvatarDefaultSrc(Context context) {
        if(instance == null || instance.mMap == null ||
                !instance.mMap.containsKey(context) || instance.mMap.get(context) == null) {
            return null;
        }
        return instance.mMap.get(context).getAvatarDefaultSrc();
    }
}

