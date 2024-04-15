package com.hyphenate.easeui.interfaces

interface OnVoiceRecorderClickListener {
    /**
     * Click the send button.
     */
    fun onClick(filePath: String?, length: Int)
}