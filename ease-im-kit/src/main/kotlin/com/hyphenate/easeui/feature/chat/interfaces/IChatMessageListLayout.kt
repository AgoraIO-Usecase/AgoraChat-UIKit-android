package com.hyphenate.easeui.feature.chat.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.adapter.EaseMessagesAdapter
import com.hyphenate.easeui.feature.chat.reply.interfaces.OnMessageReplyViewClickListener
import com.hyphenate.easeui.common.interfaces.IRecyclerView
import com.hyphenate.easeui.feature.thread.interfaces.OnMessageChatThreadClickListener
import com.hyphenate.easeui.viewmodel.messages.IChatMessageListRequest
import com.hyphenate.easeui.widget.RefreshLayout

interface IChatMessageListLayout: IRecyclerView {

    val refreshLayout: RefreshLayout?

    val messageListLayout: RecyclerView?

    /**
     * Get conversation
     * @return
     */
    val currentConversation:ChatConversation?

    /**
     * Whether the list can scroll to the bottom automatically.
     * If true, it means that when list view's height changes or new message comes, the list will scroll to the bottom automatically.
     */
    var isCanAutoScrollToBottom: Boolean

    /**
     * Set custom message list viewModel.
     */
    fun setViewModel(viewModel: IChatMessageListRequest?)

    /**
     * Set custom adapter.
     */
    fun setMessagesAdapter(adapter: EaseMessagesAdapter?)

    /**
     * Get chat message adapter.
     */
    fun getMessagesAdapter(): EaseMessagesAdapter?

    /**
     * Set the touch monitor in the chat area to determine whether the click is
     * outside the item message or whether the list is being dragged
     * @param listener
     */
    fun setOnMessageListTouchListener(listener: OnMessageListTouchListener?)

    /**
     * Set message list item click listener.
     */
    fun setOnMessageListItemClickListener(listener: OnMessageListItemClickListener?)

    /**
     * Set message reply view click listener.
     */
    fun setOnMessageReplyViewClickListener(listener: OnMessageReplyViewClickListener?)

    /**
     * Set message thread view click listener.
     */
    fun setOnMessageThreadViewClickListener(listener:OnMessageChatThreadClickListener?)

    /**
     * Set message ack send callback.
     */
    fun setOnMessageAckSendCallback(callback: OnMessageAckSendCallback?)

    /**
     * Set error listener.
     */
    fun setOnChatErrorListener(listener: OnChatErrorListener?)

    /**
     * Whether to use the default refresh method.
     * @param useDefaultRefresh True means use default refresh method, false means use custom refresh method.
     */
    fun useDefaultRefresh(useDefaultRefresh: Boolean)

    /**
     * Get cache messages and refresh.
     */
    fun refreshMessages()

    /**
     * Refresh message list to the latest message.
     */
    fun refreshToLatest()

    /**
     * Refresh the target message.
     * @param messageId The message Id.
     */
    fun refreshMessage(messageId: String?)

    /**
     * Refresh the target message.
     * @param message The message object.
     */
    fun refreshMessage(message: ChatMessage?)

    /**
     * Remove the target message.
     * @param message The message object.
     */
    fun removeMessage(message: ChatMessage?)

    /**
     * Move to the target position.
     * @param position The target position.
     */
    fun moveToTarget(position: Int)

    /**
     * Move to the target position.
     * @param position The target position.
     */
    fun moveToTarget(message: ChatMessage?)

    /**
     * Highlight the target message.
     * @param position The target position.
     */
    fun highlightTarget(position: Int)

    /**
     * Whether to show default refresh animator.
     * @param refreshing True means show, false means hide.
     */
    fun setRefreshing(refreshing: Boolean)

    /**
     * Whether to scroll to the bottom when the RecyclerView's height changes
     * @param isNeedToScrollBottom
     */
    fun isNeedScrollToBottomWhenViewChange(isNeedToScrollBottom: Boolean)
}