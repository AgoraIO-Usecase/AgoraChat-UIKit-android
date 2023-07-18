package io.agora.chat.uikit.chat.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.ChatInputMenuListener;
import io.agora.chat.uikit.chat.interfaces.EaseChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.EaseChatPrimaryMenuListener;
import io.agora.chat.uikit.chat.interfaces.EaseEmojiconMenuListener;
import io.agora.chat.uikit.chat.interfaces.IChatEmojiconMenu;
import io.agora.chat.uikit.chat.interfaces.IChatExtendMenu;
import io.agora.chat.uikit.chat.interfaces.IChatInputMenu;
import io.agora.chat.uikit.chat.interfaces.IChatPrimaryMenu;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.util.EMLog;


public class EaseChatInputMenu extends LinearLayout implements IChatInputMenu, EaseChatPrimaryMenuListener, EaseEmojiconMenuListener, EaseChatExtendMenuItemClickListener {
    private static final String TAG = EaseChatInputMenu.class.getSimpleName();
    private LinearLayout chatMenuContainer;
    private FrameLayout primaryMenuContainer;
    private FrameLayout extendMenuContainer;
    private FrameLayout topExtendMenuContainer;

    private IChatPrimaryMenu primaryMenu;
    private IChatEmojiconMenu emojiconMenu;
    private IChatExtendMenu extendMenu;
    private IChatTopExtendMenu topExtendMenu;

    private ChatInputMenuListener menuListener;

    public EaseChatInputMenu(Context context) {
        this(context, null);
    }

    public EaseChatInputMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatInputMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_input_menu_container, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        chatMenuContainer = findViewById(R.id.chat_menu_container);
        primaryMenuContainer = findViewById(R.id.primary_menu_container);
        extendMenuContainer = findViewById(R.id.extend_menu_container);
        topExtendMenuContainer = findViewById(R.id.top_extend_menu_container);

        init();
    }

    private void init() {
        showPrimaryMenu();
        if(extendMenu == null) {
            extendMenu = new EaseChatExtendMenu(getContext());
            ((EaseChatExtendMenu)extendMenu).init();
        }
        if(emojiconMenu == null) {
            emojiconMenu = new EaseEmojiconMenu(getContext());
            ((EaseEmojiconMenu)emojiconMenu).init();
        }
    }

    @Override
    public void setCustomPrimaryMenu(IChatPrimaryMenu menu) {
        this.primaryMenu = menu;
        showPrimaryMenu();
    }

    @Override
    public void setCustomEmojiconMenu(IChatEmojiconMenu menu) {
        this.emojiconMenu = menu;
    }

    @Override
    public void setCustomExtendMenu(IChatExtendMenu menu) {
        this.extendMenu = menu;
    }

    @Override
    public void setCustomTopExtendMenu(IChatTopExtendMenu menu) {
        this.topExtendMenu = menu;
    }

    @Override
    public void hideExtendContainer() {
        primaryMenu.showNormalStatus();
        extendMenuContainer.setVisibility(GONE);
    }

    @Override
    public void showEmojiconMenu(boolean show) {
        if(show) {
            showEmojiconMenu();
        }else {
           extendMenuContainer.setVisibility(GONE);
        }
    }

    @Override
    public void showExtendMenu(boolean show) {
        if(show) {
            showExtendMenu();
        }else {
            extendMenuContainer.setVisibility(GONE);
            if(primaryMenu != null) {
                primaryMenu.hideExtendStatus();
            }
        }
    }

    @Override
    public void showTopExtendMenu(boolean isShow) {
        if(isShow) {
            showTopExtendMenu();
        }else {
            topExtendMenuContainer.setVisibility(GONE);
        }
    }

    @Override
    public void hideSoftKeyboard() {
        if(primaryMenu != null) {
            primaryMenu.hideSoftKeyboard();
        }
    }

    @Override
    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.menuListener = listener;
    }

    @Override
    public IChatPrimaryMenu getPrimaryMenu() {
        return primaryMenu;
    }

    @Override
    public IChatEmojiconMenu getEmojiconMenu() {
        return emojiconMenu;
    }

    @Override
    public IChatExtendMenu getChatExtendMenu() {
        return extendMenu;
    }

    @Override
    public IChatTopExtendMenu getChatTopExtendMenu() {
        return topExtendMenu;
    }

    @Override
    public boolean onBackPressed() {
        if(extendMenuContainer.getVisibility() == VISIBLE) {
            extendMenuContainer.setVisibility(GONE);
            return false;
        }
        return true;
    }

    private void showPrimaryMenu() {
        if(primaryMenu == null) {
            primaryMenu = new EaseChatPrimaryMenu(getContext());
        }
        if(primaryMenu instanceof View) {
            primaryMenuContainer.removeAllViews();
            primaryMenuContainer.addView((View) primaryMenu);
            primaryMenu.setEaseChatPrimaryMenuListener(this);
        }
        if(primaryMenu instanceof Fragment && getContext() instanceof AppCompatActivity) {
            FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.primary_menu_container, (Fragment) primaryMenu).commitAllowingStateLoss();
            primaryMenu.setEaseChatPrimaryMenuListener(this);
        }
    }

    private void showExtendMenu() {
        if(extendMenu == null) {
            extendMenu = new EaseChatExtendMenu(getContext());
            ((EaseChatExtendMenu)extendMenu).init();
        }
        if(extendMenu instanceof View) {
            extendMenuContainer.setVisibility(VISIBLE);
            extendMenuContainer.removeAllViews();
            extendMenuContainer.addView((View) extendMenu);
            extendMenu.setEaseChatExtendMenuItemClickListener(this);
        }
        if(extendMenu instanceof Dialog) {
            extendMenuContainer.setVisibility(GONE);
            ((Dialog) extendMenu).show();
            ((Dialog) extendMenu).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(primaryMenu != null) {
                        primaryMenu.hideExtendStatus();
                    }
                }
            });
            ((Dialog) extendMenu).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(primaryMenu != null) {
                        primaryMenu.hideExtendStatus();
                    }
                }
            });
            extendMenu.setEaseChatExtendMenuItemClickListener(this);
        }
        if(extendMenu instanceof Fragment && getContext() instanceof AppCompatActivity) {
            extendMenuContainer.setVisibility(VISIBLE);
            FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.extend_menu_container, (Fragment) extendMenu).commitAllowingStateLoss();
            extendMenu.setEaseChatExtendMenuItemClickListener(this);
        }
    }

    private void showEmojiconMenu() {
        if(emojiconMenu == null) {
            emojiconMenu = new EaseEmojiconMenu(getContext());
            ((EaseEmojiconMenu)emojiconMenu).init();
        }
        if(emojiconMenu instanceof View) {
            extendMenuContainer.setVisibility(VISIBLE);
            extendMenuContainer.removeAllViews();
            extendMenuContainer.addView((View) emojiconMenu);
            emojiconMenu.setEmojiconMenuListener(this);
        }
        if(emojiconMenu instanceof Fragment && getContext() instanceof AppCompatActivity) {
            extendMenuContainer.setVisibility(VISIBLE);
            FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.extend_menu_container, (Fragment) emojiconMenu).commitAllowingStateLoss();
            emojiconMenu.setEmojiconMenuListener(this);
        }
    }

    private void showTopExtendMenu() {
        if(topExtendMenu instanceof View) {
            topExtendMenuContainer.setVisibility(VISIBLE);
            topExtendMenuContainer.removeAllViews();
            topExtendMenuContainer.addView((View) topExtendMenu);
        }
        if(topExtendMenu instanceof Fragment && getContext() instanceof AppCompatActivity) {
            topExtendMenuContainer.setVisibility(VISIBLE);
            FragmentManager manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.top_extend_menu_container, (Fragment) topExtendMenu).commitAllowingStateLoss();
        }
    }

    @Override
    public void onSendBtnClicked(String content) {
        EMLog.i(TAG, "onSendBtnClicked content:"+content);
        if(menuListener != null) {
            menuListener.onSendMessage(content);
        }
    }

    @Override
    public void onTyping(CharSequence s, int start, int before, int count) {
        EMLog.i(TAG, "onTyping: s = "+s);
        if(menuListener != null) {
            menuListener.onTyping(s, start, before, count);
        }
    }

    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        if(menuListener != null) {
            return menuListener.onPressToSpeakBtnTouch(v, event);
        }
        return false;
    }

    @Override
    public void onToggleVoiceBtnClicked() {
        EMLog.i("TAG", "onToggleVoiceBtnClicked");
        showExtendMenu(false);
    }

    @Override
    public void onToggleTextBtnClicked() {
        EMLog.i(TAG, "onToggleTextBtnClicked");
        showExtendMenu(false);
    }

    @Override
    public void onToggleExtendClicked(boolean extend) {
        EMLog.i(TAG, "onToggleExtendClicked extend:"+extend);
        showExtendMenu(extend);
    }

    @Override
    public void onToggleEmojiconClicked(boolean extend) {
        EMLog.i(TAG, "onToggleEmojiconClicked extend:"+extend);
        showEmojiconMenu(extend);
    }

    @Override
    public void onEditTextClicked() {
        EMLog.i(TAG, "onEditTextClicked");
    }

    @Override
    public void onEditTextHasFocus(boolean hasFocus) {
        EMLog.i(TAG, "onEditTextHasFocus: hasFocus = "+hasFocus);
    }

    @Override
    public void onExpressionClicked(Object emojicon) {
        EMLog.i(TAG, "onExpressionClicked");
        if(emojicon instanceof EaseEmojicon) {
            EaseEmojicon easeEmojicon = (EaseEmojicon) emojicon;
            if(easeEmojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION){
                if(easeEmojicon.getEmojiText() != null){
                    primaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(getContext(),easeEmojicon.getEmojiText()));
                }
            }else{
                if(menuListener != null){
                    menuListener.onExpressionClicked(emojicon);
                }
            }
        }else {
            if(menuListener != null){
                menuListener.onExpressionClicked(emojicon);
            }
        }
    }

    @Override
    public void onDeleteImageClicked() {
        EMLog.i(TAG, "onDeleteImageClicked");
        primaryMenu.onEmojiconDeleteEvent();
    }

    @Override
    public void onSendIconClicked() {
        EMLog.i(TAG, "onSendIconClicked");
        if(getPrimaryMenu() != null && getPrimaryMenu().getEditText() != null) {
            String content = getPrimaryMenu().getEditText().getText().toString().trim();
            if(TextUtils.isEmpty(content)) {
                return;
            }
            if(menuListener != null) {
                getPrimaryMenu().getEditText().setText("");
                menuListener.onSendMessage(content);
            }
        }
    }

    @Override
    public void onChatExtendMenuItemClick(int itemId, View view) {
        EMLog.i(TAG, "onChatExtendMenuItemClick itemId = "+itemId);
        if(menuListener != null) {
            menuListener.onChatExtendMenuItemClick(itemId, view);
        }
    }
}

