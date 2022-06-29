package io.agora.chat.uikit.chat.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.EaseChatPrimaryMenuListener;
import io.agora.chat.uikit.chat.interfaces.IChatPrimaryMenu;
import io.agora.chat.uikit.chat.model.EaseInputMenuStyle;
import io.agora.util.EMLog;


public class EaseChatPrimaryMenu extends RelativeLayout implements IChatPrimaryMenu, View.OnClickListener, EaseInputEditText.OnEditTextChangeListener, TextWatcher {
    private LinearLayout rlBottom;
    private ImageView buttonSetModeVoice;
    private ImageView buttonSetModeKeyboard;
    private FrameLayout buttonPressToSpeak;
    private FrameLayout edittext_layout;
    private EaseInputEditText editText;
    private RelativeLayout faceLayout;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private CheckBox buttonMore;
    private Button buttonSend;

    private EaseChatPrimaryMenuListener listener;
    private EaseInputMenuStyle menuType = EaseInputMenuStyle.All;
    protected InputMethodManager inputManager;
    protected Activity activity;
    private boolean isShowSendButton;

    public EaseChatPrimaryMenu(Context context) {
        this(context, null);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        activity = (Activity) context;
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
    }

    private void initViews() {
        rlBottom = findViewById(R.id.rl_bottom);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        edittext_layout = findViewById(R.id.edittext_layout);
        editText = findViewById(R.id.et_sendmessage);
        faceLayout = findViewById(R.id.rl_face);
        faceNormal = findViewById(R.id.iv_face_normal);
        faceChecked = findViewById(R.id.iv_face_checked);
        buttonMore = findViewById(R.id.btn_more);
        buttonSend = findViewById(R.id.btn_send);

        isShowSendButton = getResources().getBoolean(R.bool.ease_input_show_send_button);

        editText.requestFocus();

        setEditTextModel();

        showNormalStatus();

        initListener();
    }

    private void initListener() {
        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonSetModeVoice.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
        editText.setOnEditTextChangeListener(this);
        editText.addTextChangedListener(this);
        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });
    }

    private void checkSendButton() {
        Editable content = editText.getText();
        setSendButtonVisible(content);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        editText.removeTextChangedListener(this);
    }

    @Override
    public void setMenuShowType(EaseInputMenuStyle style) {
        this.menuType = style;
        checkMenuType();
    }

    @Override
    public void showNormalStatus() {
        hideSoftKeyboard();
        buttonSetModeVoice.setVisibility(VISIBLE);
        buttonSetModeKeyboard.setVisibility(GONE);
        edittext_layout.setVisibility(VISIBLE);
        buttonPressToSpeak.setVisibility(GONE);
        hideExtendStatus();
        checkSendButton();
        checkMenuType();
    }

    @Override
    public void showTextStatus() {
        buttonSetModeVoice.setVisibility(VISIBLE);
        buttonSetModeKeyboard.setVisibility(GONE);
        edittext_layout.setVisibility(VISIBLE);
        buttonPressToSpeak.setVisibility(GONE);
        hideExtendStatus();
        showSoftKeyboard(editText);
        checkSendButton();
        checkMenuType();
        if(listener != null) {
            listener.onToggleTextBtnClicked();
        }
    }

    @Override
    public void showVoiceStatus() {
        hideSoftKeyboard();
        buttonSetModeVoice.setVisibility(GONE);
        buttonSetModeKeyboard.setVisibility(VISIBLE);
        edittext_layout.setVisibility(GONE);
        buttonPressToSpeak.setVisibility(VISIBLE);
        hideExtendStatus();
        checkMenuType();
        if(listener != null) {
            listener.onToggleVoiceBtnClicked();
        }
    }

    @Override
    public void showEmojiconStatus() {
        buttonSetModeVoice.setVisibility(VISIBLE);
        buttonSetModeKeyboard.setVisibility(GONE);
        edittext_layout.setVisibility(VISIBLE);
        buttonPressToSpeak.setVisibility(GONE);
        buttonMore.setChecked(false);
        if(faceNormal.getVisibility() == VISIBLE) {
            hideSoftKeyboard();
            showSelectedFaceImage();
        }else {
            showSoftKeyboard(editText);
            showNormalFaceImage();
        }
        checkMenuType();
        if(listener != null) {
            listener.onToggleEmojiconClicked(faceChecked.getVisibility() == VISIBLE);
        }
    }

    @Override
    public void showMoreStatus() {
        if(buttonMore.isChecked()) {
            hideSoftKeyboard();
            buttonSetModeVoice.setVisibility(VISIBLE);
            buttonSetModeKeyboard.setVisibility(GONE);
            edittext_layout.setVisibility(VISIBLE);
            buttonPressToSpeak.setVisibility(GONE);
            showNormalFaceImage();
        }else {
            showTextStatus();
        }
        checkMenuType();
        if(listener != null) {
            listener.onToggleExtendClicked(buttonMore.isChecked());
        }
    }

    @Override
    public void hideExtendStatus() {
        buttonMore.setChecked(false);
        showNormalFaceImage();
    }

    @Override
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        editText.append(emojiContent);
    }

    @Override
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        showTextStatus();
    }

    @Override
    public EditText getEditText() {
        return editText;
    }

    @Override
    public void setMenuBackground(Drawable bg) {
        rlBottom.setBackground(bg);
    }

    @Override
    public void setSendButtonBackground(Drawable bg) {
        buttonSend.setBackground(bg);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_send) {
            if(listener != null) {
                String s = editText.getText().toString();
                editText.setText("");
                listener.onSendBtnClicked(s);
            }
        }else if(id == R.id.btn_set_mode_voice) {
            showVoiceStatus();
        }else if (id == R.id.btn_set_mode_keyboard) {
            showTextStatus();
        }else if (id == R.id.btn_more) {
            showMoreStatus();
        }else if (id == R.id.et_sendmessage) {
            showTextStatus();
        }else if (id == R.id.rl_face) {
            showEmojiconStatus();
        }
    }

    @Override
    public void onClickKeyboardSendBtn(String content) {
        if(listener != null) {
            listener.onSendBtnClicked(content);
        }
    }

    @Override
    public void onEditTextHasFocus(boolean hasFocus) {
        if(listener != null) {
            listener.onEditTextHasFocus(hasFocus);
        }
    }

    private void checkMenuType() {
        if(menuType == EaseInputMenuStyle.DISABLE_VOICE) {
            buttonSetModeVoice.setVisibility(GONE);
            buttonSetModeKeyboard.setVisibility(GONE);
            buttonPressToSpeak.setVisibility(GONE);
        }else if(menuType == EaseInputMenuStyle.DISABLE_EMOJICON) {
            faceLayout.setVisibility(GONE);
        }else if(menuType == EaseInputMenuStyle.DISABLE_VOICE_EMOJICON) {
            buttonSetModeVoice.setVisibility(GONE);
            buttonSetModeKeyboard.setVisibility(GONE);
            buttonPressToSpeak.setVisibility(GONE);
            faceLayout.setVisibility(GONE);
        }else if(menuType == EaseInputMenuStyle.ONLY_TEXT) {
            buttonSetModeVoice.setVisibility(GONE);
            buttonSetModeKeyboard.setVisibility(GONE);
            buttonPressToSpeak.setVisibility(GONE);
            faceLayout.setVisibility(GONE);
            buttonMore.setVisibility(GONE);
        }
    }

    private void showSendButton(CharSequence s) {
        setSendButtonVisible(s);
        checkMenuType();
    }

    private void setSendButtonVisible(CharSequence content) {
        if(isShowSendButton) {
            if(TextUtils.isEmpty(content)) {
                buttonMore.setVisibility(View.VISIBLE);
                buttonSend.setVisibility(View.GONE);
            }else {
                buttonMore.setVisibility(View.GONE);
                buttonSend.setVisibility(View.VISIBLE);
            }
        }else {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        }
    }

    private void setEditTextModel() {
        if(!isShowSendButton) {
            editText.setHorizontallyScrolling(false);
            // set max lines
            int maxLines = getResources().getInteger(R.integer.ease_input_edit_text_max_lines);
            if(maxLines <= 0) {
                maxLines = 4;
            }
            editText.setMaxLines(maxLines);
        }
    }

    private void showNormalFaceImage(){
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    private void showSelectedFaceImage(){
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }

    /**
     * hide soft keyboard
     */
    @Override
    public void hideSoftKeyboard() {
        if(editText == null) {
            return;
        }
        editText.requestFocus();
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * show soft keyboard
     * @param et
     */
    private void showSoftKeyboard(EditText et) {
        if(et == null) {
            return;
        }
        et.requestFocus();
        inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void setEaseChatPrimaryMenuListener(EaseChatPrimaryMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EMLog.i("TAG", this.getClass().getSimpleName() + " onTextChanged s:"+s);
        showSendButton(s);
        if(listener != null) {
            listener.onTyping(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        EMLog.i("TAG", this.getClass().getSimpleName() + " afterTextChanged s:"+s);
    }
}

