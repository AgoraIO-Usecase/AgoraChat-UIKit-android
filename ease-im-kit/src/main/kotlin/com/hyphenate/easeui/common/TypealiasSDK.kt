package com.hyphenate.easeui.common


// It is a mapping file for the Chat SDK.
// manager
typealias ChatClient = com.hyphenate.chat.EMClient
typealias ChatManager = com.hyphenate.chat.EMChatManager
typealias ChatThreadManager = com.hyphenate.chat.EMChatThreadManager
typealias ChatGroupManager = com.hyphenate.chat.EMGroupManager
typealias ChatUserInfoManager = com.hyphenate.chat.EMUserInfoManager
typealias ChatPushManager = com.hyphenate.chat.EMPushManager
typealias ChatPresenceManager = com.hyphenate.chat.EMPresenceManager
typealias ChatOptions = com.hyphenate.chat.EMOptions
typealias ChatContactManager = com.hyphenate.chat.EMContactManager
typealias ChatroomManager = com.hyphenate.chat.EMChatRoomManager
typealias FileHelper = com.hyphenate.util.EMFileHelper
typealias ChatHttpClientManager = com.hyphenate.cloud.HttpClientManager
typealias ChatHttpClientManagerBuilder = com.hyphenate.cloud.HttpClientManager.Builder

typealias ChatHttpResponse = com.hyphenate.cloud.HttpResponse

// callback
typealias ChatCallback = com.hyphenate.EMCallBack
typealias ChatValueCallback<T> = com.hyphenate.EMValueCallBack<T>
typealias ChatCursorResult<T> = com.hyphenate.chat.EMCursorResult<T>
typealias ChatPageResult<T> = com.hyphenate.chat.EMPageResult<T>

typealias ChatException = com.hyphenate.exceptions.HyphenateException
typealias ChatError =  com.hyphenate.EMError
typealias ChatLog = com.hyphenate.util.EMLog

// Group
typealias ChatGroup = com.hyphenate.chat.EMGroup
typealias ChatGroupStyle = com.hyphenate.chat.EMGroupManager.EMGroupStyle
typealias ChatGroupInfo = com.hyphenate.chat.EMGroupInfo
typealias ChatGroupOptions = com.hyphenate.chat.EMGroupOptions
typealias ChatShareFile = com.hyphenate.chat.EMMucSharedFile
typealias ChatGroupReadAck = com.hyphenate.chat.EMGroupReadAck

// utils
typealias ChatImageUtils = com.hyphenate.util.ImageUtils
typealias ChatPathUtils = com.hyphenate.util.PathUtil
typealias ChatVersionUtils = com.hyphenate.util.VersionUtils
typealias ChatDensityUtils = com.hyphenate.util.DensityUtil
typealias ChatTimeInfo = com.hyphenate.util.TimeInfo
typealias ChatTextFormater = com.hyphenate.util.TextFormater

// java bean
typealias Chatroom =  com.hyphenate.chat.EMChatRoom
typealias ChatUserInfo = com.hyphenate.chat.EMUserInfo
typealias ChatLoginExtensionInfo = com.hyphenate.chat.EMLoginExtensionInfo
typealias ChatRecallMessageInfo = com.hyphenate.chat.EMRecallMessageInfo

// Chat
typealias ChatFetchMessageOption = com.hyphenate.chat.EMFetchMessageOption
typealias ChatMessageReaction = com.hyphenate.chat.EMMessageReaction
typealias ChatMessageReactionChange = com.hyphenate.chat.EMMessageReactionChange
typealias ChatMessageReactionOperation = com.hyphenate.chat.EMMessageReactionOperation

// ChatMessage
typealias ChatConversation = com.hyphenate.chat.EMConversation
typealias ChatConversationType = com.hyphenate.chat.EMConversation.EMConversationType
typealias ChatSearchDirection = com.hyphenate.chat.EMConversation.EMSearchDirection
typealias ChatSearchScope = com.hyphenate.chat.EMConversation.EMMessageSearchScope
typealias ChatMessage = com.hyphenate.chat.EMMessage
typealias ChatType = com.hyphenate.chat.EMMessage.ChatType
typealias ChatMessageType = com.hyphenate.chat.EMMessage.Type
typealias ChatTextMessageBody = com.hyphenate.chat.EMTextMessageBody
typealias ChatCustomMessageBody = com.hyphenate.chat.EMCustomMessageBody
typealias ChatCombineMessageBody = com.hyphenate.chat.EMCombineMessageBody
typealias ChatNormalFileMessageBody = com.hyphenate.chat.EMNormalFileMessageBody
typealias ChatFileMessageBody = com.hyphenate.chat.EMFileMessageBody
typealias ChatImageMessageBody = com.hyphenate.chat.EMImageMessageBody
typealias ChatLocationMessageBody = com.hyphenate.chat.EMLocationMessageBody
typealias ChatVideoMessageBody = com.hyphenate.chat.EMVideoMessageBody
typealias ChatVoiceMessageBody = com.hyphenate.chat.EMVoiceMessageBody
typealias ChatCmdMessageBody = com.hyphenate.chat.EMCmdMessageBody
typealias ChatMessageStatus = com.hyphenate.chat.EMMessage.Status
typealias ChatMessageDirection = com.hyphenate.chat.EMMessage.Direct
typealias ChatMessageBody = com.hyphenate.chat.EMMessageBody
typealias ChatDownloadStatus = com.hyphenate.chat.EMFileMessageBody.EMDownloadStatus

// presence
typealias ChatPresence = com.hyphenate.chat.EMPresence

// thread
typealias ChatThread = com.hyphenate.chat.EMChatThread
typealias ChatThreadEvent = com.hyphenate.chat.EMChatThreadEvent

// push
typealias ChatPushHelper = com.hyphenate.push.EMPushHelper
typealias ChatPushType = com.hyphenate.push.EMPushType
typealias ChatPushListener = com.hyphenate.push.PushListener
typealias PushConfig = com.hyphenate.push.EMPushConfig
typealias PushConfigBuilder = com.hyphenate.push.EMPushConfig.Builder
typealias ChatPushConfigs = com.hyphenate.chat.EMPushConfigs
typealias ChatSilentModeParam = com.hyphenate.chat.EMSilentModeParam
typealias ChatSilentModeResult = com.hyphenate.chat.EMSilentModeResult
typealias ChatSilentModeTime = com.hyphenate.chat.EMSilentModeTime
typealias ChatSilentModelType = com.hyphenate.chat.EMSilentModeParam.EMSilentModeParamType
typealias ChatPushRemindType = com.hyphenate.chat.EMPushManager.EMPushRemindType

// user info
typealias ChatUserInfoType = com.hyphenate.chat.EMUserInfo.EMUserInfoType

// translation
typealias ChatTranslationInfo = com.hyphenate.chat.EMTextMessageBody.EMTranslationInfo

// Listeners
typealias ChatConnectionListener = com.hyphenate.EMConnectionListener
typealias ChatMessageListener = com.hyphenate.EMMessageListener
typealias ChatRoomChangeListener = com.hyphenate.EMChatRoomChangeListener
typealias ChatGroupChangeListener = com.hyphenate.EMGroupChangeListener
typealias ChatMultiDeviceListener = com.hyphenate.EMMultiDeviceListener
typealias ChatContactListener = com.hyphenate.EMContactListener
typealias ChatConversationListener = com.hyphenate.EMConversationListener
typealias ChatPresenceListener = com.hyphenate.EMPresenceListener
typealias ChatThreadChangeListener = com.hyphenate.EMChatThreadChangeListener
