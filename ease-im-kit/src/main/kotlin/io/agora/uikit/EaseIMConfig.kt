package io.agora.uikit

import io.agora.uikit.configs.EaseAvatarConfig
import io.agora.uikit.configs.EaseDateFormatConfig
import io.agora.uikit.configs.EaseChatConfig
import io.agora.uikit.configs.EaseMultiDeviceEventConfig
import io.agora.uikit.configs.EaseSystemMsgConfig

class EaseIMConfig(
    var avatarConfig: EaseAvatarConfig? = EaseAvatarConfig(),
    var chatConfig: EaseChatConfig? = EaseChatConfig(),
    var dateFormatConfig: EaseDateFormatConfig? = EaseDateFormatConfig(),
    var systemMsgConfig: EaseSystemMsgConfig? = EaseSystemMsgConfig(),
    var multiDeviceConfig: EaseMultiDeviceEventConfig? = EaseMultiDeviceEventConfig(),
)