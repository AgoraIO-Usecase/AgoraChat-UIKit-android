package io.agora.chat.uikit.conversation.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.EaseBaseRecyclerViewAdapter;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.conversation.model.EaseConversationSetStyle;
import io.agora.chat.uikit.utils.EaseUserUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseImageView;

public class EaseBaseConversationViewHolder extends EaseBaseRecyclerViewAdapter.ViewHolder<EaseConversationInfo> {
    public EaseConversationSetStyle setModel;
    public ConstraintLayout listIteaseLayout;
    public EaseImageView avatar;
    public TextView mUnreadMsgNumber;
    public TextView unreadMsgNumberRight;
    public View unreadMsgDot;
    public View unreadMsgDotRight;
    public TextView name;
    public TextView time;
    public ImageView mMsgState;
    public TextView mentioned;
    public TextView message;
    public ImageView msgMute;
    public ImageView ivTopLabel;
    public Context mContext;
    private Drawable bgDrawable;

    public EaseBaseConversationViewHolder(@NonNull View itemView, EaseConversationSetStyle style) {
        super(itemView);
        this.setModel = style;
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
        msgMute = findViewById(R.id.msg_mute);
        ivTopLabel = findViewById(R.id.iv_top_label);
        unreadMsgDot = findViewById(R.id.unread_msg_dot);
        unreadMsgDotRight = findViewById(R.id.unread_msg_dot_right);

    }

    @Override
    public void setData(EaseConversationInfo item, int position) {
        EaseUserUtils.setUserAvatarStyle(avatar);
        if(setModel != null) {
            float titleTextSize = setModel.getTitleTextSize();
            if(titleTextSize != 0) {
                name.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
            }
            int titleTextColor = setModel.getTitleTextColor();
            if(titleTextColor != 0) {
                name.setTextColor(titleTextColor);
            }
            float contentTextSize = setModel.getContentTextSize();
            if(contentTextSize != 0) {
                message.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
            }
            int contentTextColor = setModel.getContentTextColor();
            if(contentTextColor != 0) {
                message.setTextColor(contentTextColor);
            }
            float dateTextSize = setModel.getDateTextSize();
            if(dateTextSize != 0) {
                time.setTextSize(TypedValue.COMPLEX_UNIT_PX, dateTextSize);
            }
            int dateTextColor = setModel.getDateTextColor();
            if(dateTextColor != 0) {
                time.setTextColor(dateTextColor);
            }
            float mentionTextSize = setModel.getMentionTextSize();
            if(mentionTextSize != 0) {
                mentioned.setTextSize(TypedValue.COMPLEX_UNIT_PX, mentionTextSize);
            }
            int mentionTextColor = setModel.getMentionTextColor();
            if(mentionTextColor != 0) {
                mentioned.setTextColor(mentionTextColor);
            }
            float avatarSize = setModel.getAvatarSize();
            if(avatarSize != 0) {
                ViewGroup.LayoutParams layoutParams = avatar.getLayoutParams();
                layoutParams.height = (int) avatarSize;
                layoutParams.width = (int) avatarSize;
            }
            avatar.setShapeType(setModel.getShapeType());
            float borderWidth = setModel.getBorderWidth();
            if(borderWidth != 0) {
                avatar.setBorderWidth((int) borderWidth);
            }
            int borderColor = setModel.getBorderColor();
            if(borderColor != 0) {
                avatar.setBorderColor(borderColor);
            }
            float avatarRadius = setModel.getAvatarRadius();
            if(avatarRadius != 0) {
                avatar.setRadius((int) avatarRadius);
            }
            float itemHeight = setModel.getItemHeight();
            if(itemHeight != 0) {
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.height = (int) itemHeight;
            }
            Drawable bgDrawable = setModel.getBgDrawable();
            if(bgDrawable != null) {
                itemView.setBackground(bgDrawable);
            }
            mUnreadMsgNumber.setVisibility(setModel.isHideUnreadDot() ? View.GONE : View.VISIBLE);
            EaseConversationSetStyle.UnreadDotPosition dotPosition = setModel.getUnreadDotPosition();
            if(dotPosition == EaseConversationSetStyle.UnreadDotPosition.LEFT) {
                mUnreadMsgNumber.setVisibility(View.VISIBLE);
                unreadMsgNumberRight.setVisibility(View.GONE);
            }else {
                mUnreadMsgNumber.setVisibility(View.GONE);
                unreadMsgNumberRight.setVisibility(View.VISIBLE);
            }
            EaseConversationSetStyle.UnreadStyle style = setModel.getStyle();
            if(style == EaseConversationSetStyle.UnreadStyle.DOT) {
                if(dotPosition == EaseConversationSetStyle.UnreadDotPosition.LEFT) {
                    mUnreadMsgNumber.setVisibility(View.GONE);
                    unreadMsgDot.setVisibility(View.VISIBLE);
                }else {
                    unreadMsgNumberRight.setVisibility(View.GONE);
                    unreadMsgDotRight.setVisibility(View.VISIBLE);
                }
            }
        }
        bgDrawable = itemView.getBackground();


        if(item.isTop()) {
            ivTopLabel.setVisibility(View.VISIBLE);
        }else {
            ivTopLabel.setVisibility(View.GONE);
        }
        item.setOnSelectListener(new EaseConversationInfo.OnSelectListener() {
            @Override
            public void onSelect(boolean isSelected) {
                if(isSelected) {
                    itemView.setBackgroundResource(R.drawable.ease_conversation_item_selected);
                }else {
                    itemView.setBackground(bgDrawable);
                }
            }
        });
    }

    public void showUnreadNum(int unreadMsgCount) {
        mUnreadMsgNumber.setVisibility(View.GONE);
        unreadMsgNumberRight.setVisibility(View.GONE);
        unreadMsgDot.setVisibility(View.GONE);
        unreadMsgDotRight.setVisibility(View.GONE);
        if(unreadMsgCount > 0) {
            mUnreadMsgNumber.setText(handleBigNum(unreadMsgCount));
            unreadMsgNumberRight.setText(handleBigNum(unreadMsgCount));
            showUnreadRight(setModel.getUnreadDotPosition() == EaseConversationSetStyle.UnreadDotPosition.RIGHT);
        }
    }

    public String handleBigNum(int unreadMsgCount) {
        return EaseUtils.handleBigNum(unreadMsgCount);
    }

    public void showUnreadRight(boolean isRight) {
        if(isRight) {
            mUnreadMsgNumber.setVisibility(View.GONE);
            unreadMsgNumberRight.setVisibility(View.VISIBLE);
            if(setModel.getStyle() != EaseConversationSetStyle.UnreadStyle.NUM) {
                unreadMsgNumberRight.setVisibility(View.GONE);
                unreadMsgDotRight.setVisibility(View.VISIBLE);
            }
        }else {
            mUnreadMsgNumber.setVisibility(View.VISIBLE);
            unreadMsgNumberRight.setVisibility(View.GONE);
            if(setModel.getStyle() != EaseConversationSetStyle.UnreadStyle.NUM) {
                mUnreadMsgNumber.setVisibility(View.GONE);
                unreadMsgDot.setVisibility(View.VISIBLE);
            }
        }
    }
}
