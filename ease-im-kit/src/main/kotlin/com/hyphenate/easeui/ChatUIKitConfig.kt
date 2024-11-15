package com.hyphenate.easeui

import com.hyphenate.easeui.configs.ChatUIKitAvatarConfig
import com.hyphenate.easeui.configs.ChatUIKitDateFormatConfig
import com.hyphenate.easeui.configs.ChatUIKitConfig
import com.hyphenate.easeui.configs.ChatUIKitMultiDeviceEventConfig
import com.hyphenate.easeui.configs.ChatUIKitSystemMsgConfig

class ChatUIKitConfig(
    var avatarConfig: ChatUIKitAvatarConfig? = ChatUIKitAvatarConfig(),
    var chatConfig: ChatUIKitConfig? = ChatUIKitConfig(),
    var dateFormatConfig: ChatUIKitDateFormatConfig? = ChatUIKitDateFormatConfig(),
    var systemMsgConfig: ChatUIKitSystemMsgConfig? = ChatUIKitSystemMsgConfig(),
    var multiDeviceConfig: ChatUIKitMultiDeviceEventConfig? = ChatUIKitMultiDeviceEventConfig(),
)