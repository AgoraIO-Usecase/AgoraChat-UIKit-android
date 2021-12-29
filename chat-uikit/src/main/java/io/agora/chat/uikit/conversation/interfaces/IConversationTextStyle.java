package io.agora.chat.uikit.conversation.interfaces;

import androidx.annotation.ColorInt;

interface IConversationTextStyle {
    /**
     * Set title's text size
     * @param textSize px
     */
    void setTitleTextSize(int textSize);

    /**
     * Set title's text color
     * @param textColor
     */
    void setTitleTextColor(@ColorInt int textColor);

    /**
     * Set content's text size
     * @param textSize px
     */
    void setContentTextSize(int textSize);

    /**
     * Set content's text color
     * @param textColor
     */
    void setContentTextColor(@ColorInt int textColor);

    /**
     * Set date's text size
     * @param textSize
     */
    void setDateTextSize(int textSize);

    /**
     * Set date's text color
     * @param textColor
     */
    void setDateTextColor(@ColorInt int textColor);
}
