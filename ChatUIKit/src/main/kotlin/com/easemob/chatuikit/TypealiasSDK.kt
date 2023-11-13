package com.easemob.chatuikit

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
typealias ChatGroupInfo = com.hyphenate.chat.EMGroupInfo
typealias ChatGroupOptions = com.hyphenate.chat.EMGroupOptions
typealias ChatShareFile = com.hyphenate.chat.EMMucSharedFile

// java bean
typealias Chatroom =  com.hyphenate.chat.EMChatRoom
typealias ChatUserInfo = com.hyphenate.chat.EMUserInfo

// Chat
typealias ChatFetchMessageOption = com.hyphenate.chat.EMFetchMessageOption
typealias ChatMessageReaction = com.hyphenate.chat.EMMessageReaction
typealias ChatMessageReactionChange = com.hyphenate.chat.EMMessageReactionChange
typealias ChatMessageReactionOperation = com.hyphenate.chat.EMMessageReactionOperation

// ChatMessage
typealias ChatConversation = com.hyphenate.chat.EMConversation
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

// presence
typealias ChatPresence = com.hyphenate.chat.EMPresence

// push
typealias ChatPushConfig = com.hyphenate.chat.EMPushConfigs
typealias ChatSilentModeParam = com.hyphenate.chat.EMSilentModeParam
typealias ChatSilentModeResult = com.hyphenate.chat.EMSilentModeResult
typealias ChatSilentModeTime = com.hyphenate.chat.EMSilentModeTime

// Listeners
typealias ChatConnectionListener = com.hyphenate.EMConnectionListener
typealias ChatMessageListener = com.hyphenate.EMMessageListener
typealias ChatRoomChangeListener = com.hyphenate.EMChatRoomChangeListener
typealias ChatGroupChangeListener = com.hyphenate.EMGroupChangeListener
typealias ChatMultiDeviceListener = com.hyphenate.EMMultiDeviceListener
typealias ChatContactListener = com.hyphenate.EMContactListener
typealias ChatConversationListener = com.hyphenate.EMConversationListener
typealias ChatPresenceListener = com.hyphenate.EMPresenceListener