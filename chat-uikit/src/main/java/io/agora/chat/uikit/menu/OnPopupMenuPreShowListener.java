package io.agora.chat.uikit.menu;


public interface OnPopupMenuPreShowListener {
    /**
     * Monitoring before popupMenu display, you can set PopupMenu
     * @param menuHelper {@link EasePopupMenuHelper}
     * @param position item position
     */
    void onMenuPreShow(EasePopupMenuHelper menuHelper, int position);
}

