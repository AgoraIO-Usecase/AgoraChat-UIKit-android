package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatThread


/**
 * Check if the current user is the owner of the thread.
 */
fun ChatThread.isOwner() = owner == ChatClient.getInstance().currentUser