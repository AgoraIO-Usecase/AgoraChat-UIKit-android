package io.agora.chat.uikit.menu;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chat.MessageReaction;
import io.agora.chat.uikit.EaseUIKit;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseMessageMenuData;
import io.agora.chat.uikit.options.EaseReactionOptions;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.util.EMLog;

public class EaseMessageMenuHelper {
    private static final String TAG = EaseMessageMenuHelper.class.getSimpleName();
    private static final int REACTION_SPAN_COUNT = 6;
    private EaseMessageMenuPopupWindow mPopupWindow;
    private final List<ReactionItemBean> mReactionItems = new ArrayList<>();
    private final List<MenuItemBean> mMenuItems = new ArrayList<>();
    private final Map<Integer, MenuItemBean> mMenuItemMap = new HashMap<>();
    private Context mContext;
    private ReactionAdapter mReactionAdapter;
    private MenuAdapter mMenuAdapter;
    private EaseMessageMenuPopupWindow.OnPopupWindowItemClickListener mItemClickListener;
    private EaseMessageMenuPopupWindow.OnPopupWindowDismissListener mDismissListener;
    private boolean mTouchable;
    private Drawable mBackground;
    private View mLayout;
    private RecyclerView mReactionListView;
    private RecyclerView mMenuListView;

    private int mReactionListHeight;
    private int mMenuListHeight;

    private View mTopView;
    private ImageView mMessageView;
    private View mPopupView;
    private View mBottomView;

    private final Map<String, Boolean> mEmojiContainCurrentUserMap = new HashMap<>();

    private boolean mIsShowReactionView;

    public EaseMessageMenuHelper() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        clear();
        mIsShowReactionView = true;
    }

    /**
     * @param context
     */
    public void init(@NonNull Context context) {
        this.mContext = context;
        mPopupWindow = new EaseMessageMenuPopupWindow(context, true);
        mLayout = View.inflate(context.getApplicationContext(), R.layout.ease_layout_message_menu_popupwindow, null);
        mPopupWindow.setContentView(mLayout);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setBackgroundAlpha(0.3f);

        mTopView = mLayout.findViewById(R.id.top_view);
        mMessageView = mLayout.findViewById(R.id.message_view);
        mPopupView = mLayout.findViewById(R.id.popup_view);

        mReactionListView = mLayout.findViewById(R.id.rv_reaction_list);
        mReactionListView.setLayoutManager(new GridLayoutManager(context, REACTION_SPAN_COUNT + 1));

        mReactionAdapter = new ReactionAdapter();
        mReactionListView.setAdapter(mReactionAdapter);
        mReactionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mItemClickListener != null) {
                    ReactionItemBean item = mReactionAdapter.getItem(position);
                    if (EaseMessageMenuData.EMOTICON_MORE_IDENTITY_CODE.equals(item.getIdentityCode())) {
                        showAllReactionEmoticon();
                    } else {
                        boolean isAdd;
                        if (null == mEmojiContainCurrentUserMap.get(item.getIdentityCode())) {
                            isAdd = true;
                        } else {
                            isAdd = !mEmojiContainCurrentUserMap.get(item.getIdentityCode());
                        }
                        mItemClickListener.onReactionItemClick(item, isAdd);
                        dismiss();
                    }
                }
            }
        });
        mReactionListView.addItemDecoration(new ReactionSpacesItemDecoration((int) dip2px(mContext, 5)));
        mReactionListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (View.VISIBLE == mReactionListView.getVisibility()) {
                    mReactionListHeight = mReactionListView.getHeight();
                }
            }
        });

        mMenuListView = mLayout.findViewById(R.id.rv_menu_list);
        mMenuListView.setLayoutManager(new LinearLayoutManager(context));

        mMenuAdapter = new MenuAdapter();
        mMenuListView.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                if (mItemClickListener != null) {
                    mItemClickListener.onMenuItemClick(mMenuAdapter.getItem(position));
                }
            }
        });
        mMenuListView.addItemDecoration(new MenuSpacesItemDecoration((int) dip2px(mContext, 20)));
        mMenuListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (View.VISIBLE == mMenuListView.getVisibility()) {
                    mMenuListHeight = mMenuListView.getHeight();
                } else {
                    mMenuListHeight = 0;
                }
            }
        });

        mTopView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });

        mBottomView = mLayout.findViewById(R.id.bottom_view);

    }

    public void clear() {
        mReactionItems.clear();
        mMenuItems.clear();
        mMenuItemMap.clear();
        mReactionListHeight = 0;
        mMenuListHeight = 0;
    }

    public void setMessageReactions(List<MessageReaction> messageReactions) {
        mEmojiContainCurrentUserMap.clear();
        if (null == messageReactions) {
            mReactionAdapter.setEmojiContainCurrentUserMap(null);
            return;
        }

        for (MessageReaction messageReaction : messageReactions) {
            mEmojiContainCurrentUserMap.put(messageReaction.getReaction(), messageReaction.isAddedBySelf());
        }

        mReactionAdapter.setEmojiContainCurrentUserMap(mEmojiContainCurrentUserMap);
    }

    public void setIsShowReactionView(boolean isShowReactionView) {
        this.mIsShowReactionView = isShowReactionView;
    }

    public void setOutsideTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.mBackground = background;
    }

    private void showPre() {
        mPopupWindow.setOutsideTouchable(mTouchable);
        mPopupWindow.setBackgroundDrawable(mBackground);
    }

    public void show(final View parent, final View v) {
        showPre();
        initFrequentlyReactionData();
        initMenuData();


        EaseReactionOptions reactionOptions = EaseUIKit.getInstance().getReactionOptions();
        if (null != reactionOptions && reactionOptions.isOpen()) {
            if (mReactionItems.size() <= 0) {
                EMLog.e(TAG, "reaction span count should be at least 1. Provided " + mReactionItems.size());
                return;
            }
            if (!mIsShowReactionView) {
                mReactionListView.setVisibility(View.GONE);
            }
        } else {
            mReactionListView.setVisibility(View.GONE);
        }

        if (mMenuItems.size() <= 0) {
            Log.e(TAG, "menu span count should be at least 1. Provided " + mMenuItems.size());
            return;
        }

        final float screenWidth = EaseUtils.getScreenInfo(mContext)[0];
        final float screenHeight = EaseUtils.getScreenInfo(mContext)[1];
        final int navBarHeight = mPopupWindow.getNavigationBarHeight(mContext);
        final int minPopupWindowHeight = (int) screenHeight * 2 / 5;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBottomView.getLayoutParams();
        params.height = navBarHeight;
        mBottomView.setLayoutParams(params);

        mPopupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        mPopupWindow.setViewLayoutParams(mPopupView, (int) screenWidth, minPopupWindowHeight);


        View cacheView;
        //view maybe textview or RelativeLayout
        if (v instanceof AppCompatTextView) {
            cacheView = ((View) v.getParent().getParent());
        } else {
            cacheView = ((View) v.getParent());
        }
        cacheView.setDrawingCacheEnabled(true);
        cacheView.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        cacheView.buildDrawingCache(true);
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        //cacheView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        //View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //cacheView.layout(cacheView.getLeft(), cacheView.getTop(), cacheView.getRight(), cacheView.getBottom());
        Bitmap selectMessageView = Bitmap.createBitmap(cacheView.getDrawingCache());
        cacheView.destroyDrawingCache();
        cacheView.getRootView().setDrawingCacheEnabled(false); // clear drawing cache

        //mMessageView.setImageBitmap(selectMessageView);
        //mMessageView.layout(mMessageView.getLeft(), mMessageView.getTop() + minPopupWindowHeight, mMessageView.getRight(), mMessageView.getBottom() + minPopupWindowHeight);

        mPopupView.setOnTouchListener(new View.OnTouchListener() {
            int orgX, orgY;
            int offsetX, offsetY;
            int popupWindowCurHeight = minPopupWindowHeight;
            int slippingHeight;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        orgX = (int) event.getRawX();
                        orgY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        offsetX = (int) event.getRawX() - orgX;
                        offsetY = (int) event.getRawY() - orgY;
                        slippingHeight = popupWindowCurHeight - offsetY;
                        mPopupWindow.setViewLayoutParams(mPopupView, (int) screenWidth, slippingHeight);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (offsetY > 0) {
                            popupWindowCurHeight = minPopupWindowHeight;
                        } else if (offsetY < 0) {
                            popupWindowCurHeight = mReactionListHeight + mMenuListHeight + navBarHeight;
                        }
                        mPopupWindow.setViewLayoutParams(mPopupView, (int) screenWidth, popupWindowCurHeight);
                        break;
                }
                return true;
            }
        });
    }

    private void showAllReactionEmoticon() {
        clear();
        if (null != mMenuListView) {
            mMenuListView.setVisibility(View.GONE);
        }

        initAllReactionData();
        mMenuAdapter.setData(mMenuItems);
    }

    private void initFrequentlyReactionData() {
        mReactionItems.clear();
        Map<String, EaseEmojicon> reactionMap = EaseMessageMenuData.getReactionDataMap();
        int count = 1;
        ReactionItemBean item;
        for (String id : EaseMessageMenuData.REACTION_FREQUENTLY_ICONS_IDS) {
            if (count > REACTION_SPAN_COUNT) {
                break;
            }
            item = new ReactionItemBean();
            item.setEmojiText("");
            item.setIdentityCode(reactionMap.get(id).getIdentityCode());
            item.setIcon(reactionMap.get(id).getIcon());
            mReactionItems.add(item);
            count++;
        }

        // add more emoticon
        item = new ReactionItemBean();
        EaseEmojicon moreEntry = EaseMessageMenuData.getReactionMore();
        item.setEmojiText(moreEntry.getEmojiText());
        item.setIdentityCode(moreEntry.getIdentityCode());
        item.setIcon(moreEntry.getIcon());
        mReactionItems.add(item);

        mReactionAdapter.setData(mReactionItems);
    }

    private void initAllReactionData() {
        Map<String, EaseEmojicon> reactionMap = EaseMessageMenuData.getReactionDataMap();
        ReactionItemBean item;
        for (Map.Entry<String, EaseEmojicon> entry : reactionMap.entrySet()) {
            item = new ReactionItemBean();
            item.setEmojiText("");
            item.setIdentityCode(entry.getValue().getIdentityCode());
            item.setIcon(entry.getValue().getIcon());
            mReactionItems.add(item);
        }
        mReactionAdapter.setData(mReactionItems);
    }

    private void initMenuData() {
        if (mMenuItemMap.size() > 0) {
            mMenuItems.clear();
            for (MenuItemBean item : mMenuItemMap.values()) {
                if (item.isVisible()) {
                    mMenuItems.add(item);
                }
            }
        }
        sortList(mMenuItems);
        mMenuAdapter.setData(mMenuItems);
    }

    public void setDefaultMenus() {
        MenuItemBean bean;
        for (int i = 0; i < EaseMessageMenuData.MENU_ITEM_IDS.length; i++) {
            bean = new MenuItemBean(0, EaseMessageMenuData.MENU_ITEM_IDS[i], (i + 1) * 10, mContext.getString(EaseMessageMenuData.MENU_TITLES[i]));
            bean.setResourceId(EaseMessageMenuData.MENU_ICONS[i]);
            addItemMenu(bean);
        }
    }

    private void sortList(List<MenuItemBean> menuItems) {
        Collections.sort(menuItems, new Comparator<MenuItemBean>() {
            @Override
            public int compare(MenuItemBean o1, MenuItemBean o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();
                if (order2 < order1) {
                    return 1;
                } else if (order1 == order2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

    public void addItemMenu(int groupId, int itemId, int order, String title) {
        MenuItemBean item = new MenuItemBean(groupId, itemId, order, title);
        if (!mMenuItems.contains(item)) {
            mMenuItems.add(item);
        }
    }

    public void addItemMenu(MenuItemBean item) {
        if (!mMenuItemMap.containsKey(item.getItemId())) {
            mMenuItemMap.put(item.getItemId(), item);
        }
    }


    public MenuItemBean findItem(int id) {
        if (mMenuItemMap.containsKey(id)) {
            return mMenuItemMap.get(id);
        }
        return null;
    }

    public void findItemVisible(int id, boolean visible) {
        if (mMenuItemMap.containsKey(id)) {
            mMenuItemMap.get(id).setVisible(visible);
        }
    }

    public void dismiss() {
        if (mPopupWindow == null) {
            throw new NullPointerException("please must init first!");
        }
        mPopupWindow.dismiss();
        if (mDismissListener != null) {
            mDismissListener.onDismiss(mPopupWindow);
        }
    }

    /**
     * 设置条目点击事件
     *
     * @param listener
     */
    public void setOnPopupReactionItemClickListener(EaseMessageMenuPopupWindow.OnPopupWindowItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 监听PopupMenu dismiss事件
     *
     * @param listener
     */
    public void setOnPopupMenuDismissListener(EaseMessageMenuPopupWindow.OnPopupWindowDismissListener listener) {
        this.mDismissListener = listener;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getView() {
        return mLayout;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * dip to px
     *
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    private static class ReactionAdapter extends EaseBaseRecyclerViewAdapter<ReactionItemBean> {
        private static Map<String, Boolean> mDataMap;

        public void setEmojiContainCurrentUserMap(Map<String, Boolean> emojiContainCurrentUserMap) {
            mDataMap = emojiContainCurrentUserMap;
        }

        @Override
        public ReactionViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ease_layout_item_reaction_popupwindow, parent, false);
            return new ReactionViewHolder(view);
        }

        private static class ReactionViewHolder extends ViewHolder<ReactionItemBean> {
            private View layout;
            private ImageView ivEmoticon;

            public ReactionViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                layout = findViewById(R.id.reaction_layout);
                ivEmoticon = findViewById(R.id.iv_emoticon);
            }

            @Override
            public void setData(ReactionItemBean item, int position) {
                if (null != item) {
                    ivEmoticon.setImageResource(item.getIcon());
                    boolean isAdded;
                    if (null == mDataMap || 0 == mDataMap.size() ||
                            null == mDataMap.get(item.getIdentityCode())) {
                        isAdded = false;
                    } else {
                        isAdded = mDataMap.get(item.getIdentityCode());
                    }
                    if (isAdded) {
                        layout.setBackgroundResource(R.drawable.ease_bg_message_menu_reaction_popupwindow);
                    } else {
                        layout.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    private static class MenuAdapter extends EaseBaseRecyclerViewAdapter<MenuItemBean> {
        @Override
        public MenuViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.ease_layout_item_message_menu_popupwindow, parent, false);
            return new MenuViewHolder(view, mContext);
        }

        private static class MenuViewHolder extends ViewHolder<MenuItemBean> {
            private ImageView ivActionIcon;
            private TextView tvActionName;
            private Context context;

            public MenuViewHolder(@NonNull View itemView, Context context) {
                super(itemView);
                this.context = context;
            }

            @Override
            public void initView(View itemView) {
                ivActionIcon = findViewById(R.id.iv_action_icon);
                tvActionName = findViewById(R.id.tv_action_name);
            }

            @Override
            public void setData(MenuItemBean item, int position) {
                String title = item.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    tvActionName.setText(title);
                    if (item.getItemId() == R.id.action_chat_recall) {
                        tvActionName.setTextColor(context.getResources().getColor(R.color.ease_message_unsend_menu_txt));
                    }
                }
                if (item.getResourceId() != 0) {
                    ivActionIcon.setImageResource(item.getResourceId());
                }
            }
        }
    }

    private static class ReactionSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public ReactionSpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = space;
            outRect.top = space;
        }
    }

    private static class MenuSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public MenuSpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = space;
            outRect.top = 0;

            // Add top margin only for the first item to avoid double space between items
            //parent.getChildAdapterPosition(view) == 0
        }
    }
}

