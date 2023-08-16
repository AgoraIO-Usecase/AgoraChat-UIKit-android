package io.agora.chat.uikit.chat.widget;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class EaseCustomLayoutManager extends LinearLayoutManager {
    private boolean canChangeStackFromEndStatus = false;
    private int childCount;
    private boolean isNeedStackFromEnd = false;
    public EaseCustomLayoutManager(Context context) {
        super(context);
    }

    public EaseCustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    /**
     * Whether need to set stack from end
     * @param isStackFromEnd
     */
    public void setIsStackFromEnd(boolean isStackFromEnd) {
        isNeedStackFromEnd = isStackFromEnd;
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        if(!isNeedStackFromEnd) {
            return;
        }
        int itemCount = getItemCount();
        if(childCount == 0) {
            childCount = itemCount;
        }
        if(itemCount != childCount) {
            if(itemCount < childCount) {
                canChangeStackFromEndStatus = true;
            }
            childCount = itemCount;
        }else {
            canChangeStackFromEndStatus = false;
        }
        int totalHeight = 0;
        for(int i = 0; i < itemCount; i++) {
            View subView = findViewByPosition(i);
            if(subView != null) {
                int measuredHeight = subView.getMeasuredHeight();
                int paddingBottom = subView.getPaddingBottom();
                int paddingTop = subView.getPaddingTop();
                int itemHeight = measuredHeight + paddingBottom + paddingTop;
                totalHeight += itemHeight;
                // Not add the marginTop and marginBottom
            }
        }
        if(totalHeight == 0 || getHeight() == 0) {
            if(itemCount >= 10) {
                if(!getStackFromEnd()) {
                    setStackFromEnd(true);
                }
            }else {
                setStackFromEnd(false);
            }
            return;
        }
        if(totalHeight < getHeight()) {
            if(canChangeStackFromEndStatus) {
                setStackFromEnd(false);
            }
        }else if(!getStackFromEnd()) {
            setStackFromEnd(true);
        }
    }
}
