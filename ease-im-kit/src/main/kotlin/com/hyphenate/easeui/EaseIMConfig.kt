package com.hyphenate.easeui

import com.hyphenate.easeui.configs.EaseAvatarConfig
import com.hyphenate.easeui.configs.EaseDateFormatConfig
import com.hyphenate.easeui.configs.EaseChatConfig
import com.hyphenate.easeui.configs.EaseMultiDeviceEventConfig
import com.hyphenate.easeui.configs.EasePresencesConfig
import com.hyphenate.easeui.configs.EaseSystemMsgConfig

class EaseIMConfig(
    var avatarConfig: EaseAvatarConfig? = EaseAvatarConfig(),
    var chatConfig: EaseChatConfig? = EaseChatConfig(),
    var dateFormatConfig: EaseDateFormatConfig? = EaseDateFormatConfig(),
    var systemMsgConfig: EaseSystemMsgConfig? = EaseSystemMsgConfig(),
    var multiDeviceConfig: EaseMultiDeviceEventConfig? = EaseMultiDeviceEventConfig(),
    var presencesConfig:EasePresencesConfig? = EasePresencesConfig(),
)