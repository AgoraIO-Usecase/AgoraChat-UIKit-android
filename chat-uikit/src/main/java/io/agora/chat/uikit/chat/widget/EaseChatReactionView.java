package io.agora.chat.uikit.chat.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.agora.chat.ChatMessage;
import io.agora.chat.MessageReaction;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.adapter.ReactionGridAdapter;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.interfaces.OnItemLongClickListener;
import io.agora.chat.uikit.menu.EaseMessageReactionHelper;
import io.agora.chat.uikit.menu.EasePopupWindow;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseMessageMenuData;
import io.agora.chat.uikit.models.EaseReactionEmojiconEntity;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseRecyclerView;
import io.agora.chat.uikit.widget.chatextend.RecyclerViewFlowLayoutManager;
import io.agora.util.EMLog;

public class EaseChatReactionView extends LinearLayout implements OnItemClickListener, OnItemLongClickListener {
    private final static String TAG = EaseChatReactionView.class.getSimpleName();
    private final ReactionGridAdapter mListAdapter;
    private List<EaseReactionEmojiconEntity> mData;
    private OnReactionItemListener mListener;
    private static final int MAX_REACTION_SHOW = 5;
    private String mMsgId;
    private final TextView countTv;
    private EaseMessageReactionHelper mReactionHelper;
    private final Context mContext;
    private final ConstraintLayout mLayout;

    public EaseChatReactionView(Context context) {
        this(context, null);
    }

    public EaseChatReactionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public EaseChatReactionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EaseChatReactionView);
        boolean isSender = typedArray.getBoolean(R.styleable.EaseChatReactionView_ease_chat_item_sender, false);
        typedArray.recycle();

        if (isSender) {
            LayoutInflater.from(context).inflate(R.layout.ease_widget_reaction_sender_layout, this);
        } else {
            LayoutInflater.from(context).inflate(R.layout.ease_widget_reaction_received_layout, this);
        }

        mLayout = findViewById(R.id.ll_reaction_container);

        EaseRecyclerView reactionList = findViewById(R.id.rv_list);
        countTv = findViewById(R.id.count);

        RecyclerViewFlowLayoutManager ms = new RecyclerViewFlowLayoutManager();
        reactionList.setLayoutManager(ms);

        mListAdapter = new ReactionGridAdapter();
        mListAdapter.setOnItemLongClickListener(this);
        mListAdapter.setOnItemClickListener(this);
        reactionList.setAdapter(mListAdapter);
        reactionList.addItemDecoration(new ReactionSpacesItemDecoration((int) EaseUtils.dip2px(context, 5)));

        reactionList.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showMessageReaction();
                }
                return true;
            }
        });
        countTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageReaction();
            }
        });


        reactionList.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // true: consume touch event
                // false: dispatch touch event
                return true;
            }
        });
    }


    private void showMessageReaction() {
        if (null != mReactionHelper) {
            mReactionHelper.dismiss();
        }
        mReactionHelper = new EaseMessageReactionHelper();
        mReactionHelper.setReactionData(mData, mMsgId);
        mReactionHelper.init(getContext(), new OnReactionItemListener() {
            @Override
            public void removeReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (null != mListener) {
                    mListener.removeReaction(reactionEntity);
                }
            }

            @Override
            public void addReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (null != mListener) {
                    mListener.addReaction(reactionEntity);
                }
            }
        });
        mReactionHelper.setOutsideTouchable(true);
        mReactionHelper.setOnPopupMenuDismissListener(new EasePopupWindow.OnPopupWindowDismissListener() {
            @Override
            public void onDismiss(PopupWindow menu) {
                mReactionHelper = null;
            }
        });
        mReactionHelper.show(this, this);
    }

    public void updateMessageInfo(ChatMessage message) {
        if (null == message) {
            EMLog.e(TAG, "message is null, don't setup reaction view");
            return;
        }
        //default gone
        this.setVisibility(GONE);

        List<MessageReaction> messageReactions = message.getMessageReaction();
        if (null != messageReactions && messageReactions.size() > 0) {
            List<EaseReactionEmojiconEntity> list = new ArrayList<>(messageReactions.size());
            EaseReactionEmojiconEntity entity;
            EaseEmojicon emojicon;
            for (MessageReaction messageReaction : messageReactions) {
                entity = new EaseReactionEmojiconEntity();
                emojicon = EaseMessageMenuData.getReactionDataMap().get(messageReaction.getReaction());
                if (emojicon != null) {
                    entity.setEmojicon(emojicon);
                    entity.setCount(messageReaction.getUserCount());
                    entity.setUserList(messageReaction.getUserList());
                    entity.setAddedBySelf(messageReaction.isAddedBySelf());
                    list.add(entity);
                }
            }
            if (0 != list.size()) {
                this.setVisibility(VISIBLE);
                updateData(list, message.getMsgId());
            }
        }
    }

    private void updateData(List<EaseReactionEmojiconEntity> data, String msgId) {
        mData = new ArrayList<>(data);
        mMsgId = msgId;

        int totalNumber = 0;
        for (EaseReactionEmojiconEntity entity : mData) {
            totalNumber += entity.getCount();
        }

        countTv.setVisibility(VISIBLE);
        if (mData.size() > MAX_REACTION_SHOW) {
            List<EaseReactionEmojiconEntity> dataList = new ArrayList<>(MAX_REACTION_SHOW - 1);
            for (int i = 0; i < MAX_REACTION_SHOW - 1; i++) {
                dataList.add(mData.get(i));
            }
            mListAdapter.setData(dataList);

            if (totalNumber > 99) {
                countTv.setText(mContext.getResources().getString(R.string.ease_number_point_ninety_nine_more));
            } else {
                countTv.setText(mContext.getString(R.string.ease_number_more, totalNumber));
            }
        } else {
            mListAdapter.setData(mData);
            if (totalNumber > 99) {
                countTv.setText(mContext.getResources().getString(R.string.ease_number_ninety_nine_more));
            } else {
                if (1 == totalNumber) {
                    countTv.setVisibility(GONE);
                } else {
                    countTv.setText(String.valueOf(totalNumber));
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }

    public void setOnReactionItemListener(OnReactionItemListener onReactionItemListener) {
        mListener = onReactionItemListener;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public interface OnReactionItemListener {
        void removeReaction(EaseReactionEmojiconEntity reactionEntity);

        void addReaction(EaseReactionEmojiconEntity reactionEntity);
    }

    private static class ReactionSpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public ReactionSpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   RecyclerView parent, @NonNull RecyclerView.State state) {
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0;
            } else {
                outRect.left = space;
            }
        }
    }
}

