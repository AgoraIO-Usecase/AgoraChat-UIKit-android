package io.agora.chat.uikit.chat.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseChatExtendMenuAdapter;
import io.agora.chat.uikit.chat.widget.EaseChatExtendMenu;
import io.agora.chat.uikit.chat.widget.EaseChatExtendMenuDialog;
import io.agora.chat.uikit.interfaces.OnItemClickListener;


public class EaseChatExtendMenuAdapter extends EaseBaseChatExtendMenuAdapter<EaseChatExtendMenuAdapter.ViewHolder, EaseChatExtendMenu.ChatMenuItemModel> {
    private boolean isHorizontal;
    private OnItemClickListener itemListener;
    
    public EaseChatExtendMenuAdapter() {}
    
    public EaseChatExtendMenuAdapter(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    @Override
    protected int getItemLayoutId() {
        return isHorizontal? R.layout.ease_chat_menu_item_horizontal : R.layout.ease_chat_menu_item;
    }

    @Override
    protected ViewHolder easeCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EaseChatExtendMenu.ChatMenuItemModel item = mData.get(position);
        holder.imageView.setBackgroundResource(item.image);
        holder.textView.setText(item.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(item.clickListener != null){
                    item.clickListener.onChatExtendMenuItemClick(item.id, v);
                }
                if(itemListener != null) {
                    itemListener.onItemClick(v, position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}

