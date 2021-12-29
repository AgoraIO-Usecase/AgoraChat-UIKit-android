package io.agora.chat.uikit.widget.chatextend;

import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This LayoutManager provides pagination effects similar to ViewPager+GridView.
 * Refer to：https://blog.csdn.net/Y_sunny_U/article/details/89500464
 */
public class HorizontalPageLayoutManager extends RecyclerView.LayoutManager implements PageDecorationLastJudge {
    private int totalHeight = 0;
    private int totalWidth = 0;
    private int offsetY = 0;
    private int offsetX = 0;
    private int rows = 0;
    private int columns = 0;
    private int pageSize = 0;
    private int itemWidth = 0;
    private int itemHeight = 0;
    private int onePageSize = 0;
    private int itemWidthUsed;
    private int itemHeightUsed;
    private int itemSetHeight;
    private boolean isUseSetHeight;
    private int heightMode;
    private SparseArray<Rect> allItemFrames = new SparseArray<>();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public HorizontalPageLayoutManager(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.onePageSize = rows * columns;
    }
    
    public void setItemHeight(int height) {
        itemSetHeight = height;
        isUseSetHeight = height > 0;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int newX = offsetX + dx;
        int result = dx;
        if (newX > totalWidth) {
            result = totalWidth - offsetX;
        } else if (newX < 0) {
            result = 0 - offsetX;
        }
        offsetX += result;
        offsetChildrenHorizontal(-result);
        recycleAndFillItems(recycler, state);
        return result;
    }

    private int getUsableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getUsableHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        heightMode = View.MeasureSpec.getMode(heightSpec);
        if(heightMode == View.MeasureSpec.AT_MOST) {
            if(isUseSetHeight) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(itemSetHeight * rows, View.MeasureSpec.EXACTLY);
            }
            totalHeight = View.MeasureSpec.getSize(heightSpec);
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    /**
     * Returns true using recyclerView for automatic measurement
     * @return
     */
    @Override
    public boolean isAutoMeasureEnabled() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (state.isPreLayout()) {
            return;
        }
        //Gets the average width and height of each Item
        itemWidth = getUsableWidth() / columns;
        itemHeight = getUsableHeight() / rows;

        if(itemHeight == 0) {
            getWrapItemHeight();
        }

        //Calculate the amount of width and height already used, mainly for later measurement
        itemWidthUsed = (columns - 1) * itemWidth;
        itemHeightUsed = (rows - 1) * itemHeight;
        //Count the total number of pages
        computePageSize(state);
        //Calculate the maximum value that can be scrolled horizontally
        totalWidth = (pageSize - 1) * getWidth();
        //Separate views
        detachAndScrapAttachedViews(recycler);

        int count = getItemCount();
        for (int p = 0; p < pageSize; p++) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    int index = p * onePageSize + r * columns + c;
                    if (index == count) {
                        c = columns;
                        r = rows;
                        p = pageSize;
                        break;
                    }
                    View view = recycler.getViewForPosition(index);
                    addView(view);
                    //Measure item
                    measureChildWithMargins(view, itemWidthUsed, itemHeightUsed);

                    int width = getDecoratedMeasuredWidth(view);
                    int height = getDecoratedMeasuredHeight(view);
                    //If the entry height is set, use; If it is not set, the actual itemHeight is used as itemHeight
                    if(isUseSetHeight) {
                        height = getWrapItemHeight();
                        itemHeight = height;
                    }else {
                        if(index == 0 && height != 0) {
                            itemHeight = height;
                        }
                    }
                    Rect rect = allItemFrames.get(index);
                    if (rect == null) {
                        rect = new Rect();
                    }
                    int x = p * getUsableWidth() + c * itemWidth;
                    int y = r * itemHeight;
                    rect.set(x, y, width + x, height + y);
                    allItemFrames.put(index, rect);
                }
            }
            //After each page is recycled, one page of View is recycled for the next page
            removeAndRecycleAllViews(recycler);
        }
        recycleAndFillItems(recycler, state);
        requestLayout();
    }

    private int getWrapItemHeight() {
        if(heightMode == View.MeasureSpec.AT_MOST) {
            if(isUseSetHeight) {
                if(itemSetHeight * rows <= totalHeight) {
                    itemHeight = itemSetHeight;
                }else {
                    itemHeight = totalHeight / rows;
                }
            }else {
                itemHeight = totalHeight / rows;
            }
            return itemHeight;
        }
        return itemHeight;
    }

    private void computePageSize(RecyclerView.State state) {
        pageSize = state.getItemCount() / onePageSize + (state.getItemCount() % onePageSize == 0 ? 0 : 1);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        offsetX = 0;
        offsetY = 0;
    }

    private void recycleAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        Rect displayRect = new Rect(getPaddingLeft() + offsetX, getPaddingTop(), getWidth() - getPaddingLeft() - getPaddingRight() + offsetX, getHeight() - getPaddingTop() - getPaddingBottom());
        Rect childRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childRect.left = getDecoratedLeft(child);
            childRect.top = getDecoratedTop(child);
            childRect.right = getDecoratedRight(child);
            childRect.bottom = getDecoratedBottom(child);
            if (!Rect.intersects(displayRect, childRect)) {
                removeAndRecycleView(child, recycler);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayRect, allItemFrames.get(i))) {
                View view = recycler.getViewForPosition(i);
                addView(view);
                measureChildWithMargins(view, itemWidthUsed, itemHeightUsed);
                Rect rect = allItemFrames.get(i);
                layoutDecorated(view, rect.left - offsetX, rect.top, rect.right - offsetX, rect.bottom);
            }
        }

    }

    @Override
    public boolean isLastRow(int index) {
        if (index >= 0 && index < getItemCount()) {
            int indexOfPage = index % onePageSize;
            indexOfPage++;
            if (indexOfPage > (rows - 1) * columns && indexOfPage <= onePageSize) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLastColumn(int position) {
        if (position >= 0 && position < getItemCount()) {
            position++;
            if (position % columns == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPageLast(int position) {
        position++;
        return position % onePageSize == 0;
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        computePageSize(state);
        return pageSize * getWidth();
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return offsetX;
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return getWidth();
    }
}