package io.agora.chat.uikit.widget;

import static io.agora.chat.uikit.widget.DynamicDrawableSpan.ALIGN_CENTER;
import static io.agora.chat.uikit.widget.DynamicDrawableSpan.ALIGN_TOP;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.agora.Error;
import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.CombineMessageBody;
import io.agora.chat.FileMessageBody;
import io.agora.chat.ImageMessageBody;
import io.agora.chat.LocationMessageBody;
import io.agora.chat.VideoMessageBody;
import io.agora.chat.VoiceMessageBody;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.interfaces.ChatQuoteMessageProvider;
import io.agora.chat.uikit.chat.model.EaseReplyMap;
import io.agora.chat.uikit.constants.EaseConstant;
import io.agora.chat.uikit.interfaces.IUIKitInterface;
import io.agora.chat.uikit.interfaces.OnQuoteViewClickListener;
import io.agora.chat.uikit.manager.EaseChatInterfaceManager;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseUser;
import io.agora.chat.uikit.utils.EaseFileUtils;
import io.agora.chat.uikit.utils.EaseImageUtils;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.exceptions.ChatException;
import io.agora.util.EMLog;

public class EaseChatQuoteView extends LinearLayout {
    private static final String TAG = EaseChatQuoteView.class.getSimpleName();
    private static final int MAX_IMAGE_SIZE = 36;
    private final Context mContext;
    private final TextView quoteDefaultView;
    private final ViewGroup quoteDefaultLayout;
    private final TextView tvSummary;
    private ChatMessage message;
    private String quoteSender;
    private boolean isHistory;
    public static final String URL_REGEX = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
            + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";


    private static final Map<String, String> receiveMsgTypes = new HashMap<String, String>() {
        {
            put(EaseReplyMap.txt.name(),ChatMessage.Type.TXT.name());
            put(EaseReplyMap.img.name(), ChatMessage.Type.IMAGE.name());
            put(EaseReplyMap.video.name(), ChatMessage.Type.VIDEO.name());
            put(EaseReplyMap.location.name(), ChatMessage.Type.LOCATION.name());
            put(EaseReplyMap.audio.name(), ChatMessage.Type.VOICE.name());
            put(EaseReplyMap.file.name(), ChatMessage.Type.FILE.name());
            put(EaseReplyMap.cmd.name(), ChatMessage.Type.CMD.name());
            put(EaseReplyMap.custom.name(), ChatMessage.Type.CUSTOM.name());
        }
    };

    public EaseChatQuoteView(Context context) {
        this(context, null);
    }

    public EaseChatQuoteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatQuoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EaseChatQuoteView);
        boolean isSender = typedArray.getBoolean(R.styleable.EaseChatQuoteView_ease_chat_quote_sender, false);
        isHistory = typedArray.getBoolean(R.styleable.EaseChatQuoteView_ease_chat_quote_is_history, false);
        typedArray.recycle();

        if(isHistory) {
            LayoutInflater.from(mContext).inflate(R.layout.ease_chat_row_history_quote_layout, this);
        }else {
            if (isSender) {
                LayoutInflater.from(mContext).inflate(R.layout.ease_row_sent_quote_layout, this);
            } else {
                LayoutInflater.from(mContext).inflate(R.layout.ease_row_received_quote_layout, this);
            }
        }

        quoteDefaultView = findViewById(R.id.tv_default);
        quoteDefaultLayout = findViewById(R.id.subBubble_default_layout);
        tvSummary = findViewById(R.id.tv_summary);

        setTextBreakStrategy(quoteDefaultView);

        if(!isHistory) {
            initListener();
        }
    }

    private void setTextBreakStrategy(TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_BALANCED);
        }
    }

    private void initListener() {
        setOnClickListener(v -> {
            OnQuoteViewClickListener listener = getClickListener();
            if(listener != null && message != null) {
                String msgQuote = message.getStringAttribute(EaseConstant.QUOTE_MSG_QUOTE,"");
                if (!TextUtils.isEmpty(msgQuote)){
                    try {
                        JSONObject jsonObject = new JSONObject(msgQuote);
                        String quoteMsgID = jsonObject.getString(EaseConstant.QUOTE_MSG_ID);
                        ChatMessage showMsg = ChatClient.getInstance().chatManager().getMessage(quoteMsgID);
                        if(showMsg == null) {
                            listener.onQuoteViewClickError(Error.GENERAL_ERROR, mContext.getString(R.string.ease_error_message_not_exist));
                            return;
                        }
                        listener.onQuoteViewClick(showMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onQuoteViewClickError(Error.GENERAL_ERROR, e.getMessage());
                    }
                }
            }
        });

        setOnLongClickListener(v -> {
            OnQuoteViewClickListener listener = getClickListener();
            if(listener != null) {
                return listener.onQuoteViewLongClick(v, message);
            }
            return false;
        });
    }

    public boolean updateMessageInfo(@Nullable ChatMessage message){
        if(message == null) {
            EMLog.e(TAG, getContext().getString(R.string.ease_error_message_not_exist));
            return false;
        }
        if(message.ext() != null && !message.ext().containsKey(EaseConstant.QUOTE_MSG_QUOTE)) {
            return false;
        }
        this.message = message;
        quoteSender = null;
        JSONObject jsonObject = null;
        reSetLayout();
        String msgQuote = message.getStringAttribute(EaseConstant.QUOTE_MSG_QUOTE,"");
        try {
            if (!TextUtils.isEmpty(msgQuote)){
                try {
                    jsonObject = new JSONObject(msgQuote);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                jsonObject = message.getJSONObjectAttribute(EaseConstant.QUOTE_MSG_QUOTE);
            }
        } catch (ChatException e) {
            EMLog.e(TAG, "error message: "+e.getMessage());
        }
        if(jsonObject == null) {
            EMLog.e(TAG, "message: "+message.getMsgId() + " is not a quote message.");
            return false;
        }
        quoteDefaultView.setText("");
        setContent(jsonObject);
        this.setVisibility(VISIBLE);
        quoteDefaultView.setVisibility(VISIBLE);
        quoteDefaultLayout.setVisibility(VISIBLE);
        return true;
    }

    private void setContent(JSONObject jsonObject){
        try {
            String quoteMsgID = jsonObject.getString(EaseConstant.QUOTE_MSG_ID);
            String quoteSender = jsonObject.getString(EaseConstant.QUOTE_MSG_SENDER);
            String quoteType = jsonObject.getString(EaseConstant.QUOTE_MSG_TYPE);
            String quoteContent = jsonObject.getString(EaseConstant.QUOTE_MSG_PREVIEW);

            String quoteSenderNick = "";
            if (!TextUtils.isEmpty(quoteSender)){

                EaseUser user = EaseUserUtils.getUserInfo(quoteSender);
                if (user == null){
                    quoteSenderNick = quoteSender;
                }else {
                    quoteSenderNick = user.getNickname();
                }
            }
            this.quoteSender = quoteSenderNick;
            ChatMessage quoteMessage = ChatClient.getInstance().chatManager().getMessage(quoteMsgID);

            isShowType(quoteMessage, quoteSenderNick, getQuoteMessageType(quoteType), quoteContent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ChatMessage.Type getQuoteMessageType(String quoteType){
        if(receiveMsgTypes.containsKey(quoteType)) {
            return ChatMessage.Type.valueOf(receiveMsgTypes.get(quoteType));
        }
        ChatMessage.Type type;
        try {
            type = ChatMessage.Type.valueOf(quoteType.toUpperCase());
            return type;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return ChatMessage.Type.TXT;
    }

    private void reSetLayout(){
        this.setVisibility(GONE);
    }

    private void isShowType(ChatMessage quoteMessage, String quoteSender, ChatMessage.Type quoteMsgType, String content){
        reSetLayout();
        IUIKitInterface listener = EaseChatInterfaceManager.getInstance().getInterface(mContext, ChatQuoteMessageProvider.class.getSimpleName());
        if(listener instanceof ChatQuoteMessageProvider) {
            SpannableString result = ((ChatQuoteMessageProvider)listener).provideQuoteContent(quoteMessage, quoteMsgType, quoteSender, content);
            if(result != null) {
                reSetLayout();
                quoteDefaultView.setText(result);
                quoteDefaultLayout.setVisibility(VISIBLE);
                return;
            }
        }
        if(isHistory) {
            if(quoteMsgType == ChatMessage.Type.TXT) {
                txtTypeDisplay(quoteMessage,quoteSender,content);
            }else {
                SpannableString spannableString = new SpannableString(quoteSender + ": "+content);
                quoteDefaultView.setText(spannableString);
                quoteDefaultView.setEllipsize(TextUtils.TruncateAt.END);
                quoteDefaultView.setMaxLines(2);
                quoteDefaultLayout.setVisibility(View.VISIBLE);
            }
            return;
        }
        switch (quoteMsgType){
            case TXT:
                txtTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case IMAGE:
                imageTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case VIDEO:
                videoTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case LOCATION:
                locationTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case VOICE:
                voiceTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case FILE:
                fileTypeDisplay(quoteMessage,quoteSender,content);
                break;
            case COMBINE:
                combineTypeDisplay(quoteMessage,quoteSender,content);
                break;
            default:
                SpannableString spannableString = new SpannableString(quoteSender + ": "+content);
                quoteDefaultView.setText(spannableString);
                quoteDefaultLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    protected void txtTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        if (quoteMessage != null && quoteMessage.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
            showBigExpression(quoteMessage, quoteSender);
        }else {
            SpannableString spannableString = new SpannableString(EaseSmileUtils.getSmiledText(mContext, quoteSender + ": "+content));
            if (containsUrl(content)){
                appendDrawable(spannableString, ContextCompat.getDrawable(mContext, R.drawable.ease_quote_text_link), true, true);
            }else {
                quoteDefaultView.setText(spannableString);
                quoteDefaultView.setEllipsize(TextUtils.TruncateAt.END);
                quoteDefaultView.setMaxLines(2);
                quoteDefaultLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void imageTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        if (quoteMessage == null){
            addDrawable(quoteSender, ContextCompat.getDrawable(mContext, R.drawable.ease_chat_quote_default_image), true);
        }else {
            showImageView(quoteMessage, quoteSender);
        }
    }

    protected void videoTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        if (quoteMessage == null){
            addDrawable(quoteSender, ContextCompat.getDrawable(mContext, R.drawable.ease_chat_quote_default_video));
        }else {
            showVideoThumbView(quoteMessage, quoteSender);
        }
    }

    protected void locationTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        String title = "";
        int startIndex = quoteSender.length() + 1;
        if (quoteMessage == null){
            title = quoteSender + ": " + content;
        }else {
            if (quoteMessage.getBody() instanceof LocationMessageBody){
                LocationMessageBody locationMessageBody = (LocationMessageBody)quoteMessage.getBody();
                title = quoteSender + ": " + locationMessageBody.getAddress();
            }
        }
        SpannableString locationSb = new SpannableString(title);
        CenterImageSpan span = new CenterImageSpan(mContext, R.drawable.ease_chat_item_menu_location);
        if (locationSb.length() >= startIndex + 2){
            locationSb.setSpan(span, startIndex, startIndex + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        appendDrawable(locationSb, null, true, true);
    }

    protected void voiceTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        StringBuilder builder = new StringBuilder();
        String voiceLength = "";
        if (quoteMessage == null){
            builder.append(quoteSender).append(": ").append(content);
        }else {
            if (quoteMessage.getBody() instanceof VoiceMessageBody){
                VoiceMessageBody voiceMessageBody = (VoiceMessageBody) quoteMessage.getBody();
                if (voiceMessageBody != null && voiceMessageBody.getLength() > 0){
                    voiceLength = voiceMessageBody.getLength() + "\"";
                }
                builder.append(quoteSender).append(": ");
            }
        }
        builder.append(voiceLength);
        SpannableString voiceSpan = new SpannableString(builder.toString());
        appendDrawable(voiceSpan, ContextCompat.getDrawable(mContext, R.drawable.ease_chatfrom_voice_playing), true, true);
    }

    protected void fileTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        StringBuilder builder = new StringBuilder();
        builder.append(quoteSender).append(": ");
        if(quoteMessage == null) {
            builder.append(content);
        }else {
            if (quoteMessage.getBody() instanceof FileMessageBody){
                FileMessageBody fileMessageBody = (FileMessageBody) quoteMessage.getBody();
                if (fileMessageBody != null && !TextUtils.isEmpty(fileMessageBody.getFileName())){
                    builder.append(fileMessageBody.getFileName());
                }
            }
        }
        SpannableString fileSpan = new SpannableString(builder.toString());
        appendDrawable(fileSpan, ContextCompat.getDrawable(mContext, R.drawable.ease_chat_quote_file), true, true);
    }

    protected void combineTypeDisplay(ChatMessage quoteMessage,String quoteSender,String content){
        StringBuilder builder = new StringBuilder();
        builder.append(quoteSender).append(": ");
        if(quoteMessage == null) {
            builder.append(mContext.getString(R.string.ease_combine_default));
        }else {
            if (quoteMessage.getBody() instanceof CombineMessageBody){
                CombineMessageBody combineMessageBody = (CombineMessageBody) quoteMessage.getBody();
                if (combineMessageBody != null && !TextUtils.isEmpty(combineMessageBody.getTitle())){
                    builder.append(combineMessageBody.getTitle());
                }
            }
        }
        SpannableString fileSpan = new SpannableString(builder.toString());
        appendDrawable(fileSpan, ContextCompat.getDrawable(mContext, R.drawable.ease_chat_quote_combine), true, true);
    }

    /**
     * show video thumbnails
     * @param quoteMessage
     * @param quoteSender
     */
    private void showVideoThumbView(ChatMessage quoteMessage, String quoteSender) {
        Uri imageUri = null;
        String imageUrl = "";
        if (quoteMessage.getBody() instanceof VideoMessageBody){
            VideoMessageBody videoMessageBody = (VideoMessageBody) quoteMessage.getBody();
            if ( quoteMessage.direct() == ChatMessage.Direct.SEND){
                imageUri = videoMessageBody.getLocalThumbUri();
                videoMessageBody.getLocalThumb();
            }else {
                imageUrl = videoMessageBody.getThumbnailUrl();
            }
        }
        Glide.with(mContext)
                .load(imageUri == null ? imageUrl : imageUri)
                .apply(new RequestOptions()
                        .error(R.drawable.ease_chat_quote_default_video))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        addVideoDrawable(quoteSender, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        addDrawable(quoteSender, placeholder, true);
                    }
                });
    }

    /**
     * load image into image view
     *
     */
    private void showImageView(final ChatMessage message, String quoteSender) {
        if (message.getType() == ChatMessage.Type.IMAGE){
            Uri imageUri = null;
            String imageUrl = "";
            ImageMessageBody imageMessageBody = (ImageMessageBody) message.getBody();
            if(EaseFileUtils.isFileExistByUri(mContext, imageMessageBody.thumbnailLocalUri())) {
                imageUri = imageMessageBody.thumbnailLocalUri();
            }else if(EaseFileUtils.isFileExistByUri(mContext, imageMessageBody.getLocalUri())) {
                imageUri = imageMessageBody.getLocalUri();
            }else {
                imageUrl = imageMessageBody.getRemoteUrl();
            }
            Glide.with(mContext)
                    .load(imageUri == null ? imageUrl : imageUri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ease_chat_quote_default_image)
                            .error(R.drawable.ease_chat_quote_default_image))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            addDrawable(quoteSender, resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            addDrawable(quoteSender, placeholder, true);
                        }
                    });
        }
    }

    private void showBigExpression(ChatMessage message, String quoteSender){
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EaseEmojicon emojicon = null;
        if(EaseUIKit.getInstance().getEmojiconInfoProvider() != null){
            emojicon =  EaseUIKit.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }
        if(emojicon != null){
            if(emojicon.getBigIcon() != 0){
                Glide.with(mContext).load(emojicon.getIcon())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        addDrawable(quoteSender, resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        addDrawable(quoteSender, placeholder, true);
                                    }
                                });
            }else if(emojicon.getBigIconPath() != null){
                Glide.with(mContext).load(emojicon.getBigIconPath())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                addDrawable(quoteSender, resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                addDrawable(quoteSender, placeholder, true);
                            }
                        });
            }else{
                addDrawable(quoteSender, ContextCompat.getDrawable(mContext, R.drawable.ease_default_expression), true);
            }
        }
    }

    private void addVideoDrawable(String quoteSender, Drawable drawable) {
        Bitmap bitmapBig = EaseImageUtils.drawableToBitmap(drawable);
        Bitmap bitmapSmall = BitmapFactory.decodeResource(getResources(), R.drawable.ease_chat_video_triangle_in_circle);
        Bitmap result = Bitmap.createBitmap(bitmapBig.getWidth(), bitmapBig.getHeight(), bitmapBig.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmapBig, new Matrix(), null);
        canvas.drawBitmap(bitmapSmall, (bitmapBig.getWidth() - bitmapSmall.getWidth()) / 2, (bitmapBig.getHeight() - bitmapSmall.getHeight()) / 2, null);
        drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), result);
        addDrawable(quoteSender, drawable);
    }

    private void addDrawable(String quoteSender, Drawable drawable) {
        addDrawable(quoteSender, drawable, false);
    }

    private void addDrawable(String quoteSender, Drawable drawable, boolean isPlaceholder) {
        SpannableString spannableString = new SpannableString(quoteSender+": ");
        appendDrawable(spannableString, drawable, isPlaceholder, false);
    }

    private void appendDrawable(SpannableString spannableString, Drawable drawable, boolean isPlaceholder, boolean isCenter) {
        if(drawable != null) {
            //int[] fitSize = getFitSize(drawable);
            int width = (int) EaseUtils.dip2px(mContext, MAX_IMAGE_SIZE);
            int height = width;
            if(!isPlaceholder) {
                //int minSize = Math.min(width, height);
                //int maxSize = Math.max(width, height);
                //if(maxSize < minSize * 3 && minSize > EaseUtils.dip2px(mContext, 6) * 3) {
                    drawable = EaseImageUtils.getRoundedCornerDrawable(mContext, EaseImageUtils.drawableToBitmap(drawable), EaseUtils.dip2px(mContext, 6));
                //}
            }
            if(isPlaceholder) {
                width = getFitSize(drawable)[0];
                height = width;
            }
            drawable.setBounds(0, 0, width, height);
            DynamicDrawableSpan imageSpan = new DynamicDrawableSpan(drawable, isCenter ? ALIGN_CENTER : ALIGN_TOP);
            int startIndex = getStartIndex();
            spannableString.setSpan(imageSpan, startIndex, startIndex + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        quoteDefaultView.setText(spannableString);
        quoteDefaultView.post(()-> {
            int width = quoteDefaultView.getWidth();
            if(width == 0) {
                ViewGroup.LayoutParams layoutParams = quoteDefaultView.getLayoutParams();
                layoutParams.width = LayoutParams.WRAP_CONTENT;
                quoteDefaultView.setLayoutParams(layoutParams);
            }
        });
        quoteDefaultLayout.setVisibility(View.VISIBLE);
    }

    private int[] getFitSize(Drawable drawable) {
        if(drawable == null) {
            return new int[]{0, 0};
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int maxSize = (int) EaseUtils.dip2px(mContext, MAX_IMAGE_SIZE);
        int fitWidth = 0;
        int fitHeight = 0;
        if(width >= height) {
            if(width > maxSize) {
                fitWidth = maxSize;
                fitHeight = (int)(height * 1.0f * fitWidth / width);
                return new int[]{fitWidth, fitHeight};
            }
        }else {
            if(height > maxSize) {
                fitHeight = maxSize;
                fitWidth = (int)(width * 1.0f * fitHeight / height);
                return new int[]{fitWidth, fitHeight};
            }
        }
        return new int[]{width, height};
    }

    private int getStartIndex() {
        if(TextUtils.isEmpty(quoteSender)) {
            return -1;
        }
        return quoteSender.length() + 1;
    }

    /**
     * Get target listener by EaseChatInterfaceManager.
     * @return
     */
    private OnQuoteViewClickListener getClickListener() {
        IUIKitInterface kitInterface = EaseChatInterfaceManager.getInstance().getInterface(mContext, OnQuoteViewClickListener.class.getSimpleName());
        if(kitInterface instanceof OnQuoteViewClickListener) {
            return (OnQuoteViewClickListener) kitInterface;
        }
        return null;
    }

    private static boolean containsUrl(String content) {
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(content);
        boolean b = m.find();
        return b;
    }
}
