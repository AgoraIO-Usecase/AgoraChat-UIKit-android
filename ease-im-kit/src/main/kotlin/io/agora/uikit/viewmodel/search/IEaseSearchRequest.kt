package io.agora.uikit.viewmodel.search

import io.agora.uikit.common.ChatSearchDirection
import io.agora.uikit.common.ChatSearchScope
import io.agora.uikit.viewmodel.IAttachView

interface IEaseSearchRequest: IAttachView {

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