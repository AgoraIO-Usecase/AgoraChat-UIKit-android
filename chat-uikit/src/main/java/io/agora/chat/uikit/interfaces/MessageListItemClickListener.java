package io.agora.chat.uikit.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;


/**
 * Item click listener for chat list
 */
public interface MessageListItemClickListener {
	/**
	 * there is default handling when bubble is clicked, if you want handle it, return true
	 * another way is you implement in onBubbleClick() of chat row
	 * @param message
	 * @return
	 */
	boolean onBubbleClick(ChatMessage message);

	/**
	 * click resend view
	 * @param message
	 * @return
	 */
	boolean onResendClick(ChatMessage message);

	/**
	 * on long click for bubble
	 * @param v
	 * @param message
	 */
	boolean onBubbleLongClick(View v, ChatMessage message);

	/**
	 * click the user avatar
	 * @param username
	 */
	void onUserAvatarClick(String username);

	/**
	 * long click for user avatar
	 * @param username
	 */
	void onUserAvatarLongClick(String username);

	/**
	 * Click thread region
	 * @param messageId
	 * @param threadId
	 */
	default boolean onThreadClick(String messageId, String threadId) {
		return false;
	}

	/**
	 * Long click thread region
	 * @param messageId
	 * @param threadId
	 */
	default boolean onThreadLongClick(View v, String messageId, String threadId) {
		return false;
	}

	/**
	 * message is create status
	 * @param message
	 */
	default void onMessageCreate(ChatMessage message) {}

	/**
	 * message send success
	 * @param message
	 */
	default void onMessageSuccess(ChatMessage message) {}

	/**
	 * message send fail
	 * @param message
	 * @param code
	 * @param error
	 */
	default void onMessageError(ChatMessage message, int code, String error) {}

	/**
	 * message in sending progress
	 * @param message
	 * @param progress
	 */
	default void onMessageInProgress(ChatMessage message, int progress) {}

	/**
	 * remove reaction
	 *
	 * @param message
	 * @param reactionEntity
	 */
	default void onRemoveReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
	}

	/**
	 * add reaction
	 *
	 * @param message
	 * @param reactionEntity
	 */
	default void onAddReaction(ChatMessage message, EaseReactionEmojiconEntity reactionEntity) {
	}
}