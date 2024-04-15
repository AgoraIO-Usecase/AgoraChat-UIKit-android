package com.hyphenate.easeui.common.utils

import android.content.Context
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.hyphenate.easeui.common.ChatPresence
import com.hyphenate.easeui.model.EasePresenceData

object EasePresenceUtil {

    fun getPresenceString(context: Context, presence: ChatPresence?): String {
        if (presence != null) {
            var isOnline = false
            val statusList: Map<String?, Int> = presence.statusList
            for (entry in statusList) {
                if (entry.value == 1){
                    isOnline = true
                    break
                }
            }

            if (isOnline) {
                val ext: String = presence.ext
                return if (TextUtils.isEmpty(ext) || TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.ONLINE.presence)
                    )
                ) {
                    context.getString(EasePresenceData.ONLINE.presence)
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.BUSY.presence)
                    )
                ) {
                    context.getString(EasePresenceData.BUSY.presence)
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.DO_NOT_DISTURB.presence)
                    )
                ) {
                    context.getString(EasePresenceData.DO_NOT_DISTURB.presence)
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.LEAVE.presence)
                    )
                ) {
                    context.getString(EasePresenceData.LEAVE.presence)
                } else {
                    ext
                }
            }
        }
        return context.getString(EasePresenceData.OFFLINE.presence)
    }

    @DrawableRes
    fun getPresenceIcon(context: Context, presence: ChatPresence?): Int {
        if (presence != null){
            var isOnline = false
            val statusList: Map<String?, Int> = presence.statusList
            for (entry in statusList) {
                if (entry.value == 1){
                    isOnline = true
                    break
                }
            }
            if (isOnline) {
                val ext: String = presence.ext
                return if (TextUtils.isEmpty(ext) || TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.ONLINE.presence)
                    )
                ) {
                    EasePresenceData.ONLINE.presenceIcon
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.BUSY.presence)
                    )
                ) {
                    EasePresenceData.BUSY.presenceIcon
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.DO_NOT_DISTURB.presence)
                    )
                ) {
                    EasePresenceData.DO_NOT_DISTURB.presenceIcon
                } else if (TextUtils.equals(
                        ext,
                        context.getString(EasePresenceData.LEAVE.presence)
                    )
                ) {
                    EasePresenceData.LEAVE.presenceIcon
                } else {
                    EasePresenceData.CUSTOM.presenceIcon
                }
            }
        }
        return EasePresenceData.OFFLINE.presenceIcon
    }
}