package io.agora.uikit.feature.chat.widgets

/**
 * Input menu includes: voice input, emoji input, text input and extended function input
 */
enum class EaseInputMenuStyle {
    /**
     * Includes all functions
     */
    All,

    /**
     * Includes all functions except voice input
     */
    DISABLE_VOICE,

    /**
     * Includes all functions except emoji input
     */
    DISABLE_EMOJICON,

    /**
     * Includes all functions except voice input and emoji input
     */
    DISABLE_VOICE_EMOJICON,

    /**
     * Includes text input only
     */
    ONLY_TEXT
}