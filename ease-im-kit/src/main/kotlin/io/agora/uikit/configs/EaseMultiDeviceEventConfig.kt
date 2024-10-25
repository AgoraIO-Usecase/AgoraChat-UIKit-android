package io.agora.uikit.configs

import io.agora.uikit.EaseIM
import io.agora.uikit.R

class EaseMultiDeviceEventConfig {
    var useDefaultMultiDeviceContactEvent: Boolean? = null
        get() {
            if (field != null) return field
            if (EaseIM.isInited()) {
                return EaseIM.getContext()?.resources?.getBoolean(R.bool.ease_default_multi_device_contact_event) ?: false
            }
            return false
        }

    var useDefaultMultiDeviceGroupEvent: Boolean? = null
        get() {
            if (field != null) return field
            if (EaseIM.isInited()) {
                return EaseIM.getContext()?.resources?.getBoolean(R.bool.ease_default_multi_device_group_event) ?: false
            }
            return false
        }
}