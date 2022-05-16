package io.agora.chat.uikit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;


public class ReactionGridAdapter extends EaseBaseRecyclerViewAdapter<EaseReactionEmojiconEntity> {

    @Override
    public ReactionViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ease_row_reaction, parent, false);
        return new ReactionViewHolder(view);
    }

    private static class ReactionViewHolder extends ViewHolder<EaseReactionEmojiconEntity> {
        private ImageView emojicon;

        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void initView(View itemView) {
            emojicon = findViewById(R.id.iv_emojicon);
        }

        @Override
        public void setData(EaseReactionEmojiconEntity item, int position) {
            if (0 != item.getEmojicon().getIcon()) {
                emojicon.setImageResource(item.getEmojicon().getIcon());
            }
        }
    }
}
