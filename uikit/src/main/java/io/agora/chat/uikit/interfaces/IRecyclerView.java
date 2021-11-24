package io.agora.chat.uikit.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public interface IRecyclerView {

    /**
     * Add header adapter
     * @param adapter
     */
    void addHeaderAdapter(RecyclerView.Adapter adapter);

    /**
     * Add footer adapter
     * @param adapter
     */
    void addFooterAdapter(RecyclerView.Adapter adapter);

    void removeAdapter(RecyclerView.Adapter adapter);

    void addRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor);

    void removeRVItemDecoration(@NonNull RecyclerView.ItemDecoration decor);
    
    default void setOnItemClickListener(OnItemClickListener listener) {}
    
    default void setOnItemLongClickListener(OnItemLongClickListener listener) {}
}

