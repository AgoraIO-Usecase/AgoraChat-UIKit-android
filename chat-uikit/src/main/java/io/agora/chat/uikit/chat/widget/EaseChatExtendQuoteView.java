package io.agora.chat.uikit.chat.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.agora.chat.ChatMessage;
import io.agora.chat.TextMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.IChatQuote;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseChatExtendQuoteView extends FrameLayout implements IChatQuote, IChatTopExtendMenu {
    private Context context;
    private ImageView cancelSelect;
    private TextView quoteTitle;

    public EaseChatExtendQuoteView(@NonNull Context context) {
        this(context, null);
    }

    public EaseChatExtendQuoteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatExtendQuoteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initLayout();
        initListener();
    }

    private void initListener() {
        cancelSelect.setOnClickListener(v -> hideQuoteView());
    }

    private void initLayout() {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_quote, this);
        cancelSelect = findViewById(R.id.cancel_select);
        quoteTitle = findViewById(R.id.quote_title);
    }


    @Override
    public void startQuote(ChatMessage message) {
        showDefaultQuote(message);
        showTopExtendMenu(true);
    }

    @Override
    public void hideQuoteView() {
        this.setVisibility(GONE);
        quoteTitle.setText("");
    }

    private void showDefaultQuote(ChatMessage message){
        Spannable span = null;
        EaseUser user = EaseUserUtils.getUserInfo(message.getFrom());
        String from = "";
        if (user == null){
            from = message.getFrom();
        }else {
            if (TextUtils.isEmpty(user.getNickname())){
                from = user.getUsername();
            }else {
                from = user.getNickname();
            }
        }
        switch (message.getType()){
            case TXT:
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                    span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_emoji));
                }else {
                    TextMessageBody textBody = (TextMessageBody) message.getBody();
                    span = EaseSmileUtils.getSmiledText(context, textBody != null ? from + ": " + textBody.getMessage() : "");
                }
                break;
            case VOICE:
                VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
                String voiceContent = from + ": "+ getResources().getString(R.string.ease_voice) +
                        ((voiceBody != null && voiceBody.getLength() > 0)? voiceBody.getLength() : 0) + "\"";
                span = Spannable.Factory.getInstance().newSpannable(voiceContent);
                break;
            case VIDEO:
                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_video));
                break;
            case FILE:
                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_file));
                break;
            case IMAGE:
                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_picture));
                break;
            case LOCATION:
                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_location));
                break;
            case CUSTOM:
                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_custom));
                break;
//            case COMBINE:
//                span = Spannable.Factory.getInstance().newSpannable(from + ": " + getResources().getString(R.string.ease_combine));
//                break;
            default:
                break;
        }
        // 设置内容
        quoteTitle.setText(span, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void showTopExtendMenu(boolean isShow) {
        this.setVisibility(isShow ? VISIBLE : GONE);
    }
}
