package io.agora.chat.uikit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

/**
 * Refer to：https://github.com/idisfkj/EnhanceRecyclerView
 */
public class EaseRecyclerView extends RecyclerView {
    private RecyclerViewContextMenuInfo mContextMenuInfo;
    private Adapter mAdapter;
    private boolean isStaggered;
    private boolean isShouldSpan;

    public EaseRecyclerView(@NonNull Context context) {
        super(context);
    }

    public EaseRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if(!(adapter instanceof WrapperRecyclerViewAdapter)) {
            if(adapter != null) {
                adapter.registerAdapterDataObserver(mObserver);
            }
            mAdapter = new WrapperRecyclerViewAdapter(adapter);
        }
        super.setAdapter(mAdapter);
        if(isShouldSpan) {
            ((WrapperRecyclerViewAdapter)mAdapter).adjustSpanSize(this);
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if(layout instanceof GridLayoutManager || layout instanceof  StaggeredGridLayoutManager) {
            isShouldSpan = true;
        }
        super.setLayoutManager(layout);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = getChildBindingAdapterPosition(originalView);
        if(longPressPosition >= 0) {
            long longPressId = getAdapter().getItemId(longPressPosition);
            mContextMenuInfo = new RecyclerViewContextMenuInfo(longPressPosition, longPressId);
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }

    public int getChildBindingAdapterPosition(@NonNull View child) {
        final RecyclerView.ViewHolder holder = getChildViewHolderInt(child);
        return holder != null ? holder.getBindingAdapterPosition() : NO_POSITION;
    }

    RecyclerView.ViewHolder getChildViewHolderInt(View child) {
        if (child == null) {
            return null;
        }
        return getChildViewHolder(child);
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(@NonNull View child) {
        return super.getChildViewHolder(child);
    }

    public class FixedViewInfo {
        public View view;
        public int viewType;
    }

    public class WrapperRecyclerViewAdapter extends Adapter<RecyclerView.ViewHolder> {
        private Adapter mAdapter;

        public WrapperRecyclerViewAdapter(Adapter adapter) {
            this.mAdapter = adapter;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            mAdapter.onBindViewHolder(holder, position);
        }

        @Override
        public int findRelativeAdapterPositionIn(@NonNull Adapter<? extends RecyclerView.ViewHolder> adapter,
                                                 @NonNull RecyclerView.ViewHolder viewHolder, int localPosition) {
            if(adapter == this) {
                return localPosition;
            }else {
                if(mAdapter instanceof ConcatAdapter) {
                    List<? extends Adapter<? extends RecyclerView.ViewHolder>> adapters = ((ConcatAdapter) mAdapter).getAdapters();
                    int prePosition = 0;
                    for(int i = 0; i < adapters.size(); i++) {
                        Adapter<? extends RecyclerView.ViewHolder> curAdapter = adapters.get(i);
                        if(curAdapter == adapter) {
                            return localPosition - prePosition;
                        }else {
                            prePosition += curAdapter.getItemCount();
                        }
                    }
                    return NO_POSITION;
                }
            }
            return super.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return getContentCount();
        }

        @Override
        public int getItemViewType(int position) {
            return mAdapter.getItemViewType(position);
        }

        public int getContentCount() {
            return mAdapter == null ? 0 : mAdapter.getItemCount();
        }

        public void adjustSpanSize(RecyclerView recyclerView) {
            if(recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return manager.getSpanCount();
                    }
                });
            }

            if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                isStaggered = true;
            }
        }

        public Adapter getAdapter() {
            return mAdapter;
        }
    }

    private RecyclerView.ViewHolder viewHolder(View itemView) {
        if(isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            itemView.setLayoutParams(params);
        }
        return new ViewHolder(itemView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

//=====================Fix add item shortcut menu======================================
    public static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public int position;
        public long id;

        public RecyclerViewContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }
    }
}
