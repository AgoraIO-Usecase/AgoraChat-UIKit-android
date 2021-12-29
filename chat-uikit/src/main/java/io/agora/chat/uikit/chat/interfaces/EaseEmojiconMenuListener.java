package io.agora.chat.uikit.chat.interfaces;


public interface EaseEmojiconMenuListener{
        /**
         * on emojicon clicked
         * @param emojicon
         */
        void onExpressionClicked(Object emojicon);
        /**
         * on delete image clicked
         */
        default void onDeleteImageClicked() {}

        /**
         * On send icon clicked
         */
        default void onSendIconClicked() {}
}