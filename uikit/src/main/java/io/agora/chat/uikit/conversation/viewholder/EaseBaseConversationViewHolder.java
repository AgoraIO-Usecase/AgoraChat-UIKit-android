package io.agora.chat.uikit.conversation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.widget.EaseImageView;

public class EaseBaseConversationViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EaseConversationInfo> {
    public ConstraintLayout listIteaseLayout;
    public EaseImageView avatar;
    public TextView mUnreadMsgNumber;
    public TextView unreadMsgNumberRight;
    public TextView name;
    public TextView time;
    public ImageView mMsgState;
    public TextView mentioned;
    public TextView message;
    public Context mContext;
    private Drawable bgDrawable;

    public EaseBaseConversationViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initView(View itemView) {
        mContext = itemView.getContext();
        listIteaseLayout = findViewById(R.id.list_itease_layout);
        avatar = findViewById(R.id.avatar);
        mUnreadMsgNumber = findViewById(R.id.unread_msg_number);
        unreadMsgNumberRight = findViewById(R.id.unread_msg_number_right);
        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        mMsgState = findViewById(R.id.msg_state);
        mentioned = findViewById(R.id.mentioned);
        message = findViewById(R.id.message);
        EaseUserUtils.setUserAvatarStyle(avatar);
        bgDrawable = itemView.getBackground();
    }

    @Override
    public void setData(EaseConversationInfo item, int position) {
        item.setOnSelectListener(new EaseConversationInfo.OnSelectListener() {
            @Override
            public void onSelect(boolean isSelected) {
                if(isSelected) {
                    itemView.setBackgroundResource(R.drawable.ease_conversation_item_selected);
                }else {
                    if(item.isTop()) {
                        itemView.setBackgroundResource(R.drawable.ease_conversation_top_bg);
                    }else {
                        itemView.setBackground(bgDrawable);
                    }
                }
            }
        });
    }

    public void showUnreadNum(int unreadMsgCount) {
        if(unreadMsgCount > 0) {
            mUnreadMsgNumber.setText(handleBigNum(unreadMsgCount));
            unreadMsgNumberRight.setText(handleBigNum(unreadMsgCount));
            showUnreadRight(true);
        }else {
            mUnreadMsgNumber.setVisibility(View.GONE);
            unreadMsgNumberRight.setVisibility(View.GONE);
        }
    }

    public String handleBigNum(int unreadMsgCount) {
        if(unreadMsgCount <= 99) {
            return String.valueOf(unreadMsgCount);
        }else {
            return "99+";
        }
    }

    public void showUnreadRight(boolean isRight) {
        if(isRight) {
            mUnreadMsgNumber.setVisibility(View.GONE);
            unreadMsgNumberRight.setVisibility(View.VISIBLE);
        }else {
            mUnreadMsgNumber.setVisibility(View.VISIBLE);
            unreadMsgNumberRight.setVisibility(View.GONE);
        }
    }
}
