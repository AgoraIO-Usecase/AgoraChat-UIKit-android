package io.agora.chat.uikit.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.utils.EaseUtils;

public class EasePopupWindowHelper {
    private static final int[] itemIds = {R.id.action_chat_copy, R.id.action_chat_delete, R.id.action_chat_recall};
    private static final int[] titles = {R.string.ease_action_copy, R.string.ease_action_delete, R.string.ease_action_recall};
    private static final int[] icons = {R.drawable.ease_chat_item_menu_copy, R.drawable.ease_chat_item_menu_delete, R.drawable.ease_chat_item_menu_recall};
    private static final int SPAN_COUNT = 5;
    private static float screenBgAlpha = 0.3f;
    private static float popupWindowBgAlpha = 0.8f;
    private EasePopupWindow pMenu;
    private List<MenuItemBean> menuItems = new ArrayList<>();
    private Map<Integer, MenuItemBean> menuItemMap = new HashMap<>();
    private TextView tvTitle;
    private RecyclerView rvMenuList;
    private Context context;
    private MenuAdapter adapter;
    private EasePopupWindow.OnPopupWindowItemClickListener itemClickListener;
    private EasePopupWindow.OnPopupWindowDismissListener dismissListener;
    private boolean touchable;
    private Drawable background;
    private View layout;
    private EasePopupWindow.Style menuStyle = EasePopupWindow.Style.BOTTOM_SCREEN;
    private boolean itemMenuIconVisible = true;
    private RelativeLayout rvTop;

    public EasePopupWindowHelper() {
        if(pMenu != null) {
            pMenu.dismiss();
        }
        menuItems.clear();
        menuItemMap.clear();
    }

    /**
     * @param context
     */
    public void initMenu(@NonNull Context context) {
        this.context = context;
        boolean closeChangeBg = true;
        if(menuStyle != EasePopupWindow.Style.ATTACH_ITEM_VIEW) {
            closeChangeBg = false;
        }
        pMenu = new EasePopupWindow(context, closeChangeBg);
        layout = LayoutInflater.from(context).inflate(R.layout.ease_layout_menu_popupwindow, null);
        pMenu.setContentView(layout);
        tvTitle = layout.findViewById(R.id.tv_title);
        rvMenuList = layout.findViewById(R.id.rv_menu_list);
        rvTop = layout.findViewById(R.id.rl_top);
        adapter = new MenuAdapter();
        rvMenuList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                if(itemClickListener != null) {
                    itemClickListener.onMenuItemClick(adapter.getItem(position));
                }
            }
        });
        // Only bottom screen style will show expand icon
        if(menuStyle == EasePopupWindow.Style.BOTTOM_SCREEN) {
            View expandIcon = layout.findViewById(R.id.expand_icon);
            expandIcon.setVisibility(View.VISIBLE);
            expandIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    dismiss();
                    return true;
                }
            });
        }
    }

    public void clear() {
        menuItems.clear();
        menuItemMap.clear();
    }

    public void setDefaultMenus() {
        MenuItemBean bean;
        for(int i = 0; i < itemIds.length; i++) {
            bean = new MenuItemBean(0, itemIds[i], (i+1)*10, context.getString(titles[i]));
            bean.setResourceId(icons[i]);
            addItemMenu(bean);
        }
    }

    public void addItemMenu(MenuItemBean item) {
        if(!menuItemMap.containsKey(item.getItemId())) {
            menuItemMap.put(item.getItemId(), item);
        }
    }

    public void addItemMenu(int groupId, int itemId, int order, String title) {
        MenuItemBean item = new MenuItemBean(groupId, itemId, order, title);
        addItemMenu(item);
    }

    public MenuItemBean findItem(int id) {
        if(menuItemMap.containsKey(id)) {
            return menuItemMap.get(id);
        }
        return null;
    }

    public void findItemVisible(int id, boolean visible) {
        if(menuItemMap.containsKey(id)) {
            menuItemMap.get(id).setVisible(visible);
        }
    }

    public void setOutsideTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    private void showPre() {
        pMenu.setOutsideTouchable(touchable);
        pMenu.setBackgroundDrawable(background);
        checkIfShowItems();
        sortList(menuItems);
        adapter.setData(menuItems);
    }

    private void sortList(List<MenuItemBean> menuItems) {
        Collections.sort(menuItems, new Comparator<MenuItemBean>() {
            @Override
            public int compare(MenuItemBean o1, MenuItemBean o2) {
                int order1 = o1.getOrder();
                int order2 = o2.getOrder();
                if(order2 < order1) {
                    return 1;
                }else if(order1 == order2) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    private void checkIfShowItems() {
        if(menuItemMap.size() > 0) {
            menuItems.clear();
            Iterator<MenuItemBean> iterator = menuItemMap.values().iterator();
            while (iterator.hasNext()) {
                MenuItemBean item = iterator.next();
                if(item.isVisible()) {
                    menuItems.add(item);
                }
            }
        }
    }

    public void showTitle(@NonNull String title) {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setMenuStyle(EasePopupWindow.Style style) {
        this.menuStyle = style;
    }

    public void setRlTopLayout(View view) {
        rvTop.removeAllViews();
        rvTop.addView(view);
    }

    public void show(View parent, View v) {
        show(parent, v, false);
    }
    
    public void show(View parent, View v, boolean isTop) {
        showPre();
        if(menuItems.size() <= 0) {
            Log.e("EasePopupWindowHelper", "Span count should be at least 1. Provided " + menuItems.size());
            return;
        }
        if(menuStyle == EasePopupWindow.Style.ATTACH_ITEM_VIEW) {
            showAttachItemViewStyle(parent, v, isTop);
        }else if(menuStyle == EasePopupWindow.Style.BOTTOM_SCREEN) {
            showBottomToScreen(parent, v);
        }else {
            showCenterToScreen(parent, v);
        }

    }

    private void showCenterToScreen(View parent, View v) {
        // Set screen's alpha
        pMenu.setBackgroundAlpha(screenBgAlpha);
        // Set popup window's background alpha
        getView().setAlpha(popupWindowBgAlpha);
        rvMenuList.setLayoutManager(new LinearLayoutManager(context));
        pMenu.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void showBottomToScreen(View parent, View v) {
        // Set screen's alpha
        pMenu.setBackgroundAlpha(screenBgAlpha);
        // Set popup window's background alpha
        getView().setAlpha(popupWindowBgAlpha);
        rvMenuList.setLayoutManager(new LinearLayoutManager(context));
        pMenu.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        ViewGroup.LayoutParams layoutParams = getView().getLayoutParams();
        layoutParams.width = (int) EaseUtils.getScreenInfo(context)[0];
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private void showAttachItemViewStyle(View parent, View v, boolean isTop) {
        if(menuItems.size() < SPAN_COUNT) {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, menuItems.size(), RecyclerView.VERTICAL, false));
        }else {
            rvMenuList.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT, RecyclerView.VERTICAL, false));
        }
        getView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = getView().getMeasuredWidth();
        int popupHeight = getView().getMeasuredHeight();

        //Gets the coordinates attached to the view
        int[] location = new int[2];
        v.getLocationInWindow(location);

        //Gets the coordinates of the parent layout
        int[] location2 = new int[2];
        parent.getLocationInWindow(location2);

        //Sets the spacing between attached views
        int margin = (int) EaseUtils.dip2px(context, 5);

        float[] screenInfo = EaseUtils.getScreenInfo(context);

        int yOffset = 0;
        if(isTop) {
            if(location[1] - popupHeight - margin < location2[1]) {
                yOffset = location[1] + v.getHeight() + margin;
            }else {
                yOffset = location[1] - popupHeight - margin;
            }
        }else {
            if(location[1] + v.getHeight() + popupHeight + margin > screenInfo[1]) {
                yOffset = location[1] - popupHeight - margin;
            }else {
                yOffset = location[1] + v.getHeight() + margin;
            }
        }

        int xOffset = 0;
        if(location[0] + v.getWidth() / 2 + popupWidth / 2 + EaseUtils.dip2px(context, 10) > parent.getWidth()) {
            xOffset = (int) (parent.getWidth() - EaseUtils.dip2px(context, 10) - popupWidth);
        }else {
            xOffset = location[0] + v.getWidth() / 2 - popupWidth / 2;
        }
        // Add left judgment
        if(xOffset < EaseUtils.dip2px(context, 10)) {
            xOffset = (int) EaseUtils.dip2px(context, 10);
        }
        pMenu.showAtLocation(v, Gravity.NO_GRAVITY, xOffset, yOffset);
    }

    public void dismiss() {
        if(pMenu == null) {
            throw new NullPointerException("please must init first!");
        }
        pMenu.dismiss();
        if(dismissListener != null) {
            dismissListener.onDismiss(pMenu);
        }
    }


    /**
     * Set item click listener
     * @param listener
     */
    public void setOnPopupMenuItemClickListener(EasePopupWindow.OnPopupWindowItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Listener the dismiss event
     * @param listener
     */
    public void setOnPopupMenuDismissListener(EasePopupWindow.OnPopupWindowDismissListener listener) {
        this.dismissListener = listener;
    }

    public PopupWindow getPopupWindow() {
        return pMenu;
    }

    public View getView() {
        return layout;
    }

    public void setItemMenuIconVisible(boolean visible) {
        this.itemMenuIconVisible = visible;
    }

    private class MenuAdapter extends EaseBaseRecyclerViewAdapter<MenuItemBean> {

        @Override
        public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(menuStyle == EasePopupWindow.Style.ATTACH_ITEM_VIEW ?
                    R.layout.ease_layout_item_menu_popupwindow :
                    R.layout.ease_layout_item_menu_popupwindow_horizontal, parent, false);
            return new MenuViewHolder(view);
        }

        private class MenuViewHolder extends ViewHolder<MenuItemBean> {
            private ImageView ivActionIcon;
            private TextView tvActionName;

            public MenuViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            public void initView(View itemView) {
                ivActionIcon = findViewById(R.id.iv_action_icon);
                tvActionName = findViewById(R.id.tv_action_name);
            }

            @Override
            public void setData(MenuItemBean item, int position) {
                String title = item.getTitle();
                if(!TextUtils.isEmpty(title)) {
                    tvActionName.setText(title);
                }
                if(item.getResourceId() != 0 && itemMenuIconVisible) {
                    ivActionIcon.setVisibility(View.VISIBLE);
                    ivActionIcon.setImageResource(item.getResourceId());
                }else {
                    ivActionIcon.setVisibility(View.GONE);
                }
            }
        }
    }
}

