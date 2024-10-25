package io.agora.uikit.configs

import io.agora.uikit.EaseIM
import io.agora.uikit.R

class EaseSystemMsgConfig {

    var useDefaultContactSystemMsg: Boolean? = null
        get() {
            if (field != null) return field
            if (EaseIM.isInited()) {
                return EaseIM.getContext()?.resources?.getBoolean(R.bool.ease_use_default_contact_system_msg) ?: false
            }
            return false
        }

}