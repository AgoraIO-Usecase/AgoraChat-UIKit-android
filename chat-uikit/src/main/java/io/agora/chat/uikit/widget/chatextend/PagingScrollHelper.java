package io.agora.chat.uikit.widget.chatextend;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecycleView paging rolling tool class
 * Refer to：https://blog.csdn.net/Y_sunny_U/article/details/89500464
 */
public class PagingScrollHelper {
    RecyclerView mRecyclerView = null;
    private PageOnScrollListener mOnScrollListener = new PageOnScrollListener();
    private PageOnFlingListener mOnFlingListener = new PageOnFlingListener();
    private int offsetY = 0;
    private int offsetX = 0;
    int startY = 0;
    int startX = 0;
    ValueAnimator mAnimator = null;
    private MyOnTouchListener mOnTouchListener = new MyOnTouchListener();
    private boolean firstTouch = true;
    private onPageChangeListener mOnPageChangeListener;
    private int lastPageIndex;
    private ORIENTATION mOrientation = ORIENTATION.HORIZONTAL;
    private int currentPosition;

    enum ORIENTATION {
        HORIZONTAL, VERTICAL, NULL
    }

    public void setUpRecycleView(RecyclerView recycleView) {
        if (recycleView == null) {
            throw new IllegalArgumentException("recycleView must be not null");
        }
        mRecyclerView = recycleView;
        recycleView.setOnFlingListener(mOnFlingListener);
        recycleView.addOnScrollListener(mOnScrollListener);
        recycleView.setOnTouchListener(mOnTouchListener);
        updateLayoutManger();
    }
 
    public void updateLayoutManger() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager.canScrollVertically()) {
                mOrientation = ORIENTATION.VERTICAL;
            } else if (layoutManager.canScrollHorizontally()) {
                mOrientation = ORIENTATION.HORIZONTAL;
            } else {
                mOrientation = ORIENTATION.NULL;
            }
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = 0;
            startY = 0;
            offsetX = 0;
            offsetY = 0;
        }
    }
 
    public int getPageCount() {
        if (mRecyclerView != null) {
            if (mOrientation == ORIENTATION.NULL) {
                return 0;
            }
            if (mOrientation == ORIENTATION.VERTICAL && mRecyclerView.computeVerticalScrollExtent() != 0) {
                return mRecyclerView.computeVerticalScrollRange() / mRecyclerView.computeVerticalScrollExtent();
            } else if (mRecyclerView.computeHorizontalScrollExtent() != 0) {
                return mRecyclerView.computeHorizontalScrollRange() / mRecyclerView.computeHorizontalScrollExtent();
            }
        }
        return 0;
    }

    public void scrollToPosition(int position) {
        this.currentPosition = position;
        if (mAnimator == null) {
            mOnFlingListener.onFling(0, 0);
        }
        if (mAnimator != null) {
            int startPoint = mOrientation == ORIENTATION.VERTICAL ? offsetY : offsetX, endPoint = 0;
            if (mOrientation == ORIENTATION.VERTICAL) {
                endPoint = mRecyclerView.getHeight() * position;
            } else {
                endPoint = mRecyclerView.getWidth() * position;
            }
            if (startPoint != endPoint) {
                mAnimator.setIntValues(startPoint, endPoint);
                mAnimator.start();
            }
        }
    }

    public void checkCurrentStatus() {
        if(mOrientation == ORIENTATION.VERTICAL) {
            if(mRecyclerView != null) {
                if(offsetY != mRecyclerView.getHeight() * currentPosition) {
                    offsetX = mRecyclerView.getHeight() * currentPosition;
                    mRecyclerView.scrollTo(0, offsetY);
                }
            }
        }else {
            if(mRecyclerView != null) {
                if(offsetX != mRecyclerView.getWidth() * currentPosition) {
                    offsetX = mRecyclerView.getWidth() * currentPosition;
                    mRecyclerView.scrollTo(offsetX, 0);
                }
            }
        }
    }
 
    public class PageOnFlingListener extends RecyclerView.OnFlingListener {
 
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            if (mOrientation == ORIENTATION.NULL) {
                return false;
            }
            //Gets the index of the page on which scrolling begins
            int p = getStartPageIndex();
 
            //Record where scrolling begins and ends
            int endPoint = 0;
            int startPoint = 0;
 
            //If it's in the vertical direction
            if (mOrientation == ORIENTATION.VERTICAL) {
                startPoint = offsetY;
 
                if (velocityY < 0) {
                    p--;
                } else if (velocityY > 0) {
                    p++;
                }
                endPoint = p * mRecyclerView.getHeight();
 
            } else {
                startPoint = offsetX;
                if (velocityX < 0) {
                    p--;
                } else if (velocityX > 0) {
                    p++;
                }
                endPoint = p * mRecyclerView.getWidth();
 
            }
            if (endPoint < 0) {
                endPoint = 0;
            }

            if (mAnimator == null) {
                mAnimator =  ValueAnimator.ofInt(startPoint, endPoint);
 
                mAnimator.setDuration(200);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int nowPoint = (int) animation.getAnimatedValue();
 
                        if (mOrientation == ORIENTATION.VERTICAL) {
                            int dy = nowPoint - offsetY;
                            mRecyclerView.scrollBy(0, dy);
                        } else {
                            int dx = nowPoint - offsetX;
                            mRecyclerView.scrollBy(dx, 0);
                        }
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        int pageIndex = getPageIndex();
                        if(lastPageIndex != pageIndex) {
                            if (null != mOnPageChangeListener) {
                                mOnPageChangeListener.onPageChange(pageIndex);
                            }
                            lastPageIndex = pageIndex;
                        }
                        mRecyclerView.stopScroll();
                        startY = offsetY;
                        startX = offsetX;
                    }
                });
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }
            mAnimator.start();
            return true;
        }
    }
 
    public class PageOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //newState ==0 indicates that scrolling stops and rollback needs to be processed
            if (newState == 0 && mOrientation != ORIENTATION.NULL) {
                boolean move;
                int vX = 0, vY = 0;
                if (mOrientation == ORIENTATION.VERTICAL) {
                    int absY = Math.abs(offsetY - startY);
                    move = absY > recyclerView.getHeight() / 2;
                    vY = 0;
                    if (move) {
                        vY = offsetY - startY < 0 ? -1000 : 1000;
                    }
                } else {
                    int absX = Math.abs(offsetX - startX);
                    move = absX > recyclerView.getWidth() / 2;
                    if (move) {
                        vX = offsetX - startX < 0 ? -1000 : 1000;
                    }
                }
                mOnFlingListener.onFling(vX, vY);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            offsetY += dy;
            offsetX += dx;
        }
    }

    public class MyOnTouchListener implements View.OnTouchListener {
 
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (firstTouch) {
                firstTouch = false;
                startY = offsetY;
                startX = offsetX;
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                firstTouch = true;
            }
            return false;
        }
    }
 
    private int getPageIndex() {
        int p = 0;
        if (mRecyclerView.getHeight() == 0 || mRecyclerView.getWidth() == 0) {
            return p;
        }
        if (mOrientation == ORIENTATION.VERTICAL) {
            p = offsetY / mRecyclerView.getHeight();
        } else {
            p = offsetX / mRecyclerView.getWidth();
        }
        return p;
    }
 
    private int getStartPageIndex() {
        int p = 0;
        if (mRecyclerView.getHeight() == 0 || mRecyclerView.getWidth() == 0) {
            return p;
        }
        if (mOrientation == ORIENTATION.VERTICAL) {
            p = startY / mRecyclerView.getHeight();
        } else {
            p = startX / mRecyclerView.getWidth();
        }
        return p;
    }

    /**
     * set page change listener
     * @param listener
     */
    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }
 
    public interface onPageChangeListener {
        void onPageChange(int index);
    }
 
}