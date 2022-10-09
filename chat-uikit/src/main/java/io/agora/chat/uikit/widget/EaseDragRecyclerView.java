package io.agora.chat.uikit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class EaseDragRecyclerView extends RecyclerView {
    /**
     * Use dy to determine the scroll direction
     */
    private float dy, preY;
    private OnRefreshListener refreshListener;
    private OnLoadMoreListener loadMoreListener;
    private OnDraggingListener draggingListener;
    private boolean canRefresh = true;
    private boolean canLoadMore = true;
    private Status status = Status.IDLE;

    public EaseDragRecyclerView(@NonNull Context context) {
        super(context);
    }

    public EaseDragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseDragRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(canLoadMore &&
                            dy < -dip2px(getContext(), 30) &&
                            !canScrollVertically(1)) {
                        if(loadMoreListener != null) {
                            // If is refreshing, not load more; If is loading, not load more again
                            if(status == Status.IDLE) {
                                status = Status.IS_LOADING;
                                setLoadMoreAnimator();
                            }
                        }
                    }
                    if(canRefresh &&
                            dy > dip2px(getContext(), 30) &&
                            !canScrollVertically(-1)) {
                        if(refreshListener != null) {
                            if(status == Status.IDLE) {
                                status = Status.IS_REFRESHING;
                                setRefreshAnimator();
                            }
                        }
                    }
                }else {
                    if(draggingListener != null) {
                        draggingListener.onDragging();
                    }
                }
            }
        });
    }

    private void setRefreshAnimator() {
        refreshListener.onRefresh();
    }

    private void setLoadMoreAnimator() {
        loadMoreListener.onLoadMore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN :
                preY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                dy = e.getY() - preY;
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    public void finishRefresh() {
        status = Status.IDLE;
    }

    public void finishLoadMore() {
        status = Status.IDLE;
    }

    public void enableRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public void enableLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.refreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnDraggingListener(OnDraggingListener listener) {
        this.draggingListener = listener;
    }

    public interface OnDraggingListener {
        void onDragging();
    }

    public enum Status {
        IS_LOADING, IS_REFRESHING, IDLE
    }

    /**
     * dip to px
     * @param context
     * @param value
     * @return
     */
    public static float dip2px(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
