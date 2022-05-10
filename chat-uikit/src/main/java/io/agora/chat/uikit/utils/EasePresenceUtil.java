package io.agora.chat.uikit.utils;


import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import java.util.Map;

import io.agora.chat.Presence;
import io.agora.chat.uikit.widget.PresenceData;

public class EasePresenceUtil {

    public static String getPresenceString(Context context, Presence presence) {
        if (presence != null) {
            boolean isOnline = false;
            Map<String, Integer> statusList = presence.getStatusList();
            for (Map.Entry<String, Integer> entry : statusList.entrySet()) {
                Integer value = entry.getValue();
                if (value == 1) {
                    isOnline = true;
                    break;
                }
            }
            if (isOnline) {
                String ext = presence.getExt();
                if (TextUtils.isEmpty(ext)||TextUtils.equals(ext, context.getString(PresenceData.ONLINE.getPresence()))) {
                    return context.getString(PresenceData.ONLINE.getPresence());
                } else if (TextUtils.equals(ext, context.getString(PresenceData.BUSY.getPresence()))) {
                    return context.getString(PresenceData.BUSY.getPresence());
                } else if (TextUtils.equals(ext, context.getString(PresenceData.DO_NOT_DISTURB.getPresence()))) {
                    return context.getString(PresenceData.DO_NOT_DISTURB.getPresence());
                } else if (TextUtils.equals(ext, context.getString(PresenceData.LEAVE.getPresence()))) {
                    return context.getString(PresenceData.LEAVE.getPresence());
                } else {
                    return ext;
                }
            }
        }
        return context.getString(PresenceData.OFFLINE.getPresence());
    }

    @DrawableRes
    public static int getPresenceIcon(Context context, Presence presence) {
        if (presence != null) {
            boolean isOnline = false;
            Map<String, Integer> statusList = presence.getStatusList();
            for (Map.Entry<String, Integer> entry : statusList.entrySet()) {
                Integer value = entry.getValue();
                if (value == 1) {
                    isOnline = true;
                    break;
                }
            }
            if (isOnline) {
                String ext = presence.getExt();
                if (TextUtils.isEmpty(ext)||TextUtils.equals(ext, context.getString(PresenceData.ONLINE.getPresence()))) {
                    return PresenceData.ONLINE.getPresenceIcon();
                } else if (TextUtils.equals(ext, context.getString(PresenceData.BUSY.getPresence()))) {
                    return PresenceData.BUSY.getPresenceIcon();
                } else if (TextUtils.equals(ext, context.getString(PresenceData.DO_NOT_DISTURB.getPresence()))) {
                    return PresenceData.DO_NOT_DISTURB.getPresenceIcon();
                } else if (TextUtils.equals(ext, context.getString(PresenceData.LEAVE.getPresence()))) {
                    return PresenceData.LEAVE.getPresenceIcon();
                } else {
                    return PresenceData.CUSTOM.getPresenceIcon();
                }
            }

        }
        return PresenceData.OFFLINE.getPresenceIcon();
    }
}
