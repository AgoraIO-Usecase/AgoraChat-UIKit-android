package io.agora.chat.uikit.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import io.agora.chat.Presence;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.utils.EasePresenceUtil;


public class EasePresenceView extends ConstraintLayout {
    private EaseImageView ivAvatar;
    private EaseImageView ivPresence;
    private TextView tvName;
    private TextView tvPresence;

    public EasePresenceView(Context context) {
        this(context, null);
    }

    public EasePresenceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasePresenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.presence_view, this);
        ivAvatar = findViewById(R.id.iv_user_avatar);
        ivPresence = findViewById(R.id.iv_presence);
        tvName = findViewById(R.id.tv_name);
        tvPresence = findViewById(R.id.tv_presence);
        tvPresence.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPresenceClick(v);
                }
            }
        });
    }

    public void setPresenceData(String avatar, Presence presence) {
        if (!TextUtils.isEmpty(avatar)) {
            try {
                int resourceId = Integer.parseInt(avatar);
                Glide.with(this)
                        .load(resourceId)
                        .placeholder(R.drawable.ease_default_avatar)
                        .error(R.drawable.ease_default_avatar)
                        .into(ivAvatar);
            } catch (NumberFormatException e) {
                Glide.with(this)
                        .load(avatar)
                        .placeholder(R.drawable.ease_default_avatar)
                        .error(R.drawable.ease_default_avatar)
                        .into(ivAvatar);
            }
        }
        tvPresence.setText(EasePresenceUtil.getPresenceString(getContext(), presence));
        ivPresence.setImageResource(EasePresenceUtil.getPresenceIcon(getContext(), presence));
        tvName.setText(presence.getPublisher());
    }

    public interface OnPresenceClickListener {
        void onPresenceClick(View v);
    }

    private OnPresenceClickListener listener;

    public void setOnPresenceClickListener(OnPresenceClickListener listener) {
        this.listener = listener;
    }

    public void setPresenceTextViewArrowVisiable(boolean visiable) {
        if (visiable) {
            Drawable arrow = getResources().getDrawable(R.drawable.ease_presence_arrow_left);
            tvPresence.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
        } else {
            tvPresence.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public void setPresenTextViewColor(@ColorInt int color) {
        tvPresence.setTextColor(color);
    }

    public void setNameTextViewVisiablity(int visiable) {
        tvName.setVisibility(visiable);
    }

}
