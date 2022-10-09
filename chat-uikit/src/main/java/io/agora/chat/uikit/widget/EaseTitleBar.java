package io.agora.chat.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.utils.StatusBarCompat;


/**
 * title bar
 *
 */
public class EaseTitleBar extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private Toolbar toolbar;
    protected RelativeLayout leftLayout;
    protected ImageView leftImage;
    protected RelativeLayout rightLayout;
    protected ImageView rightImage;
    protected TextView titleView;
    protected TextView subTitleView;
    protected RelativeLayout titleLayout;
    private TextView titleMenu;
    private OnBackPressListener mBackPressListener;
    private OnRightClickListener mOnRightClickListener;
    private int mArrowColorId;
    private int mArrowColor;
    private int mTitleTextColor;
    private int mRightTextColor;
    private int mSubTitleTextColor;
    private int mWidth;
    private int mHeight;
    private boolean mDisplayHomeAsUpEnabled;
    private ConstraintLayout clTitle;
    private EaseImageView ivIcon;
    private ImageView msgUnreadIcon;
    private OnIconClickListener iconClickListener;

    public EaseTitleBar(Context context) {
        this(context, null);
    }

    public EaseTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initLayout();
    }

    private void initLayout() {
        ViewGroup.LayoutParams params = titleLayout.getLayoutParams();
        params.height = mHeight;
        params.width = mWidth;
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.ease_widget_title_bar, this);
        toolbar = findViewById(R.id.toolbar);
        leftLayout = (RelativeLayout) findViewById(R.id.left_layout);
        leftImage = (ImageView) findViewById(R.id.left_image);
        rightLayout = (RelativeLayout) findViewById(R.id.right_layout);
        rightImage = (ImageView) findViewById(R.id.right_image);
        titleView = (TextView) findViewById(R.id.title);
        subTitleView = (TextView) findViewById(R.id.sub_title);
        titleLayout = (RelativeLayout) findViewById(R.id.root);
        titleMenu = findViewById(R.id.right_menu);
        clTitle = findViewById(R.id.cl_title);
        ivIcon = findViewById(R.id.iv_icon);
        msgUnreadIcon = findViewById(R.id.msg_unread_icon);
        parseStyle(context, attrs);

        initToolbar();
    }

    private void parseStyle(Context context, AttributeSet attrs){
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseTitleBar);
            int titleId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarTitle, -1);
            if(titleId != -1) {
                titleView.setText(titleId);
            }else {
                String title = ta.getString(R.styleable.EaseTitleBar_titleBarTitle);
                titleView.setText(title);
            }

            int titleTextStylePosition = ta.getInteger(R.styleable.EaseTitleBar_titleBarTitleTextStyle, -1);
            setTitleTextStyle(titleTextStylePosition);

            int subTitleId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarSubTitle, -1);
            if(subTitleId != -1) {
                subTitleView.setText(subTitleId);
            }else {
                String subTitle = ta.getString(R.styleable.EaseTitleBar_titleBarSubTitle);
                subTitleView.setText(subTitle);
            }

            Drawable leftDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarLeftImage);
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable);
            }
            Drawable rightDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarRightImage);
            if (null != rightDrawable) {
                rightImage.setImageDrawable(rightDrawable);
            }

            mArrowColorId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarArrowColor, -1);
            mArrowColor = ta.getColor(R.styleable.EaseTitleBar_titleBarArrowColor, Color.BLACK);

            Drawable menuDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarMenuResource);
            if(menuDrawable != null) {
                toolbar.setOverflowIcon(menuDrawable);
            }

            int rightTitleId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarRightTitle, -1);
            if(rightTitleId != -1) {
                titleMenu.setText(rightTitleId);
            }else {
                String rightTitle = ta.getString(R.styleable.EaseTitleBar_titleBarRightTitle);
                titleMenu.setText(rightTitle);
            }

            int rightTextColor = ta.getResourceId(R.styleable.EaseTitleBar_titleBarRightTextColor, -1);
            if(rightTextColor != -1) {
                mRightTextColor = ContextCompat.getColor(getContext(), rightTextColor);
            }else {
                mRightTextColor = ta.getColor(R.styleable.EaseTitleBar_titleBarRightTextColor, ContextCompat.getColor(getContext(), R.color.ease_toolbar_color_title));
            }
            titleMenu.setTextColor(mRightTextColor);

            boolean rightVisible = ta.getBoolean(R.styleable.EaseTitleBar_titleBarRightVisible, false);
            rightLayout.setVisibility(rightVisible ? VISIBLE : GONE);

            mDisplayHomeAsUpEnabled = ta.getBoolean(R.styleable.EaseTitleBar_titleBarDisplayHomeAsUpEnabled, true);

            int titlePosition = ta.getInteger(R.styleable.EaseTitleBar_titleBarTitlePosition, 0);
            setTitlePosition(titlePosition);

            float titleTextSize = ta.getDimension(R.styleable.EaseTitleBar_titleBarTitleTextSize, (int) sp2px(getContext(), 18));
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);

            int titleTextColor = ta.getResourceId(R.styleable.EaseTitleBar_titleBarTitleTextColor, -1);
            if(titleTextColor != -1) {
                mTitleTextColor = ContextCompat.getColor(getContext(), titleTextColor);
            }else {
                mTitleTextColor = ta.getColor(R.styleable.EaseTitleBar_titleBarTitleTextColor, ContextCompat.getColor(getContext(), R.color.ease_toolbar_color_title));
            }
            titleView.setTextColor(mTitleTextColor);

            float subTitleTextSize = ta.getDimension(R.styleable.EaseTitleBar_titleBarSubTitleTextSize, (int) sp2px(getContext(), 12));
            subTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTitleTextSize);

            int subTitleTextColor = ta.getResourceId(R.styleable.EaseTitleBar_titleBarSubTitleTextColor, -1);
            if(subTitleTextColor != -1) {
                mSubTitleTextColor = ContextCompat.getColor(getContext(), subTitleTextColor);
            }else {
                mSubTitleTextColor = ta.getColor(R.styleable.EaseTitleBar_titleBarSubTitleTextColor, ContextCompat.getColor(getContext(), R.color.ease_toolbar_color_sub_title));
            }
            subTitleView.setTextColor(mSubTitleTextColor);
            boolean subTitleVisible = ta.getBoolean(R.styleable.EaseTitleBar_titleBarSubTitleVisible, false);
            subTitleView.setVisibility(subTitleVisible ? VISIBLE : GONE);

            boolean unreadIconVisible = ta.getBoolean(R.styleable.EaseTitleBar_titleBarUnreadIconVisible, false);
            msgUnreadIcon.setVisibility(unreadIconVisible ? VISIBLE : GONE);

            int arrowSrcResourceId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarIcon, -1);
            if(arrowSrcResourceId != -1) {
                ivIcon.setImageResource(arrowSrcResourceId);
            }

            int iconSizeId = ta.getResourceId(R.styleable.EaseTitleBar_titleBarIconSize, -1);
            float size = ta.getDimension(R.styleable.EaseTitleBar_titleBarIconSize, EaseUtils.dip2px(getContext(), 34));
            if(iconSizeId != -1) {
                size = getResources().getDimension(iconSizeId);
            }

            ta.recycle();

            ViewGroup.LayoutParams params = ivIcon.getLayoutParams();
            params.height = (int) size;
            params.width = (int) size;
        }
    }

    private void setTitleTextStyle(int textStylePosition){
        switch (textStylePosition){
            case 0:
                titleView.setTypeface(null, Typeface.NORMAL);
                break;
            case 1:
                titleView.setTypeface(null, Typeface.BOLD);
                break;
            case 2:
                titleView.setTypeface(null, Typeface.ITALIC);
                break;
        }
    }

    private void setTitlePosition(int titlePosition) {
        ViewGroup.LayoutParams params = clTitle.getLayoutParams();
        if(params instanceof LayoutParams) {
            if(titlePosition == 0) { //Middle
                ((LayoutParams) params).addRule(RelativeLayout.CENTER_IN_PARENT);
                ((LayoutParams) params).removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((LayoutParams) params).removeRule(RelativeLayout.CENTER_VERTICAL);
                ((LayoutParams) params).removeRule(RelativeLayout.RIGHT_OF);
            }else if(titlePosition == 1) { //Left
                ((LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((LayoutParams) params).addRule(RelativeLayout.CENTER_VERTICAL);
                ((LayoutParams) params).addRule(RelativeLayout.RIGHT_OF, leftLayout.getId());
            }else { //Right
                ((LayoutParams) params).removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                ((LayoutParams) params).removeRule(RelativeLayout.CENTER_VERTICAL);
                ((LayoutParams) params).removeRule(RelativeLayout.RIGHT_OF);
                ((LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ((LayoutParams) params).addRule(RelativeLayout.CENTER_VERTICAL);
                ((LayoutParams) params).addRule(LEFT_OF, rightLayout.getId());
                ((LayoutParams) params).setMargins(0, 0, (int) dip2px(getContext(), 60), 0);
            }
        }
        clTitle.setLayoutParams(params);
    }

    private void initToolbar() {
        rightLayout.setOnClickListener(this);
        leftLayout.setOnClickListener(this);
        ivIcon.setOnClickListener(this);
        if(leftImage.getDrawable() != null) {
            leftImage.setVisibility(mDisplayHomeAsUpEnabled ? VISIBLE : GONE);
            leftLayout.setVisibility(mDisplayHomeAsUpEnabled ? VISIBLE : GONE);
        }else {
            if(getContext() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                if(activity.getSupportActionBar() == null) {
                    activity.setSupportActionBar(toolbar);
                    if(activity.getSupportActionBar() != null) {
                        // Show back icon
                        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(mDisplayHomeAsUpEnabled);
                        // Not show title
                        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                    }
                    toolbar.setNavigationOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mBackPressListener != null) {
                                mBackPressListener.onBackPress(v);
                            }
                        }
                    });
                    if(mArrowColorId != -1) {
                        StatusBarCompat.setToolbarCustomColor(getContext(), mArrowColorId);
                    }else {
                        StatusBarCompat.setToolbarCustomColorDefault(getContext(), mArrowColor);
                    }
                }
            }
        }
    }

    public void setLeftImageResource(int resId) {
        leftImage.setImageResource(resId);
        leftLayout.setVisibility(View.VISIBLE);
    }
    
    public void setRightImageResource(int resId) {
        rightImage.setImageResource(resId);
        rightLayout.setVisibility(VISIBLE);
    }

    public void setRightTitleResource(@StringRes int title) {
        titleMenu.setText(getResources().getString(title));
        rightLayout.setVisibility(VISIBLE);
    }

    public void setRightTitle(String title) {
        if(!TextUtils.isEmpty(title)) {
            titleMenu.setText(title);
            rightLayout.setVisibility(VISIBLE);
        }
    }

    public void setRightTitleColor(@ColorRes int color){
        titleMenu.setTextColor(getResources().getColor(color));
    }

    public void setIcon(Drawable icon) {
        if(icon != null) {
            ivIcon.setImageDrawable(icon);
            ivIcon.setVisibility(VISIBLE);
        }
    }

    public void setIcon(@DrawableRes int icon) {
        if(icon != 0) {
            ivIcon.setImageResource(icon);
            ivIcon.setVisibility(VISIBLE);
        }
    }

    /**
     * Set title's position, see {@link TitlePosition}
     * @param position
     */
    public void setTitlePosition(TitlePosition position) {
        int pos;
        if(position == TitlePosition.Center) {
            pos = 0;
        }else if(position == TitlePosition.Left) {
            pos = 1;
        }else {
            pos = 2;
        }
        setTitlePosition(pos);
    }
    
    public void setLeftLayoutClickListener(OnClickListener listener){
        leftLayout.setOnClickListener(listener);
    }
    
    public void setRightLayoutClickListener(OnClickListener listener){
        rightLayout.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility){
        leftLayout.setVisibility(visibility);
    }
    
    public void setRightLayoutVisibility(int visibility){
        rightLayout.setVisibility(visibility);
    }
    
    public void setTitle(String title){
        titleView.setText(title);
    }

    public TextView getTitle() {
        return titleView;
    }

    public void setTitleSize(float sp){
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
    }

    public TextView getSubTitle() {
        return subTitleView;
    }

    public void setSubTitle(String title){
        subTitleView.setText(title);
        if(!TextUtils.isEmpty(title)) {
            subTitleView.setVisibility(VISIBLE);
        }
    }

    public void setSubTitleSize(float sp){
        subTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
    }

    public void setDisplayHomeAsUpEnabled(boolean displayHomeAsUpEnabled) {
        this.mDisplayHomeAsUpEnabled = displayHomeAsUpEnabled;
        initToolbar();
    }

    public void setUnreadIconVisible(boolean visible) {
        msgUnreadIcon.setVisibility(visible ? VISIBLE : GONE);
    }
    
    public void setBackgroundColor(int color){
        titleLayout.setBackgroundColor(color);
    }
    
    public RelativeLayout getLeftLayout(){
        return leftLayout;
    }
    
    public RelativeLayout getRightLayout(){
        return rightLayout;
    }

    public ImageView getRightImage() {
        return rightImage;
    }

    public TextView getRightText() {
        return titleMenu;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public EaseImageView getIcon() {
        return ivIcon;
    }

    public ImageView getMsgUnreadIcon() {
        return msgUnreadIcon;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.left_layout) {
            if(mBackPressListener != null) {
                mBackPressListener.onBackPress(v);
            }
        }else if(v.getId() == R.id.right_layout) {
            if(mOnRightClickListener != null) {
                mOnRightClickListener.onRightClick(v);
            }
        }else if(v.getId() == R.id.iv_icon) {
            if(iconClickListener != null) {
                iconClickListener.onIconClick(v);
            }
        }
    }

    /**
     * Set back event listener
     * @param listener
     */
    public void setOnBackPressListener(OnBackPressListener listener) {
        this.mBackPressListener = listener;
    }

    /**
     * Set Right region click listener
     * @param listener
     */
    public void setOnRightClickListener(OnRightClickListener listener) {
        this.mOnRightClickListener = listener;
    }

    public void setOnIconClickListener(OnIconClickListener iconClickListener) {
        this.iconClickListener = iconClickListener;
    }

    /**
     * Back event listener
     */
    public interface OnBackPressListener {
        void onBackPress(View view);
    }

    /**
     * Click right region listener
     */
    public interface OnRightClickListener {
        void onRightClick(View view);
    }

    /**
     * Click icon listener
     */
    public interface OnIconClickListener {
        void onIconClick(View view);
    }

    /**
     * Title position enum
     */
    public enum TitlePosition {
        Center, Left, Right
    }

    /**
     * dip to px
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * sp to px
     * @param context
     * @param value
     * @return
     */
    public static float sp2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources().getDisplayMetrics());
    }

    public <T> T getView(int viewId){
        return (T) findViewById(viewId);
    }
}
