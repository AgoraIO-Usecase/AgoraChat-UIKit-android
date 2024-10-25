package io.agora.uikit.common.extensions

import androidx.recyclerview.widget.ConcatAdapter
import io.agora.uikit.feature.chat.adapter.EaseMessagesAdapter

/**
 * Get the total item count before the target adapter.
 */
internal fun ConcatAdapter.getItemCountBeforeTarget(adapter: EaseMessagesAdapter): Int {
    var count = 0
    for (i in 0 until adapters.size) {
        val currentAdapter = adapters[i]
        if (currentAdapter == adapter) {
            break
        }
        count += currentAdapter.itemCount
    }
    return count
}