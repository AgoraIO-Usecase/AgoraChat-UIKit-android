package io.agora.chat.uikit.chat.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.chat.viewholder.EaseChatRowViewHolder;
import io.agora.chat.uikit.chat.viewholder.EaseMessageViewType;
import io.agora.chat.uikit.chat.viewholder.EaseChatViewHolderFactory;
import io.agora.chat.uikit.interfaces.OnMessageListItemClickListener;
import io.agora.chat.uikit.interfaces.MessageResultCallback;
import io.agora.chat.uikit.widget.chatrow.EaseChatRow;

public class EaseMessageAdapter extends EaseBaseRecyclerViewAdapter<ChatMessage> {
    protected OnMessageListItemClickListener listener;
    private MessageResultCallback messageResultCallback;
    private int highlightPosition = -1;

    public EaseMessageAdapter() {}
    
    @Override
    public int getItemNotEmptyViewType(int position) {
        return EaseChatViewHolderFactory.getViewType(mData.get(position));
    }

    @Override
    public ViewHolder<ChatMessage> getViewHolder(ViewGroup parent, int viewType) {
        return EaseChatViewHolderFactory.createViewHolder(parent, EaseMessageViewType.from(viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof EaseChatRowViewHolder && holder.itemView instanceof EaseChatRow) {
            EaseChatRow chatRow = (EaseChatRow) holder.itemView;
            chatRow.setOnMessageListItemClickListener(listener);
            chatRow.setMessageResultCallback(messageResultCallback);
        }

        if(position == highlightPosition) {
            View outLayout = holder.itemView.findViewById(R.id.cl_bubble_out);
            if(outLayout != null) {
                startAnimator(outLayout);
            }else {
                startAnimator(holder.itemView);
            }

            highlightPosition = -1;
        }
    }

    /**
     * Set item click listener.
     * @param listener
     */
    public void setOnMessageListItemClickListener(OnMessageListItemClickListener listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }

    /**
     * Set message result callback.
     * @param callback
     */
    public void setOnMessageResultCallback(MessageResultCallback callback) {
        this.messageResultCallback = callback;
        notifyDataSetChanged();
    }

    private void startAnimator(View view) {
        Drawable background = view.getBackground();
        int darkColor = ContextCompat.getColor(mContext, R.color.ease_chat_item_bg_dark);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.TRANSPARENT, darkColor);
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int)animator.getAnimatedValue());
                if((int)animator.getAnimatedValue() == darkColor) {
                    view.setBackground(background);
                }else if((int)animator.getAnimatedValue() == 0) {
                    view.setBackground(null);
                }
            }
        });
        colorAnimation.start();
    }

    /**
     * Highlight the item view.
     * @param position
     */
    public void highlightItem(int position) {
        this.highlightPosition = position;
        notifyItemChanged(position);
    }
}
