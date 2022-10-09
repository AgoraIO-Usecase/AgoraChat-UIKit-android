package io.agora.chat.uikit.chat.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.adapter.EaseChatExtendMenuAdapter;
import io.agora.chat.uikit.chat.interfaces.EaseChatExtendMenuItemClickListener;
import io.agora.chat.uikit.chat.interfaces.IChatExtendMenu;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.widget.dialog.EaseAlertDialog;
import io.agora.chat.uikit.chat.widget.EaseChatExtendMenu.ChatMenuItemModel;

public class EaseChatExtendMenuDialog extends EaseAlertDialog implements IChatExtendMenu, OnItemClickListener {
    private List<ChatMenuItemModel> itemModels = new ArrayList<ChatMenuItemModel>();
    private Map<Integer, ChatMenuItemModel> itemMap = new HashMap();
    private EaseChatExtendMenuAdapter adapter;
    private EaseChatExtendMenuItemClickListener itemListener;

    private int[] itemStrings = { R.string.ease_attach_take_pic, R.string.ease_attach_picture,R.string.ease_attach_video, R.string.ease_attach_file};
    private int[] itemdrawables = { R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
            R.drawable.em_chat_video_selector,R.drawable.em_chat_file_selector};
    private int[] itemIds = { R.id.extend_item_take_picture, R.id.extend_item_picture, R.id.extend_item_video, R.id.extend_item_file};
    private RecyclerView rvExtendMenu;
    private Button btnCancel;

    public EaseChatExtendMenuDialog(@NonNull Context context) {
        super(context);
    }

    public EaseChatExtendMenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        setDialogAttrs();
    }

    private void setDialogAttrs() {
        try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ease_dialog_chat_extend_menu, null);
        setContentView(view);
        rvExtendMenu = view.findViewById(R.id.rv_extend_menu_dialog);
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvExtendMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExtendMenu.setHasFixedSize(true);

        ConcatAdapter concatAdapter = new ConcatAdapter();
        adapter = new EaseChatExtendMenuAdapter(true);
        concatAdapter.addAdapter(adapter);
        rvExtendMenu.setAdapter(concatAdapter);

        addDefaultData();

        adapter.setData(itemModels);

        adapter.setOnItemClickListener(this);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void addDefaultData() {
        for(int i = 0; i < itemStrings.length; i++) {
            registerMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], null);
        }
    }

    @Override
    public void clear() {
        itemModels.clear();
        itemMap.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setMenuOrder(int itemId, int order) {
        if(itemMap.containsKey(itemId)) {
            ChatMenuItemModel model = itemMap.get(itemId);
            if(model != null) {
                model.order = order;
                sortByOrder(itemModels);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void registerMenuItem(String name, int drawableRes, int itemId) {
        registerMenuItem(name, drawableRes, itemId, null);
    }

    @Override
    public void registerMenuItem(String name, int drawableRes, int itemId, int order) {
        registerMenuItem(name, drawableRes, itemId, order, null);
    }

    @Override
    public void registerMenuItem(int nameRes, int drawableRes, int itemId) {
        registerMenuItem(nameRes, drawableRes, itemId, null);
    }

    @Override
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, int order) {
        registerMenuItem(nameRes, drawableRes, itemId, order, null);
    }

    @Override
    public void setEaseChatExtendMenuItemClickListener(EaseChatExtendMenuItemClickListener listener) {
        this.itemListener = listener;
    }

    /**
     * register menu item
     *
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerMenuItem(String name, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        if(!itemMap.containsKey(itemId)) {
            ChatMenuItemModel item = new ChatMenuItemModel();
            item.name = name;
            item.image = drawableRes;
            item.id = itemId;
            item.clickListener = listener;
            itemMap.put(itemId, item);
            itemModels.add(item);
            adapter.notifyItemInserted(itemModels.size() - 1);
        }
    }

    /**
     * register menu item
     *
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param order
     *             order by
     * @param listener
     *            on click event of item
     */
    public void registerMenuItem(String name, int drawableRes, int itemId, int order, EaseChatExtendMenuItemClickListener listener) {
        if(!itemMap.containsKey(itemId)) {
            ChatMenuItemModel item = new ChatMenuItemModel();
            item.name = name;
            item.image = drawableRes;
            item.id = itemId;
            item.order = order;
            item.clickListener = listener;
            itemMap.put(itemId, item);
            itemModels.add(item);
            sortByOrder(itemModels);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * register menu item
     *
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *             on click event of item
     */
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, EaseChatExtendMenuItemClickListener listener) {
        registerMenuItem(getContext().getString(nameRes), drawableRes, itemId, listener);
    }

    /**
     * register menu item
     *
     * @param nameRes
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param order
     *             order by
     * @param listener
     *             on click event of item
     */
    public void registerMenuItem(int nameRes, int drawableRes, int itemId, int order, EaseChatExtendMenuItemClickListener listener) {
        registerMenuItem(getContext().getString(nameRes), drawableRes, itemId, order, listener);
    }

    private void sortByOrder(List<ChatMenuItemModel> itemModels) {
        Collections.sort(itemModels, new Comparator<ChatMenuItemModel>() {
            @Override
            public int compare(ChatMenuItemModel o1, ChatMenuItemModel o2) {
                int val = o1.order - o2.order;
                if(val > 0) {
                    return 1;
                }else if(val == 0) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        dismiss();
        ChatMenuItemModel itemModel = itemModels.get(position);
        if(itemListener != null) {
            itemListener.onChatExtendMenuItemClick(itemModel.id, view);
        }
    }
}
