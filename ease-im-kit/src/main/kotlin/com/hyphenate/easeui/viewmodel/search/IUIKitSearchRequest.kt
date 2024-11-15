package com.hyphenate.easeui.viewmodel.search

import com.hyphenate.easeui.common.ChatSearchDirection
import com.hyphenate.easeui.common.ChatSearchScope
import com.hyphenate.easeui.viewmodel.IAttachView

interface IUIKitSearchRequest: IAttachView {

    fun searchUser(query:String)

    fun searchBlockUser(query: String)

    fun searchConversation(query:String)

    fun searchMessage(
        keywords:String,
        conversationId:String = "",
        timeStamp:Long = -1,
        from:String? = "",
        direction:ChatSearchDirection = ChatSearchDirection.UP,
        chatScope:ChatSearchScope
    )
}