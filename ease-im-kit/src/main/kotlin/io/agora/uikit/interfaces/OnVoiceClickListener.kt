package io.agora.uikit.interfaces

interface OnVoiceRecorderClickListener {
    /**
     * Click the send button.
     */
    fun onClick(filePath: String?, length: Int)
}