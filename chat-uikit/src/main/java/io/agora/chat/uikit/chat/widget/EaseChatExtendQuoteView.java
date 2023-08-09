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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import io.agora.chat.ChatMessage;
import io.agora.chat.FileMessageBody;
import io.agora.chat.LocationMessageBody;
import io.agora.chat.TextMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.IChatExtendQuoteView;
import io.agora.chat.uikit.chat.interfaces.IChatQuote;
import io.agora.chat.uikit.chat.interfaces.IChatTopExtendMenu;
import io.agora.chat.uikit.chat.presenter.EaseChatQuotePresenter;
import io.agora.chat.uikit.chat.presenter.EaseChatQuotePresenterImpl;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;

public class EaseChatExtendQuoteView extends FrameLayout implements IChatQuote, IChatTopExtendMenu, IChatExtendQuoteView {
    private Context context;
    private ImageView cancelSelect;
    private TextView quoteTitle;
    private ImageView quoteImage;
    private ImageView quoteIcon;
    private TextView quoteContent;
    private EaseChatQuotePresenter presenter;

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
        quoteTitle = findViewById(R.id.quote_name);
        quoteImage = findViewById(R.id.quote_image);
        quoteIcon = findViewById(R.id.quote_icon);
        quoteContent = findViewById(R.id.quote_content);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.presenter != null) {
            this.presenter.detachView();
        }
    }

    private void initData() {
        if(presenter == null) {
            presenter = new EaseChatQuotePresenterImpl();
        }
        presenter.attachView(this);
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
    }

    @Override
    public void startQuote(ChatMessage message) {
        hideQuoteView();
        presenter.showQuoteMessageInfo(message);
    }

    @Override
    public void hideQuoteView() {
        this.setVisibility(GONE);
        quoteTitle.setText("");
        quoteContent.setText("");
        quoteImage.setImageDrawable(null);
        quoteIcon.setVisibility(GONE);
    }

    @Override
    public void setPresenter(EaseChatQuotePresenter presenter) {
        if(presenter == null) {
            return;
        }
        this.presenter = presenter;
        this.presenter.attachView(this);
        if(context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getLifecycle().addObserver(presenter);
        }
    }

    @Override
    public void showTopExtendMenu(boolean isShow) {
        this.setVisibility(isShow ? VISIBLE : GONE);
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public void showQuoteMessageNickname(String nickname) {
        quoteTitle.setText(nickname);
    }

    @Override
    public void showQuoteMessageContent(StringBuilder content) {
        quoteContent.setText(content);
        showTopExtendMenu(true);
    }

    @Override
    public void showQuoteMessageAttachment(ChatMessage.Type type, String localPath, String remotePath, int defaultResource) {
        Glide.with(quoteImage)
                .load(TextUtils.isEmpty(localPath) ? remotePath : localPath)
                .apply(new RequestOptions().error(defaultResource))
                .into(quoteImage);
        quoteImage.setVisibility(VISIBLE);
        if(type == ChatMessage.Type.VIDEO) {
            quoteIcon.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onShowError(int code, String message) {

    }
}
