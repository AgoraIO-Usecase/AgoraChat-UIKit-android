package io.agora.uikit.common.extensions

import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatThread


/**
 * Check if the current user is the owner of the thread.
 */
fun ChatThread.isOwner() = owner == ChatClient.getInstance().currentUser